package me.senseiwells.essentialclient.clientscript.definitions.shapes;

import me.senseiwells.arucas.api.docs.annotations.ClassDoc;
import me.senseiwells.arucas.api.docs.annotations.FunctionDoc;
import me.senseiwells.arucas.api.docs.annotations.ParameterDoc;
import me.senseiwells.arucas.api.docs.annotations.ReturnDoc;
import me.senseiwells.arucas.builtin.NumberDef;
import me.senseiwells.arucas.classes.PrimitiveDefinition;
import me.senseiwells.arucas.core.Interpreter;
import me.senseiwells.arucas.utils.Arguments;
import me.senseiwells.arucas.utils.MemberFunction;
import me.senseiwells.arucas.utils.Util;
import me.senseiwells.essentialclient.clientscript.definitions.PosDef;
import me.senseiwells.essentialclient.utils.clientscript.impl.ScriptPos;
import me.senseiwells.essentialclient.utils.clientscript.impl.ScriptShape;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.List;

import static me.senseiwells.essentialclient.clientscript.core.MinecraftAPI.CENTRED_SHAPE;

@ClassDoc(
	name = CENTRED_SHAPE,
	desc = "This class represents shapes that are positioned centrally with a width",
	superclass = OutlinedShapeDef.class,
	language = Util.Language.Java
)
public class CentredShapeDef extends PrimitiveDefinition<ScriptShape.Centred> {
	public CentredShapeDef(Interpreter interpreter) {
		super(CENTRED_SHAPE, interpreter);
	}

	@NotNull
	@Override
	public PrimitiveDefinition<? super ScriptShape.Centred> superclass() {
		return this.getPrimitiveDef(OutlinedShapeDef.class);
	}

	@Override
	public List<MemberFunction> defineMethods() {
		return List.of(
			MemberFunction.of("setPos", 1, this::setPos),
			MemberFunction.of("setWidth", 1, this::setWidth),
			MemberFunction.of("getPos", this::getPos),
			MemberFunction.of("getWidth", this::getWidth),
			MemberFunction.of("centrePositions", this::centrePositions)
		);
	}

	@FunctionDoc(
		name = "setPos",
		desc = "This sets the central position of the shape",
		params = {@ParameterDoc(type = PosDef.class, name = "pos", desc = "the central position of the shape")},
		examples = "shape.setPos(new Pos(1, 0, 100));"
	)
	private Void setPos(Arguments arguments) {
		ScriptShape.Centred shape = arguments.nextPrimitive(this);
		ScriptPos pos = arguments.nextPrimitive(PosDef.class);
		shape.setPosition(pos.getVec3d());
		return null;
	}

	@FunctionDoc(
		name = "setWidth",
		desc = "This sets the width of the shape",
		params = {@ParameterDoc(type = NumberDef.class, name = "width", desc = "the width of the shape")},
		examples = "shape.setWidth(10.5);"
	)
	private Void setWidth(Arguments arguments) {
		ScriptShape.Centred shape = arguments.nextPrimitive(this);
		float width = arguments.nextPrimitive(NumberDef.class).floatValue();
		shape.setWidth(width);
		return null;
	}

	@FunctionDoc(
		name = "getPos",
		desc = "This gets the central position of the shape",
		returns = @ReturnDoc(type = PosDef.class, desc = "the central position of the shape"),
		examples = "shape.getPos();"
	)
	private ScriptPos getPos(Arguments arguments) {
		ScriptShape.Centred shape = arguments.nextPrimitive(this);
		return new ScriptPos(shape.getPosition());
	}

	@FunctionDoc(
		name = "getWidth",
		desc = "This gets the width of the shape",
		returns = @ReturnDoc(type = NumberDef.class, desc = "the width of the shape"),
		examples = "shape.getWidth();"
	)
	private float getWidth(Arguments arguments) {
		ScriptShape.Centred shape = arguments.nextPrimitive(this);
		return shape.getWidth();
	}

	@FunctionDoc(
		name = "centrePositions",
		desc = "This centres the positions of the shape",
		examples = "shape.centrePositions();"
	)
	private Void centrePositions(Arguments arguments) {
		ScriptShape.Centred shape = arguments.nextPrimitive(this);
		shape.setPosition(shape.getPosition().floorAlongAxes(EnumSet.of(Direction.Axis.X, Direction.Axis.Y, Direction.Axis.Z)));
		return null;
	}
}
