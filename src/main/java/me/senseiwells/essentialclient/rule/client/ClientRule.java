package me.senseiwells.essentialclient.rule.client;

import com.google.gson.JsonObject;
import me.senseiwells.essentialclient.rule.impl.SimpleRule;

import java.util.ArrayList;
import java.util.List;

public abstract class ClientRule<T> extends SimpleRule<T> {
	private List<RuleListener<T>> listeners;

	public ClientRule(String name, String description, T defaultValue) {
		super(name, description, defaultValue);
	}

	public abstract String getTypeAsString();

	public JsonObject serialise() {
		JsonObject object = new JsonObject();
		object.addProperty("type", this.getTypeAsString());
		object.addProperty("name", this.getName());
		object.addProperty("description", this.getDescription());
		object.addProperty("optional_info", this.getOptionalInfo());
		object.addProperty("max_length", this.getMaxLength());
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
	public void addListener(RuleListener<T> ruleListener) {
		if (ruleListener != null) {
			if (this.listeners == null) {
				this.listeners = new ArrayList<>();
			}
			this.listeners.add(ruleListener);
		}
	}
}
