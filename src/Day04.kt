fun main() {
    val cards = readInput("Day04")
            .map { parseCard(it) }

    val countsByCardNum = cards.associate { it.num to 1 }.toMutableMap()
    cards.forEachIndexed{ index, card ->
        repeat(countsByCardNum[index + 1]!!) {
            for (i in index + 1 until index + 1 + card.winningPlayedNumbers.size) {
                countsByCardNum[i + 1] = countsByCardNum[i + 1]!! + 1
            }
        }
    }
    val result = countsByCardNum.values.sum()
    result.println()
}

private fun parseCard(line: String): Card {
    val indexOfColon = line.indexOf(':')
    val indexOfPipe = line.indexOf('|')
    val cardNum = line.substring(5, indexOfColon)
    val winningNumberText = line.substring(indexOfColon + 2, indexOfPipe - 1)
    val playedNumberText = line.substring(indexOfPipe + 2)
    return Card(
            numberRegex.findAll(cardNum).first().value.toInt(),
            numberRegex.findAll(winningNumberText).map { it.value.toInt() }.toList(),
            numberRegex.findAll(playedNumberText).map { it.value.toInt() }.toList(),
    )
}

private data class Card(val num: Int, val winningNumbers: List<Int>, val playedNumbers: List<Int>) {

    val winningPlayedNumbers = playedNumbers.intersect(winningNumbers.toSet())

}

private val numberRegex = """\d+""".toRegex()
