fun main() {
    val testInput = readInput("Day02")

    val result = testInput.sumOf { parseGame(it).calculateMinNecessaryCubeCounts().power() }
    println(result)
}

internal fun parseGame(line: String): Game {
    val colonIndex = line.indexOf(':')

    val num = line.substring(5, colonIndex).toInt()
    val rounds = line.substring(colonIndex + 1).split("; ").map { parseRound(it) }
    return Game(num, rounds)
}

internal fun parseRound(roundText: String): CubeCounts {
    val countsMap = mutableMapOf<String, Int>()
    roundText.split(", ").forEach {
        roundRegex.findAll(it).forEach { matchResult -> countsMap[matchResult.groupValues[2]] = matchResult.groupValues[1].toInt() }
    }
    return CubeCounts(countsMap["red"]?:0, countsMap["green"]?:0, countsMap["blue"]?:0)
}

val roundRegex = """(\d+) (red|green|blue)""".toRegex()

internal data class Game(val num: Int, val rounds: List<CubeCounts>) {
    fun calculateMinNecessaryCubeCounts(): CubeCounts {
        return rounds.fold(CubeCounts(0, 0, 0)) { acc, next -> acc.coerceAtLeast(next) }
    }
}

internal data class CubeCounts(val reds: Int, val greens: Int, val blues: Int) {
    fun power(): Int {
        return reds * greens * blues
    }

    fun coerceAtLeast(other: CubeCounts): CubeCounts {
        return CubeCounts(
            reds.coerceAtLeast(other.reds),
            greens.coerceAtLeast(other.greens),
            blues.coerceAtLeast(other.blues)
        )
    }
}

