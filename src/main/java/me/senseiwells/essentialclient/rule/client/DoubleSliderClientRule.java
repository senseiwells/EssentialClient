package me.senseiwells.essentialclient.rule.client;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import me.senseiwells.essentialclient.utils.command.CommandHelper;
import me.senseiwells.essentialclient.utils.interfaces.Rule;
import net.minecraft.util.math.MathHelper;

public class DoubleSliderClientRule extends ClientRule<Double> implements Rule.Slider<Double> {
	private final double minValue;
	private final double maxValue;

	public DoubleSliderClientRule(String name, String description, double defaultValue, double min, double max) {
		super(name, description, defaultValue);
		this.minValue = min;
		this.maxValue = max;
	}

	@Override
	public Double fromJson(JsonElement element) {
		return element.getAsDouble();
	}

	@Override
	public String getTypeAsString() {
		return "double_slider";
	}

	@Override
	public JsonObject serialise() {
		JsonObject object = super.serialise();
		object.addProperty("min", this.minValue);
		object.addProperty("max", this.maxValue);
		return object;
	}

	@Override
	public ClientRule<Double> shallowCopy() {
		return new DoubleSliderClientRule(this.getName(), this.getDescription(), this.getDefaultValue(), this.getMin(), this.getMax());
	}

	@Override
	public void setValueFromString(String value) {
		Double doubleValue = EssentialUtils.catchAsNull(() -> Double.parseDouble(value));
		if (doubleValue == null || doubleValue > this.getMax() || doubleValue < this.getMin()) {
			this.logCannotSet(value);
			return;
		}
		this.setValue(doubleValue);
	}

	@Override
	public String getFormatted() {
		return CommandHelper.DECIMAL_FORMAT.format(this.getValue());
	}

	@Override
	public Double getMin() {
		return this.minValue;
	}

	@Override
	public Double getMax() {
		return this.maxValue;
	}

	@Override
	public Double getNewValue(double percent) {
		percent = MathHelper.clamp(percent, 0, 1);
		double difference = this.getMax() - this.getMin();
		return this.getMin() + difference * percent;
	}

	@Override
	public double getPercentage() {
		return (this.getValue() - this.getMin()) / (this.getMax() - this.getMin());
	}
}
