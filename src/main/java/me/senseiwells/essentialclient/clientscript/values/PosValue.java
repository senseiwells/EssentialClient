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
				MemberFunction.of("distanceTo", 1, this::distanceToPos),
				MemberFunction.of("distanceTo", 3, this::distanceToPosUnpacked),
				MemberFunction.of("isNear", 1, this::isNearPos),
				MemberFunction.of("isNear", 2, this::isWithinDistance),
				MemberFunction.of("offset", 1, this::offsetSingle),
				MemberFunction.of("offset", 2, this::offsetAmount),
				MemberFunction.of("east", this::eastSingle),
				MemberFunction.of("south", this::southSingle),
				MemberFunction.of("west", this::westSingle),
				MemberFunction.of("north",this::northSingle),
				MemberFunction.of("up", this::upSingle),
				MemberFunction.of("down", this::downSingle),
				MemberFunction.of("east", 1, this::eastAmount),
				MemberFunction.of("south", 1, this::southAmount),
				MemberFunction.of("west", 1, this::westAmount),
				MemberFunction.of("north", 1, this::northAmount),
				MemberFunction.of("up", 1, this::upAmount),
				MemberFunction.of("down", 1, this::downAmount),
				MemberFunction.of("asCenter", this::asCenter),
				MemberFunction.of("getSidePos",1, this::getSidePos),
				MemberFunction.of("subtract", 3, this::subtract),
				MemberFunction.of("subtract", 1, this::subtract1),
				MemberFunction.of("dotProduct", 1, this::dotProduct),
				MemberFunction.of("crossProduct", 1, this::crossProduct),
				MemberFunction.of("toList", this::toList)
			);
		}
		@FunctionDoc(
			name = "distanceTo",
			desc = "This returns distance to other Position",
			params = {
				POS, "other", "other position object",
			},
			returns = {NUMBER, "distance to other position"},
			example = "pos.distanceTo(new Pos(0,0,0));"
		)
		private Value distanceToPos(Arguments arguments) throws CodeError {
			PosValue thisValue = arguments.getNext(PosValue.class);
			PosValue posValue = arguments.getNext(PosValue.class);
			return NumberValue.of(thisValue.value.distanceTo(posValue.value));
		}

		@FunctionDoc(
			name = "distanceTo",
			desc = "This returns distance to other unpacked Position",
			params = {
				NUMBER, "x", "other position x",
				NUMBER, "y", "other position y",
				NUMBER, "z", "other position z",
			},
			returns = {NUMBER, "distance to other position"},
			example = "pos.distanceTo(0,0,0);"
		)
		private Value distanceToPosUnpacked(Arguments arguments) throws CodeError {
			PosValue thisValue = arguments.getNext(PosValue.class);
			double x = arguments.getNextGeneric(NumberValue.class);
			double y = arguments.getNextGeneric(NumberValue.class);
			double z = arguments.getNextGeneric(NumberValue.class);
			return NumberValue.of(thisValue.value.distanceTo(new Vec3d(x,y,z)));
		}

		@FunctionDoc(
			name = "isNear",
			desc = "This returns whether position to entity is less than 4.5",
			params = {
				ENTITY, "entityValue", "Entity Value"
			},
			returns = {BOOLEAN, "whether entity is within 4.5 block distance"},
			example = "pos.isNear(player);"
		)
		private Value isNearPos(Arguments arguments) throws CodeError {
			PosValue thisValue = arguments.getNext(PosValue.class);
			EntityValue<?> entityValue = arguments.getNext(EntityValue.class);
			return BooleanValue.of(entityValue.value.squaredDistanceTo(thisValue.value) <= 4.5);
		}

		@FunctionDoc(
			name = "isNear",
			desc = "This returns whether position to entity is less than given distance",
			params = {
				ENTITY, "entityValue", "Entity Value",
				NUMBER, "r", "distance"
			},
			returns = {BOOLEAN, "whether entity is within given distance"},
			example = "pos.isNear(player, 8);"
		)
		private Value isWithinDistance(Arguments arguments) throws CodeError {
			PosValue thisValue = arguments.getNext(PosValue.class);
			EntityValue<?> entityValue = arguments.getNext(EntityValue.class);
			double distance = arguments.getNextGeneric(NumberValue.class);
			return BooleanValue.of(entityValue.value.squaredDistanceTo(thisValue.value) <= distance);
		}

		@FunctionDoc(
			name = "offset",
			desc = "This returns new position offset with given direction",
			params = {
				STRING, "direction", "Direction Name"
			},
			returns = {POS, "new Pos object offset to given direction"},
			example = "pos.offset(\"north\");"
		)
		private Value offsetSingle(Arguments arguments) throws CodeError {
			PosValue thisValue = arguments.getNext(PosValue.class);
			StringValue stringValue = arguments.getNext(StringValue.class);
			Direction direction = Direction.byName(stringValue.value);
			if (direction == null)
				throw arguments.getError("String " + stringValue.value + " is not valid direction name");
			return new PosValue(thisValue.value.add(Vec3d.of(direction.getVector())));
		}
		@FunctionDoc(
			name = "offset",
			desc = "This returns new position offset with given direction with amount",
			params = {
				STRING, "direction", "Direction Name",
				NUMBER, "p", "amount"
			},
			returns = {POS, "new Pos object offset to given direction with amount"},
			example = "pos.offset(\"north\", 3);"
		)
		private Value offsetAmount(Arguments arguments) throws CodeError {
			PosValue thisValue = arguments.getNext(PosValue.class);
			StringValue stringValue = arguments.getNext(StringValue.class);
			double amount = arguments.getNextGeneric(NumberValue.class);
			Direction direction = Direction.byName(stringValue.value);
			if (direction == null)
				throw arguments.getError("String " + stringValue.value + " is not valid direction name");
			return innerOffsetDirection(thisValue.value, direction, amount);
		}
		@FunctionDoc(
			name = "east",
			desc = "This returns new position offset with east",
			returns = {POS, "east position value"},
			example = "pos.east();"
		)
		private Value eastSingle(Arguments arguments) throws CodeError {
			PosValue thisValue = arguments.getNext(PosValue.class);
			return innerOffsetDirection(thisValue.value, Direction.EAST, 1);
		}

		@FunctionDoc(
			name = "east",
			desc = "This returns new position with east, with given amount",
			params = {
				NUMBER, "double", "amount"
			},
			returns = {POS, "offset position value"},
			example = "pos.east(1);"
		)

		private Value eastAmount(Arguments arguments) throws CodeError {
			PosValue thisValue = arguments.getNext(PosValue.class);
			double amount = arguments.getNextGeneric(NumberValue.class);
			return innerOffsetDirection(thisValue.value, Direction.EAST, amount);
		}
		@FunctionDoc(
			name = "south",
			desc = "This returns new position offset with south",
			returns = {POS, "south position value"},
			example = "pos.south();"
		)
		private Value southSingle(Arguments arguments) throws CodeError {
			PosValue thisValue = arguments.getNext(PosValue.class);
			return innerOffsetDirection(thisValue.value, Direction.SOUTH, 1);
		}

		@FunctionDoc(
			name = "south",
			desc = "This returns new position with south, with given amount",
			params = {
				NUMBER, "double", "amount"
			},
			returns = {POS, "offset position value"},
			example = "pos.south(1);"
		)
		private Value southAmount(Arguments arguments) throws CodeError {
			PosValue thisValue = arguments.getNext(PosValue.class);
			double amount = arguments.getNextGeneric(NumberValue.class);
			return innerOffsetDirection(thisValue.value, Direction.SOUTH, amount);
		}

		@FunctionDoc(
			name = "west",
			desc = "This returns new position offset with west",
			returns = {POS, "west position value"},
			example = "pos.west();"
		)
		private Value westSingle(Arguments arguments) throws CodeError {
			PosValue thisValue = arguments.getNext(PosValue.class);
			return innerOffsetDirection(thisValue.value, Direction.WEST, 1);
		}

		@FunctionDoc(
			name = "west",
			desc = "This returns new position with west, with given amount",
			params = {
				NUMBER, "double", "amount"
			},
			returns = {POS, "offset position value"},
			example = "pos.west(1);"
		)

		private Value westAmount(Arguments arguments) throws CodeError {
			PosValue thisValue = arguments.getNext(PosValue.class);
			double amount = arguments.getNextGeneric(NumberValue.class);
			return innerOffsetDirection(thisValue.value, Direction.WEST, amount);
		}

		@FunctionDoc(
			name = "north",
			desc = "This returns new position offset with north",
			returns = {POS, "north position value"},
			example = "pos.north();"
		)
		private Value northSingle(Arguments arguments) throws CodeError {
			PosValue thisValue = arguments.getNext(PosValue.class);
			return innerOffsetDirection(thisValue.value, Direction.NORTH, 1);
		}

		@FunctionDoc(
			name = "north",
			desc = "This returns new position with north, with given amount",
			params = {
				NUMBER, "double", "amount"
			},
			returns = {POS, "offset position value"},
			example = "pos.north(1);"
		)
		private Value northAmount(Arguments arguments) throws CodeError {
			PosValue thisValue = arguments.getNext(PosValue.class);
			double amount = arguments.getNextGeneric(NumberValue.class);
			return innerOffsetDirection(thisValue.value, Direction.NORTH, amount);
		}

		@FunctionDoc(
			name = "up",
			desc = "This returns new position offset with up",
			returns = {POS, "up position value"},
			example = "pos.up();"
		)
		private Value upSingle(Arguments arguments) throws CodeError {
			PosValue thisValue = arguments.getNext(PosValue.class);
			return innerOffsetDirection(thisValue.value, Direction.UP, 1);
		}

		@FunctionDoc(
			name = "up",
			desc = "This returns new position with up, with given amount",
			params = {
				NUMBER, "double", "amount"
			},
			returns = {POS, "offset position value"},
			example = "pos.up(3);"
		)
		private Value upAmount(Arguments arguments) throws CodeError {
			PosValue thisValue = arguments.getNext(PosValue.class);
			double amount = arguments.getNextGeneric(NumberValue.class);
			return innerOffsetDirection(thisValue.value, Direction.UP, amount);
		}

		@FunctionDoc(
			name = "down",
			desc = "This returns new position offset with down",
			returns = {POS, "down position value"},
			example = "pos.down();"
		)
		private Value downSingle(Arguments arguments) throws CodeError {
			PosValue thisValue = arguments.getNext(PosValue.class);
			return innerOffsetDirection(thisValue.value, Direction.DOWN, 1);
		}

		@FunctionDoc(
			name = "down",
			desc = "This returns new position with down, with given amount",
			params = {
				NUMBER, "double", "amount"
			},
			returns = {POS, "offset position value"},
			example = "pos.down(3);"
		)
		private Value downAmount(Arguments arguments) throws CodeError {
			PosValue thisValue = arguments.getNext(PosValue.class);
			double amount = arguments.getNextGeneric(NumberValue.class);
			return innerOffsetDirection(thisValue.value, Direction.DOWN, amount);
		}

		@FunctionDoc(
			name = "asCenter",
			desc = "This returns center value of blockPos",
			params = {
				STRING, "direction", "Direction Name"
			},
			returns = {POS, "Center of the blockPos"},
			example = "pos.asCenter();"
		)
		private Value asCenter(Arguments arguments) throws CodeError {
			PosValue thisValue = arguments.getNext(PosValue.class);
			return new PosValue(Vec3d.ofCenter(thisValue.blockPos));
		}

		@FunctionDoc(
			name = "getSidePos",
			desc = "This returns Side value of blockPos",
			params = {
				STRING, "direction", "Direction Name"
			},
			returns = {POS, "Side of the blockPos"},
			example = "pos.getSidePos('east');"
		)
		private Value getSidePos(Arguments arguments) throws CodeError {
			PosValue thisValue = arguments.getNext(PosValue.class);
			StringValue stringValue = arguments.getNext(StringValue.class);
			Direction direction = Direction.byName(stringValue.value);
			if (direction == null)
				throw arguments.getError("String " + stringValue.value + " is not valid direction name");
			return new PosValue(Vec3d.ofCenter(thisValue.blockPos).add(Vec3d.of(direction.getVector()).multiply(0.5)));
		}

		private Value innerOffsetDirection(Vec3d origin, Direction direction, double amount) {
			return new PosValue(origin.add(Vec3d.of(direction.getVector()).multiply(amount)));
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

		@Override
		public Class<PosValue> getValueClass() {
			return PosValue.class;
		}
	}
}
