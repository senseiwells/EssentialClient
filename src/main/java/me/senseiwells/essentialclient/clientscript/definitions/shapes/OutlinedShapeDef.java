package me.senseiwells.essentialclient.clientscript.definitions.shapes;

import me.senseiwells.arucas.api.docs.FunctionDoc;
import me.senseiwells.arucas.builtin.NumberDef;
import me.senseiwells.arucas.classes.PrimitiveDefinition;
import me.senseiwells.arucas.core.Interpreter;
import me.senseiwells.arucas.utils.Arguments;
import me.senseiwells.arucas.utils.MemberFunction;
import me.senseiwells.essentialclient.utils.clientscript.impl.ScriptShape;

import java.util.List;

import static me.senseiwells.arucas.utils.Util.Types.NUMBER;
import static me.senseiwells.essentialclient.clientscript.core.MinecraftAPI.OUTLINED_SHAPE;

// TODO
public class OutlinedShapeDef extends PrimitiveDefinition<ScriptShape.Outlined> {
	public OutlinedShapeDef(Interpreter interpreter) {
		super(OUTLINED_SHAPE, interpreter);
	}

	@Override
	public PrimitiveDefinition<? super ScriptShape.Outlined> superclass() {
		return this.getPrimitiveDef(ShapeDef.class);
	}

	@Override
	public List<MemberFunction> defineMethods() {
		return List.of(
			MemberFunction.of("setOutlineColour", 1, this::setOutlineColour1),
			MemberFunction.of("setOutlineColor", 1, this::setOutlineColour1),
			MemberFunction.of("setOutlineColour", 3, this::setOutlineColour3),
			MemberFunction.of("setOutlineColor", 3, this::setOutlineColour3),
			MemberFunction.of("setOutlineRed", 1, this::setOutlineRed),
			MemberFunction.of("setOutlineGreen", 1, this::setOutlineGreen),
			MemberFunction.of("setOutlineBlue", 1, this::setOutlineBlue),
			MemberFunction.of("setOutlinePixelWidth", 1, this::setOutlineWidth, "Use <Shape>.setOutlineWidth(width) instead"),
			MemberFunction.of("setOutlineWidth", 1, this::setOutlineWidth),
			MemberFunction.of("getOutlineRed", this::getOutlineRed),
			MemberFunction.of("getOutlineGreen", this::getOutlineGreen),
			MemberFunction.of("getOutlineBlue", this::getOutlineBlue),
			MemberFunction.of("getOutlinePixelWidth", this::getOutlineWidth)
		);
	}

	@FunctionDoc(
		name = "setOutlineColour",
		desc = {
			"This sets the width of the shape, using a single value, this function",
			"also has a sibling named `setOutlineColor()` that has the same functionality.",
			"The colour generally should be hexadecimal in the form 0xRRGGBB"
		},
		params = {NUMBER, "colour", "the colour you want to set"},
		examples = "shape.setOutlineColour(0xFF00FF);"
	)
	private Void setOutlineColour1(Arguments arguments) {
		ScriptShape.Outlined shape = arguments.nextPrimitive(this);
		int colour = arguments.nextPrimitive(NumberDef.class).intValue();
		shape.setOutlineColour(colour);
		return null;
	}

	@FunctionDoc(
		name = "setOutlineColour",
		desc = {
			"This sets the outline colour of the shape, using three values, this function",
			"also has a sibling named `setOutlineColor()` that has the same functionality.",
			"If the colours are not between 0 and 255 an error will be thrown"
		},
		params = {
			NUMBER, "red", "the amount of red 0 - 255",
			NUMBER, "green", "the amount of green 0 - 255",
			NUMBER, "blue", "the amount of blue 0 - 255"
		},
		examples = "shape.setOutlineColour(255, 0, 255);"
	)
	private Void setOutlineColour3(Arguments arguments) {
		ScriptShape.Outlined shape = arguments.nextPrimitive(this);
		int red = arguments.nextPrimitive(NumberDef.class).intValue();
		int green = arguments.nextPrimitive(NumberDef.class).intValue();
		int blue = arguments.nextPrimitive(NumberDef.class).intValue();
		shape.setRed(red);
		shape.setGreen(green);
		shape.setBlue(blue);
		return null;
	}

	@FunctionDoc(
		name = "setOutlineRed",
		desc = "This sets the outline red value of the shape, using a single value",
		params = {NUMBER, "red", "the amount of red between 0 - 255"},
		examples = "shape.setOutlineRed(34);"
	)
	private Void setOutlineRed(Arguments arguments) {
		ScriptShape.Outlined shape = arguments.nextPrimitive(this);
		int red = arguments.nextPrimitive(NumberDef.class).intValue();
		shape.setRed(red);
		return null;
	}

	@FunctionDoc(
		name = "setOutlineGreen",
		desc = "This sets the outline green value of the shape, using a single value",
		params = {NUMBER, "green", "the amount of green between 0 - 255"},
		examples = "shape.setOutlineGreen(34);"
	)
	private Void setOutlineGreen(Arguments arguments) {
		ScriptShape.Outlined shape = arguments.nextPrimitive(this);
		int green = arguments.nextPrimitive(NumberDef.class).intValue();
		shape.setGreen(green);
		return null;
	}

	@FunctionDoc(
		name = "setOutlineBlue",
		desc = "This sets the outline blue value of the shape, using a single value",
		params = {NUMBER, "blue", "the amount of blue between 0 - 255"},
		examples = "shape.setOutlineBlue(34);"
	)
	private Void setOutlineBlue(Arguments arguments) {
		ScriptShape.Outlined shape = arguments.nextPrimitive(this);
		int blue = arguments.nextPrimitive(NumberDef.class).intValue();
		shape.setBlue(blue);
		return null;
	}

	@FunctionDoc(
		name = "setOutlineWidth",
		desc = "This sets the outline width of the shape, this should not be negative",
		params = {NUMBER, "width", "the width of the outline"},
		examples = "shape.setOutlineWidth(2);"
	)
	private Void setOutlineWidth(Arguments arguments) {
		ScriptShape.Outlined shape = arguments.nextPrimitive(this);
		int width = arguments.nextPrimitive(NumberDef.class).intValue();
		shape.setOutlineWidth(width);
		return null;
	}

	@FunctionDoc(
		name = "getOutlineRed",
		desc = "This gets the outline red value of the shape",
		returns = {NUMBER, "the red value of the outline"},
		examples = "shape.getOutlineRed();"
	)
	private Number getOutlineRed(Arguments arguments) {
		return arguments.nextPrimitive(this).getRed();
	}

	@FunctionDoc(
		name = "getOutlineGreen",
		desc = "This gets the outline green value of the shape",
		returns = {NUMBER, "the green value of the outline"},
		examples = "shape.getOutlineGreen();"
	)
	private Number getOutlineGreen(Arguments arguments) {
		return arguments.nextPrimitive(this).getGreen();
	}

	@FunctionDoc(
		name = "getOutlineBlue",
		desc = "This gets the outline blue value of the shape",
		returns = {NUMBER, "the blue value of the outline"},
		examples = "shape.getOutlineBlue();"
	)
	private Number getOutlineBlue(Arguments arguments) {
		return arguments.nextPrimitive(this).getBlue();
	}

	@FunctionDoc(
		name = "getOutlineWidth",
		desc = "This gets the outline width of the shape",
		returns = {NUMBER, "the width of the outline"},
		examples = "shape.getOutlineWidth();"
	)
	private Number getOutlineWidth(Arguments arguments) {
		return arguments.nextPrimitive(this).getOutlineWidth();
	}
}
