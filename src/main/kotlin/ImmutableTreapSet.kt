import java.util.concurrent.ThreadLocalRandom

fun<K> immutableTreapSet(comparator: Comparator<K>) = ImmutableTreapSet(comparator)
fun<K : Comparable<K>> immutableTreapSet() = ImmutableTreapSet(naturalOrder<K>())
fun<K> immutableTreapSet(elements: Iterable<K>, comparator: Comparator<K>) =
    immutableTreapSet(comparator).addAll(elements)
fun<K : Comparable<K>> immutableTreapSet(elements: Iterable<K>) = immutableTreapSet<K>().addAll(elements)
fun<K : Comparable<K>> immutableTreapSetOf(vararg elements: K) = immutableTreapSet<K>().addAll((elements))

class ImmutableTreapSet<K> private constructor(
    private val rootNode: Node<K>?,
    val comparator: Comparator<K>
) : Set<K> {
    internal constructor(comparator: Comparator<K>) : this(null, comparator)

    private operator fun Node<K>?.contains(other: K): Boolean {
        if (this == null) return false
        val comparison = comparator.compare(other, value)
        return when {
            comparison > 0 -> other in right
            comparison < 0 -> other in left
            else -> true
        }
    }

    override fun isEmpty() = size == 0
    override fun contains(element: K): Boolean = element in rootNode

    override fun containsAll(elements: Collection<K>) = elements.all { it in this }

    private inner class TreapIterator(root: Node<K>?) : Iterator<K> {

        val stack = mutableListOf<Node<K>>()

        tailrec fun Node<K>.goLeft(): Node<K> = if (this.left == null) this else {
            stack.add(this)
            this.left.goLeft()
        }

        var cur: Node<K>? = root?.goLeft()

        override fun hasNext(): Boolean = cur != null

        override fun next(): K = cur!!.value.also {
            cur = cur?.right?.goLeft() ?: stack.removeLastOrNull()
        }
    }

    override fun iterator(): Iterator<K> = TreapIterator(rootNode)

    private fun Node<K>?.splitAt(other: K): Triple<Node<K>?, K?, Node<K>?> =
        if (this == null) Triple(null, null, null) else comparator.compare(other, value).let {
            when {
                it > 0 -> {
                    val (l, v, r) = right.splitAt(other)
                    Triple(Node(value, left, l, weight), v, r)
                }
                it < 0 -> {
                    val (l, v, r) = left.splitAt(other)
                    Triple(l, v, Node(value, r, right, weight))
                }
                else -> Triple(left, value, right)
            }
        }

    private infix fun Node<K>?.mergeWith(other: Node<K>?): Node<K>? = when {
        this == null -> other
        other == null -> this
        this.weight > other.weight -> Node(this.value, this.left, this.right mergeWith other, this.weight)
        else -> Node(other.value, this mergeWith other.left, other.right, other.weight)
    }

    fun add(element: K): ImmutableTreapSet<K> {
        val (l, v, r) = rootNode.splitAt(element)
        return if (v != null) this
        else {
            val newNode = Node(element, null, null, ThreadLocalRandom.current().nextLong())
            ImmutableTreapSet(l mergeWith newNode mergeWith r, comparator)
        }
    }

    fun addAll(other: ImmutableTreapSet<K>): ImmutableTreapSet<K> = when {
        this.rootNode == null -> other
        other.rootNode == null -> this
        this.comparator != other.comparator -> addAll(other as Iterable<K>)
        this.size < other.size -> other.addAll(this)
        this.size < 100 && other.size < 100 -> immutableTreapSet(this as Iterable<K> + other as Iterable<K>, comparator)
        other.size < 100 -> addAll(other as Iterable<K>)
        else -> {
            var min : Node<K> = rootNode
            while (min.left != null) min = min.left!!
            var max: Node<K> = rootNode
            while (max.right != null) max = max.right!!
            val (l, _, cr) = other.rootNode.splitAt(min.value)
            val (c, _, r) = cr.splitAt(max.value)
            val centre = this.addAll(ImmutableTreapSet(c, comparator) as Iterable<K>)
            ImmutableTreapSet(l mergeWith centre.rootNode mergeWith r, comparator)
        }
    }
    fun addAll(elements: Iterable<K>): ImmutableTreapSet<K> = elements.fold(this) { treap, value -> treap.add(value) }
    fun addAll(elements: Array<out K>): ImmutableTreapSet<K> = elements.fold(this) { treap, value -> treap.add(value) }

    fun remove(element: K): ImmutableTreapSet<K> {
        val (l, v, r) = rootNode.splitAt(element)
        return if (v == null) this else ImmutableTreapSet(l mergeWith r, comparator)
    }

    fun removeAll(elements: Iterable<K>): ImmutableTreapSet<K> = elements.fold(this) { treap, value -> treap.remove(value) }
    fun removeAll(elements: Array<out K>): ImmutableTreapSet<K> = elements.fold(this) { treap, value -> treap.remove(value) }

    override fun toString() = this.joinToString(prefix = "{", postfix = "}")
    override val size: Int
        get() = rootNode?.count ?: 0

    operator fun plus(other: Iterable<K>) = addAll(other)
    operator fun plus(other: ImmutableTreapSet<K>) = addAll(other)

    operator fun get(index: Int) : K = rootNode.get(index)
}

private data class Node<K>(val value: K, val left: Node<K>?, val right: Node<K>?, val weight: Long) {
    val count : Int = 1 + (left?.count ?: 0) + (right?.count ?: 0)

    inline val Node<K>?.count : Int
        get() = this?.count ?: 0
}

private fun<K> Node<K>?.get(index: Int): K = when {
    this == null || index < 0 || index >= count-> throw IndexOutOfBoundsException()
    index < left.count -> left.get(index)
    index == left.count -> value
    else -> right.get(index - 1 - left.count)
}