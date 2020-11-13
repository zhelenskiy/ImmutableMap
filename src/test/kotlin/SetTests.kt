import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit
import kotlin.math.pow
import kotlin.random.Random
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue
import kotlin.time.toDuration

class MergeTests {
    @ExperimentalTime
    @Test
    fun validData() {
        repeat(10_000) {
            val data1 = Array(1000) { Random.nextInt(100) }.toList()
            val data2 = Array(1000) { Random.nextInt(100) }.toList()
            val expected = (data1 + data2).toSortedSet()
            assertIterableEquals(expected, immutableTreapSet(data1 + data2))
            assertIterableEquals(expected, immutableTreapSet(data2 + data1))
            val set1 = immutableTreapSet(data1)
            val set2 = immutableTreapSet(data2)
            assertIterableEquals(expected, set1 + set2)
            assertIterableEquals(expected, set1 + data2)
        }
    }

    @ExperimentalTime
    @Test
    fun performance() {
        for (size1 in (0..7).map { 10.0.pow(it).toInt() }) {
            for (size2 in (0..7).map { 10.0.pow(it).toInt() }) {
                repeat(3) {
                    val data1 = Array(size1) { Random.nextInt(100) }.toList()
                    val data2 = Array(size2) { Random.nextInt(100) }.toList()
                    val set1 = immutableTreapSet(data1)
                    val set2 = immutableTreapSet(data2)
                    val epsilon = 100.toDuration(TimeUnit.MICROSECONDS)

                    val (naiveValue, naiveDuration) = measureTimedValue { immutableTreapSet(data1 + data2) }
                    val (customValue, customDuration) = measureTimedValue { set1 + set2 }
                    val (standardValue, standardDuration) = measureTimedValue { (data1 + data2).toSortedSet() }

                    assertIterableEquals(naiveValue, customValue)
                    println("Naive")
                    assertTrue(
                        customDuration <= naiveDuration + epsilon,
                        "$size1 $size2: $customDuration > $naiveDuration + $epsilon"
                    )
                    if (customDuration <= naiveDuration)
                        println("$size1 + $size2: $customDuration <= $naiveDuration")
                    else {
                        System.err.println("ATTENTION")
                        println("$size1 + $size2: $customDuration <= $naiveDuration + $epsilon")
                    }

                    assertIterableEquals(standardValue, customValue)
                    if (size1 > 1 && size2 > 1) {
                        println("Standard")
                        assertTrue(
                            customDuration <= standardDuration + epsilon,
                            "$size1 $size2: $customDuration > $standardDuration + $epsilon"
                        )
                        if (customDuration <= standardDuration)
                            println("$size1 + $size2: $customDuration <= $standardDuration")
                        else {
                            System.err.println("ATTENTION")
                            println("$size1 + $size2: $customDuration <= $standardDuration + $epsilon")
                        }
                    }
                }
            }
        }
    }

    @Test
    fun contains() {
        val a = 50..100
        val set = immutableTreapSet(a)
        for (item in 1..49) assertFalse(item in set)
        for (item in 50..100) assertTrue(item in set)
    }

    @Test
    fun delete() {
        assertIterableEquals(immutableTreapSetOf(4, 5), immutableTreapSetOf(4, 5).remove(2))
        assertIterableEquals(immutableTreapSetOf<Int>(), immutableTreapSetOf(4).remove(4))
        assertIterableEquals((1..50) + (52..100), immutableTreapSet(1..100).remove(51))
        assertIterableEquals((1..9) + (91..100), immutableTreapSet(1..100).removeAll(10..90))
        assertIterableEquals((1..9) + (91..100), immutableTreapSet(1..100).removeAll((10..90).toList().toTypedArray()))
    }

    @Test
    fun iterate() {
        val arr = Array(100) { Random.nextInt() }.toList()
        val set = immutableTreapSet(arr)
        assertIterableEquals(arr.sorted(), set)
        val it1 = set.iterator()
        val it2 = set.iterator()
        it1.next(); it1.next()
        assertIterableEquals(
            arr.sorted().drop(2) zip arr.sorted(),
            generateSequence { if (it1.hasNext()) Pair(it1.next(), it2.next()) else null }.asIterable()
        )
    }
}