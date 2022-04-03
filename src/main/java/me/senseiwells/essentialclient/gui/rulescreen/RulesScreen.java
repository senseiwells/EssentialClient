package me.senseiwells.essentialclient.gui.rulescreen;

import me.senseiwells.essentialclient.gui.ConfigListWidget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.OrderedText;

import java.util.ArrayList;
import java.util.List;

import static me.senseiwells.essentialclient.utils.render.Texts.CLIENT_SCREEN;
import static me.senseiwells.essentialclient.utils.render.Texts.SERVER_SCREEN;

public class RulesScreen extends Screen {
	private final Screen parent;
	private final List<TextFieldWidget> textFields;
	private final boolean isServerScreen;
	private List<OrderedText> tooltip;
	private ConfigListWidget widget;
	private TextFieldWidget searchBox;
	private boolean invalid;
	private boolean isEmpty;


	public RulesScreen(Screen parent, boolean isServerScreen) {
		super(isServerScreen ? SERVER_SCREEN : CLIENT_SCREEN);
		this.parent = parent;
		this.textFields = new ArrayList<>();
		this.isServerScreen = isServerScreen;
	}

	public String getSearchBoxText() {
		return this.searchBox.getText();
	}

	public boolean isServerScreen() {
		return this.isServerScreen;
	}

	public void setTooltip(List<OrderedText> tooltip) {
		this.tooltip = tooltip;
	}

	public void setInvalid(boolean invalid) {
		this.invalid = invalid;
	}

	public void setEmpty(boolean isEmpty) {
		this.isEmpty = isEmpty;
	}

	public void addTextField(TextFieldWidget textFieldWidget) {
		this.textFields.add(textFieldWidget);
	}

	public void refreshRules(String filter) {
		this.widget.reloadEntries(this, filter);
	}

	@Override
	protected void init() {
		if (this.client == null) {
			return;
		}
		this.searchBox = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, 22, 200, 15, LiteralText.EMPTY);
		this.searchBox.setChangedListener(this::refreshRules);
		this.widget = new ConfigListWidget(this, this.client, this.searchBox.getText());
		this.addDrawableChild(this.widget);
		this.addDrawableChild(this.searchBox);
		this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, this.height - 27, 200, 20, ScreenTexts.DONE, buttonWidget -> this.onClose()));
		this.setInitialFocus(this.searchBox);
	}

	@Override
	public void tick() {
		this.textFields.forEach(TextFieldWidget::tick);
		this.searchBox.tick();
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		this.widget.render(matrices, mouseX, mouseY, delta);
		super.render(matrices, mouseX, mouseY, delta);
		drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 8, 0xFFFFFF);
		if (this.invalid) {
			String text = this.isEmpty ? "You can't leave a field empty!" : "Invalid value!";
			this.fillGradient(matrices, 8, 9, 20 + this.textRenderer.getWidth(text), 14 + this.textRenderer.fontHeight, 0x68000000, 0x68000000);
			this.drawTexture(matrices, 10, 10, 0, 54, 3, 11);
			this.textRenderer.draw(matrices, text, 18, 12, 16733525);
		}
		if (this.tooltip != null) {
			this.renderOrderedTooltip(matrices, this.tooltip, mouseX, mouseY);
			this.tooltip = null;
		}
	}

	@Override
	public void onClose() {
		if (this.client != null) {
			this.widget.updateAllEntriesOnClose();
			this.client.setScreen(this.parent);
		}
	}
}
