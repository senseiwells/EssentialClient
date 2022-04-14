package me.senseiwells.essentialclient.rule.carpet;

import com.google.gson.JsonElement;
import me.senseiwells.essentialclient.utils.EssentialUtils;

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
	public void setValueFromString(String value) {
		Double doubleValue = EssentialUtils.catchAsNull(() -> Double.parseDouble(value));
		if (doubleValue == null) {
			this.logCannotSet(value);
			return;
		}
		this.setValue(doubleValue);
	}
}
