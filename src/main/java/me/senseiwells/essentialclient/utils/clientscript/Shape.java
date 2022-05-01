package me.senseiwells.essentialclient.utils.clientscript;

import me.senseiwells.arucas.api.wrappers.ArucasFunction;
import me.senseiwells.arucas.api.wrappers.ArucasMember;
import me.senseiwells.arucas.api.wrappers.IArucasWrappedClass;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.impl.ArucasList;
import me.senseiwells.arucas.values.*;
import me.senseiwells.essentialclient.clientscript.values.PosValue;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.Locale;

/**
 * This is an abstract class intended to be
 * the parent of all different Shape wrapper
 * classes used to render shaped in clientscript
 */
@SuppressWarnings("unused")
public abstract class Shape implements IArucasWrappedClass {
	private Context context;
	private int red;
	private int green;
	private int blue;
	private int alpha;
	private int outlineRed;
	private int outlineGreen;
	private int outlineBlue;
	private int outlineWidth;
	private boolean isRendering;
	private boolean renderThroughBlocks;

	public Shape() {
		this.setRed(255);
		this.setGreen(255);
		this.setBlue(255);
		this.setAlpha(255);

		this.setOutlineRed(0);
		this.setOutlineGreen(0);
		this.setOutlineBlue(0);
		this.setOutlineWidth(0);

		this.setRendering(false);
		this.setRenderThroughBlocks(false);
	}

	// Mmmm, yes we love boilerplate

	public Context getCreatedContext() {
		return this.context;
	}

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

	public boolean isRendering() {
		return this.isRendering;
	}

	public boolean shouldRenderThroughBlocks() {
		return this.renderThroughBlocks;
	}

	public void setCreatedContext(Context context) {
		this.context = context;
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

	public void setRendering(boolean rendering) {
		this.isRendering = rendering;
	}

	public void setRenderThroughBlocks(boolean renderThroughBlocks) {
		this.renderThroughBlocks = renderThroughBlocks;
	}

	public boolean hasPosition() {
		return this.getPosition() != null;
	}

	public Vec3d getPosition() {
		return null;
	}

	public void setPosition(Vec3d position) { }

	public boolean hasSecondPosition() {
		return this.getSecondPosition() != null;
	}

	public Vec3d getSecondPosition() {
		return null;
	}

	public void setSecondPosition(Vec3d position) { }

	/**
	 * Name: <code>&lt;Shape>.setColour(colour)</code> <br>
	 * Description: This sets the colour of the shape, using a single value, this
	 * function also has a sibling named <code>setColor()</code> that has the same functionality <br>
	 * Parameters - Number: the colour, usually you would use hexadecimal, 0xRRGGBB
	 * where RR represents red from 00 - FF, GG represents green from 00 - FF,
	 * and BB represents blue from 00 - FF <br>
	 * Example: <code>shape.setColour(0xFF0000);</code>
	 */
	@ArucasFunction
	public final void setColour(Context context, NumberValue numberValue) {
		int colour = numberValue.value.intValue();
		this.setRed((colour >> 16) & 0xFF);
		this.setGreen((colour >> 8) & 0xFF);
		this.setBlue(colour & 0xFF);
	}

	// See above documentation
	@ArucasFunction
	public final void setColor(Context context, NumberValue numberValue) {
		this.setColour(context, numberValue);
	}

	/**
	 * Name: <code>&lt;Shape>.setColour(red, green, blue)</code> <br>
	 * Description: This sets the colour of the shape, using three values this function
	 * also has a sibling named <code>setColor()</code> that has the same functionality <br>
	 * Parameters - Number, Number, Number: the amount of red 0 - 255, amount of blue 0 - 255
	 * and the amount of green 0 - 255 <br>
	 * Throws - Error: <code>""Colour ... is out of bounds, must be between 0 - 255""</code> if the colour is not valid <br>
	 * Example: <code>shape.setColour(34, 55, 0);</code>
	 */
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

	/**
	 * Name: <code>&lt;Shape>.setRed(red)</code> <br>
	 * Description: This sets the red value of the shape, using a single value <br>
	 * Parameters - Number: the amount of red between 0 - 255 <br>
	 * Throws - Error: <code>""Colour ... is out of bounds, must be between 0 - 255""</code> if the red value is not valid <br>
	 * Example: <code>shape.setRed(34);</code>
	 */
	@ArucasFunction
	public final void setRed(Context context, NumberValue numberValue) {
		int red = numberValue.value.intValue();
		this.throwIfOutOfRange(red);
		this.setRed(red);
	}

	/**
	 * Name: <code>&lt;Shape>.setGreen(green)</code> <br>
	 * Description: This sets the green value of the shape, using a single value <br>
	 * Parameters - Number: the amount of green between 0 - 255 <br>
	 * Throws - Error: <code>""Colour ... is out of bounds, must be between 0 - 255""</code> if the green value is not valid <br>
	 * Example: <code>shape.setGreen(34);</code>
	 */
	@ArucasFunction
	public final void setGreen(Context context, NumberValue numberValue) {
		int green = numberValue.value.intValue();
		this.throwIfOutOfRange(green);
		this.setGreen(green);
	}

	/**
	 * Name: <code>&lt;Shape>.setBlue(blue)</code> <br>
	 * Description: This sets the blue value of the shape, using a single value <br>
	 * Parameters - Number: the amount of blue between 0 - 255 <br>
	 * Throws - Error: <code>""Colour ... is out of bounds, must be between 0 - 255""</code> if the blue value is not valid <br>
	 * Example: <code>shape.setBlue(34);</code>
	 */
	@ArucasFunction
	public final void setBlue(Context context, NumberValue numberValue) {
		int blue = numberValue.value.intValue();
		this.throwIfOutOfRange(blue);
		this.setBlue(blue);
	}

	/**
	 * Name: <code>&lt;Shape>.setOpacity(alpha)</code> <br>
	 * Description: This sets the opacity of the shape, using a single value <br>
	 * Parameters - Number: the opacity, where 255 is solid colour and 0 is no colour <br>
	 * Throws - Error: <code>""Colour ... is out of bounds, must be between 0 - 255""</code> if the opacity is not valid <br>
	 * Example: <code>shape.setOpacity(0xFF);</code>
	 */
	@ArucasFunction
	public final void setOpacity(Context context, NumberValue numberValue) {
		int alpha = numberValue.value.intValue();
		this.throwIfOutOfRange(alpha);
		this.setAlpha(alpha);
	}

	/**
	 * Name: <code>&lt;Shape>.setOutlineColour(colour)</code> <br>
	 * Description: This sets the width of the shape, using a single value, this function
	 * also has a sibling named <code>setOutlineColor()</code> that has the same functionality <br>
	 * Parameters - Number: the colour, usually you would use hexadecimal, 0xRRGGBB
	 * where RR represents red from 00 - FF, GG represents green from 00 - FF,
	 * and BB represents blue from 00 - FF <br>
	 * Example: <code>shape.setOutlineColour(0xFF00FF);</code>
	 */
	@ArucasFunction
	public final void setOutlineColour(Context context, NumberValue numberValue) {
		int colour = numberValue.value.intValue();
		this.setOutlineRed((colour >> 16) & 0xFF);
		this.setOutlineGreen((colour >> 8) & 0xFF);
		this.setOutlineBlue(colour & 0xFF);
	}

	// See above documentation
	@ArucasFunction
	public final void setOutlineColor(Context context, NumberValue numberValue) {
		this.setColour(context, numberValue);
	}

	/**
	 * Name: <code>&lt;Shape>.setOutlineColour(red, green, blue)</code> <br>
	 * Description: This sets the outline colour of the shape, using three values, this function
	 * also has a sibling named <code>setOutlineColor()</code> that has the same functionality <br>
	 * Parameters - Number, Number, Number: the amount of red 0 - 255, amount of blue 0 - 255
	 * and the amount of green 0 - 255 <br>
	 * Throws - Error: <code>""Colour ... is out of bounds, must be between 0 - 255""</code> if the colour value is not valid <br>
	 * Example: <code>shape.setOutlineColour(255, 0, 255);</code>
	 */
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

	/**
	 * Name: <code>&lt;Shape>.setOutlineRed(red)</code> <br>
	 * Description: This sets the outline red value of the shape, using a single value <br>
	 * Parameters - Number: the amount of red between 0 - 255 <br>
	 * Throws - Error: <code>""Colour ... is out of bounds, must be between 0 - 255""</code> if the red value is not valid <br>
	 * Example: <code>shape.setOutlineRed(34);</code>
	 */
	@ArucasFunction
	public final void setOutlineRed(Context context, NumberValue numberValue) {
		int outlineRed = numberValue.value.intValue();
		this.throwIfOutOfRange(outlineRed);
		this.setOutlineRed(outlineRed);
	}

	/**
	 * Name: <code>&lt;Shape>.setOutlineGreen(green)</code> <br>
	 * Description: This sets the outline green value of the shape, using a single value <br>
	 * Parameters - Number: the amount of green between 0 - 255 <br>
	 * Throws - Error: <code>""Colour ... is out of bounds, must be between 0 - 255""</code> if the green value is not valid <br>
	 * Example: <code>shape.setOutlineGreen(34);</code>
	 */
	@ArucasFunction
	public final void setOutlineGreen(Context context, NumberValue numberValue) {
		int outlineGreen = numberValue.value.intValue();
		this.throwIfOutOfRange(outlineGreen);
		this.setOutlineGreen(outlineGreen);
	}

	/**
	 * Name: <code>&lt;Shape>.setOutlineBlue(blue)</code> <br>
	 * Description: This sets the outline blue value of the shape, using a single value <br>
	 * Parameters - Number: the amount of blue between 0 - 255 <br>
	 * Throws - Error: <code>""Colour ... is out of bounds, must be between 0 - 255""</code> if the blue value is not valid <br>
	 * Example: <code>shape.setOutlineBlue(34);</code>
	 */
	@ArucasFunction
	public final void setOutlineBlue(Context context, NumberValue numberValue) {
		int outlineBlue = numberValue.value.intValue();
		this.throwIfOutOfRange(outlineBlue);
		this.setOutlineBlue(outlineBlue);
	}

	/**
	 * Name: <code>&lt;Shape>.setOutlinePixelWidth(width)</code> <br>
	 * Description: This sets the outline pixel width of the shape, using a single value <br>
	 * Parameters - Number: the width of the outline in pixels <br>
	 * Example: <code>shape.setOutlinePixelWidth(5);</code>
	 */
	@ArucasFunction
	public final void setOutlinePixelWidth(Context context, NumberValue width) {
		this.setOutlineWidth(width.value.intValue());
	}

	/**
	 * Name: <code>&lt;Shape>.setRenderThroughBlocks(boolean)</code> <br>
	 * Description: This sets whether the shape should render through blocks <br>
	 * Parameters - Boolean: whether the shape should render through blocks <br>
	 * Example: <code>shape.setRenderThroughBlocks(true);</code>
	 */
	@ArucasFunction
	public final void setRenderThroughBlocks(Context context, BooleanValue booleanValue) {
		this.setRenderThroughBlocks(booleanValue.value);
	}

	/**
	 * Name: <code>&lt;Shape>.getRed()</code> <br>
	 * Description: This returns the red value of the shape <br>
	 * Returns - Number: the red value of the shape <br>
	 * Example: <code>shape.getRed();</code>
	 */
	@ArucasFunction
	public final NumberValue getRed(Context context) {
		return NumberValue.of(this.getRed());
	}

	/**
	 * Name: <code>&lt;Shape>.getGreen()</code> <br>
	 * Description: This returns the green value of the shape <br>
	 * Returns - Number: the green value of the shape <br>
	 * Example: <code>shape.getGreen();</code>
	 */
	@ArucasFunction
	public final NumberValue getGreen(Context context) {
		return NumberValue.of(this.getGreen());
	}

	/**
	 * Name: <code>&lt;Shape>.getBlue()</code> <br>
	 * Description: This returns the blue value of the shape <br>
	 * Returns - Number: the blue value of the shape <br>
	 * Example: <code>shape.getBlue();</code>
	 */
	@ArucasFunction
	public final NumberValue getBlue(Context context) {
		return NumberValue.of(this.getBlue());
	}

	/**
	 * Name: <code>&lt;Shape>.getRBG()</code> <br>
	 * Description: This returns the RGB value of the shape <br>
	 * Returns - Number: the RGB value of the shape as a single number in the form 0xRRGGBB <br>
	 * Example: <code>shape.getRGB();</code>
	 */
	@ArucasFunction
	public final NumberValue getRBG(Context context) {
		return NumberValue.of(this.getRed() << 16 | this.getGreen() << 8 | this.getBlue());
	}

	/**
	 * Name: <code>&lt;Shape>.getRBGList()</code> <br>
	 * Description: This returns the RGB value of the shape as a list <br>
	 * Returns - List: the RGB value of the shape as a list in the form [red, green, blue] <br>
	 * Example: <code>r, g, b = shape.getRGBList();</code>
	 */
	@ArucasFunction
	public final ListValue getRGBList(Context context) {
		ArucasList list = new ArucasList();
		list.add(this.getRed(context));
		list.add(this.getGreen(context));
		list.add(this.getBlue(context));
		return new ListValue(list);
	}

	/**
	 * Name: <code>&lt;Shape>.getOpacity()</code> <br>
	 * Description: This returns the opacity of the shape <br>
	 * Returns - Number: the opacity of the shape <br>
	 * Example: <code>shape.getOpacity();</code>
	 */
	@ArucasFunction
	public final NumberValue getOpacity(Context context) {
		return NumberValue.of(this.getAlpha());
	}

	/**
	 * Name: <code>&lt;Shape>.getRBGAList()</code> <br>
	 * Description: This returns the RGBA value of the shape as a list <br>
	 * Returns - List: the RGBA value of the shape as a list in the form [red, green, blue, opacity] <br>
	 * Example: <code>r, g, b, a = shape.getRBGAList();</code>
	 */
	@ArucasFunction
	public final ListValue getRGBAList(Context context) {
		ArucasList list = new ArucasList();
		list.add(this.getRed(context));
		list.add(this.getGreen(context));
		list.add(this.getBlue(context));
		list.add(this.getOpacity(context));
		return new ListValue(list);
	}

	/**
	 * Name: <code>&lt;Shape>.shouldRenderThroughBlocks()</code> <br>
	 * Description: This returns whether the shape should render through blocks <br>
	 * Returns - Boolean: whether the shape should render through blocks <br>
	 * Example: <code>shape.shouldRenderThroughBlocks();</code>
	 */
	@ArucasFunction
	public final BooleanValue shouldRenderThroughBlocks(Context context) {
		return BooleanValue.of(this.shouldRenderThroughBlocks());
	}

	/**
	 * Name: <code>&lt;Shape>.render()</code> <br>
	 * Description: This sets the shape to be rendered indefinitely, the shape will only
	 * stop rendering when the script ends or when you call the <code>stopRendering()</code> method <br>
	 * Example: <code>shape.render();</code>
	 */
	@ArucasFunction
	public void render(Context context) {
		this.setRendering(true);
	}

	/**
	 * Name: <code>&lt;Shape>.stopRendering()</code> <br>
	 * Description: This stops the shape from rendering <br>
	 * Example: <code>shape.stopRendering();</code>
	 */
	@ArucasFunction
	public void stopRendering(Context context) {
		this.setRendering(false);
	}

	private void throwIfOutOfRange(int... colours) {
		for (int colour : colours) {
			if (colour < 0 || colour > 255) {
				throw new RuntimeException(
					"Colour %d is out of bounds, must be between 0 - 255".formatted(colour)
				);
			}
		}
	}

	public abstract static class CentrePositioned extends Shape {
		@ArucasMember(assignable = false)
		public PosValue pos;
		private float width;

		public CentrePositioned() {
			super();
			this.setWidth(5);
		}

		@Override
		public boolean hasPosition() {
			return true;
		}

		@Override
		public Vec3d getPosition() {
			return this.pos.value;
		}

		@Override
		public void setPosition(Vec3d pos) {
			this.pos = new PosValue(pos);
		}

		@Override
		public boolean hasSecondPosition() {
			return false;
		}

		public float getWidth() {
			return this.width;
		}

		public void setWidth(float width) {
			this.width = width;
		}

		/**
		 * Name: <code>&lt;Shape>.setPos(pos)</code> <br>
		 * Description: This returns the position of the shape <br>
		 * Parameter - Pos: the position of the shape <br>
		 * Example: <code>shape.setPos(new Pos(0, 0, 0));</code>
		 */
		@ArucasFunction
		public void setPos(Context context, PosValue posValue) {
			this.pos = posValue;
		}

		/**
		 * Name: <code>&lt;Shape>.getWidth()</code> <br>
		 * Description: This returns the width of the shape <br>
		 * Returns - Number: the width of the shape <br>
		 * Example: <code>shape.getWidth();</code>
		 */
		@ArucasFunction
		public NumberValue getWidth(Context context) {
			return NumberValue.of(this.getWidth());
		}

		/**
		 * Name: <code>&lt;Shape>.setWidth(width)</code> <br>
		 * Description: This sets the width of the shape <br>
		 * Parameter - Number: the width of the shape <br>
		 * Example: <code>shape.setWidth(10.5);</code>
		 */
		@ArucasFunction
		public void setWidth(Context context, NumberValue width) {
			this.setWidth(width.value.floatValue());
		}

		@ArucasFunction
		public void centrePosition(Context context) {
			this.setPos(context, new PosValue(this.pos.toBlockPos()));
		}
	}

	public abstract static class Positioned extends Shape {
		@ArucasMember(assignable = false)
		public PosValue pos1;
		@ArucasMember(assignable = false)
		public PosValue pos2;

		@Override
		public boolean hasPosition() {
			return true;
		}

		@Override
		public Vec3d getPosition() {
			return this.pos1.value;
		}

		@Override
		public void setPosition(Vec3d pos1) {
			this.pos1 = new PosValue(pos1);
		}

		@Override
		public boolean hasSecondPosition() {
			return true;
		}

		@Override
		public Vec3d getSecondPosition() {
			return this.pos2.value;
		}

		@Override
		public void setSecondPosition(Vec3d pos2) {
			this.pos2 = new PosValue(pos2);
		}

		/**
		 * Name: <code>&lt;Shape>.setPos1(pos1)</code> <br>
		 * Description: This sets the first position of the shape <br>
		 * Parameters - Pos: the position of the shape <br>
		 * Example: <code>shape.setPos1(new Pos(1, 0, 100));</code>
		 */
		@ArucasFunction
		public void setPos1(Context context, PosValue posValue) {
			this.pos1 = posValue;
		}

		/**
		 * Name: <code>&lt;Shape>.setPos2(pos2)</code> <br>
		 * Description: This sets the second position of the shape <br>
		 * Parameters - Pos: the position of the shape <br>
		 * Example: <code>shape.setPos2(new Pos(100, 0, 200));</code>
		 */
		@ArucasFunction
		public void setPos2(Context context, PosValue posValue) {
			this.pos2 = posValue;
		}

		/**
		 * Name: <code>&lt;Shape>.centrePositions()</code> <br>
		 * Description: This centres the positions of the shape <br>
		 * Example: <code>shape.centrePositions();</code>
		 */
		@ArucasFunction
		public void centrePositions(Context context) {
			this.setPos1(context, new PosValue(this.pos1.toBlockPos()));
			this.setPos2(context, new PosValue(this.pos2.toBlockPos()));
		}
	}

	public interface Tiltable {
		float getXTilt();
		float getYTilt();
		float getZTilt();

		void setXTilt(float xTilt);
		void setYTilt(float yTilt);
		void setZTilt(float zTilt);

		/**
		 * Name: <code>&lt;Shape>.setTilt(xTilt, yTilt, zTilt)</code> <br>
		 * Description: This sets the tilt of the shape <br>
		 * Parameters - Number, Number, Number: the x tilt, the y tilt, the z tilt <br>
		 * Example: <code>shape.setTilt(100, 0, 80);</code>
		 */
		@ArucasFunction
		default void setTilt(Context context, NumberValue xTilt, NumberValue yTilt, NumberValue zTilt) {
			this.setXTilt(xTilt.value.floatValue());
			this.setYTilt(yTilt.value.floatValue());
			this.setZTilt(zTilt.value.floatValue());
		}

		/**
		 * Name: <code>&lt;Shape>.setXTilt()</code> <br>
		 * Description: This sets the x tilt of the shape <br>
		 * Parameters - Number: the x tilt <br>
		 * Example: <code>shape.setXTilt(100);</code>
		 */
		@ArucasFunction
		default void setXTilt(Context context, NumberValue xTilt) {
			this.setXTilt(xTilt.value.floatValue());
		}

		/**
		 * Name: <code>&lt;Shape>.setYTilt()</code> <br>
		 * Description: This sets the y tilt of the shape <br>
		 * Parameters - Number: the y tilt <br>
		 * Example: <code>shape.setYTilt(100);</code>
		 */
		@ArucasFunction
		default void setYTilt(Context context, NumberValue yTilt) {
			this.setYTilt(yTilt.value.floatValue());
		}

		/**
		 * Name: <code>&lt;Shape>.setZTilt()</code> <br>
		 * Description: This sets the z tilt of the shape <br>
		 * Parameters - Number: the z tilt <br>
		 * Example: <code>shape.setZTilt(100);</code>
		 */
		@ArucasFunction
		default void setZTilt(Context context, NumberValue zTilt) {
			this.setZTilt(zTilt.value.floatValue());
		}

		/**
		 * Name: <code>&lt;Shape>.getXTilt()</code> <br>
		 * Description: This gets the x tilt of the shape <br>
		 * Returns - Number: the x tilt <br>
		 * Example: <code>shape.getXTilt();</code>
		 */
		@ArucasFunction
		default NumberValue getXTilt(Context context) {
			return NumberValue.of(this.getXTilt());
		}

		/**
		 * Name: <code>&lt;Shape>.getYTilt()</code> <br>
		 * Description: This gets the y tilt of the shape <br>
		 * Returns - Number: the y tilt <br>
		 * Example: <code>shape.getYTilt();</code>
		 */
		@ArucasFunction
		default NumberValue getYTilt(Context context) {
			return NumberValue.of(this.getYTilt());
		}

		/**
		 * Name: <code>&lt;Shape>.getZTilt()</code> <br>
		 * Description: This gets the z tilt of the shape <br>
		 * Returns - Number: the z tilt <br>
		 * Example: <code>shape.getZTilt();</code>
		 */
		@ArucasFunction
		default NumberValue getZTilt(Context context) {
			return NumberValue.of(this.getZTilt());
		}
	}

	public interface Directional {
		Direction getDirection();

		void setDirection(Direction direction);

		/**
		 * Name: <code>&lt;Shape>.setDirection(direction)</code> <br>
		 * Description: This sets the direction of the shape <br>
		 * Parameters - String: the direction of the shape as a string, this can be
		 * "north", "south", "east", "west", "down", or "up" <br>
		 * Example: <code>shape.setDirection("down");</code>
		 */
		@ArucasFunction
		default void setDirection(Context context, StringValue direction) {
			this.setDirection(Direction.byName(direction.value.toUpperCase(Locale.ROOT)));
		}

		/**
		 * Name: <code>&lt;Shape>.getDirection()</code> <br>
		 * Description: This gets the direction of the shape <br>
		 * Returns - String/Null: the direction of the shape as a string, this can be
		 * "north", "south", "east", "west", "down", or "up", null if it has no direction <br>
		 * Example: <code>shape.getDirection();</code>
		 */
		@ArucasFunction
		default Value<?> getDirection(Context context) {
			Direction direction = this.getDirection();
			return direction == null ? NullValue.NULL : StringValue.of(direction.getName());
		}
	}
}