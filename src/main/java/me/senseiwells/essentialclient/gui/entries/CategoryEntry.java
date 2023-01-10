package me.senseiwells.essentialclient.gui.entries;

import com.google.common.collect.ImmutableList;
import me.senseiwells.essentialclient.gui.config.ConfigListWidget;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;

import java.util.List;

public class CategoryEntry extends ConfigListWidget.Entry {
	private final String name;

	public CategoryEntry(String name) {
		this.name = name;
	}

	//#if MC >= 11700
	@Override
	public List<ButtonWidget> selectableChildren() {
		return ImmutableList.of();
	}
	//#endif

	@Override
	public List<ButtonWidget> children() {
		return ImmutableList.of();
	}

	@Override
	public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
		int fontX = (x + entryWidth) / 2;
		int fontY = (y + entryHeight / 2 - 9 / 2);
		DrawableHelper.drawCenteredText(matrices, EssentialUtils.getClient().textRenderer, this.name, fontX, fontY, 16777215);
	}
}
