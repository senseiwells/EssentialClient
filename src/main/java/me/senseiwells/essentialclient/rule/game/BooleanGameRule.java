package me.senseiwells.essentialclient.rule.game;

import me.senseiwells.essentialclient.utils.interfaces.Rule;
import net.minecraft.world.GameRules;

public class BooleanGameRule extends GameRule<Boolean> implements Rule.Bool {
	public BooleanGameRule(String name, String description, Boolean defaultValue, GameRules.Key<?> key) {
		super(name, description, defaultValue, key);
	}

	@Override
	public GameRule<Boolean> shallowCopy() {
		return new BooleanGameRule(this.getName(), this.getDescription(), this.getDefaultValue(), this.getKey());
	}

	@Override
	public Boolean getValueFromString(String value) {
		return "true".equals(value);
	}
}
