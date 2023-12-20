import java.lang.Exception

fun main() {
    val sourcesByModuleName = mutableMapOf<String, MutableList<String>>()
    val modules = readInput("Day20").map { parseModule(it, sourcesByModuleName) }
    val modulesByName = modules.associateBy { it.name }
    modules.forEach { module ->
        module.sources += sourcesByModuleName[module.name] ?: emptyList()
    }

    val broadcaster = modules.first { it is Broadcaster }
    var buttonPresses = 0
    try {
        while (true) {
            buttonPresses++
            broadcaster.sendPulse(Pulse.LOW, modulesByName)
        }
    } catch (ex: Exception) {
        ex.printStackTrace()
    }

    buttonPresses.println()
}

private fun parseModule(line: String, sourcesByModuleName: MutableMap<String, MutableList<String>>): Module {
    val arrowIndex = line.indexOf("->")
    val destinationsPart = line.substring(arrowIndex + 2)
    val destinations = destinationsPart.split(", ").map { it.trim() }
    val moduleName = if (line.startsWith("broadcaster")) "broadcaster" else line.substring(1, arrowIndex).trim()
    destinations.forEach { destName ->
        sourcesByModuleName.computeIfAbsent(destName) { _ -> mutableListOf<String>() }.add(moduleName)
    }

    if (line.startsWith("broadcaster")) {
        return Broadcaster(moduleName, mutableListOf(), destinations)
    } else if (line.startsWith("%")) {
        return FlipFlopModule(moduleName, mutableListOf(), destinations)
    } else if (line.startsWith("&")) {
        return ConjunctionModule(moduleName, mutableListOf(), destinations)
    }
    throw IllegalArgumentException(line)
}

private sealed class Module {
    abstract val name: String
    abstract val sources: MutableList<String>
    abstract val destinations: List<String>

    abstract fun receivePulse(sentPulse: SentPulse, modulesByName: Map<String, Module>): Pulse?

    fun sendPulse(pulse: Pulse, modulesByName: Map<String, Module>) {
        val sentPulse = SentPulse(name, pulse)
        val newPulsesByDest = destinations.associateWith { destName ->
            if (modulesByName.containsKey(destName)) {
                modulesByName[destName]!!.receivePulse(sentPulse, modulesByName)
            } else if (destName == "rx" && pulse == Pulse.LOW) {
                throw RuntimeException(destName)
            } else {
                null
            }
        }

        newPulsesByDest.forEach { (destName, pulse) ->
            if (pulse != null) {
                modulesByName[destName]?.sendPulse(pulse, modulesByName)
            }
        }
    }
}

private data class Broadcaster(override val name: String, override val sources: MutableList<String>, override val destinations: List<String>) : Module() {
    override fun receivePulse(sentPulse: SentPulse, modulesByName: Map<String, Module>): Pulse {
        return sentPulse.pulse
    }
}


private data class FlipFlopModule(override val name: String, override val sources: MutableList<String>, override val destinations: List<String>, var on: Boolean = false) : Module() {
    override fun receivePulse(sentPulse: SentPulse, modulesByName: Map<String, Module>): Pulse? {
        if (sentPulse.pulse == Pulse.HIGH) return null

        val wasOn = on
        on = !on
        return if (!wasOn) Pulse.HIGH else Pulse.LOW
    }
}

private data class ConjunctionModule(override val name: String, override val sources: MutableList<String>, override val destinations: List<String>) : Module() {

    private val lastPulsesBySource = mutableMapOf<String, Pulse>()

    override fun receivePulse(sentPulse: SentPulse, modulesByName: Map<String, Module>): Pulse? {
        lastPulsesBySource[sentPulse.sourceModuleName] = sentPulse.pulse

        return if (sources.all { lastPulsesBySource.computeIfAbsent(it) { _ -> Pulse.LOW } == Pulse.HIGH }) {
            Pulse.LOW
        } else {
            Pulse.HIGH
        }
    }
}

private enum class Pulse {
    LOW,
    HIGH
}

private data class SentPulse(val sourceModuleName: String, val pulse: Pulse)

