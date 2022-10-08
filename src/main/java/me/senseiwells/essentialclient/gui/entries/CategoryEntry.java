package me.senseiwells.essentialclient.gui.entries;

import com.google.common.collect.ImmutableList;
import me.senseiwells.essentialclient.gui.config.ConfigListWidget;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.util.math.MatrixStack;

import java.util.List;

public class CategoryEntry extends ConfigListWidget.Entry {
	private final String name;

	public CategoryEntry(String name) {
		this.name = name;
	}

	@Override
	public List<? extends Selectable> selectableChildren() {
		return ImmutableList.of();
	}

	@Override
	public List<? extends Element> children() {
		return ImmutableList.of();
	}

	@Override
	public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
		int fontX = (x + entryWidth) / 2;
		int fontY = (y + entryHeight / 2 - 9 / 2);
		DrawableHelper.drawCenteredText(matrices, EssentialUtils.getClient().textRenderer, this.name, fontX, fontY, 16777215);
	}
}
