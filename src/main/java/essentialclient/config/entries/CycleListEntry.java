package essentialclient.config.entries;

import essentialclient.config.clientrule.ClientRuleHelper;
import essentialclient.config.clientrule.CycleClientRule;
import essentialclient.config.rulescreen.RulesScreen;
import essentialclient.config.rulescreen.ServerRulesScreen;
import essentialclient.feature.EssentialCarpetClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.LiteralText;

public class CycleListEntry extends BaseListEntry {
	public CycleListEntry(final CycleClientRule clientRule, final MinecraftClient client, final RulesScreen rulesScreen) {
		super(clientRule, client, rulesScreen, rulesScreen instanceof ServerRulesScreen);
		this.editButton = new ButtonWidget(0, 0, 100, 20, new LiteralText(clientRule.getValue()), (buttonWidget) -> {
			if (this.isServerScreen && EssentialCarpetClient.handleRuleChange(clientRule.getName(), clientRule.getNextValue())) {
				return;
			}
			clientRule.cycleValues();
			buttonWidget.setMessage(new LiteralText(clientRule.getValue()));
			ClientRuleHelper.writeSaveFile();
			clientRule.run();
		});
		this.resetButton = new ButtonWidget(0, 0, 50, 20, new LiteralText(I18n.translate("controls.reset")), (buttonWidget) -> {
			if (this.isServerScreen && EssentialCarpetClient.handleRuleChange(clientRule.getName(), clientRule.getDefaultValue())) {
				return;
			}
			clientRule.resetToDefault();
			this.editButton.setMessage(new LiteralText(clientRule.getDefaultValue()));
			ClientRuleHelper.writeSaveFile();
			clientRule.run();
		});
	}
}
