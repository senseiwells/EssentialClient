package me.senseiwells.essentialclient.gui.entries;

import com.google.common.collect.ImmutableList;
import me.senseiwells.essentialclient.gui.config.ConfigListWidget;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import me.senseiwells.essentialclient.utils.render.RenderContextWrapper;
import me.senseiwells.essentialclient.utils.render.Texts;
import net.minecraft.client.gui.widget.ButtonWidget;

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
	public void render(RenderContextWrapper wrapper, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
		int fontX = (x + entryWidth) / 2;
		int fontY = (y + entryHeight / 2 - 9 / 2);
		wrapper.drawCenteredTextWithShadow(EssentialUtils.getClient().textRenderer, Texts.literal(this.name), fontX, fontY, 16777215);
	}
}
