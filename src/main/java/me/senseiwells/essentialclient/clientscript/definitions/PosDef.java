package me.senseiwells.essentialclient.clientscript.definitions;

import me.senseiwells.arucas.api.docs.ClassDoc;
import me.senseiwells.arucas.api.docs.ConstructorDoc;
import me.senseiwells.arucas.api.docs.FunctionDoc;
import me.senseiwells.arucas.builtin.ListDef;
import me.senseiwells.arucas.builtin.NumberDef;
import me.senseiwells.arucas.classes.ClassInstance;
import me.senseiwells.arucas.classes.CreatableDefinition;
import me.senseiwells.arucas.core.Interpreter;
import me.senseiwells.arucas.exceptions.RuntimeError;
import me.senseiwells.arucas.utils.*;
import me.senseiwells.arucas.utils.impl.ArucasList;
import me.senseiwells.essentialclient.utils.clientscript.ClientScriptUtils;
import me.senseiwells.essentialclient.utils.clientscript.impl.ScriptPos;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import shadow.kotlin.Unit;

import java.util.List;

import static me.senseiwells.arucas.utils.Util.Types.*;
import static me.senseiwells.essentialclient.clientscript.core.MinecraftAPI.ENTITY;
import static me.senseiwells.essentialclient.clientscript.core.MinecraftAPI.POS;

@ClassDoc(
	name = POS,
	desc = "This class is a wrapper for 3 coordinate points in Minecraft",
	importPath = "Minecraft",
	language = Util.Language.Java
)
public class PosDef extends CreatableDefinition<ScriptPos> {
	public PosDef(Interpreter interpreter) {
		super(POS, interpreter);
	}

	@Override
	public Object asJavaValue(ClassInstance instance) {
		return instance.asPrimitive(this).getVec3d();
	}

	@Override
	public boolean equals$Arucas(ClassInstance instance, Interpreter interpreter, ClassInstance other, LocatableTrace trace) {
		ScriptPos pos = other.getPrimitive(this);
		return pos != null && instance.asPrimitive(this).getVec3d().equals(pos.getVec3d());
	}

	@Override
	public int hashCode$Arucas(ClassInstance instance, Interpreter interpreter, LocatableTrace trace) {
		return instance.asPrimitive(this).getVec3d().hashCode();
	}

	@Override
	public String toString$Arucas(ClassInstance instance, Interpreter interpreter, LocatableTrace trace) {
		return instance.asPrimitive(this).getVec3d().toString();
	}

	@Override
	public List<ConstructorFunction> defineConstructors() {
		return List.of(
			ConstructorFunction.of(1, this::construct1),
			ConstructorFunction.of(3, this::construct3)
		);
	}

	@ConstructorDoc(
		desc = "Creates a new Pos object with the given coordinates in a list",
		params = {LIST, "list", "the list containing three coordinates"},
		examples = "new Pos([1, 2, 3])"
	)
	private Unit construct1(Arguments arguments) {
		ClassInstance instance = arguments.next();
		ArucasList list = arguments.nextPrimitive(ListDef.class);
		if (list.size() != 3) {
			throw new RuntimeError("Expected a list with 3 coordinates");
		}
		double[] coords = new double[3];
		for (int i = 0; i < 3; i++) {
			Double value = list.get(i).getPrimitive(NumberDef.class);
			if (value == null) {
				throw new RuntimeError("Expected a number at index " + i);
			}
			coords[i] = value;
		}
		instance.setPrimitive(this, new ScriptPos(coords[0], coords[1], coords[2]));
		return null;
	}

	@ConstructorDoc(
		desc = "This creates a new Pos with the given x, y, and z",
		params = {
			NUMBER, "x", "the x position",
			NUMBER, "y", "the y position",
			NUMBER, "z", "the z position"
		},
		examples = "new Pos(100, 0, 96);"
	)
	private Unit construct3(Arguments arguments) {
		ClassInstance instance = arguments.next();
		double x = arguments.nextPrimitive(NumberDef.class);
		double y = arguments.nextPrimitive(NumberDef.class);
		double z = arguments.nextPrimitive(NumberDef.class);
		instance.setPrimitive(this, new ScriptPos(x, y, z));
		return null;
	}

	@Override
	public List<MemberFunction> defineMethods() {
		return List.of(
			MemberFunction.of("getX", this::getX),
			MemberFunction.of("getY", this::getY),
			MemberFunction.of("getZ", this::getZ),
			MemberFunction.of("multiply", 3, this::multiply),
			MemberFunction.of("multiply", 1, this::multiply1),
			MemberFunction.of("add", 3, this::add),
			MemberFunction.of("add", 1, this::add1),
			MemberFunction.of("subtract", 3, this::subtract),
			MemberFunction.of("subtract", 1, this::subtract1),
			MemberFunction.of("dotProduct", 1, this::dotProduct),
			MemberFunction.of("crossProduct", 1, this::crossProduct),
			MemberFunction.of("toBlockPos", this::toBlockPos),
			MemberFunction.of("toList", this::toList),
			MemberFunction.of("offset", 1, this::offset),
			MemberFunction.of("offset", 2, this::offset1),
			MemberFunction.of("up", this::up),
			MemberFunction.of("up", 1, this::up1),
			MemberFunction.of("down", this::down),
			MemberFunction.of("down", 1, this::down1),
			MemberFunction.of("north", this::north),
			MemberFunction.of("north", 1, this::north1),
			MemberFunction.of("south", this::south),
			MemberFunction.of("south", 1, this::south1),
			MemberFunction.of("east", this::east),
			MemberFunction.of("east", 1, this::east1),
			MemberFunction.of("west", this::west),
			MemberFunction.of("west", 1, this::west1),
			MemberFunction.of("getSidePos", 1, this::getSidePos),
			MemberFunction.of("asCentre", this::asCentre),
			MemberFunction.of("isNear", 1, this::isNear),
			MemberFunction.of("isWithin", 2, this::isWithin),
			MemberFunction.of("distanceTo", 1, this::distanceTo),
			MemberFunction.of("distanceTo", 3, this::distanceTo1),
			MemberFunction.of("normalize", 0, this::normalize)
		);
	}

	@FunctionDoc(
		name = "getX",
		desc = "This returns the x position of the Pos",
		returns = {NUMBER, "the x position"},
		examples = "pos.getX();"
	)
	private double getX(Arguments arguments) {
		return arguments.nextPrimitive(this).getX();
	}

	@FunctionDoc(
		name = "getY",
		desc = "This returns the y position of the Pos",
		returns = {NUMBER, "the y position"},
		examples = "pos.getY();"
	)
	private double getY(Arguments arguments) {
		return arguments.nextPrimitive(this).getY();
	}

	@FunctionDoc(
		name = "getZ",
		desc = "This returns the z position of the Pos",
		returns = {NUMBER, "the z position"},
		examples = "pos.getZ();"
	)
	private double getZ(Arguments arguments) {
		return arguments.nextPrimitive(this).getZ();
	}

	@FunctionDoc(
		name = "multiply",
		desc = "This returns a new Pos with the current pos x, y, and z multiplied by the given x, y, and z",
		params = {
			NUMBER, "x", "the x multiplier",
			NUMBER, "y", "the y multiplier",
			NUMBER, "z", "the z multiplier"
		},
		returns = {POS, "the new Pos"},
		examples = "pos.multiply(2, 3, 5);"
	)
	private Vec3d multiply(Arguments arguments) {
		ScriptPos pos = arguments.nextPrimitive(this);
		double x = arguments.nextPrimitive(NumberDef.class);
		double y = arguments.nextPrimitive(NumberDef.class);
		double z = arguments.nextPrimitive(NumberDef.class);
		return pos.getVec3d().multiply(x, y, z);
	}

	@FunctionDoc(
		name = "multiply",
		desc = "This returns a new Pos with the current pos x, y, and z multiplied by the given pos x, y, and z",
		params = {POS, "pos", "the Pos to multiply by"},
		returns = {POS, "the new Pos"},
		examples = "pos.multiply(new Pos(2, 3, 5));"
	)
	private Vec3d multiply1(Arguments arguments) {
		ScriptPos pos = arguments.nextPrimitive(this);
		ScriptPos other = arguments.nextPrimitive(this);
		return pos.getVec3d().multiply(other.getVec3d());
	}

	@FunctionDoc(
		name = "subtract",
		desc = "This returns a new Pos with the current pos x, y, and z subtracted by the given x, y, and z",
		params = {
			NUMBER, "x", "the x subtractor",
			NUMBER, "y", "the y subtractor",
			NUMBER, "z", "the z subtractor"
		},
		returns = {POS, "the new Pos"},
		examples = "pos.subtract(2, 3, 5);"
	)
	private Vec3d subtract(Arguments arguments) {
		ScriptPos pos = arguments.nextPrimitive(this);
		double x = arguments.nextPrimitive(NumberDef.class);
		double y = arguments.nextPrimitive(NumberDef.class);
		double z = arguments.nextPrimitive(NumberDef.class);
		return pos.getVec3d().subtract(x, y, z);
	}

	@FunctionDoc(
		name = "subtract",
		desc = "This returns a new Pos with the current pos x, y, and z subtracted by the given pos x, y, and z",
		params = {POS, "pos", "the Pos to subtract by"},
		returns = {POS, "the new Pos"},
		examples = "pos.subtract(new Pos(2, 3, 5));"
	)
	private Vec3d subtract1(Arguments arguments) {
		ScriptPos pos = arguments.nextPrimitive(this);
		ScriptPos other = arguments.nextPrimitive(this);
		return pos.getVec3d().subtract(other.getVec3d());
	}

	@FunctionDoc(
		name = "add",
		desc = "This returns a new Pos with the current pos x, y, and z added by the given x, y, and z",
		params = {
			NUMBER, "x", "the x adder",
			NUMBER, "y", "the y adder",
			NUMBER, "z", "the z adder"
		},
		returns = {POS, "the new Pos"},
		examples = "pos.add(2, 3, 5);"
	)
	private Vec3d add(Arguments arguments) {
		ScriptPos pos = arguments.nextPrimitive(this);
		double x = arguments.nextPrimitive(NumberDef.class);
		double y = arguments.nextPrimitive(NumberDef.class);
		double z = arguments.nextPrimitive(NumberDef.class);
		return pos.getVec3d().add(x, y, z);
	}

	@FunctionDoc(
		name = "add",
		desc = "This returns a new Pos with the current pos x, y, and z added by the given pos x, y, and z",
		params = {POS, "pos", "the Pos to add by"},
		returns = {POS, "the new Pos"},
		examples = "pos.add(new Pos(2, 3, 5));"
	)
	private Vec3d add1(Arguments arguments) {
		ScriptPos pos = arguments.nextPrimitive(this);
		ScriptPos other = arguments.nextPrimitive(this);
		return pos.getVec3d().add(other.getVec3d());
	}

	@FunctionDoc(
		name = "dotProduct",
		desc = "This returns the dot product of the current pos and the given pos",
		params = {POS, "pos", "the Pos to dot product with"},
		returns = {NUMBER, "the dot product"},
		examples = "pos.dotProduct(new Pos(2, 3, 5));"
	)
	private double dotProduct(Arguments arguments) {
		ScriptPos pos = arguments.nextPrimitive(this);
		ScriptPos other = arguments.nextPrimitive(this);
		return pos.getVec3d().dotProduct(other.getVec3d());
	}

	@FunctionDoc(
		name = "crossProduct",
		desc = "This returns the cross product of the current pos and the given pos",
		params = {POS, "pos", "the Pos to cross product with"},
		returns = {POS, "the cross product"},
		examples = "pos.crossProduct(new Pos(2, 3, 5));"
	)
	private Vec3d crossProduct(Arguments arguments) {
		ScriptPos pos = arguments.nextPrimitive(this);
		ScriptPos other = arguments.nextPrimitive(this);
		return pos.getVec3d().crossProduct(other.getVec3d());
	}

	@FunctionDoc(
		name = "toBlockPos",
		desc = "This floors all of the positions values to the nearest block",
		returns = {POS, "the new Pos"},
		examples = "pos.toBlockPos();"
	)
	private BlockPos toBlockPos(Arguments arguments) {
		return arguments.nextPrimitive(this).getBlockPos();
	}

	@FunctionDoc(
		name = "toList",
		desc = "This returns the Pos as a List containing the x, y, and z positions in order",
		returns = {LIST, "the Pos as a List"},
		examples = "x, y, z = pos.toList();"
	)
	private ArucasList toList(Arguments arguments) {
		ScriptPos pos = arguments.nextPrimitive(this);
		NumberDef numberDef = arguments.getInterpreter().getPrimitive(NumberDef.class);
		return ArucasList.of(
			numberDef.create(pos.getX()),
			numberDef.create(pos.getY()),
			numberDef.create(pos.getZ())
		);
	}

	@FunctionDoc(
		name = "offset",
		desc = "This returns a new Pos with the current pos x, y, and z offset by a direction",
		params = {STRING, "direction", "the direction to offset by, must be one of: north, south, east, west, up, down"},
		returns = {POS, "the new Pos"},
		examples = "pos.offset('north');"
	)
	private Vec3d offset(Arguments arguments) {
		ScriptPos pos = arguments.nextPrimitive(this);
		String string = arguments.nextConstant();
		Direction direction = ClientScriptUtils.stringToDirection(string, null);
		return pos.getVec3d().add(direction.getOffsetX(), direction.getOffsetY(), direction.getOffsetZ());
	}

	@FunctionDoc(
		name = "offset",
		desc = "This returns a new Pos with the current pos x, y, and z offset by a direction and a distance",
		params = {
			STRING, "direction", "the direction to offset by, must be one of: north, south, east, west, up, down",
			NUMBER, "distance", "the distance to offset by"
		},
		returns = {POS, "the new Pos"},
		examples = "pos.offset('north', 2);"
	)
	private Vec3d offset1(Arguments arguments) {
		ScriptPos pos = arguments.nextPrimitive(this);
		String string = arguments.nextConstant();
		double distance = arguments.nextPrimitive(NumberDef.class);
		Direction direction = ClientScriptUtils.stringToDirection(string, null);
		return pos.getVec3d().add(
			direction.getOffsetX() * distance,
			direction.getOffsetY() * distance,
			direction.getOffsetZ() * distance
		);
	}

	@FunctionDoc(
		name = "up",
		desc = "This returns a new Pos with the current pos y incremented by 1",
		returns = {POS, "the new Pos"},
		examples = "pos.up();"
	)
	private Vec3d up(Arguments arguments) {
		ScriptPos pos = arguments.nextPrimitive(this);
		return pos.getVec3d().add(0, 1, 0);
	}

	@FunctionDoc(
		name = "up",
		desc = "This returns a new Pos with the current pos y incremented by the given number",
		params = {NUMBER, "number", "the number to increment by"},
		returns = {POS, "the new Pos"},
		examples = "pos.up(2);"
	)
	private Vec3d up1(Arguments arguments) {
		ScriptPos pos = arguments.nextPrimitive(this);
		double number = arguments.nextPrimitive(NumberDef.class);
		return pos.getVec3d().add(0, number, 0);
	}

	@FunctionDoc(
		name = "down",
		desc = "This returns a new Pos with the current pos y decremented by 1",
		returns = {POS, "the new Pos"},
		examples = "pos.down();"
	)
	private Vec3d down(Arguments arguments) {
		ScriptPos pos = arguments.nextPrimitive(this);
		return pos.getVec3d().add(0, -1, 0);
	}

	@FunctionDoc(
		name = "down",
		desc = "This returns a new Pos with the current pos y decremented by the given number",
		params = {NUMBER, "number", "the number to decrement by"},
		returns = {POS, "the new Pos"},
		examples = "pos.down(2);"
	)
	private Vec3d down1(Arguments arguments) {
		ScriptPos pos = arguments.nextPrimitive(this);
		double number = arguments.nextPrimitive(NumberDef.class);
		return pos.getVec3d().add(0, -number, 0);
	}

	@FunctionDoc(
		name = "north",
		desc = "This returns a new Pos with the current pos z incremented by 1",
		returns = {POS, "the new Pos"},
		examples = "pos.north();"
	)
	private Vec3d north(Arguments arguments) {
		ScriptPos pos = arguments.nextPrimitive(this);
		return pos.getVec3d().add(0, 0, -1);
	}

	@FunctionDoc(
		name = "north",
		desc = "This returns a new Pos with the current pos z incremented by the given number",
		params = {NUMBER, "number", "the number to increment by"},
		returns = {POS, "the new Pos"},
		examples = "pos.north(2);"
	)
	private Vec3d north1(Arguments arguments) {
		ScriptPos pos = arguments.nextPrimitive(this);
		double number = arguments.nextPrimitive(NumberDef.class);
		return pos.getVec3d().add(0, 0, -number);
	}

	@FunctionDoc(
		name = "south",
		desc = "This returns a new Pos with the current pos z decremented by 1",
		returns = {POS, "the new Pos"},
		examples = "pos.south();"
	)
	private Vec3d south(Arguments arguments) {
		ScriptPos pos = arguments.nextPrimitive(this);
		return pos.getVec3d().add(0, 0, 1);
	}

	@FunctionDoc(
		name = "south",
		desc = "This returns a new Pos with the current pos z decremented by the given number",
		params = {NUMBER, "number", "the number to decrement by"},
		returns = {POS, "the new Pos"},
		examples = "pos.south(2);"
	)
	private Vec3d south1(Arguments arguments) {
		ScriptPos pos = arguments.nextPrimitive(this);
		double number = arguments.nextPrimitive(NumberDef.class);
		return pos.getVec3d().add(0, 0, number);
	}

	@FunctionDoc(
		name = "east",
		desc = "This returns a new Pos with the current pos x incremented by 1",
		returns = {POS, "the new Pos"},
		examples = "pos.east();"
	)
	private Vec3d east(Arguments arguments) {
		ScriptPos pos = arguments.nextPrimitive(this);
		return pos.getVec3d().add(1, 0, 0);
	}

	@FunctionDoc(
		name = "east",
		desc = "This returns a new Pos with the current pos x incremented by the given number",
		params = {NUMBER, "number", "the number to increment by"},
		returns = {POS, "the new Pos"},
		examples = "pos.east(2);"
	)
	private Vec3d east1(Arguments arguments) {
		ScriptPos pos = arguments.nextPrimitive(this);
		double number = arguments.nextPrimitive(NumberDef.class);
		return pos.getVec3d().add(number, 0, 0);
	}

	@FunctionDoc(
		name = "west",
		desc = "This returns a new Pos with the current pos x decremented by 1",
		returns = {POS, "the new Pos"},
		examples = "pos.west();"
	)
	private Vec3d west(Arguments arguments) {
		ScriptPos pos = arguments.nextPrimitive(this);
		return pos.getVec3d().add(-1, 0, 0);
	}

	@FunctionDoc(
		name = "west",
		desc = "This returns a new Pos with the current pos x decremented by the given number",
		params = {NUMBER, "number", "the number to decrement by"},
		returns = {POS, "the new Pos"},
		examples = "pos.west(2);"
	)
	private Vec3d west1(Arguments arguments) {
		ScriptPos pos = arguments.nextPrimitive(this);
		double number = arguments.nextPrimitive(NumberDef.class);
		return pos.getVec3d().add(-number, 0, 0);
	}

	@FunctionDoc(
		name = "getSidePos",
		desc = "This returns side position value of position",
		params = {STRING, "direction", "the direction, can be: north, south, east, west, up, down"},
		returns = {POS, "the side of the position"},
		examples = "pos.getSidePos('east');"
	)
	private Vec3d getSidePos(Arguments arguments) {
		ScriptPos pos = arguments.nextPrimitive(this);
		String string = arguments.nextConstant();
		Direction direction = ClientScriptUtils.stringToDirection(string, null);
		return Vec3d.ofCenter(pos.getBlockPos()).add(Vec3d.of(direction.getVector()).multiply(0.5));
	}

	@FunctionDoc(
		name = "asCentre",
		desc = "This returns center value of the position",
		returns = {POS, "the center of the position"},
		examples = "pos.asCentre();"
	)
	private Vec3d asCentre(Arguments arguments) {
		ScriptPos pos = arguments.nextPrimitive(this);
		return Vec3d.ofCenter(pos.getBlockPos());
	}

	@FunctionDoc(
		name = "isNear",
		desc = "This returns whether position to entity is less than 4.5",
		params = {ENTITY, "entity", "the entity you want to check"},
		returns = {BOOLEAN, "whether entity is within 4.5 block distance"},
		examples = "pos.isNear(Player.get());"
	)
	private boolean isNear(Arguments arguments) {
		ScriptPos pos = arguments.nextPrimitive(this);
		Entity entity = arguments.nextPrimitive(EntityDef.class);
		return entity.squaredDistanceTo(pos.getVec3d()) <= 4.5 * 4.5;
	}

	@FunctionDoc(
		name = "isWithin",
		desc = "This returns whether position to entity is less than given distance",
		params = {
			ENTITY, "entity", "the entity you want to check",
			NUMBER, "distance", "the distance you want to check"
		},
		returns = {BOOLEAN, "whether entity is within given distance"},
		examples = "pos.isNear(player, 8);"
	)
	private boolean isWithin(Arguments arguments) {
		ScriptPos pos = arguments.nextPrimitive(this);
		Entity entity = arguments.nextPrimitive(EntityDef.class);
		double distance = arguments.nextPrimitive(NumberDef.class);
		return entity.squaredDistanceTo(pos.getVec3d()) <= distance * distance;
	}

	@FunctionDoc(
		name = "distanceTo",
		desc = "This returns distance to other position",
		params = {POS, "other", "other position"},
		returns = {NUMBER, "distance to other position"},
		examples = "pos.distanceTo(new Pos(0, 0, 0));"
	)
	private double distanceTo(Arguments arguments) {
		ScriptPos pos = arguments.nextPrimitive(this);
		ScriptPos other = arguments.nextPrimitive(this);
		return pos.getVec3d().distanceTo(other.getVec3d());
	}

	@FunctionDoc(
		name = "distanceTo",
		desc = "This returns distance to other x, y, z position",
		params = {
			NUMBER, "x", "other position x",
			NUMBER, "y", "other position y",
			NUMBER, "z", "other position z"
		},
		returns = {NUMBER, "distance to other position"},
		examples = "pos.distanceTo(0, 0, 0);"
	)
	private double distanceTo1(Arguments arguments) {
		ScriptPos pos = arguments.nextPrimitive(this);
		double x = arguments.nextPrimitive(NumberDef.class);
		double y = arguments.nextPrimitive(NumberDef.class);
		double z = arguments.nextPrimitive(NumberDef.class);
		return pos.getVec3d().distanceTo(new Vec3d(x, y, z));
	}

	@FunctionDoc(
		name = "normalize",
		desc = "Normalizes the vector to have a magnitude of 1",
		returns = {POS, "the normalized position"},
		examples = "pos.normalize();"
	)
	private Vec3d normalize(Arguments arguments) {
		ScriptPos pos = arguments.nextPrimitive(this);
		return pos.getVec3d().normalize();
	}
}
