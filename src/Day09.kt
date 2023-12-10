import kotlin.math.absoluteValue

fun main() {
    val inputLines = readInput("Day09")
    val predictions = inputLines.mapIndexed { index, line ->
        val sensorValues = numbersRegex.findAll(line).map { matchResult -> matchResult.value.toLong() }.toList()
        val diffTree = getDiffTree(sensorValues)
        calculatePrediction(diffTree)
    }
    predictions.println()
    predictions.sum().println()
}

private fun calculatePrediction(diffTree: List<List<Long>>): Long {
    var result = 0L
    for (index in diffTree.size - 1 downTo 0) {
        result = diffTree[index].first() - result
    }
    return result
}

private fun getDiffTree(values: List<Long>): List<List<Long>> {
    val diffTree = mutableListOf<List<Long>>()
    diffTree += values

    var nextLevelDiffs = values
    while (!nextLevelDiffs.all { it == 0L }) {
        nextLevelDiffs = getNextLevelDiffs(nextLevelDiffs)
        diffTree += nextLevelDiffs
    }
    return diffTree
}

private fun getNextLevelDiffs(currentValues: List<Long>): List<Long> {
    val result = mutableListOf<Long>()
    for (index in 0 .. currentValues.size - 2) {
        result += currentValues[index + 1] - currentValues[index]
    }
    return result
}

private val numbersRegex = """-?\d+""".toRegex()