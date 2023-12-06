fun main() {
    val input = readInput("Day05")
    val seedIdNumbers = numberRegex.findAll(input[0]).map { it.value.toLong() }.toList()
    val seedIdRanges = mutableListOf<LongRange>()
    for (i in seedIdNumbers.indices step 2) {
        seedIdRanges += LongRange(seedIdNumbers[i], seedIdNumbers[i] + seedIdNumbers[i + 1] - 1)
    }
    val mappings = parseMappings(input.subList(1, input.size))

    var minLocation = Long.MAX_VALUE
    for (seedIdRange in seedIdRanges) {
        for (seedId in seedIdRange) {
            val location = mappings.fold(seedId) { acc, entityIdMappings -> entityIdMappings.getDestinationId(acc) }
            minLocation = minLocation.coerceAtMost(location)
        }
    }
    minLocation.println()
}

private val numberRegex = """\d+""".toRegex()

private fun parseMappings(lines: List<String>): List<EntityIdMappings> {
    val entityIdMappingsBuilder = EntityIdMappingsBuilder()
    for (line in lines) {
        if (line.isEmpty() || !line[0].isDigit()) {
            entityIdMappingsBuilder.finishMappingsBlock()
            continue
        }
        val nums = numberRegex.findAll(line).toList()
        entityIdMappingsBuilder.addRule(EntityIdMappingRule(nums[0].value.toLong(), nums[1].value.toLong(), nums[2].value.toLong()))
    }
    entityIdMappingsBuilder.finishMappingsBlock()
    return entityIdMappingsBuilder.mappings
}

private class EntityIdMappingsBuilder {
    val mappings = mutableListOf<EntityIdMappings>()
    var currentRules = mutableListOf<EntityIdMappingRule>()

    fun addRule(rule: EntityIdMappingRule) {
        currentRules += rule
    }

    fun finishMappingsBlock() {
        if (currentRules.isNotEmpty()) {
            mappings += EntityIdMappings(currentRules)
            currentRules = mutableListOf()
        }
    }
}

private data class EntityIdMappings(val rules: List<EntityIdMappingRule>) {
    fun getDestinationId(sourceId: Long): Long {
        for (rule in rules) {
            if (sourceId >= rule.sourceIdStart && sourceId < (rule.sourceIdStart + rule.range)) {
                return rule.destIdStart + (sourceId - rule.sourceIdStart)
            }
        }
        return sourceId
    }
}

private data class EntityIdMappingRule(val destIdStart: Long, val sourceIdStart: Long, val range: Long)


