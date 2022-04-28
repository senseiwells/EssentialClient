package me.senseiwells.essentialclient.utils.clientscript;

import me.senseiwells.arucas.api.wrappers.ArucasFunction;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.impl.ArucasList;
import me.senseiwells.arucas.values.BooleanValue;
import me.senseiwells.arucas.values.ListValue;
import me.senseiwells.arucas.values.NumberValue;

/**
 * This is an abstract class intended to be
 * the parent of all different Shape wrapper
 * classes used to render shaped in clientscript
 */
@SuppressWarnings("unused")
public abstract class Shape {
	private int red;
	private int green;
	private int blue;
	private int alpha;
	private int outlineRed;
	private int outlineGreen;
	private int outlineBlue;
	private int outlineWidth;
	private boolean renderThroughBlocks;

	// Mmmm, yes we love boilerplate

	public int getRed() {
		return this.red;
	}

	public int getGreen() {
		return this.green;
	}

	public int getBlue() {
		return this.blue;
	}

	public int getAlpha() {
		return this.alpha;
	}

	public int getOutlineRed() {
		return this.outlineRed;
	}

	public int getOutlineGreen() {
		return this.outlineGreen;
	}

	public int getOutlineBlue() {
		return this.outlineBlue;
	}

	public int getOutlineWidth() {
		return this.outlineWidth;
	}

	public boolean hasOutline() {
		return this.outlineWidth > 0;
	}

	public boolean shouldRenderThroughBlocks() {
		return this.renderThroughBlocks;
	}

	public void setRed(int red) {
		this.red = red;
	}

	public void setGreen(int green) {
		this.green = green;
	}

	public void setBlue(int blue) {
		this.blue = blue;
	}

	public void setAlpha(int alpha) {
		this.alpha = alpha;
	}

	public void setOutlineRed(int outlineRed) {
		this.outlineRed = outlineRed;
	}

	public void setOutlineGreen(int outlineGreen) {
		this.outlineGreen = outlineGreen;
	}

	public void setOutlineBlue(int outlineBlue) {
		this.outlineBlue = outlineBlue;
	}

	public void setOutlineWidth(int outline) {
		this.outlineWidth = outline;
	}

	public void setRenderThroughBlocks(boolean renderThroughBlocks) {
		this.renderThroughBlocks = renderThroughBlocks;
	}

	@ArucasFunction
	public final void setColour(Context context, NumberValue numberValue) {
		int colour = numberValue.value.intValue();
		this.setRed((colour >> 24) & 0xFF);
		this.setGreen((colour >> 16) & 0xFF);
		this.setBlue((colour >> 8) & 0xFF);
		this.setAlpha(colour & 0xFF);
	}

	@ArucasFunction
	public final void setColour(Context context, NumberValue redValue, NumberValue greenValue, NumberValue blueValue) {
		int red = redValue.value.intValue();
		int green = greenValue.value.intValue();
		int blue = blueValue.value.intValue();
		this.throwIfOutOfRange(red, green, blue);
		this.setRed(red);
		this.setGreen(green);
		this.setBlue(blue);
	}

	@ArucasFunction
	public final void setRed(Context context, NumberValue numberValue) {
		int red = numberValue.value.intValue();
		this.throwIfOutOfRange(red);
		this.setRed(red);
	}

	@ArucasFunction
	public final void setGreen(Context context, NumberValue numberValue) {
		int green = numberValue.value.intValue();
		this.throwIfOutOfRange(green);
		this.setGreen(green);
	}

	@ArucasFunction
	public final void setBlue(Context context, NumberValue numberValue) {
		int blue = numberValue.value.intValue();
		this.throwIfOutOfRange(blue);
		this.setBlue(blue);
	}

	@ArucasFunction
	public final void setOpacity(Context context, NumberValue numberValue) {
		int alpha = numberValue.value.intValue();
		this.throwIfOutOfRange(alpha);
		this.setAlpha(alpha);
	}

	@ArucasFunction
	public final void setOutlineColour(Context context, NumberValue numberValue) {
		int colour = numberValue.value.intValue();
		this.setOutlineRed((colour >> 16) & 0xFF);
		this.setOutlineGreen((colour >> 8) & 0xFF);
		this.setOutlineBlue(colour & 0xFF);
	}

	@ArucasFunction
	public final void setOutlineColour(Context context, NumberValue redValue, NumberValue greenValue, NumberValue blueValue) {
		int red = redValue.value.intValue();
		int green = greenValue.value.intValue();
		int blue = blueValue.value.intValue();
		this.throwIfOutOfRange(red, green, blue);
		this.setOutlineRed(red);
		this.setOutlineGreen(green);
		this.setOutlineBlue(blue);
	}

	@ArucasFunction
	public final void setOutlineRed(Context context, NumberValue numberValue) {
		int outlineRed = numberValue.value.intValue();
		this.throwIfOutOfRange(outlineRed);
		this.setOutlineRed(outlineRed);
	}

	@ArucasFunction
	public final void setOutlineGreen(Context context, NumberValue numberValue) {
		int outlineGreen = numberValue.value.intValue();
		this.throwIfOutOfRange(outlineGreen);
		this.setOutlineGreen(outlineGreen);
	}

	@ArucasFunction
	public final void setOutlineBlue(Context context, NumberValue numberValue) {
		int outlineBlue = numberValue.value.intValue();
		this.throwIfOutOfRange(outlineBlue);
		this.setOutlineBlue(outlineBlue);
	}

	@ArucasFunction
	public final void setOutlinePixelWidth(Context context, NumberValue width) {
		this.setOutlineWidth(width.value.intValue());
	}

	@ArucasFunction
	public final void setRenderThroughBlocks(Context context, BooleanValue booleanValue) {
		this.setRenderThroughBlocks(booleanValue.value);
	}

	@ArucasFunction
	public final NumberValue getRed(Context context) {
		return NumberValue.of(this.getRed());
	}

	@ArucasFunction
	public final NumberValue getGreen(Context context) {
		return NumberValue.of(this.getGreen());
	}

	@ArucasFunction
	public final NumberValue getBlue(Context context) {
		return NumberValue.of(this.getBlue());
	}

	@ArucasFunction
	public final ListValue getRGB(Context context) {
		ArucasList list = new ArucasList();
		list.add(this.getRed(context));
		list.add(this.getGreen(context));
		list.add(this.getBlue(context));
		return new ListValue(list);
	}

	@ArucasFunction
	public final NumberValue getOpacity(Context context) {
		return NumberValue.of(this.getAlpha());
	}

	@ArucasFunction
	public final ListValue getRGBA(Context context) {
		ArucasList list = new ArucasList();
		list.add(this.getRed(context));
		list.add(this.getGreen(context));
		list.add(this.getBlue(context));
		list.add(this.getOpacity(context));
		return new ListValue(list);
	}

	@ArucasFunction
	public final BooleanValue shouldRenderThroughBlocks(Context context) {
		return BooleanValue.of(this.shouldRenderThroughBlocks());
	}

	@ArucasFunction
	public abstract void render(Context context);

	@ArucasFunction
	public abstract void stopRendering(Context context);

	private void throwIfOutOfRange(int... colours) {
		for (int colour : colours) {
			if (colour < 0 || colour > 255) {
				throw new RuntimeException(
					"Colour %d is out of bounds, must be between 0 - 255".formatted(colour)
				);
			}
		}
	}
}