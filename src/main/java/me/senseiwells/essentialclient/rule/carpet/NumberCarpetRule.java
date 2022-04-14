package me.senseiwells.essentialclient.rule.carpet;

import me.senseiwells.essentialclient.utils.interfaces.Rule;

public abstract class NumberCarpetRule<T extends Number> extends CarpetClientRule<T> implements Rule.Num<T> {
	public NumberCarpetRule(String name, String description, T defaultValue) {
		super(name, description, defaultValue);
	}
}
