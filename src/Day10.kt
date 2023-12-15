fun main() {
    val map = readInput("Day10").map { line ->
        line.map { Pipe.fromSymbol(it) }
    }
    val mapWidth = map[0].size

    var startCoordinates = Position(0, 0, Pipe.START)
    for ((index, pipesInRow) in map.withIndex()) {
        val startIndex = pipesInRow.indexOf(Pipe.START)
        if (startIndex >= 0) {
            startCoordinates = Position(startIndex, index, Pipe.START)
            break
        }
    }

    var currentRoutes = listOf(listOf(startCoordinates))
    var mainLoop = listOf<Position>()
    label@ while (true) {
        val nextRoutes = mutableListOf<List<Position>>()
        for (route in currentRoutes) {
            val prevCoordinates = if (route.size >= 2) route[route.size - 2] else null
            val currentCoordinates = route.last()
            val nextCoordinates = findNextPositions(map, currentCoordinates, prevCoordinates)
            val intersectingRoute = currentRoutes.find { it.any { coordinates -> nextCoordinates.any { nc -> nc == coordinates} }}
            if (intersectingRoute != null) {
                mainLoop = (route + intersectingRoute.reversed()).distinct()
                break@label
            }
            for (nextCoordinate in nextCoordinates) {
                val newRoute = mutableListOf<Position>()
                newRoute += route
                newRoute += nextCoordinate
                nextRoutes += newRoute
            }
        }
        currentRoutes = nextRoutes
    }

    val result = mutableListOf<Position>()
    map.forEachIndexed { y, pipes ->
        pipes.forEachIndexed { x, pipe ->
            val position = Position(x, y, pipe)
            if (position !in mainLoop && countIntersections(position, mainLoop, mapWidth) % 2 == 1) {
                result += position
            }
        }
    }
    result.println()
    result.size.println()
}

private val pipesToIgnore = setOf(Pipe.HORIZONTAL, Pipe.TOP_LEFT, Pipe.TOP_RIGHT)

private fun countIntersections(position: Position, loop: List<Position>, mapWidth: Int): Int {
    var count = 0
    for (x in position.x + 1..< mapWidth) {
        if (loop.any { it.x == x && it.y == position.y && it.pipe !in pipesToIgnore}) {
            count++
        }
    }
    return count
}

private fun findNextPositions(map: List<List<Pipe>>, currentPosition: Position, prevPosition: Position?): List<Position> {
    val nextCoordinates = mutableListOf<Position>()
    for (entry in currentPosition.pipe.possibleDirections) {
        var nextCoordinate: Position? = null
        when(entry) {
            Direction_.LEFT -> {
                if (currentPosition.x >= 1) {
                    val pipe = map[currentPosition.y][currentPosition.x - 1]
                    nextCoordinate = Position(currentPosition.x - 1, currentPosition.y, pipe)
                }
            }
            Direction_.RIGHT -> {
                if (currentPosition.x <= map[0].size - 2) {
                    nextCoordinate = Position(currentPosition.x + 1, currentPosition.y, map[currentPosition.y][currentPosition.x + 1])
                }
            }
            Direction_.TOP -> {
                if (currentPosition.y >= 1) {
                    nextCoordinate = Position(currentPosition.x , currentPosition.y - 1, map[currentPosition.y - 1][currentPosition.x])
                }
            }
            Direction_.BOTTOM -> {
                if (currentPosition.y <= map.size - 2) {
                    nextCoordinate = Position(currentPosition.x , currentPosition.y + 1, map[currentPosition.y + 1][currentPosition.x])
                }
            }
        }
        if (nextCoordinate != null && nextCoordinate.pipe.possibleDirections.any { it.opposite() == entry }) {
            nextCoordinates += nextCoordinate
        }
    }
    return nextCoordinates.filter { it != prevPosition }
}

private data class Position(val x: Int, val y: Int, val pipe: Pipe)

private enum class Direction_ {
    LEFT,
    RIGHT,
    TOP,
    BOTTOM;

    fun opposite(): Direction_ {
        return when (this) {
            LEFT -> RIGHT
            RIGHT -> LEFT
            TOP -> BOTTOM
            BOTTOM -> TOP
        }
    }
}

private enum class Pipe(val symbol: Char, val possibleDirections: Set<Direction_>) {
    START('S', Direction_.entries.toSet()),
    VERTICAL('|', setOf(Direction_.TOP, Direction_.BOTTOM)),
    HORIZONTAL('-', setOf(Direction_.LEFT, Direction_.RIGHT)),
    TOP_RIGHT('L', setOf(Direction_.TOP, Direction_.RIGHT)),
    TOP_LEFT('J', setOf(Direction_.TOP, Direction_.LEFT)),
    BOTTOM_LEFT('7', setOf(Direction_.BOTTOM, Direction_.LEFT)),
    BOTTOM_RIGHT('F', setOf(Direction_.BOTTOM, Direction_.RIGHT)),
    GROUND('.', emptySet());

    companion object {
        fun fromSymbol(symbol: Char): Pipe {
            return Pipe.entries.first { it.symbol == symbol }
        }
    }
}