package me.senseiwells.essentialclient.clientscript.values;

import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.api.docs.ClassDoc;
import me.senseiwells.arucas.api.docs.ConstructorDoc;
import me.senseiwells.arucas.api.docs.FunctionDoc;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.Arguments;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.impl.ArucasList;
import me.senseiwells.arucas.values.*;
import me.senseiwells.arucas.values.functions.ConstructorFunction;
import me.senseiwells.arucas.values.functions.MemberFunction;
import me.senseiwells.essentialclient.utils.clientscript.ClientScriptUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import static me.senseiwells.arucas.utils.ValueTypes.*;
import static me.senseiwells.essentialclient.clientscript.core.MinecraftAPI.ENTITY;
import static me.senseiwells.essentialclient.clientscript.core.MinecraftAPI.POS;

public class PosValue extends GenericValue<Vec3d> {
	private BlockPos blockPos;

	public PosValue(Vec3d value) {
		super(value);
	}

	public PosValue(BlockPos blockPos) {
		super(new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ()));
		this.blockPos = blockPos.toImmutable();
	}

	public PosValue(double x, double y, double z) {
		this(new Vec3d(x, y, z));
	}

	public NumberValue getX() {
		return NumberValue.of(this.value.getX());
	}

	public NumberValue getY() {
		return NumberValue.of(this.value.getY());
	}

	public NumberValue getZ() {
		return NumberValue.of(this.value.getZ());
	}

	public BlockPos toBlockPos() {
		if (this.blockPos == null) {
			this.blockPos = new BlockPos(this.value);
		}
		return this.blockPos;
	}

	@Override
	public GenericValue<Vec3d> copy(Context context) throws CodeError {
		return new PosValue(new Vec3d(this.value.x, this.value.y, this.value.z));
	}

	@Override
	public String getAsString(Context context) throws CodeError {
		return this.value.toString();
	}

	@Override
	public int getHashCode(Context context) throws CodeError {
		return this.value.hashCode();
	}

	@Override
	public boolean isEquals(Context context, Value value) throws CodeError {
		return this.value.equals(value.getValue());
	}

	@Override
	public String getTypeName() {
		return POS;
	}

	@ClassDoc(
		name = POS,
		desc = "This class is a wrapper for 3 coordinate points in Minecraft",
		importPath = "Minecraft"
	)
	public static class ArucasPosClass extends ArucasClassExtension {
		public ArucasPosClass() {
			super(POS);
		}

		@Override
		public ArucasFunctionMap<ConstructorFunction> getDefinedConstructors() {
			return ArucasFunctionMap.of(
				ConstructorFunction.of(1, this::newPos1),
				ConstructorFunction.of(3, this::newPos)
			);
		}

		@ConstructorDoc(
			desc = "Creates a new Pos object with the given coordinates in a list",
			params = {LIST, "list", "the list containing three coordinates"},
			example = "new Pos([1, 2, 3])"
		)
		private Value newPos1(Arguments arguments) throws RuntimeError {
			ArucasList list = arguments.getNextList().value;
			if (list.size() != 3) {
				throw arguments.getError("Expected a list with 3 coordinates");
			}
			double[] coords = new double[3];
			for (int i = 0; i < 3; i++) {
				Value value = list.get(i);
				if (value instanceof NumberValue number) {
					coords[i] = number.getValue();
					continue;
				}
				throw arguments.getError("Expected a number at index " + i);
			}
			return new PosValue(coords[0], coords[1], coords[2]);
		}

		@ConstructorDoc(
			desc = "This creates a new Pos with the given x, y, and z",
			params = {
				NUMBER, "x", "the x position",
				NUMBER, "y", "the y position",
				NUMBER, "z", "the z position"
			},
			example = "new Pos(100, 0, 96);"
		)
		private Value newPos(Arguments arguments) throws CodeError {
			double x = arguments.getNextGeneric(NumberValue.class);
			double y = arguments.getNextGeneric(NumberValue.class);
			double z = arguments.getNextGeneric(NumberValue.class);
			return new PosValue(x, y, z);
		}

		@Override
		public ArucasFunctionMap<MemberFunction> getDefinedMethods() {
			return ArucasFunctionMap.of(
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
				MemberFunction.of("distanceTo", 3, this::distanceTo1)
			);
		}

		@FunctionDoc(
			name = "getX",
			desc = "This returns the x position of the Pos",
			returns = {NUMBER, "the x position"},
			example = "pos.getX();"
		)
		private Value getX(Arguments arguments) throws CodeError {
			PosValue thisValue = arguments.getNext(PosValue.class);
			return NumberValue.of(thisValue.value.getX());
		}

		@FunctionDoc(
			name = "getY",
			desc = "This returns the y position of the Pos",
			returns = {NUMBER, "the y position"},
			example = "pos.getY();"
		)
		private Value getY(Arguments arguments) throws CodeError {
			PosValue thisValue = arguments.getNext(PosValue.class);
			return NumberValue.of(thisValue.value.getY());
		}

		@FunctionDoc(
			name = "getZ",
			desc = "This returns the z position of the Pos",
			returns = {NUMBER, "the z position"},
			example = "pos.getZ();"
		)
		private Value getZ(Arguments arguments) throws CodeError {
			PosValue thisValue = arguments.getNext(PosValue.class);
			return NumberValue.of(thisValue.value.getZ());
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
			example = "pos.multiply(2, 3, 5);"
		)
		private Value multiply(Arguments arguments) throws CodeError {
			PosValue thisValue = arguments.getNext(PosValue.class);
			double x = arguments.getNextGeneric(NumberValue.class);
			double y = arguments.getNextGeneric(NumberValue.class);
			double z = arguments.getNextGeneric(NumberValue.class);
			return new PosValue(thisValue.value.multiply(x, y, z));
		}

		@FunctionDoc(
			name = "multiply",
			desc = "This returns a new Pos with the current pos x, y, and z multiplied by the given pos x, y, and z",
			params = {POS, "pos", "the Pos to multiply by"},
			returns = {POS, "the new Pos"},
			example = "pos.multiply(new Pos(2, 3, 5));"
		)
		private Value multiply1(Arguments arguments) throws CodeError {
			PosValue thisValue = arguments.getNext(PosValue.class);
			PosValue posValue = arguments.getNext(PosValue.class);
			return new PosValue(thisValue.value.multiply(posValue.value));
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
			example = "pos.subtract(2, 3, 5);"
		)
		private Value subtract(Arguments arguments) throws CodeError {
			PosValue thisValue = arguments.getNext(PosValue.class);
			double x = arguments.getNextGeneric(NumberValue.class);
			double y = arguments.getNextGeneric(NumberValue.class);
			double z = arguments.getNextGeneric(NumberValue.class);
			return new PosValue(thisValue.value.subtract(x, y, z));
		}

		@FunctionDoc(
			name = "subtract",
			desc = "This returns a new Pos with the current pos x, y, and z subtracted by the given pos x, y, and z",
			params = {POS, "pos", "the Pos to subtract by"},
			returns = {POS, "the new Pos"},
			example = "pos.subtract(new Pos(2, 3, 5));"
		)
		private Value subtract1(Arguments arguments) throws CodeError {
			PosValue thisValue = arguments.getNext(PosValue.class);
			PosValue posValue = arguments.getNext(PosValue.class);
			return new PosValue(thisValue.value.subtract(posValue.value));
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
			example = "pos.add(2, 3, 5);"
		)
		private Value add(Arguments arguments) throws CodeError {
			PosValue thisValue = arguments.getNext(PosValue.class);
			double x = arguments.getNextGeneric(NumberValue.class);
			double y = arguments.getNextGeneric(NumberValue.class);
			double z = arguments.getNextGeneric(NumberValue.class);
			return new PosValue(thisValue.value.add(x, y, z));
		}

		@FunctionDoc(
			name = "add",
			desc = "This returns a new Pos with the current pos x, y, and z added by the given pos x, y, and z",
			params = {POS, "pos", "the Pos to add by"},
			returns = {POS, "the new Pos"},
			example = "pos.add(new Pos(2, 3, 5));"
		)
		private Value add1(Arguments arguments) throws CodeError {
			PosValue thisValue = arguments.getNext(PosValue.class);
			PosValue posValue = arguments.getNext(PosValue.class);
			return new PosValue(thisValue.value.add(posValue.value));
		}

		@FunctionDoc(
			name = "dotProduct",
			desc = "This returns the dot product of the current pos and the given pos",
			params = {POS, "pos", "the Pos to dot product with"},
			returns = {NUMBER, "the dot product"},
			example = "pos.dotProduct(new Pos(2, 3, 5));"
		)
		private Value dotProduct(Arguments arguments) throws CodeError {
			PosValue thisValue = arguments.getNext(PosValue.class);
			PosValue posValue = arguments.getNext(PosValue.class);
			return NumberValue.of(thisValue.value.dotProduct(posValue.value));
		}

		@FunctionDoc(
			name = "crossProduct",
			desc = "This returns the cross product of the current pos and the given pos",
			params = {POS, "pos", "the Pos to cross product with"},
			returns = {POS, "the cross product"},
			example = "pos.crossProduct(new Pos(2, 3, 5));"
		)
		private Value crossProduct(Arguments arguments) throws CodeError {
			PosValue thisValue = arguments.getNext(PosValue.class);
			PosValue posValue = arguments.getNext(PosValue.class);
			return new PosValue(thisValue.value.crossProduct(posValue.value));
		}

		@FunctionDoc(
			name = "toBlockPos",
			desc = "This floors all of the positions values to the nearest block",
			returns = {POS, "the new Pos"},
			example = "pos.toBlockPos();"
		)
		private Value toBlockPos(Arguments arguments) throws CodeError {
			PosValue thisValue = arguments.getNext(PosValue.class);
			return new PosValue(thisValue.toBlockPos());
		}

		@FunctionDoc(
			name = "toList",
			desc = "This returns the Pos as a List containing the x, y, and z positions in order",
			returns = {LIST, "the Pos as a List"},
			example = "x, y, z = pos.toList();"
		)
		private Value toList(Arguments arguments) throws CodeError {
			PosValue thisValue = arguments.getNext(PosValue.class);
			ArucasList arucasList = new ArucasList();
			arucasList.add(thisValue.getX());
			arucasList.add(thisValue.getY());
			arucasList.add(thisValue.getZ());
			return new ListValue(arucasList);
		}

		@FunctionDoc(
			name = "offset",
			desc = "This returns a new Pos with the current pos x, y, and z offset by a direction",
			params = {STRING, "direction", "the direction to offset by, must be one of: north, south, east, west, up, down"},
			returns = {POS, "the new Pos"},
			example = "pos.offset('north');"
		)
		private Value offset(Arguments arguments) throws CodeError {
			PosValue thisValue = arguments.getNext(PosValue.class);
			String directionString = arguments.getNextGeneric(StringValue.class);
			Direction direction = ClientScriptUtils.stringToDirection(directionString, null);
			return new PosValue(thisValue.value.add(direction.getOffsetX(), direction.getOffsetY(), direction.getOffsetZ()));
		}

		@FunctionDoc(
			name = "offset",
			desc = "This returns a new Pos with the current pos x, y, and z offset by a direction and a distance",
			params = {
				STRING, "direction", "the direction to offset by, must be one of: north, south, east, west, up, down",
				NUMBER, "distance", "the distance to offset by"
			},
			returns = {POS, "the new Pos"},
			example = "pos.offset('north', 2);"
		)
		private Value offset1(Arguments arguments) throws CodeError {
			PosValue thisValue = arguments.getNext(PosValue.class);
			String directionString = arguments.getNextGeneric(StringValue.class);
			double distance = arguments.getNextGeneric(NumberValue.class);
			Direction direction = ClientScriptUtils.stringToDirection(directionString, null);
			return new PosValue(thisValue.value.add(
				direction.getOffsetX() * distance,
				direction.getOffsetY() * distance,
				direction.getOffsetZ() * distance)
			);
		}

		@FunctionDoc(
			name = "up",
			desc = "This returns a new Pos with the current pos y incremented by 1",
			returns = {POS, "the new Pos"},
			example = "pos.up();"
		)
		private Value up(Arguments arguments) throws CodeError {
			PosValue thisValue = arguments.getNext(PosValue.class);
			return new PosValue(thisValue.value.add(0, 1, 0));
		}

		@FunctionDoc(
			name = "up",
			desc = "This returns a new Pos with the current pos y incremented by the given number",
			params = {NUMBER, "number", "the number to increment by"},
			returns = {POS, "the new Pos"},
			example = "pos.up(2);"
		)
		private Value up1(Arguments arguments) throws CodeError {
			PosValue thisValue = arguments.getNext(PosValue.class);
			double number = arguments.getNextGeneric(NumberValue.class);
			return new PosValue(thisValue.value.add(0, number, 0));
		}

		@FunctionDoc(
			name = "down",
			desc = "This returns a new Pos with the current pos y decremented by 1",
			returns = {POS, "the new Pos"},
			example = "pos.down();"
		)
		private Value down(Arguments arguments) throws CodeError {
			PosValue thisValue = arguments.getNext(PosValue.class);
			return new PosValue(thisValue.value.add(0, -1, 0));
		}

		@FunctionDoc(
			name = "down",
			desc = "This returns a new Pos with the current pos y decremented by the given number",
			params = {NUMBER, "number", "the number to decrement by"},
			returns = {POS, "the new Pos"},
			example = "pos.down(2);"
		)
		private Value down1(Arguments arguments) throws CodeError {
			PosValue thisValue = arguments.getNext(PosValue.class);
			double number = arguments.getNextGeneric(NumberValue.class);
			return new PosValue(thisValue.value.add(0, -number, 0));
		}

		@FunctionDoc(
			name = "north",
			desc = "This returns a new Pos with the current pos z incremented by 1",
			returns = {POS, "the new Pos"},
			example = "pos.north();"
		)
		private Value north(Arguments arguments) throws CodeError {
			PosValue thisValue = arguments.getNext(PosValue.class);
			return new PosValue(thisValue.value.add(0, 0, -1));
		}

		@FunctionDoc(
			name = "north",
			desc = "This returns a new Pos with the current pos z incremented by the given number",
			params = {NUMBER, "number", "the number to increment by"},
			returns = {POS, "the new Pos"},
			example = "pos.north(2);"
		)
		private Value north1(Arguments arguments) throws CodeError {
			PosValue thisValue = arguments.getNext(PosValue.class);
			double number = arguments.getNextGeneric(NumberValue.class);
			return new PosValue(thisValue.value.add(0, 0, -number));
		}

		@FunctionDoc(
			name = "south",
			desc = "This returns a new Pos with the current pos z decremented by 1",
			returns = {POS, "the new Pos"},
			example = "pos.south();"
		)
		private Value south(Arguments arguments) throws CodeError {
			PosValue thisValue = arguments.getNext(PosValue.class);
			return new PosValue(thisValue.value.add(0, 0, 1));
		}

		@FunctionDoc(
			name = "south",
			desc = "This returns a new Pos with the current pos z decremented by the given number",
			params = {NUMBER, "number", "the number to decrement by"},
			returns = {POS, "the new Pos"},
			example = "pos.south(2);"
		)
		private Value south1(Arguments arguments) throws CodeError {
			PosValue thisValue = arguments.getNext(PosValue.class);
			double number = arguments.getNextGeneric(NumberValue.class);
			return new PosValue(thisValue.value.add(0, 0, number));
		}

		@FunctionDoc(
			name = "east",
			desc = "This returns a new Pos with the current pos x incremented by 1",
			returns = {POS, "the new Pos"},
			example = "pos.east();"
		)
		private Value east(Arguments arguments) throws CodeError {
			PosValue thisValue = arguments.getNext(PosValue.class);
			return new PosValue(thisValue.value.add(1, 0, 0));
		}

		@FunctionDoc(
			name = "east",
			desc = "This returns a new Pos with the current pos x incremented by the given number",
			params = {NUMBER, "number", "the number to increment by"},
			returns = {POS, "the new Pos"},
			example = "pos.east(2);"
		)
		private Value east1(Arguments arguments) throws CodeError {
			PosValue thisValue = arguments.getNext(PosValue.class);
			double number = arguments.getNextGeneric(NumberValue.class);
			return new PosValue(thisValue.value.add(number, 0, 0));
		}

		@FunctionDoc(
			name = "west",
			desc = "This returns a new Pos with the current pos x decremented by 1",
			returns = {POS, "the new Pos"},
			example = "pos.west();"
		)
		private Value west(Arguments arguments) throws CodeError {
			PosValue thisValue = arguments.getNext(PosValue.class);
			return new PosValue(thisValue.value.add(-1, 0, 0));
		}

		@FunctionDoc(
			name = "west",
			desc = "This returns a new Pos with the current pos x decremented by the given number",
			params = {NUMBER, "number", "the number to decrement by"},
			returns = {POS, "the new Pos"},
			example = "pos.west(2);"
		)
		private Value west1(Arguments arguments) throws CodeError {
			PosValue thisValue = arguments.getNext(PosValue.class);
			double number = arguments.getNextGeneric(NumberValue.class);
			return new PosValue(thisValue.value.add(-number, 0, 0));
		}

		@FunctionDoc(
			name = "getSidePos",
			desc = "This returns side position value of position",
			params = {STRING, "direction", "the direction, can be: north, south, east, west, up, down"},
			returns = {POS, "the side of the position"},
			example = "pos.getSidePos('east');"
		)
		private Value getSidePos(Arguments arguments) throws CodeError {
			PosValue thisValue = arguments.getNext(PosValue.class);
			StringValue stringValue = arguments.getNext(StringValue.class);
			Direction direction = ClientScriptUtils.stringToDirection(stringValue.value, null);
			return new PosValue(Vec3d.ofCenter(thisValue.blockPos).add(Vec3d.of(direction.getVector()).multiply(0.5)));
		}


		@FunctionDoc(
			name = "asCentre",
			desc = "This returns center value of the position",
			returns = {POS, "the center of the position"},
			example = "pos.asCentre();"
		)
		private Value asCentre(Arguments arguments) throws CodeError {
			PosValue thisValue = arguments.getNext(PosValue.class);
			return new PosValue(Vec3d.ofCenter(thisValue.toBlockPos()));
		}

		@FunctionDoc(
			name = "isNear",
			desc = "This returns whether position to entity is less than 4.5",
			params = {ENTITY, "entity", "the entity you want to check"},
			returns = {BOOLEAN, "whether entity is within 4.5 block distance"},
			example = "pos.isNear(Player.get());"
		)
		private Value isNear(Arguments arguments) throws CodeError {
			PosValue thisValue = arguments.getNext(PosValue.class);
			EntityValue<?> entityValue = arguments.getNext(EntityValue.class);
			return BooleanValue.of(entityValue.value.squaredDistanceTo(thisValue.value) <= 4.5);
		}

		@FunctionDoc(
			name = "isWithin",
			desc = "This returns whether position to entity is less than given distance",
			params = {
				ENTITY, "entity", "the entity you want to check",
				NUMBER, "distance", "the distance you want to check"
			},
			returns = {BOOLEAN, "whether entity is within given distance"},
			example = "pos.isNear(player, 8);"
		)
		private Value isWithin(Arguments arguments) throws CodeError {
			PosValue thisValue = arguments.getNext(PosValue.class);
			EntityValue<?> entityValue = arguments.getNext(EntityValue.class);
			double distance = arguments.getNextGeneric(NumberValue.class);
			return BooleanValue.of(entityValue.value.squaredDistanceTo(thisValue.value) <= distance);
		}

		@FunctionDoc(
			name = "distanceTo",
			desc = "This returns distance to other position",
			params = {POS, "other", "other position",},
			returns = {NUMBER, "distance to other position"},
			example = "pos.distanceTo(new Pos(0, 0, 0));"
		)
		private Value distanceTo(Arguments arguments) throws CodeError {
			PosValue thisValue = arguments.getNext(PosValue.class);
			PosValue posValue = arguments.getNext(PosValue.class);
			return NumberValue.of(thisValue.value.distanceTo(posValue.value));
		}

		@FunctionDoc(
			name = "distanceTo",
			desc = "This returns distance to other x, y, z position",
			params = {
				NUMBER, "x", "other position x",
				NUMBER, "y", "other position y",
				NUMBER, "z", "other position z",
			},
			returns = {NUMBER, "distance to other position"},
			example = "pos.distanceTo(0, 0, 0);"
		)
		private Value distanceTo1(Arguments arguments) throws CodeError {
			PosValue thisValue = arguments.getNext(PosValue.class);
			double x = arguments.getNextGeneric(NumberValue.class);
			double y = arguments.getNextGeneric(NumberValue.class);
			double z = arguments.getNextGeneric(NumberValue.class);
			return NumberValue.of(thisValue.value.distanceTo(new Vec3d(x,y,z)));
		}

		@Override
		public Class<PosValue> getValueClass() {
			return PosValue.class;
		}
	}
}
