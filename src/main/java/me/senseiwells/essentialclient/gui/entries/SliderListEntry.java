package me.senseiwells.essentialclient.gui.entries;

import me.senseiwells.essentialclient.gui.RulesScreen;
import me.senseiwells.essentialclient.utils.interfaces.Rule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class SliderListEntry extends BaseListEntry<SliderListEntry.RuleSliderWidget> {
	public SliderListEntry(Rule.Slider<?> rule, MinecraftClient client, RulesScreen rulesScreen) {
		super(rule, client, rulesScreen, () -> {
			return new RuleSliderWidget(rule, 0, 0, 100, 20, new LiteralText(rule.getValue().toString()), rule.getPercentage());
		});
		this.setResetButton(buttonWidget -> {
			this.rule.resetToDefault();
			this.editButton.setValueQuietly(rule.getPercentage());
		});
	}

	public static class RuleSliderWidget extends SliderWidget {
		private final Rule.Slider<?> slider;

		public RuleSliderWidget(Rule.Slider<?> slider, int x, int y, int width, int height, Text text, double value) {
			super(x, y, width, height, text, value);
			this.slider = slider;
		}

		protected void setValueQuietly(double percentage) {
			this.value = percentage;
			this.updateMessage();
		}

		@Override
		protected void updateMessage() {
			this.setMessage(new LiteralText(this.slider.getFormatted()));
		}

		@Override
		protected void applyValue() {
			this.slider.setFromPercentage(this.value);
		}
	}
}
