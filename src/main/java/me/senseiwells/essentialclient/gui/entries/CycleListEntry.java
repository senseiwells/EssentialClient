package me.senseiwells.essentialclient.gui.entries;

import me.senseiwells.essentialclient.gui.rulescreen.RulesScreen;
import me.senseiwells.essentialclient.utils.interfaces.Rule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.LiteralText;

public class CycleListEntry extends BaseListEntry<ButtonWidget> {
	public CycleListEntry(Rule.Cycle rule, MinecraftClient client, RulesScreen rulesScreen) {
		super(rule, client, rulesScreen, () -> {
			return new ButtonWidget(0, 0, 100, 20, new LiteralText(rule.getValue()), buttonWidget -> {
				rule.cycleValues();
				buttonWidget.setMessage(new LiteralText(rule.getValue()));
			});
		});
	}
}
