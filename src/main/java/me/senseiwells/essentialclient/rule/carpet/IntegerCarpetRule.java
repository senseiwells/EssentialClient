package me.senseiwells.essentialclient.rule.carpet;

import com.google.gson.JsonElement;
import me.senseiwells.essentialclient.utils.EssentialUtils;

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
	public void setValueFromString(String value) {
		Integer intValue = EssentialUtils.catchAsNull(() -> Integer.parseInt(value));
		if (intValue == null) {
			this.logCannotSet(value);
			return;
		}
		this.setValue(intValue);
	}
}
