package me.senseiwells.essentialclient.rule.client;

import com.google.gson.JsonObject;
import me.senseiwells.essentialclient.rule.impl.SimpleRule;

import java.util.ArrayList;
import java.util.List;

public abstract class ClientRule<T> extends SimpleRule<T> {
	private final String category;

	private List<RuleListener<T>> listeners;

	public ClientRule(String name, String description, T defaultValue) {
		this(name, description, defaultValue, null);
	}

	public ClientRule(String name, String description, T defaultValue, String category) {
		super(name, description, defaultValue);
		this.category = category;
	}

	public abstract String getTypeAsString();

	public JsonObject serialise() {
		JsonObject object = new JsonObject();
		object.addProperty("type", this.getTypeAsString());
		object.addProperty("name", this.getName());
		object.addProperty("description", this.getDescription());
		object.addProperty("optional_info", this.getOptionalInfo());
		object.add("default_value", this.getDefaultValueAsJson());
		object.add("value", this.getValueAsJson());
		return object;
	}

	@Override
	public List<RuleListener<T>> getListeners() {
		return this.listeners;
	}

	@Override
	public abstract ClientRule<T> shallowCopy();

	@Override
	public String getCategory() {
		return this.category;
	}

	@Override
	public void addListener(RuleListener<T> ruleListener) {
		if (ruleListener != null) {
			if (this.listeners == null) {
				this.listeners = new ArrayList<>();
			}
			this.listeners.add(ruleListener);
		}
	}
}
