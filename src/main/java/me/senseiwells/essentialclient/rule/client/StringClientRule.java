package me.senseiwells.essentialclient.rule.client;

import me.senseiwells.essentialclient.utils.interfaces.Rule;

public class StringClientRule extends ClientRule<String> implements Rule.Str {
	public StringClientRule(String name, String description, String defaultValue, RuleListener<String> listener) {
		super(name, description, defaultValue);
		this.addListener(listener);
	}

	public StringClientRule(String name, String description, String defaultValue) {
		this(name, description, defaultValue, null);
	}

	@Override
	public StringClientRule shallowCopy() {
		StringClientRule rule = new StringClientRule(this.getName(), this.getDescription(), this.getDefaultValue());
		if (this.getListeners() != null) {
			for (RuleListener<String> listener : this.getListeners()) {
				rule.addListener(listener);
			}
		}
		return rule;
	}
}
