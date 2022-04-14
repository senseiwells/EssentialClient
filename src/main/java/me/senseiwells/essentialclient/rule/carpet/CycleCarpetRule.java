package me.senseiwells.essentialclient.rule.carpet;

import me.senseiwells.essentialclient.utils.interfaces.Rule;

import java.util.List;

public class CycleCarpetRule extends CarpetClientRule<String> implements Rule.Cycle {
	private final List<String> cycleValues;
	private int index;

	public CycleCarpetRule(String name, String description, List<String> cycleValues, String defaultValue) {
		super(name, description, defaultValue);
		this.cycleValues = cycleValues;
		this.index = cycleValues.indexOf(defaultValue);
		if (this.index == -1) {
			this.index = 0;
		}
	}

	public static CycleCarpetRule commandOf(String name, String description, String defaultValue) {
		switch (defaultValue) {
			case "true", "false", "ops" -> { }
			default -> defaultValue = "false";
		}
		return new CycleCarpetRule(name, description, List.of("true", "false", "ops"), defaultValue);
	}

	@Override
	public CarpetClientRule<String> shallowCopy() {
		return new CycleCarpetRule(this.getName(), this.getDescription(), this.cycleValues, this.getDefaultValue());
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
}
