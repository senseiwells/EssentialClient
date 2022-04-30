package me.senseiwells.essentialclient.rule.game;

import me.senseiwells.arucas.utils.ExceptionUtils;
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
		Integer intValue = ExceptionUtils.catchAsNull(() -> Integer.parseInt(value));
		if (intValue == null) {
			this.logCannotSet(value);
			return null;
		}
		return intValue;
	}
}
