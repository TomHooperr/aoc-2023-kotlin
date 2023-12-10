package day10

import java.io.File

val input = File("src/main/day10/input.txt").readLines()

val pipeType = mapOf(
    '|' to PipeType.VERTICAL,
    '-' to PipeType.HORIZONTAL,
    'L' to PipeType.BOTTOM_LEFT,
    'J' to PipeType.BOTTOM_RIGHT,
    '7' to PipeType.TOP_RIGHT,
    'F' to PipeType.TOP_LEFT,
    'S' to PipeType.START
)

// Map pipe type to the directions it can connect to
val pipeToDirection: Map<PipeType, List<Direction>> = mapOf(
    PipeType.TOP_LEFT to listOf(Direction.DOWN, Direction.RIGHT),
    PipeType.TOP_RIGHT to listOf(Direction.DOWN, Direction.LEFT),
    PipeType.BOTTOM_LEFT to listOf(Direction.UP, Direction.RIGHT),
    PipeType.BOTTOM_RIGHT to listOf(Direction.UP, Direction.LEFT),
    PipeType.VERTICAL to listOf(Direction.UP, Direction.DOWN),
    PipeType.HORIZONTAL to listOf(Direction.LEFT, Direction.RIGHT)
)

// Map direction to the pipe types it can connect to
val directionToPipe = mapOf(
    Direction.UP to listOf(PipeType.TOP_RIGHT, PipeType.TOP_LEFT, PipeType.VERTICAL),
    Direction.DOWN to listOf(PipeType.BOTTOM_RIGHT, PipeType.BOTTOM_LEFT, PipeType.VERTICAL),
    Direction.LEFT to listOf(PipeType.TOP_LEFT, PipeType.BOTTOM_LEFT, PipeType.HORIZONTAL),
    Direction.RIGHT to listOf(PipeType.TOP_RIGHT, PipeType.BOTTOM_RIGHT, PipeType.HORIZONTAL)
)

// Map direction to the scanner direction
val scannerMap = mapOf(
    Direction.UP to Pair(-1, 0),
    Direction.DOWN to Pair(1, 0),
    Direction.LEFT to Pair(0, -1),
    Direction.RIGHT to Pair(0, 1)
)

var start: Pipe? = null
val pipes = mutableListOf<Pipe>()

val mapHeight = input.size
val mapWidth = input[0].length
val pipeMap = Array(mapHeight) { CharArray(mapWidth) { '.' } }

fun main() {
    input.forEachIndexed { row, s ->
        s.forEachIndexed { col, c ->
            if (c == 'S') {
                start = Pipe(PipeType.START, c, row, col, true)
            }
            else if (c != '.') {
                val pipe = Pipe(pipeType[c]!!, c, row, col, false)
                pipes.add(pipe)
            }
        }
    }

    // Identify pipe type of S and its connected pipes
    val startDirections = mutableListOf<Direction>()
    start!!.let { pipe ->
        val possible = pipes.filter {
            (it.row == pipe.row && it.col in pipe.adjacentCols)
                    || (it.col == pipe.col && it.row in pipe.adjacentRows)
        }.filter { adj ->
            val direction = when {
                (adj.col == pipe.adjacentCols.first) -> Direction.LEFT
                (adj.col == pipe.adjacentCols.last) -> Direction.RIGHT
                (adj.row == pipe.adjacentRows.first) -> Direction.UP
                (adj.row == pipe.adjacentRows.last) -> Direction.DOWN
                else -> throw UnsupportedOperationException()
            }

            if (adj.pipeType in directionToPipe[direction]!!) {
                startDirections.add(direction)
                return@filter true
            }

            return@filter false
        }

        start!!.pipeType = pipeToDirection.keys.first {
            val directions = pipeToDirection[it]
            val isType = directions!!.containsAll(startDirections)
            isType
        }

        if (possible.size == 2) {
            start!!.next = possible[0]
            possible[0].prev = start
        }
    }
    pipes.add(start!!)

    // PART 1
    // Find pipe loop from the starting pipe
    var steps = 1
    var current = start!!.next
    while (!followPipeToStart(current, pipes)) {
        current = current!!.next
        steps++
    }

    reconstructPipeMap()

    // PART 2
    // Count the tiles inside the loop
    var inside = Direction.RIGHT // HARD CODED BECAUSE I CAN'T AUTOMATE THIS
    var prevInside = inside
    var currPipe = start!!
    var counter = 0
    var atStart = false
    while (!atStart) {
        // Count the tiles from the previous inside direction if it has changed
        if (prevInside != inside) {
            counter += countInsideTiles(prevInside, currPipe)
            prevInside = inside
        }

        // Count the tiles from the current pipe
        counter += countInsideTiles(inside, currPipe)

        // Update direction information for next pipe
        val heading = currPipe.getHeading()
        val nextPipeType = currPipe.next!!.pipeType
        inside = transformInsideDirection(inside, heading, nextPipeType)

        currPipe = currPipe.next!!
        atStart = currPipe.isStart
    }

    println("Part 1: ${steps/2}")
    println("Part 2: $counter")

    //pipeMap.forEach { println(it) }
}

/**
 * Search for connecting pipes and set the next and prev pointers.
 * Returns false until the specified pipe is the starting pipe
 */
fun followPipeToStart(pipe: Pipe?, pipes: List<Pipe>): Boolean {
    requireNotNull(pipe)

    if (pipe.isStart) {
        return true
    }

    // Find adjacent pipes
    val adj = pipes.filter {
        (it.row == pipe.row && it.col in pipe.adjacentCols)
                || (it.col == pipe.col && it.row in pipe.adjacentRows)
    }

    // Filter out pipes that are not in the correct direction
    val possible = adj.filter { a ->
        val direction = when {
            (a.col == pipe.adjacentCols.first) -> Direction.LEFT
            (a.col == pipe.adjacentCols.last) -> Direction.RIGHT
            (a.row == pipe.adjacentRows.first) -> Direction.UP
            (a.row == pipe.adjacentRows.last) -> Direction.DOWN
            else -> null
        }

        direction != null
                && a.pipeType in directionToPipe[direction]!!
                && direction in pipeToDirection[pipe.pipeType]!!
    }

    // There is only pipe that can be next
    val nextPipe = possible.first {
        it != pipe.prev || it.isStart
    }

    pipe.next = nextPipe
    nextPipe.prev = pipe

    return false
}

/**
 * Reconstruct the pipe map based on the pipe list.
 * Excludes all the pipes that are not connected to the loop.
 */
private fun reconstructPipeMap() {
    var currPipe = start!!
    var atStart = false
    while (!atStart) {
        pipeMap[currPipe.row][currPipe.col] = currPipe.value
        //println("Pipe: ${currPipe.pipeType} Heading: ${currPipe.getHeading()}")

        currPipe = currPipe.next!!
        atStart = currPipe.isStart
    }
}

/**
 * Count the number of tiles inside the loop in the specified direction from the given pipe.
 * We'll even update the pipe map while we're at it.
 */
private fun countInsideTiles(inside: Direction, pipe: Pipe): Int {
    var counter = 0
    val scanner = scannerMap[inside]!!
    var rowNext = pipe.row + scannerMap[inside]!!.first
    var colNext = pipe.col + scannerMap[inside]!!.second

    while (rowNext in 0..<mapHeight
        && colNext in 0..<mapWidth
        && pipeMap[rowNext][colNext] !in pipeType.keys
    ) {
        if (pipeMap[rowNext][colNext] != 'X') {
            pipeMap[rowNext][colNext] = 'X'
            counter++
        }

        rowNext += scanner.first
        colNext += scanner.second
    }
    return counter
}

/**
 * Transform the direction of the inside of the loop based on the heading
 * of the current pipe and upcoming pipe type.
 */
private fun transformInsideDirection(outside: Direction, heading: Direction, nextPipeType: PipeType): Direction {
    return when {
        (heading == Direction.UP && nextPipeType == PipeType.TOP_LEFT) ->
            if (outside == Direction.LEFT)
                Direction.UP
            else Direction.DOWN

        (heading == Direction.UP && nextPipeType == PipeType.TOP_RIGHT) ->
            if (outside == Direction.RIGHT)
                Direction.UP
            else Direction.DOWN

        (heading == Direction.DOWN && nextPipeType == PipeType.BOTTOM_LEFT) ->
            if (outside == Direction.LEFT)
                Direction.DOWN
            else Direction.UP

        (heading == Direction.DOWN && nextPipeType == PipeType.BOTTOM_RIGHT) ->
            if (outside == Direction.RIGHT)
                Direction.DOWN
            else Direction.UP

        (heading == Direction.LEFT && nextPipeType == PipeType.BOTTOM_LEFT) ->
            if (outside == Direction.UP)
                Direction.RIGHT
            else Direction.LEFT

        (heading == Direction.LEFT && nextPipeType == PipeType.TOP_LEFT) ->
            if (outside == Direction.DOWN)
                Direction.RIGHT
            else Direction.LEFT

        (heading == Direction.RIGHT && nextPipeType == PipeType.BOTTOM_RIGHT) ->
            if (outside == Direction.UP)
                Direction.LEFT
            else Direction.RIGHT

        (heading == Direction.RIGHT && nextPipeType == PipeType.TOP_RIGHT) ->
            if (outside == Direction.DOWN)
                Direction.LEFT
            else Direction.RIGHT

        else -> outside
    }
}

data class Pipe(var pipeType: PipeType, val value: Char , val row: Int, val col: Int, val isStart: Boolean) {
    var next: Pipe? = null
    var prev: Pipe? = null

    val adjacentRows: IntRange = row-1..row+1
    val adjacentCols: IntRange = col-1..col+1

    fun getHeading(): Direction = when {
        (next!!.row < row) -> Direction.UP
        (next!!.row > row) -> Direction.DOWN
        (next!!.col < col) -> Direction.LEFT
        (next!!.col > col) -> Direction.RIGHT
        else -> throw UnsupportedOperationException()
    }
}

enum class PipeType {
    TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT, VERTICAL, HORIZONTAL, START
}

enum class Direction {
    UP, DOWN, LEFT, RIGHT
}

