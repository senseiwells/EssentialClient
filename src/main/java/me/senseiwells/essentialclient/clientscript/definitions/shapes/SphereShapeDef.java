package me.senseiwells.essentialclient.clientscript.definitions.shapes;

import kotlin.Unit;
import me.senseiwells.arucas.api.docs.ClassDoc;
import me.senseiwells.arucas.api.docs.ConstructorDoc;
import me.senseiwells.arucas.api.docs.FunctionDoc;
import me.senseiwells.arucas.builtin.NumberDef;
import me.senseiwells.arucas.classes.ClassInstance;
import me.senseiwells.arucas.classes.CreatableDefinition;
import me.senseiwells.arucas.classes.PrimitiveDefinition;
import me.senseiwells.arucas.core.Interpreter;
import me.senseiwells.arucas.utils.Arguments;
import me.senseiwells.arucas.utils.ConstructorFunction;
import me.senseiwells.arucas.utils.MemberFunction;
import me.senseiwells.arucas.utils.Util;
import me.senseiwells.essentialclient.clientscript.definitions.PosDef;
import me.senseiwells.essentialclient.utils.clientscript.impl.ScriptPos;
import me.senseiwells.essentialclient.utils.clientscript.impl.ScriptSphere;
import net.minecraft.util.math.Vec3d;

import java.util.List;

import static me.senseiwells.arucas.utils.Util.Types.NUMBER;
import static me.senseiwells.essentialclient.clientscript.core.MinecraftAPI.POS;
import static me.senseiwells.essentialclient.clientscript.core.MinecraftAPI.SPHERE_SHAPE;

@ClassDoc(
	name = SPHERE_SHAPE,
	desc = "This class is used to create a sphere shape which can be rendered in the world.",
	importPath = "Minecraft",
	superclass = CentredShapeDef.class,
	language = Util.Language.Java
)
public class SphereShapeDef extends CreatableDefinition<ScriptSphere> {
	public SphereShapeDef(Interpreter interpreter) {
		super(SPHERE_SHAPE, interpreter);
	}

	@Override
	public PrimitiveDefinition<? super ScriptSphere> superclass() {
		return this.getPrimitiveDef(CentredShapeDef.class);
	}

	@Override
	public List<ConstructorFunction> defineConstructors() {
		return List.of(
			ConstructorFunction.of(1, this::construct1),
			ConstructorFunction.of(3, this::construct3)
		);
	}

	@ConstructorDoc(
		desc = "This creates a new sphere shape",
		params = {POS, "pos", "The position of the sphere"},
		examples = "new SphereShape(new Pos(0, 10, 0));"
	)
	private Unit construct1(Arguments arguments) {
		ClassInstance instance = arguments.next();
		ScriptPos pos = arguments.nextPrimitive(PosDef.class);
		instance.setPrimitive(this, new ScriptSphere(arguments.getInterpreter(), pos.getVec3d()));
		return null;
	}

	@ConstructorDoc(
		desc = "This creates a new sphere shape",
		params = {
			NUMBER, "x", "The x position of the sphere",
			NUMBER, "y", "The y position of the sphere",
			NUMBER, "z", "The z position of the sphere"
		},
		examples = "new SphereShape(0, 10, 0);"
	)
	private Unit construct3(Arguments arguments) {
		ClassInstance instance = arguments.next();
		double x = arguments.nextPrimitive(NumberDef.class);
		double y = arguments.nextPrimitive(NumberDef.class);
		double z = arguments.nextPrimitive(NumberDef.class);
		instance.setPrimitive(this, new ScriptSphere(arguments.getInterpreter(), new Vec3d(x, y, z)));
		return null;
	}

	@Override
	public List<MemberFunction> defineMethods() {
		return List.of(
			MemberFunction.of("setSteps", 1, this::setSteps),
			MemberFunction.of("getSteps", this::getSteps)
		);
	}

	@FunctionDoc(
		name = "setSteps",
		desc = "This sets the number of steps the sphere will take to render",
		params = {NUMBER, "steps", "The number of steps"},
		examples = "sphere.setSteps(30);"
	)
	private Void setSteps(Arguments arguments) {
		ScriptSphere sphere = arguments.nextPrimitive(this);
		sphere.setSteps(arguments.nextPrimitive(NumberDef.class).floatValue());
		return null;
	}

	@FunctionDoc(
		name = "getSteps",
		desc = "This gets the number of steps the sphere will take to render",
		returns = {NUMBER, "The number of steps"},
		examples = "sphere.getSteps();"
	)
	private float getSteps(Arguments arguments) {
		return arguments.nextPrimitive(this).getSteps();
	}
}
