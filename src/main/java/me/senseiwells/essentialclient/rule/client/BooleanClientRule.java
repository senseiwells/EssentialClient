package me.senseiwells.essentialclient.rule.client;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import me.senseiwells.essentialclient.utils.interfaces.Rule;

public class BooleanClientRule extends ClientRule<Boolean> implements Rule.Bool {
	public BooleanClientRule(String name, String description, boolean defaultValue, RuleListener<Boolean> ruleListener) {
		super(name, description, defaultValue);
		this.addListener(ruleListener);
	}

	public BooleanClientRule(String name, String description, boolean defaultValue) {
		this(name, description, defaultValue, null);
	}

	public BooleanClientRule(String name, String description, RuleListener<Boolean> ruleListener) {
		this(name, description, false, ruleListener);
	}

	public BooleanClientRule(String name, String description) {
		this(name, description, null);
	}

	@Override
	public JsonElement toJson(Boolean value) {
		return new JsonPrimitive(value);
	}

	@Override
	public Boolean fromJson(JsonElement value) {
		return value.getAsBoolean();
	}

	@Override
	public void setValueFromString(String value) {
		this.setValue(value.equals("true"));
	}

	@Override
	public String getTypeAsString() {
		return "boolean";
	}

	@Override
	public BooleanClientRule shallowCopy() {
		BooleanClientRule rule = new BooleanClientRule(this.getName(), this.getDescription(), this.getDefaultValue());
		if (this.getListeners() != null) {
			for (RuleListener<Boolean> listener : this.getListeners()) {
				rule.addListener(listener);
			}
		}
		return rule;
	}
}
