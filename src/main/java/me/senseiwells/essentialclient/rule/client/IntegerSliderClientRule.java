package me.senseiwells.essentialclient.rule.client;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.senseiwells.essentialclient.utils.interfaces.Rule;
import net.minecraft.util.math.MathHelper;

public class IntegerSliderClientRule extends ClientRule<Integer> implements Rule.Slider<Integer> {
	private final int minValue;
	private final int maxValue;

	public IntegerSliderClientRule(String name, String description, int defaultValue, String category, int minValue, int maxValue) {
		super(name, description, defaultValue, category);
		this.minValue = minValue;
		this.maxValue = maxValue;
	}

	@Override
	public Integer fromJson(JsonElement element) {
		return element.getAsInt();
	}

	@Override
	public String getTypeAsString() {
		return "integer_slider";
	}

	@Override
	public JsonObject serialise() {
		JsonObject object = super.serialise();
		object.addProperty("min", this.minValue);
		object.addProperty("max", this.maxValue);
		return object;
	}

	@Override
	public ClientRule<Integer> shallowCopy() {
		return new IntegerSliderClientRule(this.getName(), this.getDescription(), this.getDefaultValue(), this.getCategory(), this.getMin(), this.getMax());
	}

	@Override
	public void setValueFromString(String value) {
		Integer integer;
		try {
			integer = Integer.parseInt(value);
		} catch (NumberFormatException e) {
			integer = null;
		}
		if (integer == null || integer > this.getMax() || integer < this.getMin()) {
			this.logCannotSet(value);
			return;
		}
		this.setValue(integer);
	}

	@Override
	public String getFormatted() {
		return this.getValue().toString();
	}

	@Override
	public Integer getMin() {
		return this.minValue;
	}

	@Override
	public Integer getMax() {
		return this.maxValue;
	}

	@Override
	public Integer getNewValue(double percent) {
		percent = MathHelper.clamp(percent, 0, 1);
		int difference = this.getMax() - this.getMin();
		return (int) Math.round(this.getMin() + difference * percent);
	}

	@Override
	public double getPercentage() {
		return (this.getValue() - this.getMin()) / (double) (this.getMax() - this.getMin());
	}
}
