fun main() {
    val map = readInput("Day16_test")
    val starterBeam = Beam(Position__(0, 0), Direction__.RIGHT)

    var currentBeams = listOf(starterBeam)

    val energizedCells = mutableSetOf(starterBeam.pos)
    do {
        val newBeams = mutableListOf<Beam>()
        for (beam in currentBeams) {
            val (position, direction) = beam
            val nextPosition = position.next(direction)
            val nextCell = getCell(map, nextPosition)

            when (nextCell) {
                Empty -> newBeams += Beam(nextPosition, direction)
                is Mirror -> {
                    val newDirection = nextCell.newDirection(direction)
                    newBeams += Beam(nextPosition, newDirection)
                }
                is Splitter -> {
                    if (nextCell.isHorizontal && !direction.isHorizontal) {
                        newBeams += Beam(nextPosition, Direction__.LEFT)
                        newBeams += Beam(nextPosition, Direction__.RIGHT)
                    } else if (!nextCell.isHorizontal && direction.isHorizontal) {
                        newBeams += Beam(nextPosition, Direction__.TOP)
                        newBeams += Beam(nextPosition, Direction__.BOTTOM)
                    } else {
                        newBeams += Beam(nextPosition, direction)
                    }
                }
                null -> {}
            }
        }

        energizedCells += newBeams.map { it.pos }
        currentBeams = newBeams
    } while (newBeams.isNotEmpty())

    energizedCells.println()
    energizedCells.size.println()
}

private fun getCell(map: List<String>, pos: Position__): Cell? {
    if (pos.x < 0 || pos.x >= map[0].length || pos.y < 0 || pos.y >= map.size) {
        return null
    }
    return parseCell(map[pos.y][pos.x])
}

private fun parseCell(c: Char): Cell {
    return when (c) {
        '\\' -> Mirror(false)
        '/' -> Mirror(true)
        '-' -> Splitter(true)
        '|' -> Splitter(false)
        else -> Empty
    }
}

private sealed interface Cell
private data object Empty : Cell
private data class Splitter(val isHorizontal: Boolean) : Cell
private data class Mirror(val forward: Boolean) : Cell {
    fun newDirection(direction: Direction__): Direction__ {
        return if (forward) {
            when (direction) {
                Direction__.TOP -> Direction__.RIGHT
                Direction__.RIGHT -> Direction__.TOP
                Direction__.BOTTOM -> Direction__.LEFT
                Direction__.LEFT -> Direction__.BOTTOM
            }
        } else {
            when (direction) {
                Direction__.TOP -> Direction__.LEFT
                Direction__.RIGHT -> Direction__.BOTTOM
                Direction__.BOTTOM -> Direction__.RIGHT
                Direction__.LEFT -> Direction__.TOP
            }
        }
    }
}

private data class Position__(val x: Int, val y: Int){
    fun next(dir: Direction__): Position__ {
        return when (dir) {
            Direction__.TOP -> Position__(x, y - 1)
            Direction__.RIGHT -> Position__(x + 1, y)
            Direction__.BOTTOM -> Position__(x, y + 1)
            Direction__.LEFT -> Position__(x - 1, y)
        }
    }
}
private enum class Direction__(val isHorizontal: Boolean) {
    TOP(false),
    RIGHT(true),
    BOTTOM(false),
    LEFT(true)
}

private data class Beam(val pos: Position__, val direction: Direction__)