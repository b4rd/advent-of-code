import java.lang.IllegalStateException

fun main() {
    val hands = readInput("Day07").map { parseHand(it) }
    val sortedHands = hands.sorted()

    var result = 0L
    for (i in sortedHands.indices) {
        val rank = i + 1
        result += (rank * sortedHands[i].bid)
    }
    result.println()
}

private fun parseHand(line: String): Hand {
    val bid = line.substring(6).trim().toLong()
    val labels = line.substring(0, 5).map { Label.fromSymbol(it) }
    return Hand(labels, bid)
}

private data class Hand(val labels: List<Label>, val bid: Long) : Comparable<Hand> {

    val type: Type

    init {
        type = calculateType()
    }

    fun calculateType(): Type {
        val frequenciesByLabel = labels.filter { it != Label.J }.groupingBy { it }.eachCount()
        val frequencyCounts = frequenciesByLabel.values.groupingBy { it }.eachCount()
        val jokerCount = labels.count { it == Label.J }
        if (frequencyCounts.containsKey(5)) {
            return Type.FIVE_OF_A_KIND.withJokers(jokerCount)
        }
        if (frequencyCounts.containsKey(4)) {
            return Type.FOUR_OF_A_KIND.withJokers(jokerCount)
        }
        if (frequencyCounts.containsKey(3)) {
            if (frequencyCounts.containsKey(2)) {
                return Type.FULL_HOUSE.withJokers(jokerCount)
            }
            return Type.THREE_OF_A_KIND.withJokers(jokerCount)
        }

        val pairCount = frequencyCounts.getOrDefault(2, 0)
        if (pairCount == 2) {
            return Type.TWO_PAIR.withJokers(jokerCount)
        }
        if (pairCount == 1) {
            return Type.ONE_PAIR.withJokers(jokerCount)
        }
        if (jokerCount > 0) {
            return when (jokerCount) {
                1 -> Type.ONE_PAIR
                2 -> Type.THREE_OF_A_KIND
                3 -> Type.FOUR_OF_A_KIND
                4 -> Type.FIVE_OF_A_KIND
                5 -> Type.FIVE_OF_A_KIND
                else -> throw IllegalStateException()
            }
        }
        return Type.HIGH_CARD.withJokers(jokerCount)
    }

    override fun compareTo(other: Hand): Int {
        val typeDiff = type.ordinal - other.type.ordinal
        if (typeDiff != 0) {
            return typeDiff
        }
        val numCards = labels.size
        for (i in 0 until numCards) {
            val labelDiff = labels[i].ordinal - other.labels[i].ordinal
            if (labelDiff != 0) {
                return labelDiff
            }
        }
        return 0
    }
}

private enum class Type {
    HIGH_CARD,
    ONE_PAIR,
    TWO_PAIR,
    THREE_OF_A_KIND,
    FULL_HOUSE,
    FOUR_OF_A_KIND,
    FIVE_OF_A_KIND;

    fun withJokers(jokerCount: Int): Type {
        return when (this) {
            HIGH_CARD -> when (jokerCount) {
                0 -> HIGH_CARD
                else -> throw IllegalStateException()
            }
            ONE_PAIR -> when (jokerCount) {
                0 -> ONE_PAIR
                1 -> THREE_OF_A_KIND
                2 -> FOUR_OF_A_KIND
                3 -> FIVE_OF_A_KIND
                else -> throw IllegalStateException()
            }
            TWO_PAIR -> when (jokerCount) {
                0 -> TWO_PAIR
                1 -> FULL_HOUSE
                else -> throw IllegalStateException()
            }
            THREE_OF_A_KIND -> when (jokerCount) {
                0 -> THREE_OF_A_KIND
                1 -> FOUR_OF_A_KIND
                2 -> FIVE_OF_A_KIND
                else -> throw IllegalStateException()
            }
            FULL_HOUSE -> when (jokerCount) {
                0 -> FULL_HOUSE
                else -> throw IllegalStateException()
            }
            FOUR_OF_A_KIND -> when (jokerCount) {
                0 -> FOUR_OF_A_KIND
                1 -> FIVE_OF_A_KIND
                else -> throw IllegalStateException()
            }
            FIVE_OF_A_KIND -> when (jokerCount) {
                0 -> FIVE_OF_A_KIND
                else -> throw IllegalStateException()
            }
        }
    }
}

private enum class Label(val symbol: Char) {
    J('J'),
    TWO('2'),
    THREE('3'),
    FOUR('4'),
    FIVE('5'),
    SIX('6'),
    SEVEN('7'),
    EIGHT('8'),
    NINE('9'),
    T('T'),
    Q('Q'),
    K('K'),
    A('A');

    companion object {
        fun fromSymbol(symbol: Char): Label {
            return entries.first { it.symbol == symbol }
        }
    }
}