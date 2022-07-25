package me.senseiwells.essentialclient.utils.clientscript;

import me.senseiwells.arucas.api.docs.FunctionDoc;
import me.senseiwells.arucas.api.docs.MemberDoc;
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

import static me.senseiwells.arucas.utils.ValueTypes.*;
import static me.senseiwells.essentialclient.clientscript.core.MinecraftAPI.POS;

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

	public void render() {
		this.setRendering(true);
	}

	public void stopRendering() {
		this.setRendering(false);
	}

	// Checkstyle off
	// This is done because checkstyle wants to put
	// overloaded methods together which in this case
	// isn't applicable, and makes more sense not to

	@FunctionDoc(
		name = "setColour",
		desc = {
			"This sets the colour of the shape, using a single value, this",
			"function also has a sibling named `setColor()` that has the same functionality"
		},
		params = {
			NUMBER, "colour", "the colour, usually you would use hexadecimal, 0xRRGGBB where RR represents red from 00 - FF, " +
				"GG represents green from 00 - FF, and BB represents blue from 00 - FF"
		},
		example = "shape.setColour(0xFF0000);"
	)
	@ArucasFunction
	public final void setColour(Context context, NumberValue numberValue) {
		int colour = numberValue.value.intValue();
		this.setRed((colour >> 16) & 0xFF);
		this.setGreen((colour >> 8) & 0xFF);
		this.setBlue(colour & 0xFF);
	}

	@FunctionDoc(
		name = "setColor",
		desc = "This sets the colour of the shape, using a single value, this",
		params = {
			NUMBER, "colour", "the colour, usually you would use hexadecimal, 0xRRGGBB where RR represents red from 00 - FF, " +
				"GG represents green from 00 - FF, and BB represents blue from 00 - FF"
		},
		example = "shape.setColor(0xFF0000);"
	)
	@ArucasFunction
	public final void setColor(Context context, NumberValue numberValue) {
		this.setColour(context, numberValue);
	}

	@FunctionDoc(
		name = "setColour",
		desc = {
			"This sets the colour of the shape, using three values this function",
			"also has a sibling named `setColor()` that has the same functionality"
		},
		params = {
			NUMBER, "red", "the amount of red 0 - 255",
			NUMBER, "green", "the amount of green 0 - 255",
			NUMBER, "blue", "the amount of blue 0 - 255"
		},
		throwMsgs = "Colour ... is out of bounds, must be between 0 - 255",
		example = "shape.setColour(34, 55, 0);"
	)
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

	@FunctionDoc(
		name = "setColor",
		desc = "This sets the colour of the shape, using three values this function",
		params = {
			NUMBER, "red", "the amount of red 0 - 255",
			NUMBER, "green", "the amount of green 0 - 255",
			NUMBER, "blue", "the amount of blue 0 - 255"
		},
		throwMsgs = "Colour ... is out of bounds, must be between 0 - 255",
		example = "shape.setColor(34, 55, 0);"
	)
	@ArucasFunction
	public final void setColor(Context context, NumberValue redValue, NumberValue greenValue, NumberValue blueValue) {
		this.setColour(context, redValue, greenValue, blueValue);
	}

	@FunctionDoc(
		name = "setRed",
		desc = "This sets the red value of the shape, using a single value",
		params = {NUMBER, "red", "the amount of red between 0 - 255"},
		throwMsgs = "Colour ... is out of bounds, must be between 0 - 255",
		example = "shape.setRed(34);"
	)
	@ArucasFunction
	public final void setRed(Context context, NumberValue numberValue) {
		int red = numberValue.value.intValue();
		this.throwIfOutOfRange(red);
		this.setRed(red);
	}

	@FunctionDoc(
		name = "setGreen",
		desc = "This sets the green value of the shape, using a single value",
		params = {NUMBER, "green", "the amount of green between 0 - 255"},
		throwMsgs = "Colour ... is out of bounds, must be between 0 - 255",
		example = "shape.setGreen(34);"
	)
	@ArucasFunction
	public final void setGreen(Context context, NumberValue numberValue) {
		int green = numberValue.value.intValue();
		this.throwIfOutOfRange(green);
		this.setGreen(green);
	}

	@FunctionDoc(
		name = "setBlue",
		desc = "This sets the blue value of the shape, using a single value",
		params = {NUMBER, "blue", "the amount of blue between 0 - 255"},
		throwMsgs = "Colour ... is out of bounds, must be between 0 - 255",
		example = "shape.setBlue(34);"
	)
	@ArucasFunction
	public final void setBlue(Context context, NumberValue numberValue) {
		int blue = numberValue.value.intValue();
		this.throwIfOutOfRange(blue);
		this.setBlue(blue);
	}

	@FunctionDoc(
		name = "setOpacity",
		desc = "This sets the opacity of the shape, using a single value",
		params = {NUMBER, "alpha", "the opacity, where 255 is solid colour and 0 is no colour"},
		throwMsgs = "Colour ... is out of bounds, must be between 0 - 255",
		example = "shape.setOpacity(34);"
	)
	@ArucasFunction
	public final void setOpacity(Context context, NumberValue numberValue) {
		int alpha = numberValue.value.intValue();
		this.throwIfOutOfRange(alpha);
		this.setAlpha(alpha);
	}

	@FunctionDoc(
		name = "setOutlineColour",
		desc = {
			"This sets the width of the shape, using a single value, this function",
			"also has a sibling named `setOutlineColor()` that has the same functionality"
		},
		params = {
			NUMBER, "colour", "the colour, usually you would use hexadecimal, 0xRRGGBB where RR represents red from 00 - FF, " +
				"GG represents green from 00 - FF, and BB represents blue from 00 - FF"
		},
		example = "shape.setOutlineColour(0xFF00FF);"
	)
	@ArucasFunction
	public final void setOutlineColour(Context context, NumberValue numberValue) {
		int colour = numberValue.value.intValue();
		this.setOutlineRed((colour >> 16) & 0xFF);
		this.setOutlineGreen((colour >> 8) & 0xFF);
		this.setOutlineBlue(colour & 0xFF);
	}

	@FunctionDoc(
		name = "setOutlineColor",
		desc = "This sets the width of the shape, using a single value, this function",
		params = {
			NUMBER, "colour", "the colour, usually you would use hexadecimal, 0xRRGGBB where RR represents red from 00 - FF, " +
				"GG represents green from 00 - FF, and BB represents blue from 00 - FF"
		},
		example = "shape.setOutlineColor(0xFF00FF);"
	)
	@ArucasFunction
	public final void setOutlineColor(Context context, NumberValue numberValue) {
		this.setColour(context, numberValue);
	}

	@FunctionDoc(
		name = "setOutlineColour",
		desc = {
			"This sets the outline colour of the shape, using three values, this function",
			"also has a sibling named `setOutlineColor()` that has the same functionality"
		},
		params = {
			NUMBER, "red", "the amount of red 0 - 255",
			NUMBER, "green", "the amount of green 0 - 255",
			NUMBER, "blue", "the amount of blue 0 - 255"
		},
		example = "shape.setOutlineColour(255, 0, 255);"
	)
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

	@FunctionDoc(
		name = "setOutlineColor",
		desc = "This sets the outline colour of the shape, using three values, this function",
		params = {
			NUMBER, "red", "the amount of red 0 - 255",
			NUMBER, "green", "the amount of green 0 - 255",
			NUMBER, "blue", "the amount of blue 0 - 255"
		},
		example = "shape.setOutlineColor(255, 0, 255);"
	)
	@ArucasFunction
	public final void setOutlineColor(Context context, NumberValue redValue, NumberValue greenValue, NumberValue blueValue) {
		this.setOutlineColour(context, redValue, greenValue, blueValue);
	}

	@FunctionDoc(
		name = "setOutlineRed",
		desc = "This sets the outline red value of the shape, using a single value",
		params = {NUMBER, "red", "the amount of red between 0 - 255"},
		example = "shape.setOutlineRed(34);"
	)
	@ArucasFunction
	public final void setOutlineRed(Context context, NumberValue numberValue) {
		int outlineRed = numberValue.value.intValue();
		this.throwIfOutOfRange(outlineRed);
		this.setOutlineRed(outlineRed);
	}

	@FunctionDoc(
		name = "setOutlineGreen",
		desc = "This sets the outline green value of the shape, using a single value",
		params = {NUMBER, "green", "the amount of green between 0 - 255"},
		example = "shape.setOutlineGreen(34);"
	)
	@ArucasFunction
	public final void setOutlineGreen(Context context, NumberValue numberValue) {
		int outlineGreen = numberValue.value.intValue();
		this.throwIfOutOfRange(outlineGreen);
		this.setOutlineGreen(outlineGreen);
	}

	@FunctionDoc(
		name = "setOutlineBlue",
		desc = "This sets the outline blue value of the shape, using a single value",
		params = {NUMBER, "blue", "the amount of blue between 0 - 255"},
		example = "shape.setOutlineBlue(34);"
	)
	@ArucasFunction
	public final void setOutlineBlue(Context context, NumberValue numberValue) {
		int outlineBlue = numberValue.value.intValue();
		this.throwIfOutOfRange(outlineBlue);
		this.setOutlineBlue(outlineBlue);
	}

	@FunctionDoc(
		name = "setOutlinePixelWidth",
		desc = "This sets the outline pixel width of the shape, using a single value",
		params = {NUMBER, "width", "the width of the outline in pixels"},
		example = "shape.setOutlinePixelWidth(5);"
	)
	@ArucasFunction
	public final void setOutlinePixelWidth(Context context, NumberValue width) {
		this.setOutlineWidth(width.value.intValue());
	}

	@FunctionDoc(
		name = "setRenderThroughBlocks",
		desc = "This sets whether the shape should render through blocks",
		params = {BOOLEAN, "boolean", "whether the shape should render through blocks"},
		example = "shape.setRenderThroughBlocks(true);"
	)
	@ArucasFunction
	public final void setRenderThroughBlocks(Context context, BooleanValue booleanValue) {
		this.setRenderThroughBlocks(booleanValue.value);
	}

	@FunctionDoc(
		name = "getRed",
		desc = "This returns the red value of the shape",
		returns = {NUMBER, "the red value of the shape"},
		example = "shape.getRed();"
	)
	@ArucasFunction
	public final NumberValue getRed(Context context) {
		return NumberValue.of(this.getRed());
	}

	@FunctionDoc(
		name = "getGreen",
		desc = "This returns the green value of the shape",
		returns = {NUMBER, "the green value of the shape"},
		example = "shape.getGreen();"
	)
	@ArucasFunction
	public final NumberValue getGreen(Context context) {
		return NumberValue.of(this.getGreen());
	}

	@FunctionDoc(
		name = "getBlue",
		desc = "This returns the blue value of the shape",
		returns = {NUMBER, "the blue value of the shape"},
		example = "shape.getBlue();"
	)
	@ArucasFunction
	public final NumberValue getBlue(Context context) {
		return NumberValue.of(this.getBlue());
	}

	@FunctionDoc(
		name = "getRGB",
		desc = "This returns the RGB value of the shape",
		returns = {NUMBER, "the RGB value of the shape as a single number in the form 0xRRGGBB"},
		example = "shape.getRGB();"
	)
	@ArucasFunction
	public final NumberValue getRGB(Context context) {
		return NumberValue.of(this.getRed() << 16 | this.getGreen() << 8 | this.getBlue());
	}

	@FunctionDoc(
		name = "getRGBList",
		desc = "This returns the RGB value of the shape as a list",
		returns = {LIST, "the RGB value of the shape as a list in the form [red, green, blue]"},
		example = "r, g, b = shape.getRGBList();"
	)
	@ArucasFunction
	public final ListValue getRGBList(Context context) {
		ArucasList list = new ArucasList();
		list.add(this.getRed(context));
		list.add(this.getGreen(context));
		list.add(this.getBlue(context));
		return new ListValue(list);
	}

	@FunctionDoc(
		name = "getOpacity",
		desc = "This returns the opacity of the shape",
		returns = {NUMBER, "the opacity of the shape"},
		example = "shape.getOpacity();"
	)
	@ArucasFunction
	public final NumberValue getOpacity(Context context) {
		return NumberValue.of(this.getAlpha());
	}

	@FunctionDoc(
		name = "getRGBAList",
		desc = "This returns the RGBA value of the shape as a list",
		returns = {LIST, "the RGBA value of the shape as a list in the form [red, green, blue, opacity]"},
		example = "r, g, b, a = shape.getRGBAList();"
	)
	@ArucasFunction
	public final ListValue getRGBAList(Context context) {
		ArucasList list = new ArucasList();
		list.add(this.getRed(context));
		list.add(this.getGreen(context));
		list.add(this.getBlue(context));
		list.add(this.getOpacity(context));
		return new ListValue(list);
	}

	@FunctionDoc(
		name = "shouldRenderThroughBlocks",
		desc = "This returns whether the shape should render through blocks",
		returns = {BOOLEAN, "whether the shape should render through blocks"},
		example = "shape.shouldRenderThroughBlocks();"
	)
	@ArucasFunction
	public final BooleanValue shouldRenderThroughBlocks(Context context) {
		return BooleanValue.of(this.shouldRenderThroughBlocks());
	}

	@FunctionDoc(
		name = "render",
		desc = "This sets the shape to be rendered indefinitely, the shape will only stop rendering when the script ends or when you call the stopRendering() method",
		example = "shape.render();"
	)
	@ArucasFunction
	public final void render(Context context) {
		this.render();
	}

	@FunctionDoc(
		name = "stopRendering",
		desc = "This stops the shape from rendering",
		example = "shape.stopRendering();"
	)
	@ArucasFunction
	public final void stopRendering(Context context) {
		this.stopRendering();
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
		@MemberDoc(
			name = "pos",
			desc = "This is the position of the shape",
			type = POS,
			examples = "shape.pos;"
		)
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

		@FunctionDoc(
			name = "setPos",
			desc = "This returns the position of the shape",
			params = {POS, "pos", "the position of the shape"},
			example = "shape.setPos(new Pos(0, 0, 0));"
		)
		@ArucasFunction
		public void setPos(Context context, PosValue posValue) {
			this.pos = posValue;
		}

		@FunctionDoc(
			name = "getWidth",
			desc = "This returns the width of the shape",
			returns = {NUMBER, "the width of the shape"},
			example = "shape.getWidth();"
		)
		@ArucasFunction
		public NumberValue getWidth(Context context) {
			return NumberValue.of(this.getWidth());
		}

		@FunctionDoc(
			name = "setWidth",
			desc = "This sets the width of the shape",
			params = {NUMBER, "width", "the width of the shape"},
			example = "shape.setWidth(10.5);"
		)
		@ArucasFunction
		public void setWidth(Context context, NumberValue width) {
			this.setWidth(width.value.floatValue());
		}

		@FunctionDoc(
			name = "centerPosition",
			desc = "This rounds the position to the nearest block position",
			example = "shape.centerPosition();"
		)
		@ArucasFunction
		public void centerPosition(Context context) {
			this.setPos(context, new PosValue(this.pos.toBlockPos()));
		}
	}

	public abstract static class Positioned extends Shape {
		@MemberDoc(
			name = "pos1",
			desc = "The first position of the shape",
			type = POS,
			examples = "shape.pos1;"
		)
		@ArucasMember(assignable = false)
		public PosValue pos1;

		@MemberDoc(
			name = "pos2",
			desc = "The second position of the shape",
			type = POS,
			examples = "shape.pos2;"
		)
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

		@FunctionDoc(
			name = "setPos1",
			desc = "This sets the first position of the shape",
			params = {POS, "pos1", "the first position of the shape"},
			example = "shape.setPos1(new Pos(1, 0, 100));"
		)
		@ArucasFunction
		public void setPos1(Context context, PosValue posValue) {
			this.pos1 = posValue;
		}

		@FunctionDoc(
			name = "setPos2",
			desc = "This sets the second position of the shape",
			params = {POS, "pos2", "the second position of the shape"},
			example = "shape.setPos2(new Pos(100, 0, 200));"
		)
		@ArucasFunction
		public void setPos2(Context context, PosValue posValue) {
			this.pos2 = posValue;
		}

		@FunctionDoc(
			name = "centerPositions",
			desc = "This centers the positions of the shape",
			example = "shape.centerPositions();"
		)
		@ArucasFunction
		public void centerPositions(Context context) {
			this.setPos1(context, new PosValue(this.pos1.toBlockPos()));
			this.setPos2(context, new PosValue(this.pos2.toBlockPos()));
		}
	}

	public interface Scalable {
		float getXScale();

		float getYScale();

		float getZScale();

		void setXScale(float xScale);

		void setYScale(float yScale);

		void setZScale(float zScale);

		default void setDefaultScale() {
			this.setXScale(1.0F);
			this.setYScale(1.0F);
			this.setZScale(1.0F);
		}

		@FunctionDoc(
			name = "setScale",
			desc = "This sets the scale of the shape",
			params = {
				NUMBER, "xScale", "the x scale of the shape",
				NUMBER, "yScale", "the y scale of the shape",
				NUMBER, "zScale", "the z scale of the shape"
			},
			example = "shape.setScale(1.5, 2.5, 3.5);"
		)
		@ArucasFunction
		default void setScale(Context context, NumberValue xScale, NumberValue yScale, NumberValue zScale) {
			this.setXScale(xScale.value.floatValue());
			this.setYScale(yScale.value.floatValue());
			this.setZScale(zScale.value.floatValue());
		}

		@FunctionDoc(
			name = "setXScale",
			desc = "This sets the x scale of the shape",
			params = {NUMBER, "xScale", "the x scale of the shape"},
			example = "shape.setXScale(1.5);"
		)
		@ArucasFunction
		default void setXScale(Context context, NumberValue xScale) {
			this.setXScale(xScale.value.floatValue());
		}

		@FunctionDoc(
			name = "setYScale",
			desc = "This sets the y scale of the shape",
			params = {NUMBER, "yScale", "the y scale of the shape"},
			example = "shape.setYScale(2.5);"
		)
		@ArucasFunction
		default void setYScale(Context context, NumberValue yScale) {
			this.setYScale(yScale.value.floatValue());
		}

		@FunctionDoc(
			name = "setZScale",
			desc = "This sets the z scale of the shape",
			params = {NUMBER, "zScale", "the z scale of the shape"},
			example = "shape.setZScale(3.5);"
		)
		@ArucasFunction
		default void setZScale(Context context, NumberValue zScale) {
			this.setZScale(zScale.value.floatValue());
		}

		@FunctionDoc(
			name = "getXScale",
			desc = "This gets the x scale of the shape",
			example = "shape.getXScale();"
		)
		@ArucasFunction
		default NumberValue getXScale(Context context) {
			return NumberValue.of(this.getXScale());
		}

		@FunctionDoc(
			name = "getYScale",
			desc = "This gets the y scale of the shape",
			example = "shape.getYScale();"
		)
		@ArucasFunction
		default NumberValue getYScale(Context context) {
			return NumberValue.of(this.getYScale());
		}

		@FunctionDoc(
			name = "getZScale",
			desc = "This gets the z scale of the shape",
			example = "shape.getZScale();"
		)
		@ArucasFunction
		default NumberValue getZScale(Context context) {
			return NumberValue.of(this.getZScale());
		}
	}

	public interface Tiltable {
		float getXTilt();

		float getYTilt();

		float getZTilt();

		void setXTilt(float xTilt);

		void setYTilt(float yTilt);

		void setZTilt(float zTilt);

		@FunctionDoc(
			name = "setTilt",
			desc = "This sets the tilt of the shape",
			params = {
				NUMBER, "xTilt", "the x tilt",
				NUMBER, "yTilt", "the y tilt",
				NUMBER, "zTilt", "the z tilt"
			},
			example = "shape.setTilt(100, 0, 80);"
		)
		@ArucasFunction
		default void setTilt(Context context, NumberValue xTilt, NumberValue yTilt, NumberValue zTilt) {
			this.setXTilt(xTilt.value.floatValue());
			this.setYTilt(yTilt.value.floatValue());
			this.setZTilt(zTilt.value.floatValue());
		}

		@FunctionDoc(
			name = "setXTilt",
			desc = "This sets the x tilt of the shape",
			params = {NUMBER, "xTilt", "the x tilt"},
			example = "shape.setXTilt(100);"
		)
		@ArucasFunction
		default void setXTilt(Context context, NumberValue xTilt) {
			this.setXTilt(xTilt.value.floatValue());
		}

		@FunctionDoc(
			name = "setYTilt",
			desc = "This sets the y tilt of the shape",
			params = {NUMBER, "yTilt", "the y tilt"},
			example = "shape.setYTilt(100);"
		)
		@ArucasFunction
		default void setYTilt(Context context, NumberValue yTilt) {
			this.setYTilt(yTilt.value.floatValue());
		}

		@FunctionDoc(
			name = "setZTilt",
			desc = "This sets the z tilt of the shape",
			params = {NUMBER, "zTilt", "the z tilt"},
			example = "shape.setZTilt(100);"
		)
		@ArucasFunction
		default void setZTilt(Context context, NumberValue zTilt) {
			this.setZTilt(zTilt.value.floatValue());
		}

		@FunctionDoc(
			name = "getXTilt",
			desc = "This gets the x tilt of the shape",
			returns = {NUMBER, "the x tilt"},
			example = "shape.getXTilt();"
		)
		@ArucasFunction
		default NumberValue getXTilt(Context context) {
			return NumberValue.of(this.getXTilt());
		}

		@FunctionDoc(
			name = "getYTilt",
			desc = "This gets the y tilt of the shape",
			returns = {NUMBER, "the y tilt"},
			example = "shape.getYTilt();"
		)
		@ArucasFunction
		default NumberValue getYTilt(Context context) {
			return NumberValue.of(this.getYTilt());
		}

		@FunctionDoc(
			name = "getZTilt",
			desc = "This gets the z tilt of the shape",
			returns = {NUMBER, "the z tilt"},
			example = "shape.getZTilt();"
		)
		@ArucasFunction
		default NumberValue getZTilt(Context context) {
			return NumberValue.of(this.getZTilt());
		}
	}

	public interface Directional {
		Direction getDirection();

		void setDirection(Direction direction);

		@FunctionDoc(
			name = "setDirection",
			desc = "This sets the direction of the shape",
			params = {STRING, "direction", "the direction of the shape as a string, this can be 'north', 'south', 'east', 'west', 'down', or 'up'"},
			example = "shape.setDirection('down');"
		)
		@ArucasFunction
		default void setDirection(Context context, StringValue direction) {
			this.setDirection(Direction.byName(direction.value.toUpperCase(Locale.ROOT)));
		}

		@FunctionDoc(
			name = "getDirection",
			desc = "This gets the direction of the shape",
			returns = {STRING, "the direction of the shape as a string, this can be 'north', 'south', 'east', 'west', 'down', or 'up', null if it has no direction"},
			example = "shape.getDirection();"
		)
		@ArucasFunction
		default Value getDirection(Context context) {
			Direction direction = this.getDirection();
			return direction == null ? NullValue.NULL : StringValue.of(direction.getName());
		}
	}
	
	// Checkstyle on

	public static class CentreTiltableScalable extends CentrePositioned implements Tiltable, Scalable {
		private float xScale;
		private float yScale;
		private float zScale;

		private float xTilt;
		private float yTilt;
		private float zTilt;

		public CentreTiltableScalable() {
			this.setDefaultScale();
		}

		@Override
		public float getXScale() {
			return this.xScale;
		}

		@Override
		public float getYScale() {
			return this.yScale;
		}

		@Override
		public float getZScale() {
			return this.zScale;
		}

		@Override
		public void setXScale(float xScale) {
			this.xScale = xScale;
		}

		@Override
		public void setYScale(float yScale) {
			this.yScale = yScale;
		}

		@Override
		public void setZScale(float zScale) {
			this.zScale = zScale;
		}

		@Override
		public float getXTilt() {
			return this.xTilt;
		}

		@Override
		public float getYTilt() {
			return this.yTilt;
		}

		@Override
		public float getZTilt() {
			return this.zTilt;
		}

		@Override
		public void setXTilt(float xTilt) {
			this.xTilt = xTilt;
		}

		@Override
		public void setYTilt(float yTilt) {
			this.yTilt = yTilt;
		}

		@Override
		public void setZTilt(float zTilt) {
			this.zTilt = zTilt;
		}
	}

	public static class PositionTiltableScalable extends Positioned implements Tiltable, Scalable {
		private float xScale;
		private float yScale;
		private float zScale;

		private float xTilt;
		private float yTilt;
		private float zTilt;

		public PositionTiltableScalable() {
			this.setDefaultScale();
		}

		@Override
		public float getXScale() {
			return this.xScale;
		}

		@Override
		public float getYScale() {
			return this.yScale;
		}

		@Override
		public float getZScale() {
			return this.zScale;
		}

		@Override
		public void setXScale(float xScale) {
			this.xScale = xScale;
		}

		@Override
		public void setYScale(float yScale) {
			this.yScale = yScale;
		}

		@Override
		public void setZScale(float zScale) {
			this.zScale = zScale;
		}

		@Override
		public float getXTilt() {
			return this.xTilt;
		}

		@Override
		public float getYTilt() {
			return this.yTilt;
		}

		@Override
		public float getZTilt() {
			return this.zTilt;
		}

		@Override
		public void setXTilt(float xTilt) {
			this.xTilt = xTilt;
		}

		@Override
		public void setYTilt(float yTilt) {
			this.yTilt = yTilt;
		}

		@Override
		public void setZTilt(float zTilt) {
			this.zTilt = zTilt;
		}
	}
}