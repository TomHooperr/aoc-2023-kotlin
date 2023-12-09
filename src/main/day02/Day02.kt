package day02

import java.io.File
import kotlin.math.max

fun main() {
    val input = File("src/main/day02/input.txt").readLines().map {
        val (_, id, game) = it.split("Game ", ": ")
        game.split("; ").map {
            val red: Int = "(\\d+) red".toRegex().find(it)?.groupValues?.get(1)?.toInt() ?: 0
            val green: Int = "(\\d+) green".toRegex().find(it)?.groupValues?.get(1)?.toInt() ?: 0
            val blue: Int = "(\\d+) blue".toRegex().find(it)?.groupValues?.get(1)?.toInt() ?: 0

            CubeSet(red, green, blue)
        }.let {
            Game(id.toInt(), it)
        }
    }

    println("Part 1: ${part1(input)}")
    println("Part 2: ${part2(input)}")
}

fun part1(input: List<Game>): Int {
    return input.sumOf { game ->
        if (game.cubeSets.all {
            it.red <= 12 && it.green <= 13 && it.blue <= 14
        }) game.id else 0
    }
}

fun part2(input: List<Game>): Int {
    return input.sumOf {
        it.let {
            val minPossibleSet = it.cubeSets.reduce {
                acc, cubeSet ->
                CubeSet(
                    max(acc.red, cubeSet.red),
                    max(acc.green, cubeSet.green),
                    max(acc.blue, cubeSet.blue)
                )
            }

            minPossibleSet.red * minPossibleSet.green * minPossibleSet.blue
        }
    }
}

data class Game(val id: Int, val cubeSets: List<CubeSet>)

data class CubeSet(val red: Int, val green: Int, val blue: Int)