package me.senseiwells.essentialclient.clientscript.values;

import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.Arguments;
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
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static me.senseiwells.essentialclient.clientscript.core.MinecraftAPI.BLOCK;

public class BlockValue extends GenericValue<BlockState> {
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

	public Value getPos() {
		return this.hasPos ? this.blockPos : NullValue.NULL;
	}

	public Value getBlockX() {
		return this.hasPos ? this.blockPos.getX() : NullValue.NULL;
	}

	public Value getBlockY() {
		return this.hasPos ? this.blockPos.getY() : NullValue.NULL;
	}

	public Value getBlockZ() {
		return this.hasPos ? this.blockPos.getZ() : NullValue.NULL;
	}

	@Override
	public GenericValue<BlockState> copy(Context context) {
		return this;
	}

	@Override
	public int getHashCode(Context context) {
		return this.getAsString(context).hashCode();
	}

	@Override
	public boolean isEquals(Context context, Value value) {
		if (!(value instanceof BlockValue otherValue)) {
			return false;
		}
		return this.value.getBlock().equals(otherValue.value.getBlock());
	}

	@Override
	public String getTypeName() {
		return BLOCK;
	}

	@Override
	public String getAsString(Context context) {
		return "Block{id=%s}".formatted(Registry.BLOCK.getId(this.value.getBlock()).getPath());
	}

	public static class ArucasBlockClass extends ArucasClassExtension {
		public ArucasBlockClass() {
			super(BLOCK);
		}

		@Override
		public ArucasFunctionMap<BuiltInFunction> getDefinedStaticMethods() {
			return ArucasFunctionMap.of(
				BuiltInFunction.of("of", 1, this::of)
			);
		}

		private Value of(Arguments arguments) throws CodeError {
			Value value = arguments.getNext();
			if (value instanceof StringValue stringValue) {
				Identifier id = ArucasMinecraftExtension.getId(arguments, stringValue.value);
				Optional<Block> block = Registry.BLOCK.getOrEmpty(id);
				return new BlockValue(block.orElseThrow(() -> {
					return arguments.getError("'%s' is not a value block", id);
				}).getDefaultState());
			}
			if (!(value instanceof MaterialValue materialValue)) {
				throw arguments.getError("Parameter must be of type String or Material");
			}
			return new BlockValue(materialValue.asBlock(arguments).getDefaultState());
		}

		@Override
		public ArucasFunctionMap<MemberFunction> getDefinedMethods() {
			return ArucasFunctionMap.of(
				MemberFunction.of("getMaterial", this::getMaterial),
				MemberFunction.of("getFullId", this::getFullId),
				MemberFunction.of("getId", this::getId),
				MemberFunction.of("isBlockEntity", this::isBlockEntity),
				MemberFunction.of("isTransparent", this::isTransparent),
				MemberFunction.of("asItemStack", this::asItemStack),
				MemberFunction.of("getBlastResistance", this::getBlastResistance),
				MemberFunction.of("getBlockProperties", this::getBlockProperties),
				MemberFunction.of("hasBlockPosition", this::hasBlockPosition),
				MemberFunction.of("getPos", this::getPos),
				MemberFunction.of("getX", this::getBlockX),
				MemberFunction.of("getZ", this::getBlockZ),
				MemberFunction.of("getY", this::getBlockY),
				MemberFunction.of("getTranslatedName", this::getTranslatedName),
				MemberFunction.of("isSolidBlock", this::isSolidBlock),
				MemberFunction.of("rotateYClockwise", this::rotateYClockwise),
				MemberFunction.of("rotateYCounterClockwise", this::rotateYCounterClockwise),
				MemberFunction.of("mirrorFrontBack", this::mirrorFrontBack),
				MemberFunction.of("mirrorLeftRight", this::mirrorLeftRight),
				MemberFunction.of("isFluid", this::isFluid),
				MemberFunction.of("isFluidSource", this::isFluidSource),
				MemberFunction.of("isReplaceable", this::isReplaceable),
				MemberFunction.of("getHardness", this::getHardness),
				MemberFunction.of("sideCoversSmallSquare", 1, this::sideCoversSmallSquare),
				MemberFunction.of("isSideSolidFullSquare", 1, this::isSideSolidFullSquare),
				MemberFunction.of("isSpawnable", this::isSpawnable),
				MemberFunction.of("isSpawnable", 1, this::isSpawnableType),
				MemberFunction.of("getLuminance", this::getLuminance),
				MemberFunction.of("getBlockEntityNbt", this::getBlockNbt)
			);
		}

		private Value getMaterial(Arguments arguments) throws CodeError {
			BlockState blockState = this.getBlockState(arguments);
			Item blockItem = blockState.getBlock().asItem();
			if (blockItem == Items.AIR && blockState.getBlock() != Blocks.AIR) {
				return MaterialValue.blockMaterial(blockState.getBlock());
			}
			return new MaterialValue(blockItem);
		}

		private Value getFullId(Arguments arguments) throws CodeError {
			return StringValue.of(Registry.BLOCK.getId(this.getBlockState(arguments).getBlock()).toString());
		}

		private Value getId(Arguments arguments) throws CodeError {
			return StringValue.of(Registry.BLOCK.getId(this.getBlockState(arguments).getBlock()).getPath());
		}

		private Value isBlockEntity(Arguments arguments) throws CodeError {
			return BooleanValue.of(this.getBlockState(arguments).getBlock() instanceof BlockEntityProvider);
		}

		private Value isTransparent(Arguments arguments) throws CodeError {
			return BooleanValue.of(!this.getBlockState(arguments).isOpaque());
		}

		private Value asItemStack(Arguments arguments) throws CodeError {
			return new ItemStackValue(this.getBlockState(arguments).getBlock().asItem().getDefaultStack());
		}

		private Value getBlastResistance(Arguments arguments) throws CodeError {
			return NumberValue.of(this.getBlockState(arguments).getBlock().getBlastResistance());
		}

		private Value getBlockProperties(Arguments arguments) throws CodeError {
			BlockState blockState = this.getBlockState(arguments);
			ArucasMap propertyMap = new ArucasMap();
			for (Map.Entry<Property<?>, Comparable<?>> entry : blockState.getEntries().entrySet()) {
				Value mapValue;
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
				propertyMap.put(arguments.getContext(), StringValue.of(entry.getKey().getName()), mapValue);
			}
			return new MapValue(propertyMap);
		}

		private Value hasBlockPosition(Arguments arguments) throws CodeError {
			BlockValue blockValue = arguments.getNext(BlockValue.class);
			return BooleanValue.of(blockValue.hasBlockPos());
		}

		private Value getPos(Arguments arguments) throws CodeError {
			BlockValue blockValue = arguments.getNext(BlockValue.class);
			return blockValue.getPos();
		}

		private Value getBlockX(Arguments arguments) throws CodeError {
			BlockValue blockValue = arguments.getNext(BlockValue.class);
			return blockValue.getBlockX();
		}

		private Value getBlockY(Arguments arguments) throws CodeError {
			BlockValue blockValue = arguments.getNext(BlockValue.class);
			return blockValue.getBlockY();
		}

		private Value getBlockZ(Arguments arguments) throws CodeError {
			BlockValue blockValue = arguments.getNext(BlockValue.class);
			return blockValue.getBlockZ();
		}

		private Value getTranslatedName(Arguments arguments) throws CodeError {
			BlockState blockState = this.getBlockState(arguments);
			return StringValue.of(I18n.translate(blockState.getBlock().getTranslationKey()));
		}

		private Value isSolidBlock(Arguments arguments) throws CodeError {
			BlockValue blockValue = this.getBlockWithPos(arguments);
			boolean isSolid = blockValue.value.isSolidBlock(ArucasMinecraftExtension.getWorld(), blockValue.blockPos.toBlockPos());
			return BooleanValue.of(isSolid);
		}

		private Value rotateYClockwise(Arguments arguments) throws CodeError {
			BlockState blockState = this.getBlockState(arguments);
			return new BlockValue(blockState.rotate(BlockRotation.CLOCKWISE_90));
		}

		private Value rotateYCounterClockwise(Arguments arguments) throws CodeError {
			BlockState blockState = this.getBlockState(arguments);
			return new BlockValue(blockState.rotate(BlockRotation.COUNTERCLOCKWISE_90));
		}

		private Value mirrorFrontBack(Arguments arguments) throws CodeError {
			BlockState blockState = this.getBlockState(arguments);
			return new BlockValue(blockState.mirror(BlockMirror.FRONT_BACK));
		}

		private Value mirrorLeftRight(Arguments arguments) throws CodeError {
			BlockState blockState = this.getBlockState(arguments);
			return new BlockValue(blockState.mirror(BlockMirror.LEFT_RIGHT));
		}

		private Value isFluid(Arguments arguments) throws CodeError {
			BlockValue blockValue = arguments.getNext(BlockValue.class);
			return BooleanValue.of(blockValue.value.contains(FluidBlock.LEVEL));
		}

		private Value isFluidSource(Arguments arguments) throws CodeError {
			BlockState blockState = this.getBlockState(arguments);
			Block block = blockState.getBlock();
			if ((block instanceof FluidBlock && blockState.get(FluidBlock.LEVEL) == 0) || block instanceof BubbleColumnBlock) {
				return BooleanValue.TRUE;
			}
			return BooleanValue.of(block instanceof Waterloggable && blockState.get(Properties.WATERLOGGED));
		}

		private Value isReplaceable(Arguments arguments) throws CodeError {
			BlockState blockState = this.getBlockState(arguments);
			return BooleanValue.of(blockState.getMaterial().isReplaceable());
		}

		private Value getHardness(Arguments arguments) throws CodeError {
			BlockState blockState = this.getBlockState(arguments);
			return NumberValue.of(blockState.getHardness(ArucasMinecraftExtension.getWorld(), BlockPos.ORIGIN));
		}

		private Value sideCoversSmallSquare(Arguments arguments) throws CodeError {
			BlockValue blockValue = this.getBlockWithPos(arguments);
			StringValue stringDirection = arguments.getNextString();
			Direction direction = Objects.requireNonNullElse(Direction.byName(stringDirection.value), Direction.DOWN);
			return BooleanValue.of(Block.sideCoversSmallSquare(ArucasMinecraftExtension.getWorld(), blockValue.blockPos.toBlockPos(), direction));
		}

		private Value isSideSolidFullSquare(Arguments arguments) throws CodeError {
			BlockValue blockValue = this.getBlockWithPos(arguments);
			StringValue stringDirection = arguments.getNextString();
			Direction direction = Objects.requireNonNullElse(Direction.byName(stringDirection.value), Direction.DOWN);
			return BooleanValue.of(blockValue.value.isSideSolidFullSquare(ArucasMinecraftExtension.getWorld(), blockValue.blockPos.toBlockPos(), direction));
		}

		private Value isSpawnable(Arguments arguments) throws CodeError {
			BlockValue blockValue = this.getBlockWithPos(arguments);
			boolean isSpawnable = blockValue.value.allowsSpawning(ArucasMinecraftExtension.getWorld(), blockValue.blockPos.toBlockPos(), EntityType.ZOMBIE);
			return BooleanValue.of(isSpawnable);
		}

		private Value isSpawnableType(Arguments arguments) throws CodeError {
			BlockValue blockValue = this.getBlockWithPos(arguments);
			EntityValue<?> entityValue = arguments.getNext(EntityValue.class);
			boolean isSpawnable = blockValue.value.allowsSpawning(ArucasMinecraftExtension.getWorld(), blockValue.blockPos.toBlockPos(), entityValue.value.getType());
			return BooleanValue.of(isSpawnable);
		}

		private Value getLuminance(Arguments arguments) throws CodeError {
			BlockState blockState = this.getBlockState(arguments);
			return NumberValue.of(blockState.getLuminance());
		}

		private Value getBlockNbt(Arguments arguments) throws CodeError {
			BlockValue blockValue = arguments.getNext(BlockValue.class);
			BlockEntity blockEntity;
			if (blockValue.hasBlockPos() && (blockEntity = ArucasMinecraftExtension.getWorld().getBlockEntity(blockValue.blockPos.toBlockPos())) != null) {
				return new MapValue(NbtUtils.nbtToMap(arguments.getContext(), blockEntity.writeNbt(new NbtCompound()), 10));
			}
			return NullValue.NULL;
		}

		private BlockValue getBlockWithPos(Arguments arguments) throws RuntimeError {
			BlockValue blockValue = arguments.getNext(BlockValue.class);
			if (!blockValue.hasPos) {
				throw arguments.getError("Block does not have position");
			}
			return blockValue;
		}

		private BlockState getBlockState(Arguments arguments) throws CodeError {
			BlockState block = arguments.getNextGeneric(BlockValue.class);
			if (block == null) {
				throw arguments.getError("Block was null");
			}
			return block;
		}

		@Override
		public Class<BlockValue> getValueClass() {
			return BlockValue.class;
		}
	}
}
