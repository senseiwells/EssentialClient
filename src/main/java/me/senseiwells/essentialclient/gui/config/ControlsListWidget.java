package me.senseiwells.essentialclient.gui.config;

import com.google.common.collect.ImmutableList;
import me.senseiwells.essentialclient.feature.keybinds.ClientKeyBind;
import me.senseiwells.essentialclient.feature.keybinds.ClientKeyBinds;
import me.senseiwells.essentialclient.gui.entries.AbstractListEntry;
import me.senseiwells.essentialclient.utils.render.Texts;
import me.senseiwells.essentialclient.utils.render.WidgetHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
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
			this.addEntry(new CategoryEntry(Text.translatable(category)));
			for (ClientKeyBind keyBind : keys) {
				Text text = Text.translatable(keyBind.getName());
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

	abstract static class Entry extends AbstractListEntry<Entry> { }

	class CategoryEntry extends Entry {
		private final int textWidth;
		final Text text;

		CategoryEntry(Text text) {
			this.textWidth = ControlsListWidget.this.client.textRenderer.getWidth(text);
			this.text = text;
		}

		@Override
		public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			context.drawTextWithShadow(ControlsListWidget.this.client.textRenderer, this.text, (int) (ControlsListWidget.this.controlsScreen.width / 2.0F - this.textWidth / 2.0F), y + entryHeight - 9 - 1, 0xFFFFFF);
		}

		@Override
		public List<ButtonWidget> selectableChildren() {
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
			this.editButton = WidgetHelper.newButton(0, 0, 75, 20, text, button -> {
				ControlsListWidget.this.controlsScreen.setFocusedKeyBinding(this.keyBind);
			});
			this.resetButton = WidgetHelper.newButton(0, 0, 50, 20, Texts.RESET, button -> {
				this.keyBind.clearKey();
				this.keyBind.resetKey();
			});
			this.resetButton.active = !this.keyBind.isDefault();
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
		public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			boolean focused = ControlsListWidget.this.controlsScreen.isBindingFocused(this.keyBind);
			float width = x + 90 - ControlsListWidget.this.maxKeyNameLength;
			context.drawTextWithShadow(ControlsListWidget.this.client.textRenderer, this.bindingText, (int) width, (int) (y + entryHeight / 2.0F - 9 / 2.0F), 0xFFFFFF);
			WidgetHelper.setPosition(this.resetButton, x + 190, y);
			this.resetButton.active = !this.keyBind.isDefault();
			this.resetButton.render(context, mouseX, mouseY, tickDelta);
			WidgetHelper.setPosition(this.editButton, x + 105, y);

			MutableText editMessage = Text.literal(this.keyBind.getDisplay());
			int textWidth = ControlsListWidget.this.client.textRenderer.getWidth(editMessage);
			if (textWidth > 70) {
				editMessage = Text.literal("...");
			}

			if (focused) {
				this.editButton.setMessage(
					Text.literal("> ").append(editMessage.formatted(Formatting.YELLOW)).append(" <").formatted(Formatting.YELLOW)
				);
			} else {
				this.editButton.setMessage(editMessage);
			}

			this.editButton.render(context, mouseX, mouseY, tickDelta);

			if (this.editButton.isMouseOver(mouseX, mouseY)) {
				ControlsListWidget.this.controlsScreen.setHoveredKeyBinding(this.keyBind);
			}
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
