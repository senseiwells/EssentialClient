package essentialclient.gui.entries;

import carpet.settings.ParsedRule;
import essentialclient.feature.clientrule.ClientRuleHelper;
import essentialclient.feature.clientrule.ClientRules;
import essentialclient.gui.rulescreen.ClientRulesScreen;
import essentialclient.gui.rulescreen.ServerRulesScreen;
import essentialclient.utils.carpet.CarpetSettingsServerNetworkHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.LiteralText;

@Environment(EnvType.CLIENT)
public class BooleanListEntry extends BaseListEntry {

    public BooleanListEntry(final ParsedRule<?> settings, MinecraftClient client, ServerRulesScreen gui) {
        super(settings, client, gui);
        this.editButton = new ButtonWidget(0, 0, 100, 20, new LiteralText(settings.getAsString().equals("true") ? "§2true" : "§4false"), (buttonWidget) -> {
            String invertedBoolean = buttonWidget.getMessage().getString().equals("§2true") ? "false" : "true";
            CarpetSettingsServerNetworkHandler.ruleChange(settings.name, invertedBoolean, client);
            buttonWidget.setMessage(new LiteralText(invertedBoolean.equals("true") ? "§2true" : "§4false"));
        });
        this.resetButton = new ButtonWidget(0, 0, 50, 20, new LiteralText(I18n.translate("controls.reset")), (buttonWidget) -> {
            CarpetSettingsServerNetworkHandler.ruleChange(settings.name, settings.defaultAsString, client);
            this.editButton.setMessage(new LiteralText(settings.defaultAsString.equals("true") ? "§2true" : "§4false"));
        });
    }

    public BooleanListEntry(final ClientRules settings, MinecraftClient client, ClientRulesScreen gui) {
        super(settings, client, gui);
        this.editButton = new ButtonWidget(0, 0, 100, 20, new LiteralText(settings.getString().equals("true") ? "§2true" : "§4false"), (buttonWidget) -> {
            settings.invertBoolean();
            buttonWidget.setMessage(new LiteralText(settings.getString().equals("true") ? "§2true" : "§4false"));
            ClientRuleHelper.writeSaveFile();
            ClientRuleHelper.executeOnChange(client, settings, gui);
        });
        this.resetButton = new ButtonWidget(0, 0, 50, 20, new LiteralText(I18n.translate("controls.reset")), (buttonWidget) -> {
            settings.setValue(settings.defaultValue);
            ClientRuleHelper.writeSaveFile();
            this.editButton.setMessage(new LiteralText(settings.defaultValue.equals("true") ? "§2true" : "§4false"));
        });
    }
}
