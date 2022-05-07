package me.senseiwells.essentialclient.clientscript.values;

import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.impl.ArucasList;
import me.senseiwells.arucas.values.ListValue;
import me.senseiwells.arucas.values.NumberValue;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.functions.BuiltInFunction;
import me.senseiwells.arucas.values.functions.ConstructorFunction;
import me.senseiwells.arucas.values.functions.MemberFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class PosValue extends Value<Vec3d> {
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
	public Value<Vec3d> copy(Context context) throws CodeError {
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
	public boolean isEquals(Context context, Value<?> value) throws CodeError {
		return this.value.equals(value.value);
	}

	@Override
	public String getTypeName() {
		return "Pos";
	}

	public static class ArucasPosClass extends ArucasClassExtension {
		public ArucasPosClass() {
			super("Pos");
		}

		@Override
		public ArucasFunctionMap<ConstructorFunction> getDefinedConstructors() {
			return ArucasFunctionMap.of(
				new ConstructorFunction(List.of("x", "y", "z"), this::newPos)
			);
		}

		private Value<?> newPos(Context context, BuiltInFunction function) throws CodeError {
			double x = function.getParameterValueOfType(context, NumberValue.class, 0).value;
			double y = function.getParameterValueOfType(context, NumberValue.class, 1).value;
			double z = function.getParameterValueOfType(context, NumberValue.class, 2).value;
			return new PosValue(x, y, z);
		}

		@Override
		public ArucasFunctionMap<MemberFunction> getDefinedMethods() {
			return ArucasFunctionMap.of(
				new MemberFunction("getX", this::getX),
				new MemberFunction("getY", this::getY),
				new MemberFunction("getZ", this::getZ),
				new MemberFunction("toList", this::toList)
			);
		}

		private Value<?> getX(Context context, MemberFunction function) throws CodeError {
			PosValue thisValue = function.getThis(context, PosValue.class);
			return NumberValue.of(thisValue.value.getX());
		}

		private Value<?> getY(Context context, MemberFunction function) throws CodeError {
			PosValue thisValue = function.getThis(context, PosValue.class);
			return NumberValue.of(thisValue.value.getY());
		}

		private Value<?> getZ(Context context, MemberFunction function) throws CodeError {
			PosValue thisValue = function.getThis(context, PosValue.class);
			return NumberValue.of(thisValue.value.getZ());
		}

		private Value<?> toList(Context context, MemberFunction function) throws CodeError {
			PosValue thisValue = function.getThis(context, PosValue.class);
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
