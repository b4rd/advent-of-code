fun main() {
    val inputLines = readInput("Day08")
    val directionsInput = inputLines[0].trim().map { Direction.fromString(it) }
    val mapEntries = parseMap(inputLines.subList(2, inputLines.size))
    val mapEntriesByCurrentNode = mapEntries.associateBy { it.currentNode }

    val startMapEntries = mapEntries.filter { it.currentNode[2] == 'A' }.toMutableList()
    val steps = startMapEntries.map { mapEntry ->
        var steps = 0L
        var currentEntry = mapEntry
        for (nextDirection in directionsSequence(directionsInput)) {
            steps++

            val nextNode = currentEntry.nextNode(nextDirection)
            currentEntry = mapEntriesByCurrentNode[nextNode]!!

            if (currentEntry.currentNode[2] == 'Z') {
                break
            }
        }
        steps
    }

    val result = steps.fold(1L) { acc, l -> lcm(acc, l) }
    result.println()
}

private fun parseMap(lines: List<String>): List<MapEntry> {
    return lines.map { line ->
        val matchResultList = nodeRegex.findAll(line).toList()
        MapEntry(matchResultList[0].value, matchResultList[1].value to matchResultList[2].value)
    }
}

private fun directionsSequence(directionsInput: List<Direction>): Sequence<Direction> {
    var nextIndex = 0
    return generateSequence {
        val next = directionsInput[nextIndex]

        nextIndex++
        if (nextIndex == directionsInput.size) {
            nextIndex = 0
        }
        next
    }
}

private data class MapEntry(val currentNode: String, val nextNodes: Pair<String, String>) {
    fun nextNode(direction: Direction): String {
        return if (direction == Direction.L) {
            nextNodes.first
        } else {
            nextNodes.second
        }
    }
}


private enum class Direction {
    L,
    R;

    companion object {
        fun fromString(c: Char): Direction {
            return Direction.entries.first { it.name == c.toString() }
        }
    }
}

private val nodeRegex = """[0-9A-Z]{3}""".toRegex()


