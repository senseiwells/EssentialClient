package me.senseiwells.essentialclient.rule.client;

import me.senseiwells.essentialclient.utils.interfaces.Rule;

import java.util.ArrayList;
import java.util.List;

public abstract class ClientRule<T> implements Rule<T> {
	private final String name;
	private final String description;
	private final T defaultValue;

	private List<RuleListener<T>> listeners;
	private String optionalInfo;
	private T value;

	public ClientRule(String name, String description, T defaultValue) {
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
	public List<RuleListener<T>> getListeners() {
		return this.listeners;
	}

	@Override
	public abstract ClientRule<T> shallowCopy();

	@Override
	public void setValueQuietly(T value) {
		this.value = value;
	}

	@Override
	public void setOptionalInfo(String optionalInfo) {
		this.optionalInfo = optionalInfo;
	}

	@Override
	public void addListener(RuleListener<T> ruleListener) {
		if (ruleListener != null) {
			if (this.listeners == null) {
				this.listeners = new ArrayList<>();
			}
			this.listeners.add(ruleListener);
		}
	}
}
