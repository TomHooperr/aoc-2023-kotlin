package day13

import java.io.File

fun main() {
    val input = File("src/main/day13/input.txt").readLines()

    val pattern = mutableListOf<String>()
    val patternList = mutableListOf<List<String>>()
    input.forEach {
        if (it.isNotBlank()) {
            pattern.add(it)
        }
        else {
            patternList.add(pattern.toList())
            pattern.clear()
        }
    }
    patternList.add(pattern.toList())

    val horizontalReflections = mutableListOf<Int>()
    val verticalReflections = mutableListOf<Int>()
    patternList.forEach {
        horizontalReflections.add(findReflection(it))
        verticalReflections.add(findReflection(rotatePattern(it)))
    }

//    println(horizontalReflections)
//    println(verticalReflections)

    val horizontalTotal = horizontalReflections.sumOf { (it+1) * 100 }
    val verticalTotal = verticalReflections.sumOf{ it+1 }

    println("Part 1: ${horizontalTotal + verticalTotal}")

    val fixedHorizontalReflections = mutableListOf<Int>()
    val fixedVerticalReflections = mutableListOf<Int>()
    patternList.forEachIndexed { idx, pat ->
        val fixedHorizontal = fixSmudges(pat)
            .map {findReflection(it, horizontalReflections[idx])}
            .firstOrNull{it != -1}

        val fixedVertical = fixSmudges(rotatePattern(pat))
            .map {findReflection(it, verticalReflections[idx])}
            .firstOrNull{it != -1}

        fixedHorizontalReflections.add(fixedHorizontal ?: -1)
        fixedVerticalReflections.add(fixedVertical ?: -1)
    }

//    println(fixedHorizontalReflections)
//    println(fixedVerticalReflections)

    val fixedHorizontalTotal = fixedHorizontalReflections.sumOf { (it+1) * 100 }
    val fixedVerticalTotal = fixedVerticalReflections.sumOf{ it+1 }
    println("Part 2: ${fixedHorizontalTotal + fixedVerticalTotal}")
}

/**
 * Find the index of the reflection line in given pattern.
 */
fun findReflection(pattern: List<String>): Int {
    val result = pattern.mapIndexed { i, s ->
        val next = i + 1
        var patternFound = false
        if (next < pattern.size && s == pattern[next]) {
            var left = i - 1
            var right = next + 1
            while (!patternFound) {
                if (left < 0 || right >= pattern.size ) {
                    patternFound = true
                } else if (pattern[left] != pattern[right]) {
                    break
                }
                left--
                right++
            }
        }
        if (patternFound) i else -1
    }.filter { it != -1 }

    return if (result.isEmpty()) -1 else result.first()
}

/**
 * Find the index of the reflection line of in the given line other than the line at the specified index.
 */
fun findReflection(pattern: List<String>, excluded: Int): Int {
    val result = pattern.mapIndexed { i, s ->
        if (i == excluded) return@mapIndexed -1

        val next = i + 1
        var patternFound = false
        if (next < pattern.size && s == pattern[next]) {
            var left = i - 1
            var right = next + 1
            while (!patternFound) {
                if (left < 0 || right >= pattern.size ) {
                    patternFound = true
                } else if (pattern[left] != pattern[right]) {
                    break
                }
                left--
                right++
            }
        }
        if (patternFound) i else -1
    }.filter { it != -1 }

    return if (result.isEmpty()) -1 else result.first()
}

/**
 * Rotate the pattern so that we can search for the vertical reflection horizontally
 */
fun rotatePattern(pattern: List<String>): List<String>{
    return pattern[0].indices.map { i ->
        pattern.map {
            it[i]
        }.joinToString("")
    }
}

/**
 * Find lines that differ by one symbol and generate copies of the original pattern where these lines are identical
 */
fun fixSmudges(pattern: List<String>): List<List<String>> {
    val desmudged = mutableListOf<MutableList<String>>()
    pattern.forEachIndexed { i, line ->
        for (other in pattern.indices) {
            if (line.zip(pattern[other]).count { pair -> pair.first != pair.second } == 1) {
                val pattern1 = pattern.toMutableList()
                pattern1[i] = pattern[other]
                val pattern2 = pattern.toMutableList()
                pattern2[other] = line
                desmudged.addAll(listOf(pattern1, pattern2))
            }
        }
    }
    return desmudged
}