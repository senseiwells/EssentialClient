package me.senseiwells.essentialclient.gui.entries;

import com.google.common.collect.ImmutableList;
import me.senseiwells.essentialclient.clientrule.entries.ClientRule;
import me.senseiwells.essentialclient.feature.CarpetClient;
import me.senseiwells.essentialclient.gui.ConfigListWidget;
import me.senseiwells.essentialclient.gui.rulescreen.RulesScreen;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import me.senseiwells.essentialclient.utils.render.RuleWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class BaseListEntry<T extends ClickableWidget> extends ConfigListWidget.Entry {
	protected final ClientRule<?> clientRule;
	protected final RulesScreen rulesScreen;
	protected final MinecraftClient client;
	protected final String ruleName;
	protected final RuleWidget ruleWidget;
	protected final T editButton;
	protected final ButtonWidget resetButton;
	private Consumer<ButtonWidget> resetConsumer;


	public BaseListEntry(ClientRule<?> clientRule, MinecraftClient client, RulesScreen rulesScreen, Supplier<T> editButton) {
		this.clientRule = clientRule;
		this.client = client;
		this.rulesScreen = rulesScreen;
		this.ruleName = clientRule.getName();
		this.ruleWidget = new RuleWidget(this.ruleName, 190, 15);
		this.editButton = editButton.get();
		this.resetButton = new ButtonWidget(0, 0, 50, 20, new TranslatableText("controls.reset"), button -> {
			this.resetConsumer.accept(button);
		});
		this.resetConsumer = buttonWidget -> {
			this.clientRule.resetToDefault();
			this.editButton.setMessage(new LiteralText(this.clientRule.getDefaultValue().toString()));
		};
		this.checkDisabled();
	}

	protected void setResetButton(Consumer<ButtonWidget> consumer) {
		this.resetConsumer = consumer;
	}

	protected void renderEditButton(MatrixStack matrices, int x, int y, int mouseX, int mouseY, float delta) {
		this.editButton.x = x + 180;
		this.editButton.y = y;
		this.editButton.render(matrices, mouseX, mouseY, delta);
	}

	protected void checkDisabled() {
		if (this.cannotEdit()) {
			this.children().forEach(child -> {
				child.active = false;
			});
		}
	}

	protected boolean cannotEdit() {
		return this.rulesScreen.isServerScreen() && !EssentialUtils.getClient().isInSingleplayer() && !(CarpetClient.INSTANCE.isServerCarpet() && EssentialUtils.playerHasOp());
	}

	protected boolean isRuleWidgetHovered(double mouseX, double mouseY) {
		return this.ruleWidget.isHovered((int) mouseX, (int) mouseY) && mouseY > 32 && mouseY < this.rulesScreen.height - 32;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (button == 0 && this.isRuleWidgetHovered(mouseX, mouseY)) {
			this.ruleWidget.toggle();
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public final void render(MatrixStack matrices, int index, int y, int x, int width, int height, int mouseX, int mouseY, boolean hovering, float delta) {
		TextRenderer font = this.client.textRenderer;
		float fontX = (float) (x + 90 - ConfigListWidget.length);
		float fontY = (float) (y + height / 2 - 9 / 2);
		this.ruleWidget.setPos(x - 50, y + 2);
		this.ruleWidget.drawRule(matrices, font, fontX, fontY, 16777215);
		if (this.isRuleWidgetHovered(mouseX, mouseY)) {
			List<OrderedText> lines = new ArrayList<>();
			Text nameText = new LiteralText(this.ruleName).formatted(Formatting.GOLD);
			lines.add(nameText.asOrderedText());
			String info;
			if (this.ruleWidget.isToggled() && this.clientRule.getOptionalInfo() != null) {
				info = "§3Extra Info:§r\n" + this.clientRule.getOptionalInfo();
			}
			else {
				info = this.clientRule.getDescription();
			}
			lines.addAll(this.client.textRenderer.wrapLines(StringVisitable.plain(info.replace("\r", "")), 220));
			this.rulesScreen.setTooltip(lines);
		}
		this.resetButton.x = x + 290;
		this.resetButton.y = y;
		this.resetButton.active = this.clientRule.isNotDefault();
		this.resetButton.render(matrices, mouseX, mouseY, delta);

		this.renderEditButton(matrices, x, y, mouseX, mouseY, delta);
	}

	@Override
	public List<ClickableWidget> children() {
		return this.selectableChildren();
	}

	@Override
	public List<ClickableWidget> selectableChildren() {
		return ImmutableList.of(this.editButton, this.resetButton);
	}

	@Override
	public void updateEntryOnClose() { }
}
