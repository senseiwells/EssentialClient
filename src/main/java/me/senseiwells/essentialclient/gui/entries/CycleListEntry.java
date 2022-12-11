package me.senseiwells.essentialclient.gui.entries;

import me.senseiwells.essentialclient.gui.RulesScreen;
import me.senseiwells.essentialclient.utils.interfaces.Rule;
import me.senseiwells.essentialclient.utils.render.WidgetHelper;
import me.senseiwells.essentialclient.utils.render.Texts;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;

public class CycleListEntry extends BaseListEntry<ButtonWidget> {
	public CycleListEntry(Rule.Cycle rule, MinecraftClient client, RulesScreen rulesScreen) {
		super(rule, client, rulesScreen, () -> {
			return WidgetHelper.newButton(0, 0, 100, 20, Texts.literal(rule.getValue()), buttonWidget -> {
				rule.cycleValues();
				buttonWidget.setMessage(Texts.literal(rule.getValue()));
			});
		});
	}
}
