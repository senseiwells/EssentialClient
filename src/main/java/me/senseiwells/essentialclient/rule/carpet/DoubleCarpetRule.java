package me.senseiwells.essentialclient.rule.carpet;

import com.google.gson.JsonElement;

public class DoubleCarpetRule extends NumberCarpetRule<Double> {
	public DoubleCarpetRule(String name, String description, Double defaultValue) {
		super(name, description, defaultValue);
	}

	@Override
	public Type getType() {
		return Type.DOUBLE;
	}

	@Override
	public Double fromJson(JsonElement element) {
		return element.getAsDouble();
	}

	@Override
	public CarpetClientRule<Double> shallowCopy() {
		return new DoubleCarpetRule(this.getName(), this.getDescription(), this.getDefaultValue());
	}

	@Override
	public Double getValueFromString(String value) {
		try {
			return Double.parseDouble(value);
		} catch (NumberFormatException e) {
			this.logCannotSet(value);
			return null;
		}
	}
}
