import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@ExperimentalTime
fun main() {
    val set1 = immutableTreapSet(10 downTo 1)
    val set2 = immutableTreapSet(100 downTo -10)
    println(set1 + set2)
    println(set1)
    val message = immutableTreapSetOf(4) + immutableTreapSetOf(4)
    measureTime {
        immutableTreapSet(1..1000000) + immutableTreapSet(-100000..100000)
    }.also(::println)
    measureTime {
        immutableTreapSet(1..1000) + immutableTreapSet(-100000..100000)
    }.also(::println)
    measureTime {
        immutableTreapSet(1..1000) as Iterable<Int> + immutableTreapSet(-100000..100000)
    }.also(::println)
    measureTime {
        3 in immutableTreapSet(1..1000)
    }.also(::println)
    measureTime {
        @Suppress("ControlFlowWithEmptyBody")
        for (a in immutableTreapSet(1..1000)) {}
    }.also(::println)
    println(message)
    println(set2)
    println(set2 + set1)
    println(immutableTreapSet(1..10)[5])
}