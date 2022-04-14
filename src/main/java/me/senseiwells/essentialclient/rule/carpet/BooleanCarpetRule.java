package me.senseiwells.essentialclient.rule.carpet;

import me.senseiwells.essentialclient.utils.interfaces.Rule;

public class BooleanCarpetRule extends CarpetClientRule<Boolean> implements Rule.Bool {
	public BooleanCarpetRule(String name, String description, Boolean defaultValue) {
		super(name, description, defaultValue);
	}

	@Override
	public CarpetClientRule<Boolean> shallowCopy() {
		return new BooleanCarpetRule(this.getName(), this.getDescription(), this.getDefaultValue());
	}
}
