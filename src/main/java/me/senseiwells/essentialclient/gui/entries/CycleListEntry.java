package me.senseiwells.essentialclient.gui.entries;

import me.senseiwells.essentialclient.gui.RulesScreen;
import me.senseiwells.essentialclient.utils.interfaces.Rule;
import me.senseiwells.essentialclient.utils.render.WidgetHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class CycleListEntry extends BaseListEntry<ButtonWidget> {
	public CycleListEntry(Rule.Cycle rule, MinecraftClient client, RulesScreen rulesScreen) {
		super(rule, client, rulesScreen, () -> {
			return WidgetHelper.newButton(0, 0, 100, 20, Text.literal(rule.getValue()), buttonWidget -> {
				rule.cycleValues();
				buttonWidget.setMessage(Text.literal(rule.getValue()));
			});
		});
	}
}
