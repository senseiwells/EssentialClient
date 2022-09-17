package me.senseiwells.essentialclient.gui.entries;

import me.senseiwells.essentialclient.gui.RulesScreen;
import me.senseiwells.essentialclient.gui.config.ListScreen;
import me.senseiwells.essentialclient.utils.interfaces.Rule;
import me.senseiwells.essentialclient.utils.render.Texts;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;

public class ListListEntry extends BaseListEntry<ButtonWidget> {
	public ListListEntry(Rule.ListRule rule, MinecraftClient client, RulesScreen rulesScreen) {
		super(rule, client, rulesScreen, () -> {
			return new ButtonWidget(0, 0, 100, 20, Texts.EDIT_LIST, buttonWidget -> {
				client.setScreen(new ListScreen(Texts.EDITING_LIST.generate(rule.getName()), rulesScreen, rule));
			});
		});
	}
}
