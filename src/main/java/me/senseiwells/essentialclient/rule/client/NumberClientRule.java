package me.senseiwells.essentialclient.rule.client;

import me.senseiwells.essentialclient.utils.interfaces.Rule;

public abstract class NumberClientRule<T extends Number> extends ClientRule<T> implements Rule.Num<T> {
	public NumberClientRule(String name, String description, T defaultValue, RuleListener<T> ruleListener) {
		super(name, description, defaultValue);
		this.addListener(ruleListener);
	}
}
