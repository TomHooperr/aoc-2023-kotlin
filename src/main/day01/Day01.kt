package day01

import java.io.File

fun main() {
    val input = File("src/main/day01/input.txt").readLines()

    println("Part 1: ${part1(input)}")
    println("Part 2: ${part2(input)}")
}

fun part1(input: List<String>): Int {
    return input.sumOf { line ->
        "${line.first{ it.isDigit() }}${line.last { it.isDigit() }}".toInt()
    }
}

fun part2(input: List<String>): Int {
    val words = listOf("one", "two", "three", "four", "five", "six", "seven", "eight", "nine")

    val replaced = input.map {
        var new = it
        words.forEachIndexed { index, word ->
            // Replace a letter in the word with its digit
            new = new.replace(word, word.replaceRange(1, 1, (index + 1).toString()))
        }
        new
    }

    return part1(replaced)
}