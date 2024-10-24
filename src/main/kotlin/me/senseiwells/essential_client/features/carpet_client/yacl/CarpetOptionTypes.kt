package me.senseiwells.essential_client.features.carpet_client.yacl

import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.controller.*
import dev.isxander.yacl3.gui.controllers.BooleanController.TRUE_FALSE_FORMATTER
import dev.isxander.yacl3.gui.controllers.slider.DoubleSliderController
import dev.isxander.yacl3.gui.controllers.slider.FloatSliderController
import dev.isxander.yacl3.gui.controllers.slider.IntegerSliderController
import dev.isxander.yacl3.gui.controllers.slider.LongSliderController
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component

sealed interface CarpetOptionType<T> {
    fun controller(
        builder: Option.Builder<T>,
        suggestions: Collection<String>,
        strict: Boolean
    )

    fun clazz(): Class<T>

    fun mapToString(value: T): String

    fun mapFromString(value: String): T?

    companion object {
        private const val COMMAND_CATEGORY = "command"

        fun resolve(type: String, categories: Iterable<String>): CarpetOptionType<*> {
            return when (type) {
                "boolean" -> BooleanCarpetOptionType
                "int" -> IntCarpetOptionType
                "long" -> LongCarpetOptionType
                "float" -> FloatCarpetOptionType
                "double" -> DoubleCarpetOptionType
                else -> when {
                    categories.any { it.equals(COMMAND_CATEGORY, true) } -> CommandCarpetOptionType
                    else -> StringCarpetOptionType
                }
            }
        }

        fun <T> resolve(type: Class<T>, categories: Iterable<String>): CarpetOptionType<T> {
            @Suppress("UNCHECKED_CAST")
            return when (type) {
                Boolean::class.javaObjectType -> BooleanCarpetOptionType
                Int::class.javaObjectType -> IntCarpetOptionType
                Float::class.javaObjectType -> FloatCarpetOptionType
                Double::class.javaObjectType -> DoubleCarpetOptionType
                Enum::class.java -> EnumCarpetOptionType(type)
                String::class.java -> when {
                    categories.any { it.equals(COMMAND_CATEGORY, true) } -> CommandCarpetOptionType
                    else -> StringCarpetOptionType
                }
                else -> when {
                    type.isEnum -> EnumCarpetOptionType(type as Class<Enum<*>>)
                    else -> throw IllegalArgumentException("Unsupported type: $type")
                }
            } as CarpetOptionType<T>
        }
    }
}

data object BooleanCarpetOptionType: CarpetOptionType<Boolean> {
    override fun controller(
        builder: Option.Builder<Boolean>,
        suggestions: Collection<String>,
        strict: Boolean
    ) {
        builder.controller { option ->
            BooleanControllerBuilder.create(option)
                .trueFalseFormatter()
                .coloured(true)
        }
    }

    override fun mapToString(value: Boolean): String {
        return value.toString()
    }

    override fun clazz(): Class<Boolean> {
        return Boolean::class.javaObjectType
    }

    override fun mapFromString(value: String): Boolean? {
        return value.toBooleanStrictOrNull()
    }
}

data object IntCarpetOptionType: CarpetOptionType<Int> {
    override fun controller(
        builder: Option.Builder<Int>,
        suggestions: Collection<String>,
        strict: Boolean
    ) {
        addSuggestionsControllerOr(
            builder,
            suggestions,
            strict,
            String::toIntOrNull,
            IntegerSliderController.DEFAULT_FORMATTER::apply
        ) { option ->
            IntegerFieldControllerBuilder.create(option)
        }
    }

    override fun mapToString(value: Int): String {
        return value.toString()
    }

    override fun clazz(): Class<Int> {
        return Int::class.javaObjectType
    }

    override fun mapFromString(value: String): Int? {
        return value.toIntOrNull()
    }
}

data object LongCarpetOptionType: CarpetOptionType<Long> {
    override fun controller(
        builder: Option.Builder<Long>,
        suggestions: Collection<String>,
        strict: Boolean
    ) {
        addSuggestionsControllerOr(
            builder,
            suggestions,
            strict,
            String::toLongOrNull,
            LongSliderController.DEFAULT_FORMATTER::apply
        ) { option ->
            LongFieldControllerBuilder.create(option)
        }
    }

    override fun mapToString(value: Long): String {
        return value.toString()
    }

    override fun clazz(): Class<Long> {
        return Long::class.javaObjectType
    }

    override fun mapFromString(value: String): Long? {
        return value.toLongOrNull()
    }
}

data object FloatCarpetOptionType: CarpetOptionType<Float> {
    override fun controller(
        builder: Option.Builder<Float>,
        suggestions: Collection<String>,
        strict: Boolean
    ) {
        addSuggestionsControllerOr(
            builder,
            suggestions,
            strict,
            String::toFloatOrNull,
            FloatSliderController.DEFAULT_FORMATTER::apply
        ) { option ->
            FloatFieldControllerBuilder.create(option)
        }
    }

    override fun mapToString(value: Float): String {
        return value.toString()
    }

    override fun clazz(): Class<Float> {
        return Float::class.javaObjectType
    }

    override fun mapFromString(value: String): Float? {
        return value.toFloatOrNull()
    }
}

data object DoubleCarpetOptionType: CarpetOptionType<Double> {
    override fun controller(
        builder: Option.Builder<Double>,
        suggestions: Collection<String>,
        strict: Boolean
    ) {
        addSuggestionsControllerOr(
            builder,
            suggestions,
            strict,
            String::toDoubleOrNull,
            DoubleSliderController.DEFAULT_FORMATTER::apply
        ) { option ->
            DoubleFieldControllerBuilder.create(option)
        }
    }

    override fun mapToString(value: Double): String {
        return value.toString()
    }

    override fun clazz(): Class<Double> {
        return Double::class.javaObjectType
    }

    override fun mapFromString(value: String): Double? {
        return value.toDoubleOrNull()
    }
}

class EnumCarpetOptionType(
    private val type: Class<Enum<*>>
): CarpetOptionType<Enum<*>> {
    override fun controller(
        builder: Option.Builder<Enum<*>>,
        suggestions: Collection<String>,
        strict: Boolean
    ) {
        builder.controller { option ->
            CyclingListControllerBuilder.create(option)
                .values(this.type.enumConstants.toList())
                .formatValue {
                    formatAsBoolean(it.name) { Component.literal(it.name.lowercase()) }
                }
        }
    }

    override fun mapToString(value: Enum<*>): String {
        return value.name
    }

    override fun clazz(): Class<Enum<*>> {
        return this.type
    }

    override fun mapFromString(value: String): Enum<*>? {
        return this.type.enumConstants.firstOrNull {
            it.name.equals(value, true)
        }
    }
}

data object StringCarpetOptionType: CarpetOptionType<String> {
    override fun controller(
        builder: Option.Builder<String>,
        suggestions: Collection<String>,
        strict: Boolean
    ) {
        addSuggestionsControllerOr(builder, suggestions, strict, { it }) { option ->
            StringControllerBuilder.create(option)
        }
    }

    override fun mapToString(value: String): String {
        return value
    }

    override fun clazz(): Class<String> {
        return String::class.java
    }

    override fun mapFromString(value: String): String {
        return value
    }
}

data object CommandCarpetOptionType: CarpetOptionType<String> {
    private val COMMAND_LEVELS = listOf("true", "false", "ops", "0", "1", "2", "3", "4")

    override fun controller(
        builder: Option.Builder<String>,
        suggestions: Collection<String>,
        strict: Boolean
    ) {
        builder.controller { option ->
            CyclingListControllerBuilder.create(option)
                .values(COMMAND_LEVELS)
                .formatValue {
                    formatAsBoolean(it)
                }
        }
    }

    override fun mapToString(value: String): String {
        return value
    }

    override fun clazz(): Class<String> {
        return String::class.java
    }

    override fun mapFromString(value: String): String? {
        if (COMMAND_LEVELS.any { it.equals(value, true) }) {
            return value
        }
        return null
    }
}

private inline fun formatAsBoolean(
    value: String,
    formatter: () -> Component = { Component.literal(value) }
): Component {
    val boolean = value.lowercase().toBooleanStrictOrNull()
    if (boolean != null) {
        return Component.empty()
            .append(TRUE_FALSE_FORMATTER.apply(boolean))
            .withStyle(if (boolean) ChatFormatting.GREEN else ChatFormatting.RED)
    }
    return formatter.invoke()
}

private fun <T> addSuggestionsControllerOr(
    builder: Option.Builder<T>,
    suggestions: Collection<String>,
    strict: Boolean,
    mapper: (String) -> T?,
    formatter: ValueFormatter<T> = ValueFormatter { Component.literal(it.toString()) },
    otherwise: (Option<T>) -> ControllerBuilder<T>
) {
    if (strict) {
        val options = suggestions.mapNotNull(mapper)
        if (options.isNotEmpty()) {
            builder.controller { option ->
                CyclingListControllerBuilder.create(option)
                    .values(options)
                    .formatValue { value -> formatAsBoolean(value.toString()) { formatter.format(value) } }
            }
            return
        }
    }
    builder.controller(otherwise)
}