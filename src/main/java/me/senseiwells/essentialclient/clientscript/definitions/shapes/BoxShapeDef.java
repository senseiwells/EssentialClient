package me.senseiwells.essentialclient.clientscript.definitions.shapes;

import kotlin.Unit;
import me.senseiwells.arucas.api.docs.ClassDoc;
import me.senseiwells.arucas.api.docs.ConstructorDoc;
import me.senseiwells.arucas.builtin.NumberDef;
import me.senseiwells.arucas.classes.ClassInstance;
import me.senseiwells.arucas.classes.CreatableDefinition;
import me.senseiwells.arucas.classes.PrimitiveDefinition;
import me.senseiwells.arucas.core.Interpreter;
import me.senseiwells.arucas.utils.Arguments;
import me.senseiwells.arucas.utils.ConstructorFunction;
import me.senseiwells.arucas.utils.Util;
import me.senseiwells.essentialclient.clientscript.definitions.PosDef;
import me.senseiwells.essentialclient.utils.clientscript.impl.ScriptBox;
import me.senseiwells.essentialclient.utils.clientscript.impl.ScriptPos;
import net.minecraft.util.math.Vec3d;

import java.util.List;

import static me.senseiwells.arucas.utils.Util.Types.NUMBER;
import static me.senseiwells.essentialclient.clientscript.core.MinecraftAPI.BOX_SHAPE;
import static me.senseiwells.essentialclient.clientscript.core.MinecraftAPI.POS;

@ClassDoc(
	name = BOX_SHAPE,
	desc = "This class allows you to create box shapes that can be rendered in the world.",
	importPath = "Minecraft",
	superclass = CorneredShapeDef.class,
	language = Util.Language.Java
)
public class BoxShapeDef extends CreatableDefinition<ScriptBox> {
	public BoxShapeDef(Interpreter interpreter) {
		super(BOX_SHAPE, interpreter);
	}

	@Override
	public PrimitiveDefinition<? super ScriptBox> superclass() {
		return this.getPrimitiveDef(CorneredShapeDef.class);
	}

	@Override
	public List<ConstructorFunction> defineConstructors() {
		return List.of(
			ConstructorFunction.of(1, this::construct1),
			ConstructorFunction.of(2, this::construct2),
			ConstructorFunction.of(3, this::construct3),
			ConstructorFunction.of(6, this::construct6)
		);
	}

	@ConstructorDoc(
		desc = "Creates a new box shape, this is used to render boxes",
		params = {POS, "pos", "The position which will be used for the first and second corner of the box"},
		examples = "new BoxShape(new Pos(0, 0, 0));"
	)
	private Unit construct1(Arguments arguments) {
		ClassInstance instance = arguments.next();
		ScriptPos pos = arguments.nextPrimitive(PosDef.class);
		instance.setPrimitive(this, new ScriptBox(arguments.getInterpreter(), pos.getVec3d(), pos.getVec3d()));
		return null;
	}

	@ConstructorDoc(
		desc = "Creates a new box shape, this is used to render boxes",
		params = {
			POS, "pos1", "The position of the first corner of the box",
			POS, "pos2", "The position of the second corner of the box"
		},
		examples = "new BoxShape(new Pos(0, 0, 0), new Pos(10, 10, 10));"
	)
	private Unit construct2(Arguments arguments) {
		ClassInstance instance = arguments.next();
		ScriptPos pos1 = arguments.nextPrimitive(PosDef.class);
		ScriptPos pos2 = arguments.nextPrimitive(PosDef.class);
		instance.setPrimitive(this, new ScriptBox(arguments.getInterpreter(), pos1.getVec3d(), pos2.getVec3d()));
		return null;
	}

	@ConstructorDoc(
		desc = "Creates a new box shape, this is used to render boxes",
		params = {
			NUMBER, "x", "The x position which will be used for the first and second corner of the box",
			NUMBER, "y", "The y position which will be used for the first and second corner of the box",
			NUMBER, "z", "The z position which will be used for the first and second corner of the box"
		},
		examples = "new BoxShape(0, 0, 0);"
	)
	private Unit construct3(Arguments arguments) {
		ClassInstance instance = arguments.next();
		double x = arguments.nextPrimitive(NumberDef.class).intValue();
		double y = arguments.nextPrimitive(NumberDef.class).intValue();
		double z = arguments.nextPrimitive(NumberDef.class).intValue();
		Vec3d pos = new Vec3d(x, y, z);
		instance.setPrimitive(this, new ScriptBox(arguments.getInterpreter(), pos, pos));
		return null;
	}

	@ConstructorDoc(
		desc = "Creates a new box shape, this is used to render boxes",
		params = {
			NUMBER, "x1", "The x position of the first corner of the box",
			NUMBER, "y1", "The y position of the first corner of the box",
			NUMBER, "z1", "The z position of the first corner of the box",
			NUMBER, "x2", "The x position of the second corner of the box",
			NUMBER, "y2", "The y position of the second corner of the box",
			NUMBER, "z2", "The z position of the second corner of the box"
		},
		examples = "new BoxShape(0, 0, 0, 10, 10, 10);"
	)
	private Unit construct6(Arguments arguments) {
		ClassInstance instance = arguments.next();
		double x1 = arguments.nextPrimitive(NumberDef.class).intValue();
		double y1 = arguments.nextPrimitive(NumberDef.class).intValue();
		double z1 = arguments.nextPrimitive(NumberDef.class).intValue();
		double x2 = arguments.nextPrimitive(NumberDef.class).intValue();
		double y2 = arguments.nextPrimitive(NumberDef.class).intValue();
		double z2 = arguments.nextPrimitive(NumberDef.class).intValue();
		instance.setPrimitive(this, new ScriptBox(arguments.getInterpreter(), new Vec3d(x1, y1, z1), new Vec3d(x2, y2, z2)));
		return null;
	}
}
