package day08

import java.io.File

fun main() {
    val input = File("src/main/day08/input.txt").readLines()
    val instructions = input[0]
    val nodes = mutableListOf<String>()

    val nodesMap = input.subList(2, input.size).map {
        val (node, left, right) = it.trim(')').split(" = (", ", ")
        //println("Node:$node Left:$left Right:$right")
        nodes.add(node)

        return@map node to Pair(left, right)
    }.toMap()

    // Solve for part 1
    part1(instructions, nodesMap)

    // Solve for part 2
    val nodesEndInA = nodes.filter { it.endsWith('A') }
    part2(instructions, nodesEndInA, nodesMap)
}

fun part1(instructions: String, nodeMap: Map<String, Pair<String, String>>) {
    var nodeCurr = "AAA"
    val nodeEnd = "ZZZ"

    var stepCount = 0
    while (nodeCurr != nodeEnd) {
        for (inst in instructions) {
            stepCount++
            val node = nodeMap[nodeCurr]!!
            nodeCurr = if (inst == 'L') node.first else node.second

            if (nodeCurr == nodeEnd) {
                break
            }
        }
    }

    println("Part 1: $stepCount")
}

fun part2(instructions: String, nodesPos: List<String>, nodeMap: Map<String, Pair<String, String>>) {
    val stepsToReachZFromEachStartNode = mutableListOf<Long>()
    var stepCount = 0L
    val newNodePos = nodesPos.map { it }.toMutableList()

    var allReachedZ = false
    while (!allReachedZ) {
        for (inst in instructions) {
            if (allReachedZ) {
                break
            }
            stepCount++

            for (item in newNodePos.withIndex()) {
                val node = nodeMap[item.value]!!.run { if (inst == 'L') first else second }

                if (node.endsWith('Z')) {
                    stepsToReachZFromEachStartNode.add(stepCount)

                    if (stepsToReachZFromEachStartNode.size == newNodePos.size) {
                        // All nodes have reached Z
                        allReachedZ = true
                        break
                    }
                }
                newNodePos[item.index] = node
            }
        }
    }

    // Find the LCM of all the steps to reach for each Z
    // This is the lowest number of steps for all the nodes to reach Z at the same time
    val minSteps = lcm(stepsToReachZFromEachStartNode)

    println("Part 2: $minSteps")
}

// There is probably a library for this stuff, but I just copy pasta
/** Greatest Common Divisor of 2 numbers*/
fun gcd(a: Long, b: Long): Long {
    return if (b == 0L) a else gcd(b, a % b)
}

/** Least Common Multiple of 2 numbers */
fun lcm(a: Long, b: Long): Long {
    return a * b / gcd(a, b)
}

/** Least Common Multiple of a collection of numbers */
fun lcm(a: Collection<Long>): Long {
    return a.reduce { acc, l -> lcm(acc, l) }
}