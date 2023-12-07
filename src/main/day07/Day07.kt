package day07

import java.io.File

val cardValuesPart1 = mapOf(
    '2' to "01",
    '3' to "02",
    '4' to "03",
    '5' to "04",
    '6' to "05",
    '7' to "06",
    '8' to "07",
    '9' to "08",
    'T' to "09",
    'J' to "10",
    'Q' to "11",
    'K' to "12",
    'A' to "13"
)

val cardValuesPart2 = mapOf(
    'J' to "01",
    '2' to "02",
    '3' to "03",
    '4' to "04",
    '5' to "05",
    '6' to "06",
    '7' to "07",
    '8' to "08",
    '9' to "09",
    'T' to "10",
    'Q' to "11",
    'K' to "12",
    'A' to "13"
)

fun main() {
    val hands = File("src/day07.day06.main/day07/input.txt").readLines().map {
        it.split(' ')
    }

    part1(hands)
    part2(hands)
}

fun part1(hands: List<List<String>>) {
    val handsSorted = sortHandsPart1(hands)
    val result = calculateWinnings(handsSorted)
    println("Part 1: $result")
}

fun part2(hands: List<List<String>>) {
    val handsSorted = sortHandsPart2(hands)
    val result = calculateWinnings(handsSorted)
    println("Part 2: $result")
}

fun sortHandsPart1(hands: List<List<String>>): List<Hand> {
    return hands.map {
        val cards = it[0]
        val cardCounts = cards.groupingBy { it }.eachCount()
        val bid = it[1].toInt()

        return@map Hand(cards, bid, cardCounts, cardValuesPart1)
    }.sortedBy {
        handStrength(it)
    }
}

fun sortHandsPart2(hands: List<List<String>>): List<Hand> {
    return hands.map {
        val cards = it[0]
        var cardCounts = cards.groupingBy { it }.eachCount()
        val bid = it[1].toInt()

        cardCounts = applyJokers(cards, cardCounts)

        return@map Hand(cards, bid, cardCounts, cardValuesPart2)
    }.sortedBy {
        handStrength(it)
    }
}

fun calculateWinnings(hands: List<Hand>): Int = hands.indices.sumOf {
    val hand = hands[it]
    val rank = it + 1

    return@sumOf hand.bid * rank
}

fun applyJokers(cards: String, cardCounts: Map<Char, Int>): Map<Char, Int> {
    var maxOccurCard = cardCounts.filterKeys { it != 'J' }.keys.firstOrNull() ?: return cardCounts

    cardCounts.filterKeys { it != 'J' }.forEach {
        if (it.value > cardCounts[maxOccurCard]!!) {
            maxOccurCard = it.key
        }
    }

    return cards.replace('J', maxOccurCard)
            .groupingBy { it }
            .eachCount()
}

fun handStrength(hand: Hand): Long {
    val handTypeValue = hand.cardCounts.values.max() + (hand.cards.length - hand.cardCounts.size)
    val cardValuesString = hand.cards.map {
        hand.cardValues.getOrDefault(it, "00")
    }.joinToString("")

    return "${handTypeValue}${cardValuesString}".toLong()
}

class Hand(var cards: String, var bid: Int, var cardCounts: Map<Char, Int>, var cardValues: Map<Char, String>)