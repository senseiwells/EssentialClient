package me.senseiwells.essentialclient.rule.client;

import com.google.gson.JsonElement;

public class DoubleClientRule extends NumberClientRule<Double> {
	public DoubleClientRule(String name, String description, Double defaultValue, String category, RuleListener<Double> ruleListener) {
		super(name, description, defaultValue, category, ruleListener);
	}

	public DoubleClientRule(String name, String description, Double defaultValue, String category) {
		this(name, description, defaultValue, category, null);
	}

	@Override
	public String getTypeAsString() {
		return "double";
	}

	@Override
	public Type getType() {
		return Type.DOUBLE;
	}

	@Override
	public Double fromJson(JsonElement value) {
		return value.getAsDouble();
	}

	@Override
	public DoubleClientRule shallowCopy() {
		DoubleClientRule rule = new DoubleClientRule(this.getName(), this.getDescription(), this.getDefaultValue(), this.getCategory());
		if (this.getListeners() != null) {
			for (RuleListener<Double> listener : this.getListeners()) {
				rule.addListener(listener);
			}
		}
		return rule;
	}

	@Override
	public void setValueFromString(String value) {
		try {
			this.setValue(Double.parseDouble(value));
		} catch (NumberFormatException e) {
			this.logCannotSet(value);
		}
	}
}
