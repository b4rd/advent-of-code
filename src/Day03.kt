fun main() {
    val positionedNumbers = mutableListOf<PositionedNumber>()
    val gears = mutableListOf<PositionedGear>()

    readInput("Day03").forEachIndexed { index, line ->
        numberRegex_.findAll(line).forEach { matchResult ->
            positionedNumbers += PositionedNumber(index, matchResult.range, matchResult.value.toInt())
        }
        gearRegex_.findAll(line).forEach { matchResult ->
            gears += PositionedGear(index, matchResult.range)
        }
    }

    val result = gears
            .map { getAdjacentNumbers(it, positionedNumbers) }
            .filter { adjacentNumbers -> adjacentNumbers.size == 2 }
            .sumOf { adjacentNumbers -> adjacentNumbers[0].number * adjacentNumbers[1].number }

    println(result)
}

fun getAdjacentNumbers(gear: PositionedGear, numbers: List<PositionedNumber>): List<PositionedNumber> {
    val candidatesByRow = numbers.filter { it.row in gear.row - 1 .. gear.row + 1 }
    return candidatesByRow.filter { positionedNumber -> positionedNumber.range.expand(1).intersect(gear.range).isNotEmpty() }
}

internal val numberRegex_ = """\d+""".toRegex()
internal val gearRegex_ = """\*""".toRegex()

internal interface Positioned {
    val row: Int
    val range: IntRange
}

data class PositionedNumber(override val row: Int, override val range: IntRange, val number: Int) : Positioned

fun IntRange.expand(i: Int): IntRange {
    return IntRange(start - i, endInclusive + i)
}

data class PositionedGear(override val row: Int, override val range: IntRange) : Positioned