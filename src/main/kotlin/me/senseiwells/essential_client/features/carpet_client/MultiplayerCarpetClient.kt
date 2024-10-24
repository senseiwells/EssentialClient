package me.senseiwells.essential_client.features.carpet_client

import dev.isxander.yacl3.api.ConfigCategory
import dev.isxander.yacl3.api.utils.OptionUtils
import dev.isxander.yacl3.gui.YACLScreen
import it.unimi.dsi.fastutil.objects.Object2ObjectAVLTreeMap
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap
import me.senseiwells.essential_client.features.carpet_client.database.CarpetRuleData
import me.senseiwells.essential_client.features.carpet_client.database.CarpetRuleDataRegistry
import me.senseiwells.essential_client.features.carpet_client.database.CarpetRulesDatabase
import me.senseiwells.essential_client.features.carpet_client.yacl.CarpetOption
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientPacketListener
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component

class MultiplayerCarpetClient(
    private val connection: ClientPacketListener
): CarpetClient() {
    private lateinit var registries: Map<String, CarpetRuleDataRegistry>

    override fun isValidRule(name: String, manager: String): Boolean {
        for (registry in this.registries.values) {
            val rule = registry.get(name) ?: continue
            if (rule.settingManagers.contains(manager)) {
                return true
            }
        }
        return false
    }

    override fun synchronizeRuleData(minecraft: Minecraft, tag: CompoundTag) {
        if (!this::registries.isInitialized) {
            this.initializeRuleData(tag)
            return
        }

        val screen = minecraft.screen
        this.forEachRule(tag) { ruleName, ruleValue, manager ->
            for (registry in this.registries.values) {
                val rule = registry.get(ruleName) ?: continue
                if (rule.settingManagers.contains(manager)) {
                    rule.value = rule.type.mapFromString(ruleValue)
                    break
                }
            }
            if (screen is YACLScreen) {
                OptionUtils.forEachOptions(screen.config) { option ->
                    if (option is CarpetOption && option.name().string == ruleName) {
                        option.setValueFromString(ruleValue)
                    }
                }
            }
        }
    }

    override fun createCategories(minecraft: Minecraft, consumer: (ConfigCategory) -> Unit) {
        val initialized = this::registries.isInitialized
        val registries = if (initialized) this.registries else CarpetRulesDatabase.registries

        for ((mod, rules) in registries) {
            val category = ConfigCategory.createBuilder()
            category.name(Component.literal(mod))

            for (rule in rules.values()) {
                val builder = this.bindOption(this.createUnboundOption(rule), rule) ?: continue
                builder.applier { option ->
                    this.connection.sendCommand(
                        "${rule.defaultManager} ${rule.name} ${option.getPendingValueAsString()}"
                    )
                }
                val connection = minecraft.connection
                if (connection != null && initialized) {
                    val hasCommand = connection.commands.root.getChild(rule.defaultManager) != null
                    builder.available(hasCommand)
                } else {
                    builder.available(false)
                }
                category.option(builder.build())
            }

            consumer.invoke(category.build())
        }
    }

    private fun initializeRuleData(tag: CompoundTag) {
        val map = Object2ObjectAVLTreeMap<String, CarpetRuleDataRegistry.Builder>(String::compareTo)
        this.forEachRule(tag) { ruleName, ruleValue, manager ->
            val original = CarpetRulesDatabase.getDataForRule(ruleName, manager)
            val rule = original?.copy(value = original.type.mapFromString(ruleValue))
                ?: CarpetRuleData.createDefault(ruleName, ruleValue, manager)
            map.getOrPut(rule.modName, CarpetRuleDataRegistry::Builder).add(rule)
        }

        val registries = Object2ObjectLinkedOpenHashMap<String, CarpetRuleDataRegistry>()
        for ((mod, builder) in map) {
            registries[mod] = builder.build()
        }
        this.registries = registries
    }

    private inline fun forEachRule(tag: CompoundTag, consumer: (String, String, String) -> Unit) {
        for (key in tag.allKeys) {
            val outline = tag.getCompound(key)
            val ruleName = outline.getString("Rule")
            val ruleValue = outline.getString("Value")
            val manager = outline.getString("Manager").ifBlank { "carpet" }
            consumer.invoke(ruleName, ruleValue, manager)
        }
    }
}