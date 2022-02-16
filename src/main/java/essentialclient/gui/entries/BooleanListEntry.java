package essentialclient.gui.entries;

import essentialclient.clientrule.entries.BooleanClientRule;
import essentialclient.gui.rulescreen.RulesScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;

import static essentialclient.utils.render.Texts.FALSE;
import static essentialclient.utils.render.Texts.TRUE;

public class BooleanListEntry extends BaseListEntry<ButtonWidget> {
	public BooleanListEntry(BooleanClientRule clientRule, MinecraftClient client, RulesScreen rulesScreen) {
		super(clientRule, client, rulesScreen, () -> {
			return new ButtonWidget(0, 0, 100, 20, clientRule.getValue() ? TRUE : FALSE, buttonWidget -> {
				clientRule.invertBoolean();
				buttonWidget.setMessage(clientRule.getValue() ? TRUE : FALSE);
			});
		});
		this.setResetButton(buttonWidget -> {
			this.clientRule.resetToDefault();
			this.editButton.setMessage(clientRule.getDefaultValue() ? TRUE : FALSE);
		});
	}
}
