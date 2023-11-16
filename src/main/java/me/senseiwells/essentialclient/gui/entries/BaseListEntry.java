package me.senseiwells.essentialclient.gui.entries;

import com.google.common.collect.ImmutableList;
import me.senseiwells.essentialclient.gui.RulesScreen;
import me.senseiwells.essentialclient.gui.config.ConfigListWidget;
import me.senseiwells.essentialclient.utils.interfaces.Rule;
import me.senseiwells.essentialclient.utils.render.RuleWidget;
import me.senseiwells.essentialclient.utils.render.Texts;
import me.senseiwells.essentialclient.utils.render.WidgetHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class BaseListEntry<T extends ClickableWidget> extends ConfigListWidget.Entry {
	protected final Rule<?> rule;
	protected final RulesScreen rulesScreen;
	protected final MinecraftClient client;
	protected final String ruleName;
	protected final RuleWidget ruleWidget;
	protected final T editButton;
	protected final ButtonWidget resetButton;
	private Consumer<ButtonWidget> resetConsumer;

	public BaseListEntry(Rule<?> rule, MinecraftClient client, RulesScreen rulesScreen, Supplier<T> editButton) {
		this.rule = rule;
		this.client = client;
		this.rulesScreen = rulesScreen;
		this.ruleName = rule.getName();
		this.ruleWidget = new RuleWidget(this.ruleName, 190, 15);
		this.editButton = editButton.get();
		this.resetButton = WidgetHelper.newButton(0, 0, 50, 20, Texts.RESET, button -> {
			this.resetConsumer.accept(button);
		});
		this.resetConsumer = buttonWidget -> {
			this.rule.resetToDefault();
			this.editButton.setMessage(Text.literal(this.rule.getDefaultValue().toString()));
		};
		this.checkDisabled();
	}

	protected void setResetButton(Consumer<ButtonWidget> consumer) {
		this.resetConsumer = consumer;
	}

	protected void renderEditButton(DrawContext context, int x, int y, int mouseX, int mouseY, float delta) {
		this.editButton.setPosition(x + 180, y);
		this.editButton.render(context, mouseX, mouseY, delta);
	}

	protected void checkDisabled() {
		if (this.cannotEdit() || !this.rule.isAvailable() || !this.rule.changeable()) {
			this.children().forEach(child -> {
				child.active = false;
			});
		}
	}

	protected boolean cannotEdit() {
		return !this.rulesScreen.canModify();
	}

	protected boolean isRuleWidgetHovered(double mouseX, double mouseY) {
		return this.ruleWidget.isHovered((int) mouseX, (int) mouseY) && mouseY > 32 && mouseY < this.rulesScreen.height - 32;
	}

	public Rule<?> getRule() {
		return this.rule;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (button == 0 && this.isRuleWidgetHovered(mouseX, mouseY)) {
			this.ruleWidget.toggle();
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public void render(DrawContext context, int index, int y, int x, int width, int height, int mouseX, int mouseY, boolean hovered, float delta) {
		TextRenderer font = this.client.textRenderer;
		float fontX = (float) (x + 90 - ConfigListWidget.LENGTH);
		float fontY = (float) (y + height / 2 - 9 / 2);
		this.ruleWidget.setPos(x - 50, y + 2);
		this.ruleWidget.drawRule(context, font, fontX, fontY, 16777215);
		if (this.isRuleWidgetHovered(mouseX, mouseY)) {
			List<OrderedText> lines = new ArrayList<>();
			Text nameText = Text.literal(this.ruleName).formatted(Formatting.GOLD);
			lines.add(nameText.asOrderedText());
			String info;
			if (this.ruleWidget.isToggled() && this.rule.getOptionalInfo() != null) {
				info = "§3Extra Info:§r\n" + this.rule.getOptionalInfo();
			} else {
				info = this.rule.getDescription();
			}
			if (info != null) {
				lines.addAll(this.client.textRenderer.wrapLines(StringVisitable.plain(info.replace("\r", "")), 220));
				this.rulesScreen.setCurrentTooltip(lines);
			}
		}
		this.resetButton.setPosition(x + 290, y);
		this.resetButton.active = this.rule.isAvailable() && this.rule.changeable() && this.rule.isNotDefault();
		this.resetButton.render(context, mouseX, mouseY, delta);

		this.renderEditButton(context, x, y, mouseX, mouseY, delta);
	}

	@Override
	public List<ClickableWidget> children() {
		return ImmutableList.of(this.editButton, this.resetButton);
	}

	@Override
	public List<ClickableWidget> selectableChildren() {
		return this.children();
	}
}
