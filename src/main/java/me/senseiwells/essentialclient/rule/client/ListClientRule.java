package me.senseiwells.essentialclient.rule.client;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import me.senseiwells.essentialclient.utils.interfaces.Rule;

import java.util.List;

public class ListClientRule extends ClientRule<List<String>> implements Rule.ListRule {
	private static final Gson GSON = new Gson();
	private int maxLength;

	public ListClientRule(String name, String description, List<String> defaultValue, String category, RuleListener<List<String>> ruleListener) {
		super(name, description, defaultValue, category);
		this.addListener(ruleListener);
		this.maxLength = 32;
	}

	public ListClientRule(String name, String description, List<String> listValues, String category) {
		this(name, description, listValues, category, null);
	}

	@Override
	public String getTypeAsString() {
		return "list";
	}

	@Override
	public void setValueFromString(String value) {
		try {
			JsonArray array = GSON.fromJson(value, JsonArray.class);
			List<String> configs = this.fromJson(array);
			this.setValue(configs);
		} catch (JsonSyntaxException e) {
			this.logCannotSet(value);
		}
	}

	@Override
	public ListClientRule shallowCopy() {
		ListClientRule rule = new ListClientRule(this.getName(), this.getDescription(), this.getValue(), this.getCategory());
		if (this.getListeners() != null) {
			for (RuleListener<List<String>> listener : this.getListeners()) {
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
