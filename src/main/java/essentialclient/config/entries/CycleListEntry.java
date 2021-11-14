package essentialclient.config.entries;

import essentialclient.config.clientrule.ClientRuleHelper;
import essentialclient.config.clientrule.ClientRules;
import essentialclient.config.rulescreen.ClientRulesScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.LiteralText;

@Environment(EnvType.CLIENT)
public class CycleListEntry extends BaseListEntry {

    public CycleListEntry(final ClientRules settings, final MinecraftClient client, final ClientRulesScreen gui) {
        super(settings, client, gui);
        this.editButton = new ButtonWidget(0, 0, 100, 20, new LiteralText(settings.getString()), (buttonWidget) -> {
            settings.cycleValues();
            buttonWidget.setMessage(new LiteralText(settings.getString()));
            ClientRuleHelper.writeSaveFile();
            ClientRuleHelper.executeOnChange(client, settings, gui);
        });
        this.resetButton = new ButtonWidget(0, 0, 50, 20, new LiteralText(I18n.translate("controls.reset")), (buttonWidget) -> {
            ClientRuleHelper.executeOnChange(client, settings, gui);
            settings.setValue(settings.defaultValue);
            ClientRuleHelper.writeSaveFile();
            this.editButton.setMessage(new LiteralText(settings.defaultValue));
        });
    }
}
