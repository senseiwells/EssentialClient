package me.senseiwells.essentialclient.rule.client;

import com.google.gson.JsonElement;

public class IntegerClientRule extends NumberClientRule<Integer> {
	public IntegerClientRule(String name, String description, int defaultValue, String category, RuleListener<Integer> ruleListener) {
		super(name, description, defaultValue, category, ruleListener);
	}

	public IntegerClientRule(String name, String description, int defaultValue, String category) {
		this(name, description, defaultValue, category, null);
	}

	public IntegerClientRule(String name, String description, String category) {
		this(name, description, 0, category);
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
		IntegerClientRule rule = new IntegerClientRule(this.getName(), this.getDescription(), this.getDefaultValue(), this.getCategory());
		if (this.getListeners() != null) {
			for (RuleListener<Integer> listener : this.getListeners()) {
				rule.addListener(listener);
			}
		}
		return rule;
	}

	@Override
	public void setValueFromString(String value) {
		try {
			this.setValue(Integer.parseInt(value));
		} catch (NumberFormatException e) {
			this.logCannotSet(value);
		}
	}
}
