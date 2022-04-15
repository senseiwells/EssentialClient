package me.senseiwells.essentialclient.gui.entries;

import me.senseiwells.essentialclient.gui.RulesScreen;
import me.senseiwells.essentialclient.utils.interfaces.Rule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;

import static me.senseiwells.essentialclient.utils.render.Texts.FALSE;
import static me.senseiwells.essentialclient.utils.render.Texts.TRUE;

public class BooleanListEntry extends BaseListEntry<ButtonWidget> {
	public BooleanListEntry(Rule.Bool rule, MinecraftClient client, RulesScreen rulesScreen) {
		super(rule, client, rulesScreen, () -> {
			return new ButtonWidget(0, 0, 100, 20, rule.getValue() ? TRUE : FALSE, buttonWidget -> {
				rule.invert();
				buttonWidget.setMessage(rule.getValue() ? TRUE : FALSE);
			});
		});
		this.setResetButton(buttonWidget -> {
			this.rule.resetToDefault();
			this.editButton.setMessage(rule.getDefaultValue() ? TRUE : FALSE);
		});
	}
}
