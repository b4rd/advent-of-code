fun main() {
    assert(getCalibrationValue("threeight") == 38)

    val testInput = readInput("Day01")
    val result = testInput.sumOf { line -> getCalibrationValue(line) }

    println(result)
}

fun getCalibrationValue(line: String): Int {
    val matches = numberRegex.findAll(line)
    // The match is empty because all matchable characters are inside the lookahead assertion.
    // Get the matches from the capturing group.
    // https://mtsknn.fi/blog/how-to-do-overlapping-matches-with-regular-expressions/
    val firstDigit = matches.first().groupValues[1].toDigit()
    val lastDigit = matches.last().groupValues[1].toDigit()
    return firstDigit * 10 + lastDigit
}

fun String.toDigit(): Int {
    if (length == 1) {
        return toInt()
    }
    return requireNotNull(spelledDigitsToNumbers[this])
}

val spelledDigitsToNumbers = mapOf(
        "one" to 1,
        "two" to 2,
        "three" to 3,
        "four" to 4,
        "five" to 5,
        "six" to 6,
        "seven" to 7,
        "eight" to 8,
        "nine" to 9
)

private val numberRegex = """(?=(\d|one|two|three|four|five|six|seven|eight|nine))""".toRegex()
