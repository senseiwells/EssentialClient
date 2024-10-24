package me.senseiwells.essential_client.features.carpet_client.database

import com.google.common.collect.TreeMultimap
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap
import java.util.*

class CarpetRuleDataRegistry private constructor(
    private val map: SortedMap<String, CarpetRuleData>
) {
    fun get(name: String): CarpetRuleData? {
        return this.map[name]
    }

    fun values(): SequencedCollection<CarpetRuleData> {
        return this.map.sequencedValues()
    }

    class Builder {
        private val map = TreeMultimap.create<String, CarpetRuleData>(
            String::compareTo,
            CarpetRuleData::compareMinecraftVersions
        )

        fun add(rule: CarpetRuleData) {
            this.map.put(rule.name, rule)
        }

        fun build(version: String? = null): CarpetRuleDataRegistry {
            val map = Object2ObjectLinkedOpenHashMap<String, CarpetRuleData>()
            for ((key, rules) in this.map.asMap()) {
                var rule: CarpetRuleData? = null
                if (version != null) {
                    rule = rules.find { it.minecraftVersions.contains(version) }
                }
                map[key] = rule ?: rules.last()
            }
            return CarpetRuleDataRegistry(map)
        }
    }
}