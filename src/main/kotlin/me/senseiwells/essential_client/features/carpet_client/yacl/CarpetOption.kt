package me.senseiwells.essential_client.features.carpet_client.yacl

import com.google.common.collect.ImmutableSet
import dev.isxander.yacl3.api.*
import dev.isxander.yacl3.api.controller.ControllerBuilder
import dev.isxander.yacl3.impl.SafeBinding
import net.minecraft.network.chat.Component
import java.util.function.BiConsumer
import java.util.function.Consumer
import java.util.function.Function
import java.util.function.Supplier

class CarpetOption<T: Any>(
    private val name: Component,
    private val descriptionProvider: (T) -> OptionDescription,
    private val binding: Binding<T>,
    private val type: CarpetOptionType<T>,
    private val applier: (CarpetOption<T>) -> Unit,
    private val listeners: MutableList<BiConsumer<Option<T>, T>>,
    private var available: Boolean,
    controllerProvider: (Option<T>) -> Controller<T>
): Option<T> {
    private val controller: Controller<T> = controllerProvider.invoke(this)
    private var description: OptionDescription

    private var pending = this.binding.value

    private var setting: Boolean = false

    init {
        this.description = this.descriptionProvider.invoke(this.pending)
        this.triggerListeners()

        this.addListener { _, pending ->
            this.description = this.descriptionProvider.invoke(pending)
        }
    }

    fun getPendingValueAsString(): String {
        return this.type.mapToString(this.pending)
    }

    fun setValueFromString(value: String) {
        val mapped = this.type.mapFromString(value)
        if (mapped != null && this.binding.value != mapped) {
            this.setValue(mapped)
        }
    }

    fun setValue(value: T) {
        this.setting = false
        this.binding.value = value
    }

    override fun name(): Component {
        return this.name
    }

    override fun description(): OptionDescription {
        return this.description
    }

    @Deprecated("Use description")
    override fun tooltip(): Component {
        return this.description.text()
    }

    override fun controller(): Controller<T> {
        return this.controller
    }

    override fun binding(): Binding<T> {
        return this.binding
    }

    override fun available(): Boolean {
        return this.available
    }

    override fun setAvailable(available: Boolean) {
        val changed = this.available != available
        this.available = available
        if (changed) {
            if (!available) {
                this.pending = this.binding.value
            }
            this.triggerListeners()
        }
    }

    override fun flags(): ImmutableSet<OptionFlag> {
        return ImmutableSet.of()
    }

    override fun changed(): Boolean {
        if (this.setting) {
            return false
        }
        return this.binding.value != this.pending
    }

    override fun pendingValue(): T {
        return this.pending
    }

    override fun requestSet(value: T) {
        this.pending = value
        this.triggerListeners()
    }

    override fun applyValue(): Boolean {
        if (this.changed()) {
            this.setting = true
            this.applier.invoke(this)
            return true
        }
        return false
    }

    override fun forgetPendingValue() {
        this.requestSet(this.binding.value)
    }

    override fun requestSetDefault() {
        this.requestSet(this.binding.defaultValue())
    }

    override fun isPendingValueDefault(): Boolean {
        return this.binding.defaultValue() == this.pending
    }

    override fun addListener(listener: BiConsumer<Option<T>, T>) {
        this.listeners.add(listener)
    }

    private fun triggerListeners() {
        for (listener in this.listeners) {
            listener.accept(this, this.pending)
        }
    }

    class Builder<T: Any>: Option.Builder<T> {
        private var name: Component? = null
        private var description: ((T) -> OptionDescription) = { OptionDescription.EMPTY }
        private var binding: Binding<T>? = null
        private var type: CarpetOptionType<T>? = null
        private var applier: (CarpetOption<T>) -> Unit = { }
        private val listeners = ArrayList<BiConsumer<Option<T>, T>>()
        private var controller: ((Option<T>) -> Controller<T>)? = null
        private var available: Boolean = true

        override fun name(name: Component): Builder<T> {
            this.name = name
            return this
        }

        override fun description(description: OptionDescription): Builder<T> {
            this.description = { description }
            return this
        }

        override fun available(available: Boolean): Builder<T> {
            this.available = available
            return this
        }

        override fun flag(vararg flag: OptionFlag): Builder<T> {
            return this
        }

        override fun flags(flags: MutableCollection<out OptionFlag>): Builder<T> {
            return this
        }

        override fun instant(instant: Boolean): Builder<T> {
            return this
        }

        override fun listeners(listeners: MutableCollection<BiConsumer<Option<T>, T>>): Builder<T> {
            this.listeners.addAll(listeners)
            return this
        }

        override fun listener(listener: BiConsumer<Option<T>, T>): Builder<T> {
            this.listeners.add(listener)
            return this
        }

        override fun binding(def: T, getter: Supplier<T>, setter: Consumer<T>): Builder<T> {
            this.binding = Binding.generic(def, getter, setter)
            return this
        }

        override fun binding(binding: Binding<T>): Builder<T> {
            this.binding = binding
            return this
        }

        fun type(type: CarpetOptionType<T>): Builder<T> {
            this.type = type
            return this
        }

        fun applier(applier: (CarpetOption<T>) -> Unit): Builder<T> {
            this.applier = applier
            return this
        }

        override fun customController(control: Function<Option<T>, Controller<T>>): Builder<T> {
            this.controller = control::apply
            return this
        }

        override fun controller(controllerBuilder: Function<Option<T>, ControllerBuilder<T>>): Builder<T> {
            @Suppress("UnstableApiUsage")
            this.controller = { controllerBuilder.apply(it).build() }
            return this
        }

        override fun description(descriptionFunction: Function<T, OptionDescription>): Builder<T> {
            this.description = descriptionFunction::apply
            return this
        }

        override fun build(): CarpetOption<T> {
            return CarpetOption(
                requireNotNull(this.name) { "CarpetOption name must be specified" },
                this.description,
                SafeBinding(requireNotNull(this.binding) { "CarpetOption binding must be specified" }),
                requireNotNull(this.type) { "CarpetOption mapper must be specified" } ,
                this.applier,
                ArrayList(this.listeners),
                this.available,
                requireNotNull(this.controller) { "CarpetOption controller must be specified" }
            )
        }
    }
}