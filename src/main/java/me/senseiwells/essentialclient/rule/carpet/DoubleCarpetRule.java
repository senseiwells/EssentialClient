package me.senseiwells.essentialclient.rule.carpet;

import com.google.gson.JsonElement;
import me.senseiwells.arucas.utils.ExceptionUtils;

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
		Double doubleValue = ExceptionUtils.catchAsNull(() -> Double.parseDouble(value));
		if (doubleValue == null) {
			this.logCannotSet(value);
			return null;
		}
		return doubleValue;
	}
}
