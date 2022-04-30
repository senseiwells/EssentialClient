package me.senseiwells.essentialclient.rule.client;

import me.senseiwells.essentialclient.rule.impl.SimpleRule;

import java.util.ArrayList;
import java.util.List;

public abstract class ClientRule<T> extends SimpleRule<T> {
	private List<RuleListener<T>> listeners;

	public ClientRule(String name, String description, T defaultValue) {
		super(name, description, defaultValue);
	}

	@Override
	public List<RuleListener<T>> getListeners() {
		return this.listeners;
	}

	@Override
	public abstract ClientRule<T> shallowCopy();

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
