package me.senseiwells.essentialclient.rule.impl;

import me.senseiwells.essentialclient.utils.interfaces.Rule;

public abstract class SimpleRule<T> implements Rule<T> {
	private final String name;
	private final String description;
	private final T defaultValue;

	private String optionalInfo;
	private T value;

	public SimpleRule(String name, String description, T defaultValue) {
		this.name = name;
		this.description = description;
		this.defaultValue = defaultValue;
		this.value = defaultValue;
	}

	@Override
	public final String getName() {
		return this.name;
	}

	@Override
	public final String getDescription() {
		return this.description;
	}

	@Override
	public final String getOptionalInfo() {
		return this.optionalInfo;
	}

	@Override
	public final T getDefaultValue() {
		return this.defaultValue;
	}

	@Override
	public final T getValue() {
		return this.value;
	}

	@Override
	public final void setValueQuietly(T value) {
		this.value = value;
	}

	@Override
	public final void setOptionalInfo(String optionalInfo) {
		this.optionalInfo = optionalInfo;
	}
}
