package me.senseiwells.essentialclient.gui.config;

import me.senseiwells.essentialclient.gui.RulesScreen;
import me.senseiwells.essentialclient.utils.interfaces.Rule;
import me.senseiwells.essentialclient.utils.render.WidgetHelper;
import me.senseiwells.essentialclient.utils.render.ChildScreen;
import me.senseiwells.essentialclient.utils.render.Texts;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.List;

public class ListScreen extends ChildScreen.Typed<RulesScreen> {
	private final Rule.ListRule rule;
	private ListListWidget widget;

	public ListScreen(Text title, RulesScreen parent, Rule.ListRule rule) {
		super(title, parent);
		this.rule = rule;
	}

	public List<String> getValues() {
		return this.rule.getValue();
	}

	public void saveEntries() {
		List<String> values = this.widget.getTextValues();
		this.rule.setValue(values);
	}

	@Override
	protected void init() {
		if (this.client == null) {
			return;
		}
		this.widget = new ListListWidget(this, this.client);
		this.addSelectableChild(this.widget);
		this.addDrawableChild(WidgetHelper.newButton(this.width / 2 - 100, this.height - 27, 200, 20, Texts.DONE, buttonWidget -> this.close()));
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		this.widget.render(matrices, mouseX, mouseY, delta);
		super.render(matrices, mouseX, mouseY, delta);
		drawCenteredTextWithShadow(matrices, this.textRenderer, this.title, this.width / 2, 8, 0xFFFFFF);
	}

	@Override
	public void tick() {
		this.widget.tick();
	}

	@Override
	public void close() {
		this.saveEntries();
		super.close();
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		this.widget.unFocusAll();
		return super.mouseClicked(mouseX, mouseY, button);
	}

	public Rule.ListRule getRule() {
		return this.rule;
	}
}
