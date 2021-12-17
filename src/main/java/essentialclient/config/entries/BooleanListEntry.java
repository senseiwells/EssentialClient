package essentialclient.config.entries;

import carpet.settings.ParsedRule;
import essentialclient.config.clientrule.BooleanClientRule;
import essentialclient.config.clientrule.ClientRuleHelper;
import essentialclient.config.rulescreen.RulesScreen;
import essentialclient.utils.carpet.CarpetSettingsServerNetworkHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.LiteralText;

public class BooleanListEntry extends BaseListEntry {

	public BooleanListEntry(final ParsedRule<?> parsedRule, final MinecraftClient client, final RulesScreen gui) {
		super(parsedRule, client, gui);
		this.editButton = new ButtonWidget(0, 0, 100, 20, new LiteralText(parsedRule.getAsString().equals("true") ? "§2true" : "§4false"), (buttonWidget) -> {
			String invertedBoolean = buttonWidget.getMessage().getString().equals("§2true") ? "false" : "true";
			CarpetSettingsServerNetworkHandler.ruleChange(parsedRule.name, invertedBoolean, client);
			buttonWidget.setMessage(new LiteralText(invertedBoolean.equals("true") ? "§2true" : "§4false"));
		});
		this.resetButton = new ButtonWidget(0, 0, 50, 20, new LiteralText(I18n.translate("controls.reset")), (buttonWidget) -> {
			CarpetSettingsServerNetworkHandler.ruleChange(parsedRule.name, parsedRule.defaultAsString, client);
			this.editButton.setMessage(new LiteralText(parsedRule.defaultAsString.equals("true") ? "§2true" : "§4false"));
		});
	}

	public BooleanListEntry(final BooleanClientRule clientRule, final MinecraftClient client, final RulesScreen gui) {
		super(clientRule, client, gui);
		this.editButton = new ButtonWidget(0, 0, 100, 20, new LiteralText(clientRule.getValue() ? "§2true" : "§4false"), (buttonWidget) -> {
			clientRule.invertBoolean();
			buttonWidget.setMessage(new LiteralText(clientRule.getValue() ? "§2true" : "§4false"));
			ClientRuleHelper.writeSaveFile();
			clientRule.run();
		});
		this.resetButton = new ButtonWidget(0, 0, 50, 20, new LiteralText(I18n.translate("controls.reset")), (buttonWidget) -> {
			clientRule.setValue(clientRule.getDefaultValue());
			this.editButton.setMessage(new LiteralText(clientRule.getDefaultValue() ? "§2true" : "§4false"));
			clientRule.run();
			ClientRuleHelper.writeSaveFile();
		});
	}
}
