package essentialclient.config.entries;

import carpet.settings.ParsedRule;
import essentialclient.config.clientrule.BooleanClientRule;
import essentialclient.config.clientrule.ClientRuleHelper;
import essentialclient.config.rulescreen.RulesScreen;
import essentialclient.utils.carpet.CarpetSettingsServerNetworkHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.LiteralText;

@Environment(EnvType.CLIENT)
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
            buttonWidget.setMessage(new LiteralText(clientRule.getValue() ? "§2true" : "§4false"));
            clientRule.invertBoolean();
            ClientRuleHelper.writeSaveFile();
            clientRule.run();
        });
        this.resetButton = new ButtonWidget(0, 0, 50, 20, new LiteralText(I18n.translate("controls.reset")), (buttonWidget) -> {
            this.editButton.setMessage(new LiteralText(clientRule.getDefaultValue() ? "§2true" : "§4false"));
            clientRule.setValue(clientRule.getDefaultValue());
            clientRule.run();
            ClientRuleHelper.writeSaveFile();
        });
    }
}
