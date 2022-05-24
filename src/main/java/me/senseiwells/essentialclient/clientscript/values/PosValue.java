package me.senseiwells.essentialclient.clientscript.values;

import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.api.docs.ClassDoc;
import me.senseiwells.arucas.api.docs.ConstructorDoc;
import me.senseiwells.arucas.api.docs.FunctionDoc;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.Arguments;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.impl.ArucasList;
import me.senseiwells.arucas.values.GenericValue;
import me.senseiwells.arucas.values.ListValue;
import me.senseiwells.arucas.values.NumberValue;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.functions.ConstructorFunction;
import me.senseiwells.arucas.values.functions.MemberFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import static me.senseiwells.arucas.utils.ValueTypes.LIST;
import static me.senseiwells.arucas.utils.ValueTypes.NUMBER;
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
				ConstructorFunction.of(3, this::newPos)
			);
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
				MemberFunction.of("toList", this::toList)
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
