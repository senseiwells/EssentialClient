package me.senseiwells.essentialclient.rule.game;

import net.minecraft.world.GameRules;

public class IntegerGameRule extends GameRule<Integer> {
	public IntegerGameRule(String name, String description, Integer defaultValue, GameRules.Key<?> key) {
		super(name, description, defaultValue, key);
	}

	@Override
	public Type getType() {
		return Type.INTEGER;
	}

	@Override
	public GameRule<Integer> shallowCopy() {
		return new IntegerGameRule(this.getName(), this.getDescription(), this.getDefaultValue(), this.getKey());
	}

	@Override
	public Integer getValueFromString(String value) {
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			this.logCannotSet(value);
			return null;
		}
	}
}
