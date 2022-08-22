package me.senseiwells.essentialclient.rule.carpet;

import com.google.gson.JsonElement;

public class IntegerCarpetRule extends NumberCarpetRule<Integer> {
	public IntegerCarpetRule(String name, String description, Integer defaultValue) {
		super(name, description, defaultValue);
	}

	@Override
	public Type getType() {
		return Type.INTEGER;
	}

	@Override
	public Integer fromJson(JsonElement element) {
		return element.getAsInt();
	}

	@Override
	public CarpetClientRule<Integer> shallowCopy() {
		return new IntegerCarpetRule(this.getName(), this.getDescription(), this.getDefaultValue());
	}

	@Override
	public Integer getValueFromString(String value) {
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			this.logCannotSet(value);
			return null;
		}
	}
}
