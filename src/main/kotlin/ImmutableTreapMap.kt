class ImmutableTreapMap<K, V> : Map<K, V> {
    /**
     * Returns a read-only [Set] of all key/value pairs in this map.
     */
    override val entries: Set<Map.Entry<K, V>>
        get() = TODO("Not yet implemented")

    /**
     * Returns a read-only [Set] of all keys in this map.
     */
    override val keys: Set<K>
        get() = TODO("Not yet implemented")

    /**
     * Returns the number of key/value pairs in the map.
     */
    override val size: Int
        get() = TODO("Not yet implemented")

    /**
     * Returns a read-only [Collection] of all values in this map. Note that this collection may contain duplicate values.
     */
    override val values: Collection<V>
        get() = TODO("Not yet implemented")

    /**
     * Returns `true` if the map contains the specified [key].
     */
    override fun containsKey(key: K): Boolean {
        TODO("Not yet implemented")
    }

    /**
     * Returns `true` if the map maps one or more keys to the specified [value].
     */
    override fun containsValue(value: V): Boolean {
        TODO("Not yet implemented")
    }

    /**
     * Returns the value corresponding to the given [key], or `null` if such a key is not present in the map.
     */
    override fun get(key: K): V? {
        TODO("Not yet implemented")
    }

    /**
     * Returns `true` if the map is empty (contains no elements), `false` otherwise.
     */
    override fun isEmpty(): Boolean {
        TODO("Not yet implemented")
    }
}