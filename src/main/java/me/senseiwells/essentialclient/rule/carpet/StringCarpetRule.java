package me.senseiwells.essentialclient.rule.carpet;

import me.senseiwells.essentialclient.utils.interfaces.Rule;

public class StringCarpetRule extends CarpetClientRule<String> implements Rule.Str {
	public StringCarpetRule(String name, String description, String defaultValue) {
		super(name, description, defaultValue);
	}

	@Override
	public CarpetClientRule<String> shallowCopy() {
		return new StringCarpetRule(this.getName(), this.getDescription(), this.getDefaultValue());
	}

	@Override
	public String getValueFromString(String value) {
		return value;
	}

	@Override
	public int getMaxLength() {
		return 0;
	}

	@Override
	public void setMaxLength(Integer maxLength) {

	}
}
