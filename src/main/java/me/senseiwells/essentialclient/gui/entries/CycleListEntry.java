package me.senseiwells.essentialclient.gui.entries;

import me.senseiwells.essentialclient.clientrule.entries.CycleClientRule;
import me.senseiwells.essentialclient.gui.rulescreen.RulesScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.LiteralText;

public class CycleListEntry extends BaseListEntry<ButtonWidget> {
	public CycleListEntry(CycleClientRule clientRule, MinecraftClient client, RulesScreen rulesScreen) {
		super(clientRule, client, rulesScreen, () -> {
			return new ButtonWidget(0, 0, 100, 20, new LiteralText(clientRule.getValue()), buttonWidget -> {
				clientRule.cycleValues();
				buttonWidget.setMessage(new LiteralText(clientRule.getValue()));
			});
		});
	}
}
