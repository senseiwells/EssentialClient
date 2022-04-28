package me.senseiwells.essentialclient.utils.clientscript;

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

	public final int getRed() {
		return this.red;
	}

	public final int getGreen() {
		return this.green;
	}

	public final int getBlue() {
		return this.blue;
	}

	public final int getAlpha() {
		return this.alpha;
	}

	public final int getOutlineRed() {
		return this.outlineRed;
	}

	public final int getOutlineGreen() {
		return this.outlineGreen;
	}

	public final int getOutlineBlue() {
		return this.outlineBlue;
	}

	public final int getOutlineWidth() {
		return this.outlineWidth;
	}

	public final boolean hasOutline() {
		return this.outlineWidth > 0;
	}

	public final boolean shouldRenderThroughBlocks() {
		return this.renderThroughBlocks;
	}

	public final void setRed(int red) {
		this.red = red;
	}

	public final void setGreen(int green) {
		this.green = green;
	}

	public final void setBlue(int blue) {
		this.blue = blue;
	}

	public final void setAlpha(int alpha) {
		this.alpha = alpha;
	}

	public final void setOutlineRed(int outlineRed) {
		this.outlineRed = outlineRed;
	}

	public final void setOutlineGreen(int outlineGreen) {
		this.outlineGreen = outlineGreen;
	}

	public final void setOutlineBlue(int outlineBlue) {
		this.outlineBlue = outlineBlue;
	}

	public final void setOutlineWidth(int outline) {
		this.outlineWidth = outline;
	}

	public final void setRenderThroughBlocks(boolean renderThroughBlocks) {
		this.renderThroughBlocks = renderThroughBlocks;
	}

	protected void setColour(Context context, NumberValue numberValue) {
		int colour = numberValue.value.intValue();
		this.setRed((colour >> 24) & 0xFF);
		this.setGreen((colour >> 16) & 0xFF);
		this.setBlue((colour >> 8) & 0xFF);
		this.setAlpha(colour & 0xFF);
	}

	protected void setColour(Context context, NumberValue redValue, NumberValue greenValue, NumberValue blueValue) {
		int red = redValue.value.intValue();
		int green = greenValue.value.intValue();
		int blue = blueValue.value.intValue();
		this.throwIfOutOfRange(red, green, blue);
		this.setRed(red);
		this.setGreen(green);
		this.setBlue(blue);
	}

	protected void setRed(Context context, NumberValue numberValue) {
		int red = numberValue.value.intValue();
		this.throwIfOutOfRange(red);
		this.setRed(red);
	}

	protected void setGreen(Context context, NumberValue numberValue) {
		int green = numberValue.value.intValue();
		this.throwIfOutOfRange(green);
		this.setGreen(green);
	}

	protected void setBlue(Context context, NumberValue numberValue) {
		int blue = numberValue.value.intValue();
		this.throwIfOutOfRange(blue);
		this.setBlue(blue);
	}

	protected void setOpacity(Context context, NumberValue numberValue) {
		int alpha = numberValue.value.intValue();
		this.throwIfOutOfRange(alpha);
		this.setAlpha(alpha);
	}

	protected void setOutlineColour(Context context, NumberValue numberValue) {
		int colour = numberValue.value.intValue();
		this.setOutlineRed((colour >> 16) & 0xFF);
		this.setOutlineGreen((colour >> 8) & 0xFF);
		this.setOutlineBlue(colour & 0xFF);
	}

	protected void setOutlineColour(Context context, NumberValue redValue, NumberValue greenValue, NumberValue blueValue) {
		int red = redValue.value.intValue();
		int green = greenValue.value.intValue();
		int blue = blueValue.value.intValue();
		this.throwIfOutOfRange(red, green, blue);
		this.setOutlineRed(red);
		this.setOutlineGreen(green);
		this.setOutlineBlue(blue);
	}

	protected void setOutlineRed(Context context, NumberValue numberValue) {
		int outlineRed = numberValue.value.intValue();
		this.throwIfOutOfRange(outlineRed);
		this.setOutlineRed(outlineRed);
	}

	protected void setOutlineGreen(Context context, NumberValue numberValue) {
		int outlineGreen = numberValue.value.intValue();
		this.throwIfOutOfRange(outlineGreen);
		this.setOutlineGreen(outlineGreen);
	}

	protected void setOutlineBlue(Context context, NumberValue numberValue) {
		int outlineBlue = numberValue.value.intValue();
		this.throwIfOutOfRange(outlineBlue);
		this.setOutlineBlue(outlineBlue);
	}

	protected void setOutlinePixelWidth(Context context, NumberValue width) {
		this.setOutlineWidth(width.value.intValue());
	}

	protected void setRenderThroughBlocks(Context context, BooleanValue booleanValue) {
		this.setRenderThroughBlocks(booleanValue.value);
	}
	
	protected NumberValue getRed(Context context) {
		return NumberValue.of(this.getRed());
	}

	protected NumberValue getGreen(Context context) {
		return NumberValue.of(this.getGreen());
	}

	protected NumberValue getBlue(Context context) {
		return NumberValue.of(this.getBlue());
	}

	protected ListValue getRGB(Context context) {
		ArucasList list = new ArucasList();
		list.add(this.getRed(context));
		list.add(this.getGreen(context));
		list.add(this.getBlue(context));
		return new ListValue(list);
	}

	protected NumberValue getOpacity(Context context) {
		return NumberValue.of(this.getAlpha());
	}

	protected ListValue getRGBA(Context context) {
		ArucasList list = new ArucasList();
		list.add(this.getRed(context));
		list.add(this.getGreen(context));
		list.add(this.getBlue(context));
		list.add(this.getOpacity(context));
		return new ListValue(list);
	}

	protected BooleanValue shouldRenderThroughBlocks(Context context) {
		return BooleanValue.of(this.shouldRenderThroughBlocks());
	}

	protected abstract void render(Context context);

	protected abstract void stopRendering(Context context);

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