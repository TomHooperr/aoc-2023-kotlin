package day06

import java.io.File

fun main() {
    val (times, distances) = File("src/main/day06/input.txt").readLines().map {
        it.substringAfter(':').split(' ').filter {
            it.isNotBlank()
        }.map {
            it.toLong()
        }
    }

    println("Part 1: ${part1(times, distances)}")
    println("Part 2: ${part2(times, distances)}")
}

fun part1(times: List<Long>, distances: List<Long>): Long {
    var totalSolutions = 1L
    times.indices.forEach {
        totalSolutions *= findPossibleSolutions(times[it], distances[it])
    }

    return totalSolutions
}

fun part2(times: List<Long>, distances: List<Long>): Long {
    val time = times.joinToString("").toLong()
    val distance = distances.joinToString("").toLong()

    return findPossibleSolutions(time, distance)
}

fun findPossibleSolutions(time: Long, distance: Long): Long {
    var counter = 0L
    for (i in LongRange(0, time)) {
        if ((i * (time - i)) > distance) {
            counter++
        }
    }

    return counter
}
