package me.senseiwells.essentialclient.gui;

import me.senseiwells.essentialclient.EssentialClient;
import me.senseiwells.essentialclient.feature.CarpetClient;
import me.senseiwells.essentialclient.gui.config.ConfigListWidget;
import me.senseiwells.essentialclient.gui.entries.BaseListEntry;
import me.senseiwells.essentialclient.rule.ClientRules;
import me.senseiwells.essentialclient.rule.game.VanillaGameRules;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import me.senseiwells.essentialclient.utils.interfaces.Rule;
import me.senseiwells.essentialclient.utils.render.ChildScreen;
import me.senseiwells.essentialclient.utils.render.Texts;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;

public abstract class RulesScreen extends ChildScreen {
	private final List<TextFieldWidget> textFields;
	private List<OrderedText> tooltip;
	private ConfigListWidget widget;
	private TextFieldWidget searchBox;
	private boolean invalid;
	private boolean isEmpty;

	public RulesScreen(Text screenName, Screen parent) {
		super(screenName, parent);
		this.textFields = new ArrayList<>();
	}

	public String getSearchBoxText() {
		return this.searchBox.getText();
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

	public void refreshScroll() {
		this.widget.setScrollAmount(0);
	}

	public abstract Collection<? extends Rule<?>> getRules();

	public Comparator<BaseListEntry<?>> entryComparator() {
		return Comparator.comparing(a -> a.getRule().getName());
	}

	public boolean canModify() {
		return true;
	}

	public boolean shouldCategorise() {
		return ClientRules.DISPLAY_RULE_TYPE.getValue().equals("Categories");
	}

	@Override
	protected void init() {
		if (this.client == null) {
			return;
		}
		this.searchBox = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, 22, 200, 15, Texts.EMPTY);
		this.searchBox.setChangedListener(s -> {
			this.refreshScroll();
			this.refreshRules(s);
		});
		this.widget = new ConfigListWidget(this, this.client, this.searchBox.getText());
		this.addSelectableChild(this.widget);
		this.addDrawableChild(this.searchBox);
		this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, this.height - 27, 200, 20, Texts.DONE, buttonWidget -> this.close()));
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
	public void close() {
		if (this.client != null) {
			this.widget.updateAllEntriesOnClose();
		}
		super.close();
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		this.searchBox.setTextFieldFocused(false);
		this.widget.unFocusAll();
		return super.mouseClicked(mouseX, mouseY, button);
	}

	public static RulesScreen getClientRulesScreen(Screen parent) {
		return new RulesScreen(Texts.CLIENT_SCREEN, parent) {
			@Override
			public Collection<? extends Rule<?>> getRules() {
				return ClientRules.getClientRules();
			}
		};
	}

	public static RulesScreen getCarpetRulesScreen(Screen parent) {
		return new RulesScreen(Texts.SERVER_SCREEN, parent) {
			@Override
			public Collection<? extends Rule<?>> getRules() {
				return CarpetClient.INSTANCE.getCurrentCarpetRules();
			}

			@Override
			public boolean canModify() {
				return EssentialUtils.getClient().isInSingleplayer() || (CarpetClient.INSTANCE.isServerCarpet() && EssentialUtils.playerHasOp());
			}
		};
	}

	public static RulesScreen getGameRulesScreen(Screen parent) {
		return new RulesScreen(Texts.GAME_RULE_SCREEN, parent) {
			@Override
			public Collection<? extends Rule<?>> getRules() {
				return VanillaGameRules.getGameRules();
			}

			@Override
			public boolean canModify() {
				return EssentialClient.GAME_RULE_NET_HANDLER.canModifyRules();
			}

			@Override
			public boolean shouldCategorise() {
				return true;
			}
		};
	}
}
