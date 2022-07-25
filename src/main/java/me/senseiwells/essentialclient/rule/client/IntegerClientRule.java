package me.senseiwells.essentialclient.rule.client;

import com.google.gson.JsonElement;
import me.senseiwells.arucas.utils.ExceptionUtils;

public class IntegerClientRule extends NumberClientRule<Integer> {
	public IntegerClientRule(String name, String description, int defaultValue, RuleListener<Integer> ruleListener) {
		super(name, description, defaultValue, ruleListener);
	}

	public IntegerClientRule(String name, String description, int defaultValue) {
		this(name, description, defaultValue, null);
	}

	public IntegerClientRule(String name, String description) {
		this(name, description, 0);
	}

	@Override
	public Type getType() {
		return Type.INTEGER;
	}

	@Override
	public Integer fromJson(JsonElement value) {
		return value.getAsInt();
	}

	@Override
	public String getTypeAsString() {
		return "integer";
	}

	@Override
	public IntegerClientRule shallowCopy() {
		IntegerClientRule rule = new IntegerClientRule(this.getName(), this.getDescription(), this.getDefaultValue());
		if (this.getListeners() != null) {
			for (RuleListener<Integer> listener : this.getListeners()) {
				rule.addListener(listener);
			}
		}
		return rule;
	}

	@Override
	public void setValueFromString(String value) {
		Integer intValue = ExceptionUtils.catchAsNull(() -> Integer.parseInt(value));
		if (intValue == null) {
			this.logCannotSet(value);
			return;
		}
		this.setValue(intValue);
	}
}
