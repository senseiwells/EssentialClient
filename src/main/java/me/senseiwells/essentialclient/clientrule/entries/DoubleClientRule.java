package me.senseiwells.essentialclient.clientrule.entries;

import com.google.gson.JsonElement;
import me.senseiwells.essentialclient.utils.EssentialUtils;

import java.util.function.Consumer;

public class DoubleClientRule extends NumberClientRule<Double> {
	public DoubleClientRule(String name, String description, Double defaultValue, Consumer<Double> consumer) {
		super(name, Type.DOUBLE, description, defaultValue, consumer);
	}

	public DoubleClientRule(String name, String description, Double defaultValue) {
		this(name, description, defaultValue, null);
	}

	@Override
	public Double fromJson(JsonElement value) {
		return value.getAsDouble();
	}

	@Override
	public void setValueFromString(String value) {
		Double doubleValue = EssentialUtils.catchAsNull(() -> Double.parseDouble(value));
		if (doubleValue == null) {
			this.cannotSetValue(value);
			return;
		}
		this.setValue(doubleValue);
	}

	@Override
	public DoubleClientRule copy() {
		return new DoubleClientRule(this.getName(), this.getDescription(), this.getDefaultValue(), this.getConsumer());
	}
}
