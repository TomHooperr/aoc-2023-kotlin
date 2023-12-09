package day09

import java.io.File

fun main() {
    val input = File("src/main/day09/input.txt").readLines().map {
        it.split(' ').map {
            it.toLong()
        }
    }

    // Solve Part 1
    input.map { listDifferences(mutableListOf(it), it) }.sumOf {
        calcNextNumber(it)
    }.let {
        println("Part 1: $it")
    }

    // Solve Part 2
    input.map { listDifferences(mutableListOf(it), it) }.sumOf {
        calcPrevNumber(it)
    }.let {
        println("Part 2: $it")
    }
}

fun listDifferences(extrapolated: MutableList<List<Long>>, diff: List<Long>): MutableList<List<Long>> {
    val differences = mutableListOf<Long>()
    for (i in 0..<diff.size - 1) {
        differences.add(diff[i + 1] - diff[i])
    }
    extrapolated.add(differences)

    return if (differences.all { it == 0L })
        extrapolated
    else
        listDifferences(extrapolated, differences)
}

/**
 * Part 1
 */
fun calcNextNumber(extrapolated: List<List<Long>>): Long {
    var next: Long? = null
    extrapolated.reversed().forEach {
        next = if (next == null) {
            it.last()
        } else {
            it.last() + next!!
        }
    }

    return next!!
}


/**
 * Part 2
 */
fun calcPrevNumber(extrapolated: List<List<Long>>): Long {
    var prev: Long? = null
    extrapolated.reversed().forEach {
        prev = if (prev == null) {
            it.first()
        } else {
            it.first() - prev!!
        }
    }
    return prev!!
}