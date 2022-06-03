package me.senseiwells.essentialclient.gui.config;

import com.google.common.collect.ImmutableList;
import me.senseiwells.essentialclient.feature.keybinds.ClientKeyBind;
import me.senseiwells.essentialclient.feature.keybinds.ClientKeyBinds;
import me.senseiwells.essentialclient.utils.render.Texts;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.*;

public class ControlsListWidget extends ElementListWidget<ControlsListWidget.Entry> {
	private final ControlsScreen controlsScreen;
	private int maxKeyNameLength;

	public ControlsListWidget(MinecraftClient client, ControlsScreen controlsScreen) {
		super(client, controlsScreen.width + 45, controlsScreen.height, 43, controlsScreen.height - 32, 20);
		this.controlsScreen = controlsScreen;

		SortedMap<String, Set<ClientKeyBind>> sortedKeys = new TreeMap<>();
		for (ClientKeyBind keyBind : ClientKeyBinds.getAllKeyBinds()) {
			Set<ClientKeyBind> keyBinds = sortedKeys.computeIfAbsent(keyBind.getCategory(), n -> new TreeSet<>());
			keyBinds.add(keyBind);
		}

		sortedKeys.forEach((category, keys) -> {
			this.addEntry(new CategoryEntry(Texts.translatable(category)));
			for (ClientKeyBind keyBind : keys) {
				Text text = Texts.translatable(keyBind.getTranslationKey());
				int i = client.textRenderer.getWidth(text);
				if (i > this.maxKeyNameLength) {
					this.maxKeyNameLength = i;
				}
				this.addEntry(new KeyBindEntry(keyBind, text));
			}
		});

	}

	@Override
	protected int getScrollbarPositionX() {
		return super.getScrollbarPositionX() + 15;
	}

	@Override
	public int getRowWidth() {
		return super.getRowWidth() + 32;
	}

	abstract static class Entry extends ElementListWidget.Entry<ControlsListWidget.Entry> { }

	class CategoryEntry extends Entry {
		private final int textWidth;
		final Text text;

		CategoryEntry(Text text) {
			this.textWidth = ControlsListWidget.this.client.textRenderer.getWidth(text);
			this.text = text;
		}

		@Override
		public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			TextRenderer renderer = ControlsListWidget.this.client.textRenderer;
			renderer.draw(matrices, this.text, ControlsListWidget.this.controlsScreen.width / 2.0F - this.textWidth / 2.0F, y + entryHeight - 9 - 1, 0xFFFFFF);
		}

		@Override
		public List<? extends Selectable> selectableChildren() {
			return this.children();
		}

		@Override
		public List<ButtonWidget> children() {
			return ImmutableList.of();
		}
	}

	class KeyBindEntry extends Entry {
		private final ClientKeyBind keyBind;
		private final Text bindingText;
		private final ButtonWidget editButton;
		private final ButtonWidget resetButton;

		KeyBindEntry(ClientKeyBind keyBind, Text text) {
			this.keyBind = keyBind;
			this.bindingText = text;
			this.editButton = new ButtonWidget(0, 0, 75, 20, text, button -> {
				ControlsListWidget.this.controlsScreen.setFocusedKeyBinding(this.keyBind);
			});
			this.resetButton = new ButtonWidget(0, 0, 50, 20, Texts.RESET, button -> {
				this.keyBind.setBoundKey(this.keyBind.getDefaultKey());
			});
		}

		@Override
		public List<ButtonWidget> selectableChildren() {
			return this.children();
		}

		@Override
		public List<ButtonWidget> children() {
			return ImmutableList.of(this.editButton, this.resetButton);
		}

		@Override
		public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			boolean focused = ControlsListWidget.this.controlsScreen.isBindingFocused(this.keyBind);
			float width = x + 90 - ControlsListWidget.this.maxKeyNameLength;
			ControlsListWidget.this.client.textRenderer.draw(matrices, this.bindingText, width, y + entryHeight / 2.0F - 9 / 2.0F, 0xFFFFFF);
			this.resetButton.x = x + 190;
			this.resetButton.y = y;
			this.resetButton.active = !this.keyBind.isDefault();
			this.resetButton.render(matrices, mouseX, mouseY, tickDelta);
			this.editButton.x = x + 105;
			this.editButton.y = y;
			this.editButton.setMessage(this.keyBind.getBoundKeyLocalizedText());

			MutableText editMessage = this.editButton.getMessage().copy();
			if (!this.keyBind.isUnbound()) {
				for (KeyBinding binding : ControlsListWidget.this.client.options.keysAll) {
					if (!focused && binding != this.keyBind && this.keyBind.equals(binding)) {
						this.editButton.setMessage(editMessage.formatted(Formatting.RED));
					}
				}

			}
			if (focused) {
				this.editButton.setMessage(
					Texts.literal("> ").append(editMessage.formatted(Formatting.YELLOW)).append(" <").formatted(Formatting.YELLOW)
				);
			}

			this.editButton.render(matrices, mouseX, mouseY, tickDelta);
		}

		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button) {
			return this.editButton.mouseClicked(mouseX, mouseY, button) || this.resetButton.mouseClicked(mouseX, mouseY, button);
		}

		@Override
		public boolean mouseReleased(double mouseX, double mouseY, int button) {
			return this.editButton.mouseReleased(mouseX, mouseY, button) || this.resetButton.mouseReleased(mouseX, mouseY, button);
		}
	}
}
