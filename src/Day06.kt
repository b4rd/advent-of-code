import java.math.BigInteger

fun main() {
    val inputLines = readInput("Day06")
    val currentRecord = Race(parseRaceTime(inputLines[0]), parseRecordDistance(inputLines[1]))

    val result = getWinnerRaceStrategiesCount(currentRecord.time, currentRecord.recordDistance)
    result.println()
}

private fun parseRaceTime(line: String): BigInteger {
    return numbersRegex.findAll(line).map { it.value }.joinToString(separator = "").toBigInteger()
}

private fun parseRecordDistance(line: String): BigInteger {
    return numbersRegex.findAll(line).map { it.value }.joinToString(separator = "").toBigInteger()
}

private val numbersRegex = """\d+""".toRegex()

private fun getWinnerRaceStrategiesCount(time: BigInteger, recordDistance: BigInteger): Int {
    var result = 0
    var i = BigInteger.ZERO
    while (i <= time) {
        val distanceTravelled = i * (time - i)
        if (distanceTravelled > recordDistance) {
            result++
        }
        i++
    }
    return result
}


private data class Race(val time: BigInteger, val recordDistance: BigInteger)
