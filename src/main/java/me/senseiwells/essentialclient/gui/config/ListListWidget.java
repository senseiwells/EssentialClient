package me.senseiwells.essentialclient.gui.config;

import com.google.common.collect.ImmutableList;
import me.senseiwells.essentialclient.gui.entries.AbstractListEntry;
import me.senseiwells.essentialclient.utils.render.WidgetHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class ListListWidget extends ElementListWidget<ListListWidget.Entry> {
	private final ListScreen listScreen;

	public ListListWidget(ListScreen listScreen, MinecraftClient client) {
		super(client, listScreen.width + 45, listScreen.height, 43, listScreen.height - 32, 20);
		this.listScreen = listScreen;
		this.reloadEntries();
	}

	public void reloadEntries() {
		this.clearEntries();

		List<String> values = this.listScreen.getValues();

		if (values.isEmpty()) {
			this.addEntry(new Entry(this.client, 1, "", this.listScreen.getRule().getMaxLength()));
		}

		for (int index = 0; index < values.size(); index++) {
			this.addEntry(new Entry(this.client, index + 1, values.get(index), this.listScreen.getRule().getMaxLength()));
		}
	}

	public void unFocusAll() {
		this.children().stream().map(e -> e.textField).forEach(t -> t.setFocused(false));
	}

	public void tick() {

	}

	public List<String> getTextValues() {
		List<String> textValues = new ArrayList<>();
		for (Entry entry : this.children()) {
			textValues.add(entry.textField.getText());
		}
		return textValues;
	}

	@Override
	protected int getScrollbarPositionX() {
		return this.width / 2 + this.getRowWidth() / 2 + 4;
	}

	@Override
	public int getRowWidth() {
		return 360;
	}

	public class Entry extends AbstractListEntry<Entry> {
		private final MinecraftClient client;
		private final TextFieldWidget textField;
		private final ButtonWidget addButton;
		private final ButtonWidget removeButton;
		private final int index;

		public Entry(MinecraftClient client, int index, String value, int maxLength) {
			this.client = client;

			this.textField = new TextFieldWidget(client.textRenderer, 0, 0, 150, 14, Text.literal(value));
			this.textField.setMaxLength(maxLength);
			this.textField.setText(value);

			this.addButton = WidgetHelper.newButton(0, 0, 20, 20, Text.of("+"), button -> {
				ListListWidget.this.listScreen.saveEntries();
				ListListWidget.this.listScreen.modify(l -> l.add(index, ""));
				ListListWidget.this.reloadEntries();
			});
			this.removeButton = WidgetHelper.newButton(0, 0, 20, 20, Text.of("-"), button -> {
				ListListWidget.this.listScreen.saveEntries();
				ListListWidget.this.listScreen.modify(l -> l.remove(index - 1));
				ListListWidget.this.reloadEntries();
			});

			this.index = index;
		}

		@Override
		public boolean charTyped(char chr, int keyCode) {
			return this.textField.charTyped(chr, keyCode);
		}

		@Override
		public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
			if (keyCode == GLFW.GLFW_KEY_ENTER) {
				this.textField.setFocused(false);
				return true;
			}
			return super.keyPressed(keyCode, scanCode, modifiers) || this.textField.keyPressed(keyCode, scanCode, modifiers);
		}


		@Override
		public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float delta) {
			TextRenderer font = this.client.textRenderer;
			int fontX = x + 90 - 100;
			int fontY = y + height / 2 - 9 / 2;

			context.drawTextWithShadow(font, Text.literal(String.valueOf(this.index)), fontX, fontY, 0xFFFFFF);

			WidgetHelper.setPosition(this.textField, x + 65, y);
			WidgetHelper.setPosition(this.addButton, x + 235, y);
			WidgetHelper.setPosition(this.removeButton, x + 255, y);

			this.textField.render(context, mouseX, mouseY, delta);
			this.addButton.render(context, mouseX, mouseY, delta);
			this.removeButton.render(context, mouseX, mouseY, delta);
		}

		@Override
		public List<ClickableWidget> children() {
			return ImmutableList.of(this.textField, this.addButton, this.removeButton);
		}

		@Override
		public List<ClickableWidget> selectableChildren() {
			return this.children();
		}
	}
}
