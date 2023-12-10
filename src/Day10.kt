fun main() {
    val map = readInput("Day10").map { line ->
        line.map { Pipe.fromSymbol(it) }
    }

    var startCoordinates = Coordinates(0, 0, Pipe.START)
    for ((index, pipesInRow) in map.withIndex()) {
        val startIndex = pipesInRow.indexOf(Pipe.START)
        if (startIndex >= 0) {
            startCoordinates = Coordinates(startIndex, index, Pipe.START)
            break
        }
    }

    var currentRoutes = listOf(listOf(startCoordinates))
    label@ while (true) {
        val nextRoutes = mutableListOf<List<Coordinates>>()
        for (route in currentRoutes) {
            val prevCoordinates = if (route.size >= 2) route[route.size - 2] else null
            val currentCoordinates = route.last()
            val nextCoordinates = findNextCoordinates(map, currentCoordinates, prevCoordinates)
            if (nextCoordinates.any { nextCoordinates -> currentRoutes.any { coords -> coords.any { nextCoordinates == it } } }) {
                break@label
            }
            for (nextCoordinate in nextCoordinates) {
                val newRoute = mutableListOf<Coordinates>()
                newRoute += route
                newRoute += nextCoordinate
                nextRoutes += newRoute
            }
        }
        currentRoutes = nextRoutes
    }
    currentRoutes.forEach {
        it.println()
    }
    (currentRoutes.maxBy { it.size }.size - 1).println()
}

private fun findNextCoordinates(map: List<List<Pipe>>, currentCoordinates: Coordinates, prevCoordinates: Coordinates?): List<Coordinates> {
    val nextCoordinates = mutableListOf<Coordinates>()
    for (entry in currentCoordinates.pipe.possibleDirections) {
        var nextCoordinate: Coordinates? = null
        when(entry) {
            Direction_.LEFT -> {
                if (currentCoordinates.x >= 1) {
                    val pipe = map[currentCoordinates.y][currentCoordinates.x - 1]
                    nextCoordinate = Coordinates(currentCoordinates.x - 1, currentCoordinates.y, pipe)
                }
            }
            Direction_.RIGHT -> {
                if (currentCoordinates.x <= map[0].size - 2) {
                    nextCoordinate = Coordinates(currentCoordinates.x + 1, currentCoordinates.y, map[currentCoordinates.y][currentCoordinates.x + 1])
                }
            }
            Direction_.TOP -> {
                if (currentCoordinates.y >= 1) {
                    nextCoordinate = Coordinates(currentCoordinates.x , currentCoordinates.y - 1, map[currentCoordinates.y - 1][currentCoordinates.x])
                }
            }
            Direction_.BOTTOM -> {
                if (currentCoordinates.y <= map.size - 2) {
                    nextCoordinate = Coordinates(currentCoordinates.x , currentCoordinates.y + 1, map[currentCoordinates.y + 1][currentCoordinates.x])
                }
            }
        }
        if (nextCoordinate != null && nextCoordinate.pipe.possibleDirections.any { it.opposite() == entry }) {
            nextCoordinates += nextCoordinate
        }
    }
    return nextCoordinates.filter { it != prevCoordinates }
}

private data class Coordinates(val x: Int, val y: Int, val pipe: Pipe)

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