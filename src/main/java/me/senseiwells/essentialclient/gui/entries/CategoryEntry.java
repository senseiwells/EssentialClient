package me.senseiwells.essentialclient.gui.entries;

import com.google.common.collect.ImmutableList;
import me.senseiwells.essentialclient.gui.config.ConfigListWidget;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.List;

public class CategoryEntry extends ConfigListWidget.Entry {
	private final String name;

	public CategoryEntry(String name) {
		this.name = name;
	}

	@Override
	public List<ButtonWidget> selectableChildren() {
		return ImmutableList.of();
	}

	@Override
	public List<ButtonWidget> children() {
		return ImmutableList.of();
	}

	@Override
	public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
		int fontX = (x + entryWidth) / 2;
		int fontY = (y + entryHeight / 2 - 9 / 2);
		context.drawCenteredTextWithShadow(EssentialUtils.getClient().textRenderer, Text.literal(this.name), fontX, fontY, 16777215);
	}
}
