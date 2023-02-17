package me.senseiwells.essentialclient.clientscript.definitions.shapes;

import kotlin.Unit;
import me.senseiwells.arucas.api.docs.annotations.ClassDoc;
import me.senseiwells.arucas.api.docs.annotations.ConstructorDoc;
import me.senseiwells.arucas.api.docs.annotations.ParameterDoc;
import me.senseiwells.arucas.builtin.NumberDef;
import me.senseiwells.arucas.classes.CreatableDefinition;
import me.senseiwells.arucas.classes.PrimitiveDefinition;
import me.senseiwells.arucas.classes.instance.ClassInstance;
import me.senseiwells.arucas.core.Interpreter;
import me.senseiwells.arucas.utils.Arguments;
import me.senseiwells.arucas.utils.ConstructorFunction;
import me.senseiwells.arucas.utils.Util;
import me.senseiwells.essentialclient.clientscript.definitions.PosDef;
import me.senseiwells.essentialclient.utils.clientscript.impl.ScriptLine;
import me.senseiwells.essentialclient.utils.clientscript.impl.ScriptPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static me.senseiwells.essentialclient.clientscript.core.MinecraftAPI.LINE_SHAPE;

@ClassDoc(
	name = LINE_SHAPE,
	desc = "This class allows you to create a line shape which can be used to draw lines in the world.",
	superclass = CorneredShapeDef.class,
	language = Util.Language.Java
)
public class LineShapeDef extends CreatableDefinition<ScriptLine> {
	public LineShapeDef(Interpreter interpreter) {
		super(LINE_SHAPE, interpreter);
	}

	@NotNull
	@Override
	public PrimitiveDefinition<? super ScriptLine> superclass() {
		return this.getPrimitiveDef(CorneredShapeDef.class);
	}

	@Override
	public List<ConstructorFunction> defineConstructors() {
		return List.of(
			ConstructorFunction.of(2, this::construct2),
			ConstructorFunction.of(6, this::construct6)
		);
	}

	@ConstructorDoc(
		desc = "Creates a new line shape",
		params = {
			@ParameterDoc(type = PosDef.class, name = "pos1", desc = "The starting position of the line"),
			@ParameterDoc(type = PosDef.class, name = "pos2", desc = "The ending position of the line")
		},
		examples = "new LineShape(new Pos(0, 0, 0), new Pos(1, 1, 1));"
	)
	private Unit construct2(Arguments arguments) {
		ClassInstance instance = arguments.next();
		ScriptPos pos1 = arguments.nextPrimitive(PosDef.class);
		ScriptPos pos2 = arguments.nextPrimitive(PosDef.class);
		instance.setPrimitive(this, new ScriptLine(arguments.getInterpreter(), pos1.getVec3d(), pos2.getVec3d()));
		return null;
	}

	@ConstructorDoc(
		desc = "Creates a new line shape",
		params = {
			@ParameterDoc(type = NumberDef.class, name = "x1", desc = "The x position of the starting position of the line"),
			@ParameterDoc(type = NumberDef.class, name = "y1", desc = "The y position of the starting position of the line"),
			@ParameterDoc(type = NumberDef.class, name = "z1", desc = "The z position of the starting position of the line"),
			@ParameterDoc(type = NumberDef.class, name = "x2", desc = "The x position of the ending position of the line"),
			@ParameterDoc(type = NumberDef.class, name = "y2", desc = "The y position of the ending position of the line"),
			@ParameterDoc(type = NumberDef.class, name = "z2", desc = "The z position of the ending position of the line")
		},
		examples = "new LineShape(0, 0, 0, 1, 1, 1);"
	)
	private Unit construct6(Arguments arguments) {
		ClassInstance instance = arguments.next();
		double x1 = arguments.nextPrimitive(NumberDef.class);
		double y1 = arguments.nextPrimitive(NumberDef.class);
		double z1 = arguments.nextPrimitive(NumberDef.class);
		double x2 = arguments.nextPrimitive(NumberDef.class);
		double y2 = arguments.nextPrimitive(NumberDef.class);
		double z2 = arguments.nextPrimitive(NumberDef.class);
		Vec3d first = new Vec3d(x1, y1, z1);
		Vec3d second = new Vec3d(x2, y2, z2);
		instance.setPrimitive(this, new ScriptLine(arguments.getInterpreter(), first, second));
		return null;
	}
}
