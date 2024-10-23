package me.senseiwells.essential_client.features.carpet_client

import dev.isxander.yacl3.api.ConfigCategory
import me.senseiwells.essential_client.features.carpet_client.database.CarpetRulesDatabase
import net.minecraft.client.Minecraft
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component

data object EmptyCarpetClient: CarpetClient() {
    override fun synchronizeRuleData(minecraft: Minecraft, tag: CompoundTag) {
        // This will never be called
    }

    override fun createCategories(minecraft: Minecraft, consumer: (ConfigCategory) -> Unit) {
        for ((mod, registry) in CarpetRulesDatabase.registries.entries) {
            val builder = ConfigCategory.createBuilder()
            builder.name(Component.literal(mod))
            for (data in registry.values()) {
                val option = this.bindOption(this.createUnboundOption(data), data) ?: continue
                option.available(false)
                builder.option(option.build())
            }
            consumer.invoke(builder.build())
        }
    }
}