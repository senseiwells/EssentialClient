package me.senseiwells.essential_client.features.carpet_client

import dev.isxander.yacl3.api.ConfigCategory
import dev.isxander.yacl3.api.OptionDescription
import dev.isxander.yacl3.api.YetAnotherConfigLib
import me.senseiwells.essential_client.features.carpet_client.database.CarpetRuleData
import me.senseiwells.essential_client.features.carpet_client.yacl.*
import me.senseiwells.essential_client.utils.yacl.SimpleBinding
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.Minecraft
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component

sealed class CarpetClient {
    fun createConfig(minecraft: Minecraft): YetAnotherConfigLib {
        val builder = YetAnotherConfigLib.createBuilder()
        this.createCategories(minecraft, builder::category)
        builder.title(Component.literal("Carpet Settings"))
        val config = builder.build()
        return config
    }

    internal abstract fun synchronizeRuleData(minecraft: Minecraft, tag: CompoundTag)

    protected abstract fun createCategories(minecraft: Minecraft, consumer: (ConfigCategory) -> Unit)

    protected fun <T: Any> createUnboundOption(data: CarpetRuleData): CarpetOption.Builder<T> {
        return this.createUnboundOption(
            Component.literal(data.name),
            Component.literal(data.description),
            data.extras.map(Component::literal),
            data.validators.map(Component::literal)
        )
    }

    protected fun <T: Any> createUnboundOption(
        name: Component,
        description: Component,
        extra: List<Component>,
        validators: List<Component>,
    ): CarpetOption.Builder<T> {
        val builder = CarpetOption.Builder<T>()
        builder.name(name)
        builder.description(
            OptionDescription.createBuilder()
                .text(description)
                .text(extra)
                .text(validators)
                .build()
        )
        return builder
    }

    @Suppress("UNCHECKED_CAST")
    protected fun <T: Any> bindOption(
        builder: CarpetOption.Builder<T>,
        data: CarpetRuleData
    ): CarpetOption.Builder<T>? {
        val type = data.type as CarpetOptionType<T>

        builder.type(type)
        val defaultValue = data.defaultValue as? T
        val value = data.value as? T
        if (value != null && defaultValue != null) {
            builder.binding(SimpleBinding(value, defaultValue))
        } else {
            val nonNullValue = value ?: defaultValue ?: return null
            builder.binding(SimpleBinding(nonNullValue))
        }
        type.controller(builder, data.options, data.strict)
        return builder
    }

    protected fun <T: Any> bindOption(
        builder: CarpetOption.Builder<T>,
        clazz: Class<T>,
        value: T,
        defaultValue: T,
        categories: Collection<String>,
        suggestions: Collection<String>,
        strict: Boolean
    ): CarpetOption.Builder<T> {
        val type = CarpetOptionType.resolve(clazz, categories)
        builder.type(type)
        builder.binding(SimpleBinding(value, defaultValue))
        type.controller(builder, suggestions, strict)
        return builder
    }

    companion object {
        @JvmField
        val hasLocalCarpet = FabricLoader.getInstance().isModLoaded("carpet")
    }
}

