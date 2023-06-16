package me.senseiwells.essentialclient.utils.render;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

//#if MC < 12000
//$$import net.minecraft.client.gui.DrawableHelper;
//#endif

public class RenderContextWrapper {
	//#if MC >= 12000
	private final DrawContext context;
	//#else
	//$$private final MatrixStack context;
	//#endif

	//#if MC >= 12000
	public RenderContextWrapper(DrawContext context) {
		this.context = context;
	}
	//#else
	//$$public ContextWrapper(MatrixStack context) {
	//$$	this.context = context;
	//$$}
	//#endif

	//#if MC >= 12000
	public DrawContext getContext() {
		//#else
		//$$public MatrixStack getContext() {
		//#endif
		return this.context;
	}

	public MatrixStack getMatrices() {
		//#if MC >= 12000
		return this.context.getMatrices();
		//#else
		//$$return this.context;
		//#endif
	}

	public void drawCenteredTextWithShadow(TextRenderer textRenderer, Text text, int centerX, int y, int color) {
		//#if MC >= 12000
		this.context.drawCenteredTextWithShadow(textRenderer, text, centerX, y, color);
		//#else
		//$$var orderedText = text.asOrderedText();
		//$$textRenderer.drawWithShadow(this.getContext(), orderedText, (float) (centerX - textRenderer.getWidth(orderedText) / 2), (float) y, color);
		//#endif
	}

	public void drawTextWithShadow(TextRenderer textRenderer, Text text, int x, int y, int color) {
		//#if MC >= 12000
		this.context.drawTextWithShadow(textRenderer, text, x, y, color);
		//#else
		//$$textRenderer.drawWithShadow(this.getMatrices(), text, (float) x, (float) y, color);
		//#endif
	}

	public void fillGradient(int startX, int startY, int endX, int endY, int colorStart, int colorEnd) {
		//#if MC >= 12000
		this.context.fillGradient(startX, startY, endX, endY, colorStart, colorEnd);
		//#else
		//$$DrawableHelper.fillGradient(this.getMatrices(), startX, startY, endX, endY, colorStart, colorEnd);
		//#endif
	}
}
