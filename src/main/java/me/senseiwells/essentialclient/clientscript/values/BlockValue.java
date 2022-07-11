package me.senseiwells.essentialclient.clientscript.values;

import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.api.docs.FunctionDoc;
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
import me.senseiwells.essentialclient.utils.clientscript.MaterialLike;
import me.senseiwells.essentialclient.utils.clientscript.NbtUtils;
import net.minecraft.block.*;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
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

import static me.senseiwells.arucas.utils.ValueTypes.BOOLEAN;
import static me.senseiwells.arucas.utils.ValueTypes.STRING;
import static me.senseiwells.essentialclient.clientscript.core.MinecraftAPI.*;

public class BlockValue extends GenericValue<BlockState> implements MaterialLike {
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

	@Override
	public Item asItem() {
		Item item = this.value.getBlock().asItem();
		if (item == Items.AIR && !this.value.isAir()) {
			throw new RuntimeException("Material cannot be converted to an item");
		}
		return item;
	}

	@Override
	public Block asBlock() {
		return this.value.getBlock();
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

		@FunctionDoc(
			isStatic = true,
			name = "of",
			desc = "This creates a Block from a material or string",
			params = {MATERIAL_LIKE, "material", "the material, item stack, block, or string to create the Block from"},
			returns = {BLOCK, "the Block created from the material or string"},
			example = "Block.of(Material.STONE);"
		)
		private Value of(Arguments arguments) throws CodeError {
			Value value = arguments.getNext();
			if (value instanceof StringValue stringValue) {
				Identifier id = ArucasMinecraftExtension.getId(arguments, stringValue.value);
				Optional<Block> block = Registry.BLOCK.getOrEmpty(id);
				return new BlockValue(block.orElseThrow(() -> {
					return arguments.getError("'%s' is not a value block", id);
				}).getDefaultState());
			}
			if (!(value instanceof MaterialLike materialLike)) {
				throw arguments.getError("Parameter must be of type String or Material");
			}
			return new BlockValue(materialLike.asBlock().getDefaultState());
		}

		@Override
		public ArucasFunctionMap<MemberFunction> getDefinedMethods() {
			return ArucasFunctionMap.of(
				MemberFunction.of("getMaterial", this::getMaterial),
				MemberFunction.of("getFullId", this::getFullId),
				MemberFunction.of("getId", this::getId),
				MemberFunction.of("with",2, this::getStateModified),
				MemberFunction.of("getDefaultState", this::getDefaultState),
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

		@FunctionDoc(
			name = "getDefaultState",
			desc = "This gets the default state of the Block",
			returns = {BLOCK, "default state of the Block"},
			example = "block.getDefaultState();"
		)
		private Value getDefaultState(Arguments arguments) throws CodeError {
			BlockValue blockValue = arguments.getNext(BlockValue.class);
			return new BlockValue(blockValue.value.getBlock().getDefaultState(), blockValue.blockPos);
		}

		@FunctionDoc(
			name = "with",
			desc = "This gets modified Block with property, value",
			params = {STRING, "property", "property name, such as 'facing', 'extended'",
					STRING, "value", "value name, such as 'north', 'true'"},
			returns = {BLOCK, "new state of the Block"},
			example = "block.with('facing','north');"
		)
		private Value getStateModified(Arguments arguments) throws CodeError {
			BlockValue blockValue = arguments.getNext(BlockValue.class);
			String stringState = arguments.getNextString().value;
			Property<?> property = blockValue.asBlock().getStateManager().getProperty(stringState);
			if (property == null){
				throw arguments.getError("Property "+ stringState + " is not defined in block ");
			}
			String modified = arguments.getNextString().value;
			BlockState state = getStateWith(blockValue.value, property, modified);
			if (state == null) {
				throw arguments.getError("Property "+ stringState + " with value " + modified +" is not defined in block ");
			}
			return new BlockValue(state, blockValue.blockPos);
		}
		private <T extends Comparable<T>> BlockState getStateWith(BlockState blockState, Property<T> property, String value){
			Optional<T> optional = property.parse(value);
			return optional.map(t -> blockState.with(property,t)).orElse(null);
		}
		@FunctionDoc(
			name = "getMaterial",
			desc = "This gets the material of the Block",
			returns = {MATERIAL, "the material of the Block"},
			example = "block.getMaterial();"
		)
		private Value getMaterial(Arguments arguments) throws CodeError {
			BlockState blockState = this.getBlockState(arguments);
			Item blockItem = blockState.getBlock().asItem();
			if (blockItem == Items.AIR && blockState.getBlock() != Blocks.AIR) {
				return MaterialValue.blockMaterial(blockState.getBlock());
			}
			return new MaterialValue(blockItem);
		}

		@FunctionDoc(
			name = "getFullId",
			desc = "This gets the full id of the Block, for example: 'minecraft:stone'",
			returns = {STRING, "the full id of the Block"},
			example = "block.getFullId();"
		)
		private Value getFullId(Arguments arguments) throws CodeError {
			return StringValue.of(Registry.BLOCK.getId(this.getBlockState(arguments).getBlock()).toString());
		}

		@FunctionDoc(
			name = "getId",
			desc = "This gets the id of the Block, for example: 'stone'",
			returns = {STRING, "the id of the Block"},
			example = "block.getId();"
		)
		private Value getId(Arguments arguments) throws CodeError {
			return StringValue.of(Registry.BLOCK.getId(this.getBlockState(arguments).getBlock()).getPath());
		}

		@FunctionDoc(
			name = "isBlockEntity",
			desc = "This checks if the Block is a BlockEntity",
			returns = {BOOLEAN, "true if the Block is a BlockEntity"},
			example = "block.isBlockEntity();"
		)
		private Value isBlockEntity(Arguments arguments) throws CodeError {
			return BooleanValue.of(this.getBlockState(arguments).getBlock() instanceof BlockEntityProvider);
		}

		@FunctionDoc(
			name = "isTransparent",
			desc = "This checks if the Block is transparent",
			returns = {BOOLEAN, "true if the Block is transparent"},
			example = "block.isTransparent();"
		)
		private Value isTransparent(Arguments arguments) throws CodeError {
			return BooleanValue.of(!this.getBlockState(arguments).isOpaque());
		}

		@FunctionDoc(
			name = "asItemStack",
			desc = "This gets the ItemStack of the Block, if the block has no item it will return air",
			returns = {ITEM_STACK, "the ItemStack of the Block"},
			example = "block.asItemStack();"
		)
		private Value asItemStack(Arguments arguments) throws CodeError {
			return new ItemStackValue(this.getBlockState(arguments).getBlock().asItem().getDefaultStack());
		}

		@FunctionDoc(
			name = "getBlastResistance",
			desc = "This gets the blast resistance of the Block",
			returns = {NUMBER, "the blast resistance of the Block"},
			example = "block.getBlastResistance();"
		)
		private Value getBlastResistance(Arguments arguments) throws CodeError {
			return NumberValue.of(this.getBlockState(arguments).getBlock().getBlastResistance());
		}

		@FunctionDoc(
			name = "getBlockProperties",
			desc = {
				"This gets the properties of the Block",
				"You can find a list of all block properties",
				"[here](https://minecraft.fandom.com/wiki/Java_Edition_data_values#Block_states)"
			},
			returns = {MAP, "the properties of the Block, may be empty if there are no properties"},
			example = "block.getBlockProperties();"
		)
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

		@FunctionDoc(
			name = "hasBlockPosition",
			desc = "This checks if the Block has a position or not",
			returns = {BOOLEAN, "true if the Block has a position"},
			example = "block.hasBlockPosition();"
		)
		private Value hasBlockPosition(Arguments arguments) throws CodeError {
			BlockValue blockValue = arguments.getNext(BlockValue.class);
			return BooleanValue.of(blockValue.hasBlockPos());
		}

		@FunctionDoc(
			name = "getPos",
			desc = "This gets the position of the Block",
			returns = {POS, "the position of the Block, may be null if the Block has no position"},
			example = "block.getPos();"
		)
		private Value getPos(Arguments arguments) throws CodeError {
			BlockValue blockValue = arguments.getNext(BlockValue.class);
			return blockValue.getPos();
		}

		@FunctionDoc(
			name = "getX",
			desc = "This gets the X position of the Block",
			returns = {NUMBER, "the X position of the Block, may be null if the Block has no position"},
			example = "block.getX();"
		)
		private Value getBlockX(Arguments arguments) throws CodeError {
			BlockValue blockValue = arguments.getNext(BlockValue.class);
			return blockValue.getBlockX();
		}

		@FunctionDoc(
			name = "getY",
			desc = "This gets the Y position of the Block",
			returns = {NUMBER, "the Y position of the Block, may be null if the Block has no position"},
			example = "block.getY();"
		)
		private Value getBlockY(Arguments arguments) throws CodeError {
			BlockValue blockValue = arguments.getNext(BlockValue.class);
			return blockValue.getBlockY();
		}

		@FunctionDoc(
			name = "getZ",
			desc = "This gets the Z position of the Block",
			returns = {NUMBER, "the Z position of the Block, may be null if the Block has no position"},
			example = "block.getZ();"
		)
		private Value getBlockZ(Arguments arguments) throws CodeError {
			BlockValue blockValue = arguments.getNext(BlockValue.class);
			return blockValue.getBlockZ();
		}

		@FunctionDoc(
			name = "getTranslatedName",
			desc = {
				"This gets the translated name of the Block, for example",
				"'stone' would return 'Stone' if your language is in English"
			},
			returns = {STRING, "the translated name of the Block"},
			example = "block.getTranslatedName();"
		)
		private Value getTranslatedName(Arguments arguments) throws CodeError {
			BlockState blockState = this.getBlockState(arguments);
			return StringValue.of(I18n.translate(blockState.getBlock().getTranslationKey()));
		}

		@FunctionDoc(
			name = "isSolidBlock",
			desc = "This checks if the Block is a solid block",
			returns = {BOOLEAN, "true if the Block is a solid block"},
			example = "block.isSolidBlock();"
		)
		private Value isSolidBlock(Arguments arguments) throws CodeError {
			BlockValue blockValue = this.getBlockWithPos(arguments);
			boolean isSolid = blockValue.value.isSolidBlock(ArucasMinecraftExtension.getWorld(), blockValue.blockPos.toBlockPos());
			return BooleanValue.of(isSolid);
		}

		@FunctionDoc(
			name = "rotateYClockwise",
			desc = "This rotates the Block 90 degrees clockwise",
			returns = {BLOCK, "the rotated Block"},
			example = "block.rotateYClockwise();"
		)
		private Value rotateYClockwise(Arguments arguments) throws CodeError {
			BlockState blockState = this.getBlockState(arguments);
			return new BlockValue(blockState.rotate(BlockRotation.CLOCKWISE_90));
		}

		@FunctionDoc(
			name = "rotateYCounterClockwise",
			desc = "This rotates the Block 90 degrees counter-clockwise",
			returns = {BLOCK, "the rotated Block"},
			example = "block.rotateYCounterClockwise();"
		)
		private Value rotateYCounterClockwise(Arguments arguments) throws CodeError {
			BlockState blockState = this.getBlockState(arguments);
			return new BlockValue(blockState.rotate(BlockRotation.COUNTERCLOCKWISE_90));
		}

		@FunctionDoc(
			name = "mirrorFrontBack",
			desc = "This mirrors the Block around the front and back",
			returns = {BLOCK, "the mirrored Block"},
			example = "block.mirrorFrontBack();"
		)
		private Value mirrorFrontBack(Arguments arguments) throws CodeError {
			BlockState blockState = this.getBlockState(arguments);
			return new BlockValue(blockState.mirror(BlockMirror.FRONT_BACK));
		}

		@FunctionDoc(
			name = "mirrorLeftRight",
			desc = "This mirrors the Block around the left and right",
			returns = {BLOCK, "the mirrored Block"},
			example = "block.mirrorLeftRight();"
		)
		private Value mirrorLeftRight(Arguments arguments) throws CodeError {
			BlockState blockState = this.getBlockState(arguments);
			return new BlockValue(blockState.mirror(BlockMirror.LEFT_RIGHT));
		}

		@FunctionDoc(
			name = "isFluid",
			desc = "This checks if the Block is a fluid",
			returns = {BOOLEAN, "true if the Block is a fluid"},
			example = "block.isFluid();"
		)
		private Value isFluid(Arguments arguments) throws CodeError {
			BlockValue blockValue = arguments.getNext(BlockValue.class);
			return BooleanValue.of(blockValue.value.contains(FluidBlock.LEVEL));
		}

		@FunctionDoc(
			name = "isFluidSource",
			desc = "This checks if the Block is a fluid source",
			returns = {BOOLEAN, "true if the Block is a fluid source"},
			example = "block.isFluidSource();"
		)
		private Value isFluidSource(Arguments arguments) throws CodeError {
			BlockState blockState = this.getBlockState(arguments);
			Block block = blockState.getBlock();
			if ((block instanceof FluidBlock && blockState.get(FluidBlock.LEVEL) == 0) || block instanceof BubbleColumnBlock) {
				return BooleanValue.TRUE;
			}
			return BooleanValue.of(block instanceof Waterloggable && blockState.get(Properties.WATERLOGGED));
		}

		@FunctionDoc(
			name = "isReplaceable",
			desc = "This checks if the Block is replaceable",
			returns = {BOOLEAN, "true if the Block is replaceable"},
			example = "block.isReplaceable();"
		)
		private Value isReplaceable(Arguments arguments) throws CodeError {
			BlockState blockState = this.getBlockState(arguments);
			return BooleanValue.of(blockState.getMaterial().isReplaceable());
		}

		@FunctionDoc(
			name = "getHardness",
			desc = "This gets the hardness of the Block",
			returns = {NUMBER, "the hardness of the Block"},
			example = "block.getHardness();"
		)
		private Value getHardness(Arguments arguments) throws CodeError {
			BlockState blockState = this.getBlockState(arguments);
			return NumberValue.of(blockState.getHardness(ArucasMinecraftExtension.getWorld(), BlockPos.ORIGIN));
		}

		@FunctionDoc(
			name = "sideCoversSmallSquare",
			desc = "This checks if the Block covers a small square",
			params = {STRING, "side", "the side to check, for example: 'north', 'south', 'east', 'west', 'up', 'down'"},
			returns = {BOOLEAN, "true if the Block covers a small square"},
			example = "block.sideCoversSmallSquare('north');"
		)
		private Value sideCoversSmallSquare(Arguments arguments) throws CodeError {
			BlockValue blockValue = this.getBlockWithPos(arguments);
			StringValue stringDirection = arguments.getNextString();
			Direction direction = Objects.requireNonNullElse(Direction.byName(stringDirection.value), Direction.DOWN);
			return BooleanValue.of(Block.sideCoversSmallSquare(ArucasMinecraftExtension.getWorld(), blockValue.blockPos.toBlockPos(), direction));
		}

		@FunctionDoc(
			name = "isSideSolidFullSquare",
			desc = "This checks if the Block is solid on the full square",
			params = {STRING, "side", "the side to check, for example: 'north', 'south', 'east', 'west', 'up', 'down'"},
			returns = {BOOLEAN, "true if the Block is solid on the full square"},
			example = "block.isSideSolidFullSquare('north');"
		)
		private Value isSideSolidFullSquare(Arguments arguments) throws CodeError {
			BlockValue blockValue = this.getBlockWithPos(arguments);
			StringValue stringDirection = arguments.getNextString();
			Direction direction = Objects.requireNonNullElse(Direction.byName(stringDirection.value), Direction.DOWN);
			return BooleanValue.of(blockValue.value.isSideSolidFullSquare(ArucasMinecraftExtension.getWorld(), blockValue.blockPos.toBlockPos(), direction));
		}

		@FunctionDoc(
			name = "isSpawnable",
			desc = "This checks if the Block is spawnable in the case of zombies",
			returns = {BOOLEAN, "true if the Block is spawnable in the case of zombies"},
			example = "block.isSpawnable();"
		)
		private Value isSpawnable(Arguments arguments) throws CodeError {
			BlockValue blockValue = this.getBlockWithPos(arguments);
			boolean isSpawnable = blockValue.value.allowsSpawning(ArucasMinecraftExtension.getWorld(), blockValue.blockPos.toBlockPos(), EntityType.ZOMBIE);
			return BooleanValue.of(isSpawnable);
		}

		@FunctionDoc(
			name = "isSpawnable",
			desc = "This checks if the Block is spawnable in the case of the given entity",
			params = {ENTITY, "entity", "the entity to check"},
			returns = {BOOLEAN, "true if the Block is spawnable in the case of the given entity"},
			example = "block.isSpawnable(zombie);"
		)
		private Value isSpawnableType(Arguments arguments) throws CodeError {
			BlockValue blockValue = this.getBlockWithPos(arguments);
			EntityValue<?> entityValue = arguments.getNext(EntityValue.class);
			boolean isSpawnable = blockValue.value.allowsSpawning(ArucasMinecraftExtension.getWorld(), blockValue.blockPos.toBlockPos(), entityValue.value.getType());
			return BooleanValue.of(isSpawnable);
		}

		@FunctionDoc(
			name = "getLuminance",
			desc = "This gets the luminance of the Block",
			returns = {NUMBER, "the luminance of the Block"},
			example = "block.getLuminance();"
		)
		private Value getLuminance(Arguments arguments) throws CodeError {
			BlockState blockState = this.getBlockState(arguments);
			return NumberValue.of(blockState.getLuminance());
		}

		@FunctionDoc(
			name = "getBlockNbt",
			desc = "This gets the NBT of the Block",
			returns = {MAP, "the NBT of the Block, may be null if the Block has no NBT"},
			example = "block.getBlockNbt();"
		)
		private Value getBlockNbt(Arguments arguments) throws CodeError {
			BlockValue blockValue = arguments.getNext(BlockValue.class);
			BlockEntity blockEntity;
			if (blockValue.hasBlockPos() && (blockEntity = ArucasMinecraftExtension.getWorld().getBlockEntity(blockValue.blockPos.toBlockPos())) != null) {
				return new MapValue(NbtUtils.nbtToMap(arguments.getContext(), blockEntity.createNbt(), 10));
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
