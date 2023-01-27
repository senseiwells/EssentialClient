package me.senseiwells.essentialclient.clientscript.definitions.shapes;

import me.senseiwells.arucas.api.docs.ClassDoc;
import me.senseiwells.arucas.api.docs.FunctionDoc;
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

import static me.senseiwells.essentialclient.clientscript.core.MinecraftAPI.CORNERED_SHAPE;
import static me.senseiwells.essentialclient.clientscript.core.MinecraftAPI.POS;

@ClassDoc(
	name = CORNERED_SHAPE,
	desc = "This class represents all shapes that use 2 corners to dictate their position",
	importPath = "Minecraft",
	superclass = OutlinedShapeDef.class,
	language = Util.Language.Java
)
public class CorneredShapeDef extends PrimitiveDefinition<ScriptShape.Cornered> {
	public CorneredShapeDef(Interpreter interpreter) {
		super(CORNERED_SHAPE, interpreter);
	}

	@NotNull
	@Override
	public PrimitiveDefinition<? super ScriptShape.Cornered> superclass() {
		return this.getPrimitiveDef(OutlinedShapeDef.class);
	}

	@Override
	public List<MemberFunction> defineMethods() {
		return List.of(
			MemberFunction.of("setPos1", 1, this::setPos1),
			MemberFunction.of("setPos2", 1, this::setPos2),
			MemberFunction.of("getPos1", this::getPos1),
			MemberFunction.of("getPos2", this::getPos2),
			MemberFunction.of("centrePositions", this::centrePositions)
		);
	}

	@FunctionDoc(
		name = "setPos1",
		desc = "This sets the first position of the shape",
		params = {POS, "pos1", "the first position of the shape"},
		examples = "shape.setPos1(new Pos(1, 0, 100));"
	)
	private Void setPos1(Arguments arguments) {
		ScriptShape.Cornered shape = arguments.nextPrimitive(this);
		ScriptPos pos = arguments.nextPrimitive(PosDef.class);
		shape.setCornerA(pos.getVec3d());
		return null;
	}

	@FunctionDoc(
		name = "setPos2",
		desc = "This sets the second position of the shape",
		params = {POS, "pos2", "the second position of the shape"},
		examples = "shape.setPos2(new Pos(1, 0, 100));"
	)
	private Void setPos2(Arguments arguments) {
		ScriptShape.Cornered shape = arguments.nextPrimitive(this);
		ScriptPos pos = arguments.nextPrimitive(PosDef.class);
		shape.setCornerB(pos.getVec3d());
		return null;
	}

	@FunctionDoc(
		name = "getPos1",
		desc = "This gets the first position of the shape",
		returns = {POS, "the first position of the shape"},
		examples = "shape.getPos1();"
	)
	private ScriptPos getPos1(Arguments arguments) {
		ScriptShape.Cornered shape = arguments.nextPrimitive(this);
		return new ScriptPos(shape.getCornerA());
	}

	@FunctionDoc(
		name = "getPos2",
		desc = "This gets the second position of the shape",
		returns = {POS, "the second position of the shape"},
		examples = "shape.getPos2();"
	)
	private ScriptPos getPos2(Arguments arguments) {
		ScriptShape.Cornered shape = arguments.nextPrimitive(this);
		return new ScriptPos(shape.getCornerB());
	}

	@FunctionDoc(
		name = "centrePositions",
		desc = "This centres the positions of the shape",
		examples = "shape.centrePositions();"
	)
	private Void centrePositions(Arguments arguments) {
		ScriptShape.Cornered shape = arguments.nextPrimitive(this);
		shape.setCornerA(shape.getCornerA().floorAlongAxes(EnumSet.of(Direction.Axis.X, Direction.Axis.Y, Direction.Axis.Z)));
		return null;
	}
}
