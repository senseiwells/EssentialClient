package me.senseiwells.essentialclient.clientscript.definitions.shapes;

import me.senseiwells.arucas.api.docs.annotations.ClassDoc;
import me.senseiwells.arucas.api.docs.annotations.FunctionDoc;
import me.senseiwells.arucas.api.docs.annotations.ParameterDoc;
import me.senseiwells.arucas.api.docs.annotations.ReturnDoc;
import me.senseiwells.arucas.builtin.BooleanDef;
import me.senseiwells.arucas.builtin.ListDef;
import me.senseiwells.arucas.builtin.NumberDef;
import me.senseiwells.arucas.classes.PrimitiveDefinition;
import me.senseiwells.arucas.functions.builtin.Arguments;
import me.senseiwells.arucas.functions.builtin.MemberFunction;
import me.senseiwells.arucas.interpreter.Interpreter;
import me.senseiwells.arucas.utils.impl.ArucasList;
import me.senseiwells.arucas.utils.misc.Language;
import me.senseiwells.essentialclient.utils.clientscript.impl.ScriptShape;

import java.util.List;

import static me.senseiwells.essentialclient.clientscript.core.MinecraftAPI.SHAPE;

@ClassDoc(
	name = SHAPE,
	desc = {
		"This class is the base class for all shapes that can be rendered,",
		"providing the base functionality for all shapes"
	},
	language = Language.Java
)
public class ShapeDef extends PrimitiveDefinition<ScriptShape> {
	public ShapeDef(Interpreter interpreter) {
		super(SHAPE, interpreter);
	}

	@Override
	public List<MemberFunction> defineMethods() {
		return List.of(
			MemberFunction.of("setColour", 1, this::setColour1),
			MemberFunction.of("setColor", 1, this::setColour1),
			MemberFunction.of("setColour", 3, this::setColour3),
			MemberFunction.of("setColor", 3, this::setColour3),
			MemberFunction.of("setRed", 1, this::setRed),
			MemberFunction.of("setGreen", 1, this::setGreen),
			MemberFunction.of("setBlue", 1, this::setBlue),
			MemberFunction.of("setOpacity", 1, this::setOpacity),
			MemberFunction.of("setRenderThroughBlocks", 1, this::setRenderThroughBlocks),
			MemberFunction.of("getRed", this::getRed),
			MemberFunction.of("getGreen", this::getGreen),
			MemberFunction.of("getBlue", this::getBlue),
			MemberFunction.of("getOpacity", this::getOpacity),
			MemberFunction.of("getRGB", this::getRGB),
			MemberFunction.of("getRGBList", this::getRGBList),
			MemberFunction.of("getRGBAList", this::getRGBAList),
			MemberFunction.of("shouldRenderThroughBlocks", this::shouldRenderThroughBlocks),

			MemberFunction.of("render", this::render),
			MemberFunction.of("stopRendering", this::stopRendering),

			MemberFunction.of("setScale", 3, this::setScale),
			MemberFunction.of("setXScale", 1, this::setXScale),
			MemberFunction.of("setYScale", 1, this::setYScale),
			MemberFunction.of("setZScale", 1, this::setZScale),
			MemberFunction.of("getXScale", this::getXScale),
			MemberFunction.of("getYScale", this::getYScale),
			MemberFunction.of("getZScale", this::getZScale),

			MemberFunction.of("setTilt", 3, this::setTilt),
			MemberFunction.of("setXTilt", 1, this::setXTilt),
			MemberFunction.of("setYTilt", 1, this::setYTilt),
			MemberFunction.of("setZTilt", 1, this::setZTilt),
			MemberFunction.of("getXTilt", this::getXTilt),
			MemberFunction.of("getYTilt", this::getYTilt),
			MemberFunction.of("getZTilt", this::getZTilt)
		);
	}

	@FunctionDoc(
		name = "setColour",
		desc = {
			"This sets the colour of the shape, using a single value, this",
			"function also has a sibling named `setColor()` that has the same functionality.",
			"The colour generally should be hexadecimal in the form 0xRRGGBB"
		},
		params = {@ParameterDoc(type = NumberDef.class, name = "colour", desc = "the colour you want to set")},
		examples = "shape.setColour(0xFF0000);"
	)
	private Void setColour1(Arguments arguments) {
		ScriptShape shape = arguments.nextPrimitive(this);
		int colour = arguments.nextPrimitive(NumberDef.class).intValue();
		shape.setColour(colour);
		return null;
	}

	@FunctionDoc(
		name = "setColour",
		desc = {
			"This sets the colour of the shape, using three values this function",
			"also has a sibling named `setColor()` that has the same functionality.",
			"If the colours are not between 0 and 255 an error will be thrown"
		},
		params = {
			@ParameterDoc(type = NumberDef.class, name = "red", desc = "the amount of red 0 - 255"),
			@ParameterDoc(type = NumberDef.class, name = "green", desc = "the amount of green 0 - 255"),
			@ParameterDoc(type = NumberDef.class, name = "blue", desc = "the amount of blue 0 - 255")
		},
		examples = "shape.setColour(34, 55, 0);"
	)
	private Void setColour3(Arguments arguments) {
		ScriptShape shape = arguments.nextPrimitive(this);
		int red = arguments.nextPrimitive(NumberDef.class).intValue();
		int green = arguments.nextPrimitive(NumberDef.class).intValue();
		int blue = arguments.nextPrimitive(NumberDef.class).intValue();
		shape.setRed(red);
		shape.setGreen(green);
		shape.setBlue(blue);
		return null;
	}

	@FunctionDoc(
		name = "setRed",
		desc = {
			"This sets the red value of the shape, using a single value.",
			"If the colour is not between 0 and 255 an error will be thrown"
		},
		params = {@ParameterDoc(type = NumberDef.class, name = "red", desc = "the amount of red between 0 - 255")},
		examples = "shape.setRed(34);"
	)
	private Void setRed(Arguments arguments) {
		ScriptShape shape = arguments.nextPrimitive(this);
		int red = arguments.nextPrimitive(NumberDef.class).intValue();
		shape.setRed(red);
		return null;
	}

	@FunctionDoc(
		name = "setGreen",
		desc = {
			"This sets the green value of the shape, using a single value.",
			"If the colour is not between 0 and 255 an error will be thrown"
		},
		params = {@ParameterDoc(type = NumberDef.class, name = "green", desc = "the amount of green between 0 - 255")},
		examples = "shape.setGreen(34);"
	)
	private Void setGreen(Arguments arguments) {
		ScriptShape shape = arguments.nextPrimitive(this);
		int green = arguments.nextPrimitive(NumberDef.class).intValue();
		shape.setGreen(green);
		return null;
	}

	@FunctionDoc(
		name = "setBlue",
		desc = {
			"This sets the blue value of the shape, using a single value.",
			"If the colour is not between 0 and 255 an error will be thrown"
		},
		params = {@ParameterDoc(type = NumberDef.class, name = "blue", desc = "the amount of blue between 0 - 255")},
		examples = "shape.setBlue(34);"
	)
	private Void setBlue(Arguments arguments) {
		ScriptShape shape = arguments.nextPrimitive(this);
		int blue = arguments.nextPrimitive(NumberDef.class).intValue();
		shape.setBlue(blue);
		return null;
	}

	@FunctionDoc(
		name = "setOpacity",
		desc = {
			"This sets the opacity of the shape, using a single value.",
			"If the colour is not between 0 and 255 an error will be thrown"
		},
		params = {@ParameterDoc(type = NumberDef.class, name = "opacity", desc = "the amount of opacity between 0 - 255")},
		examples = "shape.setOpacity(34);"
	)
	private Void setOpacity(Arguments arguments) {
		ScriptShape shape = arguments.nextPrimitive(this);
		int opacity = arguments.nextPrimitive(NumberDef.class).intValue();
		shape.setAlpha(opacity);
		return null;
	}

	@FunctionDoc(
		name = "setRenderThroughBlocks",
		desc = "This sets whether the shape should render through blocks",
		params = {@ParameterDoc(type = BooleanDef.class, name = "boolean", desc = "whether the shape should render through blocks")},
		examples = "shape.setRenderThroughBlocks(true);"
	)
	private Void setRenderThroughBlocks(Arguments arguments) {
		ScriptShape shape = arguments.nextPrimitive(this);
		boolean renderThroughBlocks = arguments.nextPrimitive(BooleanDef.class);
		shape.setIgnoreDepth(renderThroughBlocks);
		return null;
	}

	@FunctionDoc(
		name = "getRed",
		desc = "This returns the red value of the shape",
		returns = @ReturnDoc(type = NumberDef.class, desc = "the red value of the shape"),
		examples = "shape.getRed();"
	)
	private int getRed(Arguments arguments) {
		return arguments.nextPrimitive(this).getRed();
	}

	@FunctionDoc(
		name = "getGreen",
		desc = "This returns the green value of the shape",
		returns = @ReturnDoc(type = NumberDef.class, desc = "the green value of the shape"),
		examples = "shape.getGreen();"
	)
	private int getGreen(Arguments arguments) {
		return arguments.nextPrimitive(this).getGreen();
	}

	@FunctionDoc(
		name = "getBlue",
		desc = "This returns the blue value of the shape",
		returns = @ReturnDoc(type = NumberDef.class, desc = "the blue value of the shape"),
		examples = "shape.getBlue();"
	)
	private int getBlue(Arguments arguments) {
		return arguments.nextPrimitive(this).getBlue();
	}

	@FunctionDoc(
		name = "getOpacity",
		desc = "This returns the opacity of the shape",
		returns = @ReturnDoc(type = NumberDef.class, desc = "the opacity of the shape"),
		examples = "shape.getOpacity();"
	)
	private int getOpacity(Arguments arguments) {
		return arguments.nextPrimitive(this).getAlpha();
	}

	@FunctionDoc(
		name = "getRGB",
		desc = "This returns the RGB value of the shape",
		returns = @ReturnDoc(type = NumberDef.class, desc = "the RGB value of the shape as a single number in the form 0xRRGGBB"),
		examples = "shape.getRGB();"
	)
	private int getRGB(Arguments arguments) {
		return arguments.nextPrimitive(this).getColour();
	}

	@FunctionDoc(
		name = "getRGBList",
		desc = "This returns the RGB value of the shape as a list",
		returns = @ReturnDoc(type = ListDef.class, desc = "the RGB value of the shape as a list in the form [red, green, blue]"),
		examples = "r, g, b = shape.getRGBList();"
	)
	private ArucasList getRGBList(Arguments arguments) {
		ScriptShape shape = arguments.nextPrimitive(this);
		ArucasList list = new ArucasList();
		Interpreter interpreter = arguments.getInterpreter();
		list.add(interpreter.create(NumberDef.class, (double) shape.getRed()));
		list.add(interpreter.create(NumberDef.class, (double) shape.getGreen()));
		list.add(interpreter.create(NumberDef.class, (double) shape.getBlue()));
		return list;
	}

	@FunctionDoc(
		name = "getRGBAList",
		desc = "This returns the RGBA value of the shape as a list",
		returns = @ReturnDoc(type = ListDef.class, desc = "the RGBA value of the shape as a list in the form [red, green, blue, opacity]"),
		examples = "r, g, b, a = shape.getRGBAList();"
	)
	private ArucasList getRGBAList(Arguments arguments) { // Checkstyle ignore
		ScriptShape shape = arguments.nextPrimitive(this);
		ArucasList list = new ArucasList();
		Interpreter interpreter = arguments.getInterpreter();
		list.add(interpreter.create(NumberDef.class, (double) shape.getRed()));
		list.add(interpreter.create(NumberDef.class, (double) shape.getGreen()));
		list.add(interpreter.create(NumberDef.class, (double) shape.getBlue()));
		list.add(interpreter.create(NumberDef.class, (double) shape.getAlpha()));
		return list;
	}

	@FunctionDoc(
		name = "shouldRenderThroughBlocks",
		desc = "This returns whether the shape should render through blocks",
		returns = @ReturnDoc(type = BooleanDef.class, desc = "whether the shape should render through blocks"),
		examples = "shape.shouldRenderThroughBlocks();"
	)
	private boolean shouldRenderThroughBlocks(Arguments arguments) {
		return arguments.nextPrimitive(this).shouldIgnoreDepth();
	}

	@FunctionDoc(
		name = "render",
		desc = {
			"This sets the shape to be rendered indefinitely, the shape will only stop rendering when",
			"the script ends or when you call the stopRendering() method"
		},
		examples = "shape.render();"
	)
	private Void render(Arguments arguments) {
		ScriptShape shape = arguments.nextPrimitive(this);
		shape.render(true);
		return null;
	}

	@FunctionDoc(
		name = "stopRendering",
		desc = "This stops the shape from rendering",
		examples = "shape.stopRendering();"
	)
	private Void stopRendering(Arguments arguments) {
		ScriptShape shape = arguments.nextPrimitive(this);
		shape.render(false);
		return null;
	}

	@FunctionDoc(
		name = "setScale",
		desc = "This sets the scale of the shape",
		params = {
			@ParameterDoc(type = NumberDef.class, name = "xScale", desc = "the x scale of the shape"),
			@ParameterDoc(type = NumberDef.class, name = "yScale", desc = "the y scale of the shape"),
			@ParameterDoc(type = NumberDef.class, name = "zScale", desc = "the z scale of the shape")
		},
		examples = "shape.setScale(1.5, 2.5, 3.5);"
	)
	private Void setScale(Arguments arguments) {
		ScriptShape shape = arguments.nextPrimitive(this);
		float xScale = arguments.nextPrimitive(NumberDef.class).floatValue();
		float yScale = arguments.nextPrimitive(NumberDef.class).floatValue();
		float zScale = arguments.nextPrimitive(NumberDef.class).floatValue();
		shape.setXScale(xScale);
		shape.setYScale(yScale);
		shape.setZScale(zScale);
		return null;
	}

	@FunctionDoc(
		name = "setXScale",
		desc = "This sets the x scale of the shape",
		params = {@ParameterDoc(type = NumberDef.class, name = "xScale", desc = "the x scale of the shape")},
		examples = "shape.setXScale(1.5);"
	)
	private Void setXScale(Arguments arguments) {
		ScriptShape shape = arguments.nextPrimitive(this);
		float xScale = arguments.nextPrimitive(NumberDef.class).floatValue();
		shape.setXScale(xScale);
		return null;
	}

	@FunctionDoc(
		name = "setYScale",
		desc = "This sets the y scale of the shape",
		params = {@ParameterDoc(type = NumberDef.class, name = "yScale", desc = "the y scale of the shape")},
		examples = "shape.setYScale(2.5);"
	)
	private Void setYScale(Arguments arguments) {
		ScriptShape shape = arguments.nextPrimitive(this);
		float yScale = arguments.nextPrimitive(NumberDef.class).floatValue();
		shape.setYScale(yScale);
		return null;
	}

	@FunctionDoc(
		name = "setZScale",
		desc = "This sets the z scale of the shape",
		params = {@ParameterDoc(type = NumberDef.class, name = "zScale", desc = "the z scale of the shape")},
		examples = "shape.setZScale(3.5);"
	)
	private Void setZScale(Arguments arguments) {
		ScriptShape shape = arguments.nextPrimitive(this);
		float zScale = arguments.nextPrimitive(NumberDef.class).floatValue();
		shape.setZScale(zScale);
		return null;
	}

	@FunctionDoc(
		name = "getXScale",
		desc = "This gets the x scale of the shape",
		examples = "shape.getXScale();"
	)
	private float getXScale(Arguments arguments) {
		ScriptShape shape = arguments.nextPrimitive(this);
		return shape.getXScale();
	}

	@FunctionDoc(
		name = "getYScale",
		desc = "This gets the y scale of the shape",
		examples = "shape.getYScale();"
	)
	private float getYScale(Arguments arguments) {
		ScriptShape shape = arguments.nextPrimitive(this);
		return shape.getYScale();
	}

	@FunctionDoc(
		name = "getZScale",
		desc = "This gets the z scale of the shape",
		examples = "shape.getZScale();"
	)
	private float getZScale(Arguments arguments) {
		ScriptShape shape = arguments.nextPrimitive(this);
		return shape.getZScale();
	}

	@FunctionDoc(
		name = "setTilt",
		desc = "This sets the tilt of the shape",
		params = {
			@ParameterDoc(type = NumberDef.class, name = "xTilt", desc = "the x tilt"),
			@ParameterDoc(type = NumberDef.class, name = "yTilt", desc = "the y tilt"),
			@ParameterDoc(type = NumberDef.class, name = "zTilt", desc = "the z tilt")
		},
		examples = "shape.setTilt(100, 0, 80);"
	)
	private Void setTilt(Arguments arguments) {
		ScriptShape shape = arguments.nextPrimitive(this);
		float xTilt = arguments.nextPrimitive(NumberDef.class).floatValue();
		float yTilt = arguments.nextPrimitive(NumberDef.class).floatValue();
		float zTilt = arguments.nextPrimitive(NumberDef.class).floatValue();
		shape.setXTilt(xTilt);
		shape.setYTilt(yTilt);
		shape.setZTilt(zTilt);
		return null;
	}

	@FunctionDoc(
		name = "setXTilt",
		desc = "This sets the x tilt of the shape",
		params = {@ParameterDoc(type = NumberDef.class, name = "xTilt", desc = "the x tilt")},
		examples = "shape.setXTilt(100);"
	)
	private Void setXTilt(Arguments arguments) {
		ScriptShape shape = arguments.nextPrimitive(this);
		float xTilt = arguments.nextPrimitive(NumberDef.class).floatValue();
		shape.setXTilt(xTilt);
		return null;
	}

	@FunctionDoc(
		name = "setYTilt",
		desc = "This sets the y tilt of the shape",
		params = {@ParameterDoc(type = NumberDef.class, name = "yTilt", desc = "the y tilt")},
		examples = "shape.setYTilt(0);"
	)
	private Void setYTilt(Arguments arguments) {
		ScriptShape shape = arguments.nextPrimitive(this);
		float yTilt = arguments.nextPrimitive(NumberDef.class).floatValue();
		shape.setYTilt(yTilt);
		return null;
	}

	@FunctionDoc(
		name = "setZTilt",
		desc = "This sets the z tilt of the shape",
		params = {@ParameterDoc(type = NumberDef.class, name = "zTilt", desc = "the z tilt")},
		examples = "shape.setZTilt(80);"
	)
	private Void setZTilt(Arguments arguments) {
		ScriptShape shape = arguments.nextPrimitive(this);
		float zTilt = arguments.nextPrimitive(NumberDef.class).floatValue();
		shape.setZTilt(zTilt);
		return null;
	}

	@FunctionDoc(
		name = "getXTilt",
		desc = "This gets the x tilt of the shape",
		examples = "shape.getXTilt();"
	)
	private float getXTilt(Arguments arguments) {
		ScriptShape shape = arguments.nextPrimitive(this);
		return shape.getXTilt();
	}

	@FunctionDoc(
		name = "getYTilt",
		desc = "This gets the y tilt of the shape",
		examples = "shape.getYTilt();"
	)
	private float getYTilt(Arguments arguments) {
		ScriptShape shape = arguments.nextPrimitive(this);
		return shape.getYTilt();
	}

	@FunctionDoc(
		name = "getZTilt",
		desc = "This gets the z tilt of the shape",
		examples = "shape.getZTilt();"
	)
	private float getZTilt(Arguments arguments) {
		ScriptShape shape = arguments.nextPrimitive(this);
		return shape.getZTilt();
	}
}
