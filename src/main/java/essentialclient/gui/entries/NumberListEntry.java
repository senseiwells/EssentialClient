package essentialclient.gui.entries;

import essentialclient.clientrule.entries.ClientRule;
import essentialclient.gui.rulescreen.RulesScreen;
import essentialclient.utils.EssentialUtils;
import net.minecraft.client.MinecraftClient;

public class NumberListEntry extends StringListEntry {
	public NumberListEntry(ClientRule<?> clientRule, MinecraftClient client, RulesScreen rulesScreen) {
		super(clientRule, client, rulesScreen);
	}

	@Override
	protected void checkForInvalid(String newValue) {
		super.checkForInvalid(newValue);
		Number number = switch (this.clientRule.getType()) {
			case DOUBLE -> EssentialUtils.catchAsNull(() -> Double.parseDouble(newValue));
			case INTEGER -> EssentialUtils.catchAsNull(() -> Integer.parseInt(newValue));
			default -> throw new IllegalStateException("Unexpected value: " + this.clientRule.getType());
		};
		this.setInvalid(number == null);
	}
}
