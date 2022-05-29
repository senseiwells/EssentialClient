package me.senseiwells.essentialclient.rule.client;

import com.google.gson.JsonObject;
import me.senseiwells.essentialclient.utils.interfaces.Rule;

public class StringClientRule extends ClientRule<String> implements Rule.Str {
	Integer maxLength = 32;

	public StringClientRule(String name, String description, String defaultValue, RuleListener<String> listener) {
		super(name, description, defaultValue);
		this.addListener(listener);
	}

	public StringClientRule(String name, String description, String defaultValue) {
		this(name, description, defaultValue, null);
	}

	@Override
	public String getTypeAsString() {
		return "string";
	}

	@Override
	public StringClientRule shallowCopy() {
		StringClientRule rule = new StringClientRule(this.getName(), this.getDescription(), this.getDefaultValue());
		if (this.getListeners() != null) {
			for (RuleListener<String> listener : this.getListeners()) {
				rule.addListener(listener);
			}
		}
		return rule;
	}

	@Override
	public JsonObject serialise() {
		JsonObject object = super.serialise();
		object.addProperty("max_length", this.getMaxLength());
		return object;
	}

	@Override
	public int getMaxLength() {
		return this.maxLength;
	}

	@Override
	public void setMaxLength(Integer maxLength) {
		this.maxLength = maxLength <= 0 ? 32 : maxLength;
	}
}
