fun main() {
    val steps = readInput("Day15").joinToString(separator = "").split(',')
            .map { parseStep(it) }
    val boxesByNumber = mutableMapOf<Int, Box>()
    fun findBox(index: Int): Box {
        return boxesByNumber.computeIfAbsent(index) { Box() }
    }

    for (step in steps) {
        when (step) {
            is AssignStep -> {
                val box = findBox(step.boxNumber())
                val newLens = Lens(step.label, step.lensFocalLength)
                box.addOrReplaceLens(newLens)
            }
            is RemoveStep -> {
                findBox(step.boxNumber()).removeLensWithLabel(step.label)
            }
        }
    }
    val totalFocusingPower = boxesByNumber.map { (boxIndex, box) ->
        box.lenses.mapIndexed { index, lens ->
            (boxIndex + 1) * (index + 1) * lens.lensFocalLength
        }.sum()
    }.sum()
    totalFocusingPower.println()
}

private fun parseStep(it: String): Step {
    val equalSignIndex = it.indexOf('=')
    if (equalSignIndex > 0) {
        return AssignStep(it.substring(0, equalSignIndex), it.substring(equalSignIndex + 1).toInt())
    }
    return RemoveStep(it.substring(0, it.length - 1))
}

private fun calculateHash(s: String): Int {
    return s.fold(0) { acc, c ->
        ((acc + c.code) * 17) % 256
    }
}

private sealed interface Step {
    val label: String

    fun boxNumber(): Int {
        return calculateHash(label)
    }
}
private data class RemoveStep(override val label: String) : Step
private data class AssignStep(override val label: String, val lensFocalLength: Int) : Step
private data class Lens(val label: String, val lensFocalLength: Int)
private class Box {
    private val _lenses = mutableListOf<Lens>()
    val lenses: List<Lens> = _lenses

    fun removeLensWithLabel(label: String) {
        _lenses.removeIf { it.label == label }
    }

    fun addOrReplaceLens(newLens: Lens) {
        val indexOfLens = lenses.indexOfFirst { it.label == newLens.label }
        if (indexOfLens >= 0) {
            _lenses[indexOfLens] = newLens
        } else {
            _lenses += newLens
        }
    }
}
