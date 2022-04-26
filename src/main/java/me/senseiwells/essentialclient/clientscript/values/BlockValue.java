package me.senseiwells.essentialclient.clientscript.values;

import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.impl.ArucasMap;
import me.senseiwells.arucas.values.*;
import me.senseiwells.arucas.values.functions.BuiltInFunction;
import me.senseiwells.arucas.values.functions.MemberFunction;
import me.senseiwells.essentialclient.clientscript.extensions.ArucasMinecraftExtension;
import me.senseiwells.essentialclient.utils.clientscript.NbtUtils;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.EntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;

import java.util.Map;
import java.util.Objects;

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
		return this.hasPos ? this.blockPos.getX() : NullValue.NULL;
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
	public String getTypeName() {
		return "Block";
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
				throw new RuntimeError("Material cannot be converted to block", function.syntaxPosition, context);
			}
			return new BlockValue(blockItem.getBlock().getDefaultState());
		}

		@Override
		public ArucasFunctionMap<MemberFunction> getDefinedMethods() {
			return ArucasFunctionMap.of(
				new MemberFunction("getMaterial", this::getMaterial),
				new MemberFunction("getFullId", this::getFullId),
				new MemberFunction("getId", this::getId),
				new MemberFunction("isBlockEntity", this::isBlockEntity),
				new MemberFunction("isTransparent", this::isTransparent),
				new MemberFunction("asItemStack", this::asItemStack),
				new MemberFunction("getBlastResistance", this::getBlastResistance),
				new MemberFunction("getBlockProperties", this::getBlockProperties),
				new MemberFunction("hasBlockPosition", this::hasBlockPosition),
				new MemberFunction("getPos", this::getPos),
				new MemberFunction("getX", this::getBlockX),
				new MemberFunction("getZ", this::getBlockZ),
				new MemberFunction("getY", this::getBlockY),
				new MemberFunction("getTranslatedName", this::getTranslatedName),
				new MemberFunction("isSolidBlock", this::isSolidBlock),
				new MemberFunction("rotateYClockwise", this::rotateYClockwise),
				new MemberFunction("rotateYCounterClockwise", this::rotateYCounterClockwise),
				new MemberFunction("mirrorFrontBack", this::mirrorFrontBack),
				new MemberFunction("mirrorLeftRight", this::mirrorLeftRight),
				new MemberFunction("isFluid", this::isFluid),
				new MemberFunction("isFluidSource", this::isFluidSource),
				new MemberFunction("isReplaceable", this::isReplaceable),
				new MemberFunction("getHardness", this::getHardness),
				new MemberFunction("sideCoversSmallSquare", "direction", this::sideCoversSmallSquare),
				new MemberFunction("isSideSolidFullSquare", "direction", this::isSideSolidFullSquare),
				new MemberFunction("isSpawnable", this::isSpawnable),
				new MemberFunction("isSpawnable", "entity", this::isSpawnableType),
				new MemberFunction("getLuminance", this::getLuminance),
				new MemberFunction("getBlockEntityNbt", this::getBlockNbt)
			);
		}

		private Value<?> getMaterial(Context context, MemberFunction function) throws CodeError {
			return new MaterialValue(this.getBlockState(context, function).getBlock().asItem());
		}

		private Value<?> getFullId(Context context, MemberFunction function) throws CodeError {
			return StringValue.of(Registry.BLOCK.getId(this.getBlockState(context, function).getBlock()).toString());
		}

		private Value<?> getId(Context context, MemberFunction function) throws CodeError {
			return StringValue.of(Registry.BLOCK.getId(this.getBlockState(context, function).getBlock()).getPath());
		}

		private Value<?> isBlockEntity(Context context, MemberFunction function) throws CodeError {
			return BooleanValue.of(this.getBlockState(context, function).getBlock() instanceof BlockEntityProvider);
		}

		private Value<?> isTransparent(Context context, MemberFunction function) throws CodeError {
			return BooleanValue.of(!this.getBlockState(context, function).isOpaque());
		}

		private Value<?> asItemStack(Context context, MemberFunction function) throws CodeError {
			return new ItemStackValue(this.getBlockState(context, function).getBlock().asItem().getDefaultStack());
		}

		private Value<?> getBlastResistance(Context context, MemberFunction function) throws CodeError {
			return NumberValue.of(this.getBlockState(context, function).getBlock().getBlastResistance());
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

		private Value<?> isSolidBlock(Context context, MemberFunction function) throws CodeError {
			BlockValue blockStateValue = function.getParameterValueOfType(context, BlockValue.class, 0);
			if (!blockStateValue.hasPos) {
				throw new RuntimeError("Block does not have position", function.syntaxPosition, context);
			}
			boolean isSolid = blockStateValue.value.isSolidBlock(ArucasMinecraftExtension.getWorld(), blockStateValue.blockPos.toBlockPos());
			return BooleanValue.of(isSolid);
		}

		private Value<?> rotateYClockwise(Context context, MemberFunction function) throws CodeError {
			BlockState blockState = this.getBlockState(context, function);
			return new BlockValue(blockState.rotate(BlockRotation.CLOCKWISE_90));
		}

		private Value<?> rotateYCounterClockwise(Context context, MemberFunction function) throws CodeError {
			BlockState blockState = this.getBlockState(context, function);
			return new BlockValue(blockState.rotate(BlockRotation.COUNTERCLOCKWISE_90));
		}

		private Value<?> mirrorFrontBack(Context context, MemberFunction function) throws CodeError {
			BlockState blockState = this.getBlockState(context, function);
			return new BlockValue(blockState.mirror(BlockMirror.FRONT_BACK));
		}

		private Value<?> mirrorLeftRight(Context context, MemberFunction function) throws CodeError {
			BlockState blockState = this.getBlockState(context, function);
			return new BlockValue(blockState.mirror(BlockMirror.LEFT_RIGHT));
		}

		private Value<?> isFluid(Context context, MemberFunction function) throws CodeError {
			BlockValue blockStateValue = function.getParameterValueOfType(context, BlockValue.class, 0);
			return BooleanValue.of(blockStateValue.value.contains(FluidBlock.LEVEL));
		}

		private Value<?> isFluidSource(Context context, MemberFunction function) throws CodeError {
			BlockState blockState = this.getBlockState(context, function);
			Block block = blockState.getBlock();
			if ((block instanceof FluidBlock && blockState.get(FluidBlock.LEVEL) == 0) || block instanceof BubbleColumnBlock) {
				return BooleanValue.TRUE;
			}
			return BooleanValue.of(block instanceof Waterloggable && blockState.get(Properties.WATERLOGGED));
		}

		private Value<?> isReplaceable(Context context, MemberFunction function) throws CodeError {
			BlockState blockState = this.getBlockState(context, function);
			return BooleanValue.of(blockState.getMaterial().isReplaceable());
		}

		private Value<?> getHardness(Context context, MemberFunction function) throws CodeError {
			BlockState blockState = this.getBlockState(context, function);
			return NumberValue.of(blockState.getHardness(ArucasMinecraftExtension.getWorld(), BlockPos.ORIGIN));
		}

		private Value<?> sideCoversSmallSquare(Context context, MemberFunction function) throws CodeError {
			BlockValue blockStateValue = function.getParameterValueOfType(context, BlockValue.class, 0);
			StringValue stringDirection = function.getParameterValueOfType(context, StringValue.class, 1);
			if (!blockStateValue.hasPos) {
				throw new RuntimeError("Block does not have position", function.syntaxPosition, context);
			}
			Direction direction = Objects.requireNonNullElse(Direction.byName(stringDirection.value), Direction.DOWN);
			return BooleanValue.of(Block.sideCoversSmallSquare(ArucasMinecraftExtension.getWorld(), blockStateValue.blockPos.toBlockPos(), direction));
		}

		private Value<?> isSideSolidFullSquare(Context context, MemberFunction function) throws CodeError {
			BlockValue blockStateValue = function.getParameterValueOfType(context, BlockValue.class, 0);
			StringValue stringDirection = function.getParameterValueOfType(context, StringValue.class, 1);
			if (!blockStateValue.hasPos) {
				throw new RuntimeError("Block does not have position", function.syntaxPosition, context);
			}
			Direction direction = Objects.requireNonNullElse(Direction.byName(stringDirection.value), Direction.DOWN);
			return BooleanValue.of(blockStateValue.value.isSideSolidFullSquare(ArucasMinecraftExtension.getWorld(), blockStateValue.blockPos.toBlockPos(), direction));
		}

		private Value<?> isSpawnable(Context context, MemberFunction function) throws CodeError {
			BlockValue blockStateValue = function.getParameterValueOfType(context, BlockValue.class, 0);
			if (!blockStateValue.hasPos) {
				throw new RuntimeError("Block does not have position", function.syntaxPosition, context);
			}
			boolean isSpawnable = blockStateValue.value.allowsSpawning(ArucasMinecraftExtension.getWorld(), blockStateValue.blockPos.toBlockPos(), EntityType.ZOMBIE);
			return BooleanValue.of(isSpawnable);
		}

		private Value<?> isSpawnableType(Context context, MemberFunction function) throws CodeError {
			BlockValue blockStateValue = function.getParameterValueOfType(context, BlockValue.class, 0);
			EntityValue<?> entityValue = function.getParameterValueOfType(context, EntityValue.class, 1);
			if (!blockStateValue.hasPos) {
				throw new RuntimeError("Block does not have position", function.syntaxPosition, context);
			}
			boolean isSpawnable = blockStateValue.value.allowsSpawning(ArucasMinecraftExtension.getWorld(), blockStateValue.blockPos.toBlockPos(), entityValue.value.getType());
			return BooleanValue.of(isSpawnable);
		}

		private Value<?> getLuminance(Context context, MemberFunction function) throws CodeError {
			BlockState blockState = this.getBlockState(context, function);
			return NumberValue.of(blockState.getLuminance());
		}

		private Value<?> getBlockNbt(Context context, MemberFunction function) throws CodeError {
			BlockValue blockValue = function.getThis(context, BlockValue.class);
			BlockEntity blockEntity;
			if (blockValue.hasBlockPos() && (blockEntity = ArucasMinecraftExtension.getWorld().getBlockEntity(blockValue.blockPos.toBlockPos())) != null) {
				return new MapValue(NbtUtils.nbtToMap(context, blockEntity.writeNbt(new NbtCompound()), 10));
			}
			return NullValue.NULL;
		}

		private BlockState getBlockState(Context context, MemberFunction function) throws CodeError {
			BlockState block = function.getParameterValueOfType(context, BlockValue.class, 0).value;
			if (block == null) {
				throw new RuntimeError("Block was null", function.syntaxPosition, context);
			}
			return block;
		}

		@Override
		public Class<BlockValue> getValueClass() {
			return BlockValue.class;
		}
	}
}
