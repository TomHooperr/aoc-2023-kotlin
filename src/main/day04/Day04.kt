package day04

import java.io.File
import kotlin.math.pow

fun main() {
    val input = File("src/main/day04/input.txt").readLines().map {
        val (_, id, winning, scratch) = it.split("Card ", ": ", " | ")

        ScratchCard(
            id.trim().toInt(),
            winning.trim().split("\\s+".toRegex()).map{ it.toInt() },
            scratch.trim().split("\\s+".toRegex()).map{ it.toInt() })
    }

    println("Part 1: ${part1(input)}")
    println("Part 2: ${part2(input)}")
}

fun part1(input: List<ScratchCard>): Int {
    return input.sumOf { card ->

        val scratch = card.scratch
        val winning = card.winning

        scratch.filter { num ->
            num in winning
        }.let{
            if (it.isEmpty()) 0 else 2.0.pow(it.size-1).toInt()
        }
    }
}

fun part2(input: List<ScratchCard>): Int {
    val cardCopies = input.associateBy({ it.id }, { 1 }).toMutableMap()

    input.forEach { card ->
        val scratch = card.scratch
        val winning = card.winning

        scratch.filter { num ->
            num in winning
        }.let {
            for (i in 1 .. it.size) {
                if (card.id + i in cardCopies) {
                    // Copy current copies to the following cards
                    cardCopies[card.id + i] = cardCopies[card.id + i]!!.plus(cardCopies[card.id]!!)
                }
            }
        }
    }

    return cardCopies.values.sum()
}

data class ScratchCard(val id: Int, val winning: List<Int>, val scratch: List<Int>)