package day11

import java.io.File
import kotlin.math.abs

fun main() {
    val input = File("src/main/day11/input.txt").readLines()

    val galaxies = input.mapIndexed{ row, line ->
        line.mapIndexed { col, c ->
            if (c == '#') Pair(row, col) else null
        }.filterNotNull()
    }.flatten().toSet()

    val emptyRows = input.mapIndexed { row, line ->
        if (!line.contains('#')) row else null
    }.filterNotNull().toSet()

    val emptyCols = input[0].mapIndexed { col, _ ->
        if (!searchCol(col, input)) col else null
    }.filterNotNull().toSet()

    val galaxyPairs = findGalaxyPairs(galaxies)

    println("Part 1: ${solve(galaxyPairs, emptyRows, emptyCols, false)}")
    println("Part 2: ${solve(galaxyPairs, emptyRows, emptyCols, true)}")
}

fun solve(galaxyPairs: List<GalaxyPair>, emptyRows: Set<Int>, emptyCols: Set<Int>, part2: Boolean): Long {
    // find distance between each pair of galaxies

    val distances = galaxyPairs.sumOf { pair ->
        val (row, col) = pair.galaxy
        val (otherRow, otherCol) = pair.other
        val rowRange = if (row < otherRow) row..otherRow else otherRow..row
        val colRange = if (col < otherCol) col..otherCol else otherCol..col

        var emptySpaces = 0
        for (i in emptyRows) {
            if (i in rowRange) {
                emptySpaces++
            }
        }

        for (i in emptyCols) {
            if (i in colRange) {
                emptySpaces++
            }
        }

        val scale = if(part2) 1000000L else 2
        val additionalSpaces = emptySpaces * scale - emptySpaces

        val distance = abs(row - otherRow) + abs(col - otherCol) + additionalSpaces
        distance
    }
    return distances
}

data class GalaxyPair(val galaxy: Pair<Int, Int>, val other: Pair<Int, Int>)

fun findGalaxyPairs(galaxies: Set<Pair<Int, Int>>): MutableList<GalaxyPair> {
    val galaxyPairs = mutableListOf<GalaxyPair>()
    galaxies.map { galaxy ->
        galaxies.filter { it.first >= galaxy.first }.forEach { other ->
            if (galaxy == other
                || galaxyPairs.contains(GalaxyPair(galaxy, other))
                || galaxyPairs.contains(GalaxyPair(other, galaxy))) {
                // do nothing
            } else {
                galaxyPairs.add(GalaxyPair(galaxy, other))
            }
        }
    }
    return galaxyPairs
}

fun searchCol(col: Int, input: List<String>): Boolean {
    for (i in input.size - 1 downTo 0) {
        if (input[i][col] == '#') {
            return true
        }
    }
    return false
}
