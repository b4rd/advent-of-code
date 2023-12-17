fun main() {
    val map = readInput("Day16")
//    val starterBeam = Beam(Position__(0, 0), Direction__.RIGHT)

    var max = 0
    for (i in listOf(0, map.size - 1)) {
        val line = map[i];
        line.forEachIndexed { x, c ->
            val starterBeam = Beam(Position__(x, i), if (i == 0) Direction__.BOTTOM else Direction__.TOP)
            max = max.coerceAtLeast(findEnergizedBeams(starterBeam, map))
        }
    }

    map.forEachIndexed { y: Int, line: String ->
        for (x in listOf(0, map[0].length - 1)) {
            val starterBeam = Beam(Position__(x, y), if (x == 0) Direction__.RIGHT else Direction__.LEFT)
            max = max.coerceAtLeast(findEnergizedBeams(starterBeam, map))
        }
    }

    max.println()

}

private fun findEnergizedBeams(starterBeam: Beam, map: List<String>): Int {
    var currentBeams = listOf(starterBeam)
    val energizedCells = mutableSetOf(starterBeam)
    do {
        val newBeams = mutableListOf<Beam>()
        fun addBeam(b: Beam) {
            if (b !in energizedCells) {
                newBeams += b
            }
        }
        for (beam in currentBeams) {
            val (position, direction) = beam
            val nextPosition = position.next(direction)
            val nextCell = getCell(map, nextPosition)

            when (nextCell) {
                Empty -> newBeams += Beam(nextPosition, direction)
                is Mirror -> {
                    val newDirection = nextCell.newDirection(direction)
                    addBeam(Beam(nextPosition, newDirection))
                }

                is Splitter -> {
                    if (nextCell.isHorizontal && !direction.isHorizontal) {
                        addBeam(Beam(nextPosition, Direction__.LEFT))
                        addBeam(Beam(nextPosition, Direction__.RIGHT))
                    } else if (!nextCell.isHorizontal && direction.isHorizontal) {
                        addBeam(Beam(nextPosition, Direction__.TOP))
                        addBeam(Beam(nextPosition, Direction__.BOTTOM))
                    } else {
                        addBeam(Beam(nextPosition, direction))
                    }
                }

                null -> {}
            }
        }
        energizedCells += newBeams
        currentBeams = newBeams
//        printMap(map, newBeams)
        println()
        println()
        println()
    } while (newBeams.isNotEmpty())

    energizedCells.println()
    return energizedCells.map { it.pos }.toSet().size
}

const val ANSI_RED_BACKGROUND = "\u001B[41m"
const val ANSI_RESET = "\u001B[0m"
const val ANSI_RED = "\u001B[31m"

private fun printMap(map: List<String>, currentBeams: List<Beam>) {
    map.forEachIndexed { y, s ->
        s.forEachIndexed { x, c ->
            currentBeams.firstOrNull { it.pos.x == x && it.pos.y == y }?.let{
                print(ANSI_RED_BACKGROUND + it.direction.symbol + ANSI_RESET)
            } ?: print(c)
        }
        println()
    }
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
private enum class Direction__(val isHorizontal: Boolean, val symbol: Char) {
    TOP(false, '^'),
    RIGHT(true, '>'),
    BOTTOM(false, 'v'),
    LEFT(true,'<')
}

private data class Beam(val pos: Position__, val direction: Direction__)