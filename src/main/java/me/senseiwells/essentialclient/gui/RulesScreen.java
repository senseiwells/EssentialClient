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
import me.senseiwells.essentialclient.utils.render.WidgetHelper;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public abstract class RulesScreen extends ChildScreen {
	private List<OrderedText> tooltip;
	private ConfigListWidget widget;
	private TextFieldWidget searchBox;
	private boolean invalid;
	private boolean isEmpty;

	public RulesScreen(Text screenName, Screen parent) {
		super(screenName, parent);
	}

	public String getSearchBoxText() {
		return this.searchBox.getText();
	}

	public void setCurrentTooltip(List<OrderedText> tooltip) {
		this.tooltip = tooltip;
	}

	public void setInvalid(boolean invalid) {
		this.invalid = invalid;
	}

	public void setEmpty(boolean isEmpty) {
		this.isEmpty = isEmpty;
	}

	public void refreshRules(String filter) {
		this.widget.reloadEntries(this, filter);
	}

	public void refreshScroll() {
		this.widget.setScrollAmount(0);
	}

	public abstract Collection<? extends Rule<?>> getRules(String filter);

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
		this.addDrawableChild(this.widget);
		this.addDrawableChild(this.searchBox);
		this.addDrawableChild(WidgetHelper.newButton(this.width / 2 - 100, this.height - 27, 200, 20, Texts.DONE, buttonWidget -> this.close()));
		// this.setInitialFocus(this.searchBox);
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		this.renderBackgroundTexture(context);
		this.widget.render(context, mouseX, mouseY, delta);
		super.render(context, mouseX, mouseY, delta);
		context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 8, 0xFFFFFF);
		if (this.invalid) {
			String text = this.isEmpty ? "You can't leave a field empty!" : "Invalid value!";
			context.fill(8, 9, 20 + this.textRenderer.getWidth(text), 14 + this.textRenderer.fontHeight, 0x68000000);
			context.drawTextWithShadow(this.textRenderer, Text.literal(text), 18, 12, 16733525);
		}
		if (this.tooltip != null) {
			context.drawOrderedTooltip(this.textRenderer, this.tooltip, mouseX, mouseY);
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
		this.searchBox.setFocused(false);
		this.widget.unFocusAll();
		return super.mouseClicked(mouseX, mouseY, button);
	}

	public static RulesScreen getClientRulesScreen(Screen parent) {
		return new RulesScreen(Texts.CLIENT_SCREEN, parent) {
			@Override
			public Collection<? extends Rule<?>> getRules(String filter) {
				return ClientRules.getClientRules();
			}
		};
	}

	public static RulesScreen getCarpetRulesScreen(Screen parent) {
		return new RulesScreen(Texts.SERVER_SCREEN, parent) {
			@Override
			public Collection<? extends Rule<?>> getRules(String filter) {
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
			public Collection<? extends Rule<?>> getRules(String filter) {
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
