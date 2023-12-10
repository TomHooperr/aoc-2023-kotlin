package day10

import java.io.File

val pipeType = mapOf(
    '|' to PipeType.VERTICAL,
    '-' to PipeType.HORIZONTAL,
    'L' to PipeType.BOTTOM_LEFT,
    'J' to PipeType.BOTTOM_RIGHT,
    '7' to PipeType.TOP_RIGHT,
    'F' to PipeType.TOP_LEFT,
    'S' to PipeType.START
)

val pipeToDirection: Map<PipeType, List<Direction>> = mapOf(
    PipeType.TOP_LEFT to listOf(Direction.DOWN, Direction.RIGHT),
    PipeType.TOP_RIGHT to listOf(Direction.DOWN, Direction.LEFT),
    PipeType.BOTTOM_LEFT to listOf(Direction.UP, Direction.RIGHT),
    PipeType.BOTTOM_RIGHT to listOf(Direction.UP, Direction.LEFT),
    PipeType.VERTICAL to listOf(Direction.UP, Direction.DOWN),
    PipeType.HORIZONTAL to listOf(Direction.LEFT, Direction.RIGHT)
)

val directionToPipe = mapOf(
    Direction.UP to listOf(PipeType.TOP_RIGHT, PipeType.TOP_LEFT, PipeType.VERTICAL),
    Direction.DOWN to listOf(PipeType.BOTTOM_RIGHT, PipeType.BOTTOM_LEFT, PipeType.VERTICAL),
    Direction.LEFT to listOf(PipeType.TOP_LEFT, PipeType.BOTTOM_LEFT, PipeType.HORIZONTAL),
    Direction.RIGHT to listOf(PipeType.TOP_RIGHT, PipeType.BOTTOM_RIGHT, PipeType.HORIZONTAL)
)

fun main() {
    var start: Pipe? = null
    val pipes = mutableListOf<Pipe>()
    val input = File("src/main/day10/input.txt").readLines()

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

    var steps = 1
    var current = start!!.next
    while (!followPipeToStart(current, pipes)) {
        current = current!!.next
        //println(current!!.pipeType)
        steps++
    }
    //print(steps/2)

    val blankPipeMap = Array(input.size) { CharArray(input[0].length) { '.' } }
    var forward = start!!
    blankPipeMap[forward.row][forward.col] = forward.value
    while (!forward.next!!.isStart) {
        blankPipeMap[forward.row][forward.col] = forward.value
        forward = forward.next!!
    }

    blankPipeMap.forEach { println(it) }
}

fun followPipeToStart(pipe: Pipe?, pipes: List<Pipe>): Boolean {
    requireNotNull(pipe)

    if (pipe.isStart) {
        return true
    }

    val adj = pipes.filter {
        (it.row == pipe.row && it.col in pipe.adjacentCols)
                || (it.col == pipe.col && it.row in pipe.adjacentRows)
    }

    val possible = adj.filter { a ->
        val direction = when {
            (a.col == pipe.adjacentCols.first) -> Direction.LEFT
            (a.col == pipe.adjacentCols.last) -> Direction.RIGHT
            (a.row == pipe.adjacentRows.first) -> Direction.UP
            (a.row == pipe.adjacentRows.last) -> Direction.DOWN
            else -> null
        }

        direction != null && a.pipeType in directionToPipe[direction]!!&& direction in pipeToDirection[pipe.pipeType]!!
    }

    val nextPipe = possible.first {
        it != pipe.prev || it.isStart
    }

    pipe.next = nextPipe
    nextPipe.prev = pipe

    return false
}

class Pipe(var pipeType: PipeType, val value: Char , val row: Int, val col: Int, val isStart: Boolean) {
    var next: Pipe? = null
    var prev: Pipe? = null

    val adjacentRows: IntRange = row-1..row+1
    val adjacentCols: IntRange = col-1..col+1
}

enum class PipeType {
    TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT, VERTICAL, HORIZONTAL, START
}

enum class Direction {
    UP, DOWN, LEFT, RIGHT
}

