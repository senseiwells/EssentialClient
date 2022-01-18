package essentialclient.clientscript.values;

import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.impl.ArucasMap;
import me.senseiwells.arucas.values.*;
import me.senseiwells.arucas.values.functions.BuiltInFunction;
import me.senseiwells.arucas.values.functions.MemberFunction;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.BlockItem;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

import java.util.Map;

public class BlockValue extends Value<BlockState> {
	private final PosValue blockPos;
	private final boolean hasPos;

	public BlockValue(BlockState block, PosValue pos) {
		super(block);
		this.blockPos = pos;
		this.hasPos = pos != null;
	}

	public BlockValue(BlockState block, BlockPos pos) {
		this(block, new PosValue(pos));
	}

	public BlockValue(BlockState block) {
		this(block, (PosValue) null);
	}

	public boolean hasBlockPos() {
		return this.hasPos;
	}

	public Value<?> getPos() {
		return this.hasPos ? this.blockPos : NullValue.NULL;
	}

	public Value<?> getBlockX() {
		return this.hasPos ? this.blockPos.getX() :  NullValue.NULL;
	}

	public Value<?> getBlockY() {
		return this.hasPos ? this.blockPos.getY() : NullValue.NULL;
	}

	public Value<?> getBlockZ() {
		return this.hasPos ? this.blockPos.getZ() : NullValue.NULL;
	}

	@Override
	public Value<BlockState> copy(Context context) {
		return this;
	}

	@Override
	public int getHashCode(Context context) {
		return this.getAsString(context).hashCode();
	}

	@Override
	public boolean isEquals(Context context, Value<?> value) {
		if (!(value instanceof BlockValue otherValue)) {
			return false;
		}
		return this.value.getBlock().equals(otherValue.value.getBlock());
	}

	@Override
	public String getAsString(Context context) {
		return "Block{id=%s}".formatted(Registry.BLOCK.getId(this.value.getBlock()).getPath());
	}

	public static class ArucasBlockClass extends ArucasClassExtension {
		public ArucasBlockClass() {
			super("Block");
		}

		@Override
		public ArucasFunctionMap<BuiltInFunction> getDefinedStaticMethods() {
			return ArucasFunctionMap.of(
				new BuiltInFunction("of", "material", this::of)
			);
		}

		private Value<?> of(Context context, BuiltInFunction function) throws CodeError {
			MaterialValue materialValue = function.getParameterValueOfType(context, MaterialValue.class, 0);
			if (!(materialValue.value instanceof BlockItem blockItem)) {
				throw new RuntimeError("Item cannot be converted to block", function.syntaxPosition, context);
			}
			return new BlockValue(blockItem.getBlock().getDefaultState());
		}

		@Override
		public ArucasFunctionMap<MemberFunction> getDefinedMethods() {
			return ArucasFunctionMap.of(
				new MemberFunction("getMaterial", (context, function) -> new MaterialValue(this.getBlockState(context, function).getBlock().asItem())),
				new MemberFunction("getId", (context, function) -> StringValue.of(Registry.BLOCK.getId(this.getBlockState(context, function).getBlock()).getPath())),
				new MemberFunction("isBlockEntity", (context, function) -> BooleanValue.of(this.getBlockState(context, function).getBlock() instanceof BlockEntityProvider)),
				new MemberFunction("isTransparent", (context, function) -> BooleanValue.of(!this.getBlockState(context, function).isOpaque())),
				new MemberFunction("asItemStack", (context, function) -> new ItemStackValue(this.getBlockState(context, function).getBlock().asItem().getDefaultStack())),
				new MemberFunction("getBlastResistance", (context, function) -> NumberValue.of(this.getBlockState(context, function).getBlock().getBlastResistance())),
				new MemberFunction("getBlockProperties", this::getBlockProperties),
				new MemberFunction("hasBlockPosition", this::hasBlockPosition),
				new MemberFunction("getPos", this::getPos),
				new MemberFunction("getX", this::getBlockX),
				new MemberFunction("getZ", this::getBlockZ),
				new MemberFunction("getY", this::getBlockY),
				new MemberFunction("getTranslatedName", this::getTranslatedName)
			);
		}

		private Value<?> getBlockProperties(Context context, MemberFunction function) throws CodeError {
			BlockState blockState = this.getBlockState(context, function);
			ArucasMap propertyMap = new ArucasMap();
			for (Map.Entry<Property<?>, Comparable<?>> entry : blockState.getEntries().entrySet()) {
				Value<?> mapValue;
				Comparable<?> comparable = entry.getValue();
				if (comparable instanceof Number value) {
					mapValue = NumberValue.of(value.doubleValue());
				}
				else if (comparable instanceof Boolean value) {
					mapValue = BooleanValue.of(value);
				}
				else {
					mapValue = StringValue.of(comparable.toString());
				}
				propertyMap.put(context, StringValue.of(entry.getKey().getName()), mapValue);
			}
			return new MapValue(propertyMap);
		}

		private Value<?> hasBlockPosition(Context context, MemberFunction function) throws CodeError {
			BlockValue blockStateValue = function.getParameterValueOfType(context, BlockValue.class, 0);
			return BooleanValue.of(blockStateValue.hasBlockPos());
		}

		private Value<?> getPos(Context context, MemberFunction function) throws CodeError {
			BlockValue blockStateValue = function.getParameterValueOfType(context, BlockValue.class, 0);
			return blockStateValue.getPos();
		}

		private Value<?> getBlockX(Context context, MemberFunction function) throws CodeError {
			BlockValue blockStateValue = function.getParameterValueOfType(context, BlockValue.class, 0);
			return blockStateValue.getBlockX();
		}

		private Value<?> getBlockY(Context context, MemberFunction function) throws CodeError {
			BlockValue blockStateValue = function.getParameterValueOfType(context, BlockValue.class, 0);
			return blockStateValue.getBlockY();
		}

		private Value<?> getBlockZ(Context context, MemberFunction function) throws CodeError {
			BlockValue blockStateValue = function.getParameterValueOfType(context, BlockValue.class, 0);
			return blockStateValue.getBlockZ();
		}

		private Value<?> getTranslatedName(Context context, MemberFunction function) throws CodeError {
			BlockState blockState = this.getBlockState(context, function);
			return StringValue.of(I18n.translate(blockState.getBlock().getTranslationKey()));
		}

		private BlockState getBlockState(Context context, MemberFunction function) throws CodeError {
			BlockState block = function.getParameterValueOfType(context, BlockValue.class, 0).value;
			if (block == null) {
				throw new RuntimeError("Block was null", function.syntaxPosition, context);
			}
			return block;
		}

		@Override
		public Class<?> getValueClass() {
			return BlockValue.class;
		}
	}
}
