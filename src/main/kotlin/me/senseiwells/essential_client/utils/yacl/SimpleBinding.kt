package me.senseiwells.essential_client.utils.yacl

import dev.isxander.yacl3.api.Binding

class SimpleBinding<T: Any>(
    private var value: T,
    private val defaultValue: T = value
): Binding<T> {
    override fun setValue(value: T) {
        this.value = value
    }

    override fun getValue(): T {
        return this.value
    }

    override fun defaultValue(): T {
        return this.defaultValue
    }
}