package me.senseiwells.essentialclient.rule.client;

import me.senseiwells.essentialclient.utils.interfaces.Rule;

import java.util.List;

public class CycleClientRule extends ClientRule<String> implements Rule.Cycle {
	private final List<String> cycleValues;
	private int index;

	public CycleClientRule(String name, String description, List<String> cycleValues, String defaultValue, RuleListener<String> ruleListener) {
		super(name, description, defaultValue);
		this.addListener(ruleListener);
		this.cycleValues = cycleValues;
		this.index = cycleValues.indexOf(defaultValue);
		if (this.index == -1) {
			this.index = 0;
		}
	}

	public CycleClientRule(String name, String description, List<String> cycleValues, RuleListener<String> stringRuleListener) {
		this(name, description, cycleValues, cycleValues.get(0), stringRuleListener);
	}

	public CycleClientRule(String name, String description, List<String> cycleValues) {
		this(name, description, cycleValues, null);
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
	public CycleClientRule shallowCopy() {
		CycleClientRule rule = new CycleClientRule(this.getName(), this.getDescription(), this.getCycleValues());
		if (this.getListeners() != null) {
			for (RuleListener<String> listener : this.getListeners()) {
				rule.addListener(listener);
			}
		}
		return rule;
	}
}
