package day03

import java.io.File

fun main() {
    val input = File("src/main/day03/input.txt").readLines()

    // Parse input
    val numbers = mutableListOf<Number>()
    val symbols = mutableListOf<Symbol>()
    input.forEachIndexed { row, s ->
        var numStart: Int = -1
        s.forEachIndexed { col, c ->
            if (c.isDigit()) {
                if (numStart == -1)
                    numStart = col
            }
            else {
                if (c != '.') {
                    val symbol = Symbol(c, row, col)
                    symbols.add(symbol)
                }
                if (numStart != -1) {
                    val num = s.substring(numStart, col).toInt()
                    val range = numStart..<col
                    val number = Number(num, range, row)
                    numbers.add(number)
                    numStart = -1
                }
            }
        }

        // Add number if we've reached end of row
        if (numStart != -1) {
            val num = s.substring(numStart, s.length).toInt()
            val range = numStart..<s.length
            val number = Number(num, range, row)
            numbers.add(number)
        }
    }

    // Solve for part 1 and 2
    println("Part 1: ${part1(numbers, symbols)}")
    println("Part 2: ${part2(numbers, symbols)}")
}

/**
 * Calculate sum of part numbers
 */
fun part1(numbers: List<Number>, symbols: List<Symbol>): Int {
    return symbols.sumOf { symbol ->
        numbers.filter { number ->
            symbol.row in number.adjacentRows && symbol.col in number.adjacentCols
        }.sumOf {
            it.value
        }
    }
}

/**
 * Calculate sum of gear ratios
 */
fun part2(numbers: List<Number>, symbols: List<Symbol>): Int {
    return symbols.filter {
        it.value == '*'
    }.sumOf{ symbol ->
        val adjacentNums = numbers.filter { number ->
            symbol.row in number.adjacentRows && symbol.col in number.adjacentCols
        }

        if (adjacentNums.size == 2)
            adjacentNums[0].value * adjacentNums[1].value
        else 0
    }
}

data class Number(val value: Int, val rangeInt: IntRange, val row: Int) {
    val adjacentCols: IntRange = rangeInt.first - 1..rangeInt.last + 1
    val adjacentRows: IntRange = row - 1..row + 1
}

data class Symbol(val value: Char, val row: Int, val col: Int)
