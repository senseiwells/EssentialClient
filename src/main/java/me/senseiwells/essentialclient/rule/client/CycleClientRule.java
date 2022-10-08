package me.senseiwells.essentialclient.rule.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.senseiwells.essentialclient.utils.interfaces.Rule;

import java.util.List;

public class CycleClientRule extends ClientRule<String> implements Rule.Cycle {
	private final List<String> cycleValues;
	private int index;

	public CycleClientRule(String name, String description, List<String> cycleValues, String defaultValue, String category, RuleListener<String> ruleListener) {
		super(name, description, defaultValue, category);
		this.addListener(ruleListener);
		this.cycleValues = cycleValues;
		this.index = cycleValues.indexOf(defaultValue);
		if (this.index == -1) {
			this.index = 0;
		}
	}

	public CycleClientRule(String name, String description, List<String> cycleValues, String category, RuleListener<String> stringRuleListener) {
		this(name, description, cycleValues, cycleValues.get(0), category, stringRuleListener);
	}

	public CycleClientRule(String name, String description, List<String> cycleValues, String category) {
		this(name, description, cycleValues, category, null);
	}

	@Override
	public List<String> getCycleValues() {
		return this.cycleValues;
	}

	@Override
	public int getCurrentIndex() {
		return this.index;
	}

	@Override
	public void setCurrentIndex(int index) {
		this.index = index;
	}

	@Override
	public String getTypeAsString() {
		return "cycle";
	}

	@Override
	public JsonObject serialise() {
		JsonObject object = super.serialise();

		JsonArray array = new JsonArray();
		for (String string : this.cycleValues) {
			array.add(string);
		}
		object.add("cycle_values", array);

		return object;
	}

	@Override
	public CycleClientRule shallowCopy() {
		CycleClientRule rule = new CycleClientRule(this.getName(), this.getDescription(), this.getCycleValues(), this.getCategory());
		if (this.getListeners() != null) {
			for (RuleListener<String> listener : this.getListeners()) {
				rule.addListener(listener);
			}
		}
		return rule;
	}
}
