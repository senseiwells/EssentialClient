package me.senseiwells.essential_client.features.carpet_client

import carpet.api.settings.CarpetRule
import carpet.api.settings.InvalidRuleValueException
import carpet.api.settings.RuleHelper
import com.google.common.collect.TreeMultimap
import dev.isxander.yacl3.api.ConfigCategory
import dev.isxander.yacl3.api.Option
import me.senseiwells.essential_client.EssentialClient
import me.senseiwells.essential_client.features.carpet_client.database.CarpetRuleData
import me.senseiwells.essential_client.features.carpet_client.database.CarpetRulesDatabase
import me.senseiwells.essential_client.features.carpet_client.yacl.CarpetOption
import me.senseiwells.essential_client.utils.CarpetUtils
import net.minecraft.client.Minecraft
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component

data object SingleplayerCarpetClient: CarpetClient() {
    override fun synchronizeRuleData(minecraft: Minecraft, tag: CompoundTag) {
        // In singleplayer we have access to the server,
        // carpet doesn't synchronize rules
    }

    override fun createCategories(minecraft: Minecraft, consumer: (ConfigCategory) -> Unit) {
        if (!hasLocalCarpet) {
            EssentialClient.logger.error("Tried loading singleplayer carpet rules without carpet installed!")
            return
        }

        val sorted = TreeMultimap.create<String, Option<*>>(
            String::compareTo,
            Comparator.comparing { option -> option.name().string }
        )
        for (rule in CarpetUtils.rules()) {
            val data = CarpetRulesDatabase.getDataForRule(rule)
            if (data == null) {
                sorted.put("Carpet", this.createOption(rule))
                continue
            }
            sorted.put(data.modName, this.createOption(rule, data))
        }

        for ((mod, options) in sorted.asMap()) {
            val category = ConfigCategory.createBuilder()
            category.name(Component.literal(mod))
            category.options(options)
            consumer.invoke(category.build())
        }
    }

    private fun <T: Any> bindOption(builder: CarpetOption.Builder<T>, rule: CarpetRule<T>): CarpetOption.Builder<T> {
        return this.bindOption(
            builder,
            rule.type(),
            rule.value(),
            rule.defaultValue(),
            rule.categories(),
            rule.suggestions(),
            rule.strict()
        ).applier { option ->
            try {
                rule.set(null, option.pendingValue())
                option.setValue(option.pendingValue())
            } catch (_: InvalidRuleValueException) {

            }
        }
    }

    private fun <T: Any> createOption(rule: CarpetRule<T>): CarpetOption<T> {
        return this.bindOption(this.createUnboundOption(rule), rule).build()
    }

    private fun <T: Any> createOption(rule: CarpetRule<T>, data: CarpetRuleData): CarpetOption<T> {
        return this.bindOption(this.createUnboundOption(data), rule).build()
    }

    private fun <T: Any> createUnboundOption(rule: CarpetRule<T>): CarpetOption.Builder<T> {
        val builder = this.createUnboundOption<T>(
            Component.literal(rule.name()),
            Component.literal(RuleHelper.translatedDescription(rule)),
            rule.extraInfo(),
            listOf()
        )
        builder.available(!rule.settingsManager().locked())
        return builder
    }
}