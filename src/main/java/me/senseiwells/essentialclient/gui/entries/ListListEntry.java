package me.senseiwells.essentialclient.gui.entries;

import me.senseiwells.essentialclient.gui.config.ListScreen;
import me.senseiwells.essentialclient.gui.RulesScreen;
import me.senseiwells.essentialclient.utils.interfaces.Rule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.LiteralText;

public class ListListEntry extends BaseListEntry<ButtonWidget>{
    public ListListEntry(Rule.ListRule rule, MinecraftClient client, RulesScreen rulesScreen) {
        super(rule, client, rulesScreen, () -> {
            return new ButtonWidget(0, 0, 100, 20, new LiteralText("Edit List"), buttonWidget -> {
                client.setScreen(new ListScreen(new LiteralText("Editing List: " + rule.getName()), rulesScreen, rule));
            });
        });
    }
}