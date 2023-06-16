package me.senseiwells.essentialclient.gui.entries;

import me.senseiwells.essentialclient.utils.render.RenderContextWrapper;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.gui.widget.ElementListWidget;

//#if MC >= 12000
import net.minecraft.client.gui.DrawContext;
//#else
//$$import net.minecraft.client.util.math.MatrixStack;
//#endif

public abstract class AbstractListEntry<E extends AbstractListEntry<E>> extends ElementListWidget.Entry<E> implements ParentElement {
	@Override
	public final void render(
		//#if MC >= 1200
		DrawContext context,
		//#else
		//$$MatrixStack context,
		//#endif,
		int index,
		int y,
		int x,
		int entryWidth,
		int entryHeight,
		int mouseX,
		int mouseY,
		boolean hovered,
		float tickDelta
	) {
		this.render(new RenderContextWrapper(context), index, y, x, entryWidth, entryHeight, mouseX, mouseY, hovered, tickDelta);
	}

	public abstract void render(RenderContextWrapper wrapper, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta);
}
