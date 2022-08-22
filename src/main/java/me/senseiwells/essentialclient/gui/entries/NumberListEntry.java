package me.senseiwells.essentialclient.gui.entries;

import me.senseiwells.essentialclient.gui.RulesScreen;
import me.senseiwells.essentialclient.utils.interfaces.Rule;
import net.minecraft.client.MinecraftClient;

public class NumberListEntry extends StringListEntry {
	public NumberListEntry(Rule<?> clientRule, MinecraftClient client, RulesScreen rulesScreen) {
		super(clientRule, client, rulesScreen);
	}

	@Override
	protected void checkForInvalid(String newValue) {
		super.checkForInvalid(newValue);
		try {
			switch (this.rule.getType()) {
				case DOUBLE -> Double.parseDouble(newValue);
				case INTEGER -> Integer.parseInt(newValue);
				default -> throw new IllegalStateException("Unexpected value: " + this.rule.getType());
			}
		} catch (NumberFormatException e) {
			this.setInvalid(true);
		}
	}
}
