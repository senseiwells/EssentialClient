package me.senseiwells.essentialclient.rule.client;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import me.senseiwells.essentialclient.utils.interfaces.Rule;

public class BooleanClientRule extends ClientRule<Boolean> implements Rule.Bool {
	public BooleanClientRule(String name, String description, boolean defaultValue, String category, RuleListener<Boolean> ruleListener) {
		super(name, description, defaultValue, category);
		this.addListener(ruleListener);
	}

	public BooleanClientRule(String name, String description, boolean defaultValue, String category) {
		this(name, description, defaultValue, category, null);
	}

	public BooleanClientRule(String name, String description, String category, RuleListener<Boolean> ruleListener) {
		this(name, description, false, category, ruleListener);
	}

	public BooleanClientRule(String name, String description, String category) {
		this(name, description, category, null);
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
		this.setValue("true".equals(value));
	}

	@Override
	public String getTypeAsString() {
		return "boolean";
	}

	@Override
	public BooleanClientRule shallowCopy() {
		BooleanClientRule rule = new BooleanClientRule(this.getName(), this.getDescription(), this.getDefaultValue(), this.getCategory());
		if (this.getListeners() != null) {
			for (RuleListener<Boolean> listener : this.getListeners()) {
				rule.addListener(listener);
			}
		}
		return rule;
	}
}
