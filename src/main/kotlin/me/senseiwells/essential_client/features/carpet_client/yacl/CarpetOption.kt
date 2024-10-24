package me.senseiwells.essential_client.features.carpet_client.yacl

import com.google.common.collect.ImmutableSet
import dev.isxander.yacl3.api.*
import dev.isxander.yacl3.api.OptionEventListener.Event
import dev.isxander.yacl3.api.controller.ControllerBuilder
import dev.isxander.yacl3.impl.ProvidesBindingForDeprecation
import net.minecraft.network.chat.Component
import java.util.function.BiConsumer
import java.util.function.Consumer
import java.util.function.Function
import java.util.function.Supplier

class CarpetOption<T: Any>(
    private val name: Component,
    private val descriptionProvider: (T) -> OptionDescription,
    private val state: StateManager<T>,
    private val type: CarpetOptionType<T>,
    private val applier: (CarpetOption<T>) -> Unit,
    private val listeners: MutableList<OptionEventListener<T>>,
    private var available: Boolean,
    controllerProvider: (Option<T>) -> Controller<T>
): Option<T> {
    private val controller: Controller<T> = controllerProvider.invoke(this)
    private lateinit var description: OptionDescription

    private var setting: Boolean = false

    init {
        this.state.addListener { _, _ -> triggerListeners(Event.STATE_CHANGE) }
        this.addEventListener { option, _ ->
            this.description = this.descriptionProvider.invoke(option.pendingValue())
        }
        this.triggerListeners(Event.INITIAL)
    }

    fun getPendingValueAsString(): String {
        return this.type.mapToString(this.state.get())
    }

    fun setValueFromString(value: String) {
        val mapped = this.type.mapFromString(value)
        if (mapped != null && this.state.get() != mapped) {
            this.setValue(mapped)
        }
    }

    fun setValue(value: T) {
        this.setting = false
        this.state.set(value)
        this.state.apply()
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

    override fun stateManager(): StateManager<T> {
        return this.state
    }

    @Suppress("UNCHECKED_CAST")
    @Deprecated("You should use the stateManager instead")
    override fun binding(): Binding<T> {
        if (this.state is ProvidesBindingForDeprecation<*>) {
            return this.state.binding as Binding<T>
        } else {
            throw UnsupportedOperationException("Binding is not available for this option - using a new state manager which does not directly expose the binding as it may not have one.")
        }
    }

    override fun available(): Boolean {
        return this.available
    }

    override fun setAvailable(available: Boolean) {
        val changed = this.available != available
        this.available = available
        if (changed) {
            if (!available) {
                this.state.sync()
            }
            this.triggerListeners(Event.AVAILABILITY_CHANGE)
        }
    }

    override fun flags(): ImmutableSet<OptionFlag> {
        return ImmutableSet.of()
    }

    override fun changed(): Boolean {
        if (this.setting) {
            return false
        }
        return !this.state.isSynced
    }

    override fun pendingValue(): T {
        return this.state.get()
    }

    override fun requestSet(value: T) {
        this.state.set(value)
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
        this.state.sync()
    }

    override fun requestSetDefault() {
        this.state.resetToDefault(StateManager.ResetAction.BY_OPTION)
    }

    override fun isPendingValueDefault(): Boolean {
        return this.state.isDefault
    }

    override fun addEventListener(listener: OptionEventListener<T>) {
        this.listeners.add(listener)
    }

    @Deprecated("Use addEventListener istead")
    override fun addListener(listener: BiConsumer<Option<T>, T>) {
        this.listeners.add { option, _ -> listener.accept(option, option.pendingValue()) }
    }

    private fun triggerListeners(event: Event) {
        for (listener in this.listeners) {
            listener.onEvent(this, event)
        }
    }

    class Builder<T: Any>: Option.Builder<T> {
        private var name: Component? = null
        private var description: ((T) -> OptionDescription) = { OptionDescription.EMPTY }
        private var state: StateManager<T>? = null
        private var type: CarpetOptionType<T>? = null
        private var applier: (CarpetOption<T>) -> Unit = { }
        private val listeners = ArrayList<OptionEventListener<T>>()
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

        override fun stateManager(stateManager: StateManager<T>): Option.Builder<T> {
            this.state = stateManager
            return this
        }

        override fun available(available: Boolean): Builder<T> {
            this.available = available
            return this
        }

        override fun flag(vararg flag: OptionFlag): Builder<T> {
            throw UnsupportedOperationException()
        }

        override fun flags(flags: MutableCollection<out OptionFlag>): Builder<T> {
            throw UnsupportedOperationException()
        }

        override fun addListeners(listeners: MutableCollection<OptionEventListener<T>>): Option.Builder<T> {
            this.listeners.addAll(listeners)
            return this
        }

        override fun addListener(listener: OptionEventListener<T>): Option.Builder<T> {
            this.listeners.add(listener)
            return this
        }

        override fun instant(instant: Boolean): Builder<T> {
            throw UnsupportedOperationException()
        }

        override fun listeners(listeners: MutableCollection<BiConsumer<Option<T>, T>>): Builder<T> {
            throw UnsupportedOperationException()
        }

        override fun listener(listener: BiConsumer<Option<T>, T>): Builder<T> {
            throw UnsupportedOperationException()
        }

        override fun binding(def: T, getter: Supplier<T>, setter: Consumer<T>): Builder<T> {
            return this
        }

        override fun binding(binding: Binding<T>): Builder<T> {
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
                requireNotNull(this.state) { "CarpetOption binding must be specified" },
                requireNotNull(this.type) { "CarpetOption mapper must be specified" },
                this.applier,
                ArrayList(this.listeners),
                this.available,
                requireNotNull(this.controller) { "CarpetOption controller must be specified" }
            )
        }
    }
}