package me.senseiwells.essentialclient.rule.client;

import com.google.gson.JsonObject;
import me.senseiwells.essentialclient.utils.interfaces.Rule;

public class StringClientRule extends ClientRule<String> implements Rule.Str {
	private int maxLength;

	public StringClientRule(String name, String description, String defaultValue, String category, RuleListener<String> listener) {
		super(name, description, defaultValue, category);
		this.addListener(listener);
		this.maxLength = 32;
	}

	public StringClientRule(String name, String description, String defaultValue, String category) {
		this(name, description, defaultValue, category, null);
	}

	@Override
	public String getTypeAsString() {
		return "string";
	}

	@Override
	public StringClientRule shallowCopy() {
		StringClientRule rule = new StringClientRule(this.getName(), this.getDescription(), this.getDefaultValue(), this.getCategory());
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
	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength <= 0 ? 32 : maxLength;
	}
}
