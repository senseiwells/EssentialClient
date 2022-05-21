package me.senseiwells.essentialclient.rule.client;

import com.google.gson.JsonElement;
import me.senseiwells.essentialclient.utils.EssentialUtils;

public class DoubleClientRule extends NumberClientRule<Double> {
	public DoubleClientRule(String name, String description, Double defaultValue, RuleListener<Double> ruleListener) {
		super(name, description, defaultValue, ruleListener);
	}

	public DoubleClientRule(String name, String description, Double defaultValue) {
		this(name, description, defaultValue, null);
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
		DoubleClientRule rule = new DoubleClientRule(this.getName(), this.getDescription(), this.getDefaultValue());
		if (this.getListeners() != null) {
			for (RuleListener<Double> listener : this.getListeners()) {
				rule.addListener(listener);
			}
		}
		return rule;
	}

	@Override
	public void setValueFromString(String value) {
		Double doubleValue = EssentialUtils.catchAsNull(() -> Double.parseDouble(value));
		if (doubleValue == null) {
			this.logCannotSet(value);
			return;
		}
		this.setValue(doubleValue);
	}
}
