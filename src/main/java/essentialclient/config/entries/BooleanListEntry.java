package essentialclient.config.entries;

import essentialclient.config.clientrule.BooleanClientRule;
import essentialclient.config.clientrule.ClientRuleHelper;
import essentialclient.config.rulescreen.RulesScreen;
import essentialclient.config.rulescreen.ServerRulesScreen;
import essentialclient.feature.EssentialCarpetClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.LiteralText;

public class BooleanListEntry extends BaseListEntry {
	public BooleanListEntry(final BooleanClientRule clientRule, final MinecraftClient client, final RulesScreen rulesScreen) {
		super(clientRule, client, rulesScreen, rulesScreen instanceof ServerRulesScreen);
		this.editButton = new ButtonWidget(0, 0, 100, 20, new LiteralText(clientRule.getValue() ? "§2true" : "§4false"), (buttonWidget) -> {
			if (this.isServerScreen && EssentialCarpetClient.handleRuleChange(clientRule.getName(), String.valueOf(!clientRule.getValue()))) {
				return;
			}
			clientRule.invertBoolean();
			buttonWidget.setMessage(new LiteralText(clientRule.getValue() ? "§2true" : "§4false"));
			if (!this.isServerScreen) {
				ClientRuleHelper.writeSaveFile();
				clientRule.run();
			}
		});
		this.resetButton = new ButtonWidget(0, 0, 50, 20, new LiteralText(I18n.translate("controls.reset")), (buttonWidget) -> {
			if (this.isServerScreen && EssentialCarpetClient.handleRuleChange(clientRule.getName(), clientRule.getDefaultValue().toString())) {
				return;
			}
			clientRule.setValue(clientRule.getDefaultValue());
			this.editButton.setMessage(new LiteralText(clientRule.getDefaultValue() ? "§2true" : "§4false"));
			if (!this.isServerScreen) {
				ClientRuleHelper.writeSaveFile();
				clientRule.run();
			}
		});
	}
}
