package me.senseiwells.essentialclient.clientscript.definitions;

import me.senseiwells.arucas.api.docs.annotations.ClassDoc;
import me.senseiwells.arucas.api.docs.annotations.FunctionDoc;
import me.senseiwells.arucas.api.docs.annotations.ParameterDoc;
import me.senseiwells.arucas.api.docs.annotations.ReturnDoc;
import me.senseiwells.arucas.builtin.*;
import me.senseiwells.arucas.classes.CreatableDefinition;
import me.senseiwells.arucas.classes.PrimitiveDefinition;
import me.senseiwells.arucas.classes.instance.ClassInstance;
import me.senseiwells.arucas.compiler.LocatableTrace;
import me.senseiwells.arucas.exceptions.RuntimeError;
import me.senseiwells.arucas.functions.builtin.Arguments;
import me.senseiwells.arucas.functions.builtin.BuiltInFunction;
import me.senseiwells.arucas.functions.builtin.MemberFunction;
import me.senseiwells.arucas.interpreter.Interpreter;
import me.senseiwells.arucas.utils.impl.ArucasList;
import me.senseiwells.arucas.utils.impl.ArucasMap;
import me.senseiwells.arucas.utils.misc.Language;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import me.senseiwells.essentialclient.utils.clientscript.ClientScriptUtils;
import me.senseiwells.essentialclient.utils.clientscript.impl.ScriptBlockState;
import me.senseiwells.essentialclient.utils.clientscript.impl.ScriptMaterial;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.EmptyBlockView;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

import static me.senseiwells.essentialclient.clientscript.core.MinecraftAPI.BLOCK;
import static me.senseiwells.essentialclient.utils.clientscript.ClientScriptUtils.warnMainThread;

@ClassDoc(
	name = BLOCK,
	desc = "This class allows interactions with blocks in Minecraft.",
	superclass = MaterialDef.class,
	language = Language.Java
)
public class BlockDef extends CreatableDefinition<ScriptBlockState> {
	public BlockDef(Interpreter interpreter) {
		super(BLOCK, interpreter);
	}

	@NotNull
	@Override
	public PrimitiveDefinition<? super ScriptBlockState> superclass() {
		return this.getPrimitiveDef(MaterialDef.class);
	}

	@Override
	public Object asJavaValue(ClassInstance instance) {
		return instance.asPrimitive(this).asDefault();
	}

	@Override
	public boolean equals(@NotNull ClassInstance instance, @NotNull Interpreter interpreter, @NotNull ClassInstance other, @NotNull LocatableTrace trace) {
		ScriptBlockState state = other.getPrimitive(this);
		return state != null && instance.asPrimitive(this).state.equals(state.state);
	}

	@Override
	public int hashCode(@NotNull ClassInstance instance, @NotNull Interpreter interpreter, @NotNull LocatableTrace trace) {
		return instance.asPrimitive(this).state.hashCode();
	}

	@NotNull
	@Override
	public String toString(@NotNull ClassInstance instance, @NotNull Interpreter interpreter, @NotNull LocatableTrace trace) {
		return "Block{id=" + instance.asPrimitive(this).getId().getPath() + "}";
	}

	@Override
	public List<BuiltInFunction> defineStaticMethods() {
		return List.of(
			BuiltInFunction.of("of", 1, this::of)
		);
	}

	@FunctionDoc(
		isStatic = true,
		name = "of",
		desc = "This creates a Block from a material or string",
		params = {@ParameterDoc(type = MaterialDef.class, name = "material", desc = "the material, item stack, block, or string to create the Block from")},
		returns = @ReturnDoc(type = BlockDef.class, desc = "the Block created from the material or string"),
		examples = "Block.of(Material.STONE);"
	)
	private BlockState of(Arguments arguments) {
		if (arguments.isNext(StringDef.class)) {
			String id = arguments.nextPrimitive(StringDef.class);
			return Registries.BLOCK.getOrEmpty(ClientScriptUtils.stringToIdentifier(id)).orElseThrow(
				() -> new RuntimeError("'%s' is not a value block".formatted(id))
			).getDefaultState();
		}
		if (arguments.isNext(MaterialDef.class)) {
			ScriptMaterial material = arguments.nextPrimitive(MaterialDef.class);
			return material.asBlockState();
		}
		throw new RuntimeError("Parameter must be of type String or Material");
	}

	@Override
	public List<MemberFunction> defineMethods() {
		return List.of(
			MemberFunction.of("getMaterial", this::getMaterial),
			MemberFunction.of("getDefaultState", this::getDefaultState),
			MemberFunction.of("with", 2, this::with),
			MemberFunction.of("isBlockEntity", this::isBlockEntity),
			MemberFunction.of("isTransparent", this::isTransparent),
			MemberFunction.of("getBlastResistance", this::getBlastResistance),
			MemberFunction.of("getBlockProperties", this::getBlockProperties),
			MemberFunction.of("hasBlockPosition", this::hasBlockPosition),
			MemberFunction.of("getPos", this::getPos),
			MemberFunction.of("getX", this::getBlockX),
			MemberFunction.of("getZ", this::getBlockZ),
			MemberFunction.of("getY", this::getBlockY),
			MemberFunction.of("offset", 1, this::offset),
			MemberFunction.of("offset", 2, this::offset2),
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
			MemberFunction.of("isSpawnable", this::allowsSpawning),
			MemberFunction.of("isSpawnable", 1, this::allowsSpawningType),
			MemberFunction.of("getLuminance", this::getLuminance),
			MemberFunction.of("getMapColour", this::getMapColour),
			MemberFunction.of("getMapColor", this::getMapColour),
			MemberFunction.of("getBlockEntityNbt", this::getBlockNbt)
		);
	}

	@FunctionDoc(
		name = "getMaterial",
		desc = "This gets the material of the Block",
		returns = @ReturnDoc(type = MaterialDef.class, desc = "the material of the Block"),
		examples = "block.getMaterial();"
	)
	private Object getMaterial(Arguments arguments) {
		ScriptBlockState blockState = arguments.nextPrimitive(this);
		Item item = Item.BLOCK_ITEMS.get(blockState.asBlock());
		return item == null ? blockState.asBlock() : item;
	}

	@FunctionDoc(
		name = "getDefaultState",
		desc = "This gets the default state of the block, it will conserve any positions",
		returns = @ReturnDoc(type = BlockDef.class, desc = "default state of the Block"),
		examples = "block.getDefaultState();"
	)
	private Object getDefaultState(Arguments arguments) {
		ScriptBlockState blockState = arguments.nextPrimitive(this);
		return new ScriptBlockState(blockState.asBlock().getDefaultState(), blockState.pos);
	}

	@FunctionDoc(
		name = "with",
		desc = "This gets modified block with a property value, conserving positions",
		params = {
			@ParameterDoc(type = StringDef.class, name = "property", desc = "property name, such as 'facing', 'extended'"),
			@ParameterDoc(type = StringDef.class, name = "value", desc = "value name, such as 'north', 'true'")
		},
		returns = @ReturnDoc(type = BlockDef.class, desc = "new state of the Block"),
		examples = "block.with('facing', 'north');"
	)
	private Object with(Arguments arguments) {
		ScriptBlockState blockState = arguments.nextPrimitive(this);
		String propertyAsString = arguments.nextPrimitive(StringDef.class);

		Property<?> property = blockState.asBlock().getStateManager().getProperty(propertyAsString);
		if (property == null) {
			throw new RuntimeError("Property %s is not defined in block".formatted(propertyAsString));
		}

		String value = arguments.nextPrimitive(StringDef.class);
		BlockState state = this.getStateWith(blockState.state, property, value);
		if (state == null) {
			throw new RuntimeError("Property %s with value %s is not defined in block".formatted(propertyAsString, value));
		}

		return new ScriptBlockState(state, blockState.pos);
	}

	@FunctionDoc(
		name = "isBlockEntity",
		desc = "This checks if the Block is a BlockEntity",
		returns = @ReturnDoc(type = BooleanDef.class, desc = "true if the Block is a BlockEntity"),
		examples = "block.isBlockEntity();"
	)
	private Object isBlockEntity(Arguments arguments) {
		return arguments.nextPrimitive(this).asBlock() instanceof BlockEntityProvider;
	}

	@FunctionDoc(
		name = "isTransparent",
		desc = "This checks if the Block is transparent",
		returns = @ReturnDoc(type = BooleanDef.class, desc = "true if the Block is transparent"),
		examples = "block.isTransparent();"
	)
	private Object isTransparent(Arguments arguments) {
		return !arguments.nextPrimitive(this).state.isOpaque();
	}

	@FunctionDoc(
		name = "getBlastResistance",
		desc = "This gets the blast resistance of the Block",
		returns = @ReturnDoc(type = NumberDef.class, desc = "the blast resistance of the Block"),
		examples = "block.getBlastResistance();"
	)
	private Object getBlastResistance(Arguments arguments) {
		return arguments.nextPrimitive(this).asBlock().getBlastResistance();
	}

	@FunctionDoc(
		name = "getBlockProperties",
		desc = {
			"This gets the properties of the Block",
			"You can find a list of all block properties",
			"[here](https://minecraft.fandom.com/wiki/Java_Edition_data_values#Block_states)"
		},
		returns = @ReturnDoc(type = MapDef.class, desc = "the properties of the Block, may be empty if there are no properties"),
		examples = "block.getBlockProperties();"
	)
	private Object getBlockProperties(Arguments arguments) {
		ScriptBlockState blockState = arguments.nextPrimitive(this);
		ArucasMap propertyMap = new ArucasMap();
		Interpreter interpreter = arguments.getInterpreter();
		blockState.state.getEntries().forEach((p, c) -> {
			Object value = p instanceof EnumProperty<?> ? ((StringIdentifiable) c).asString() : c;
			propertyMap.put(interpreter, interpreter.create(StringDef.class, p.getName()), interpreter.convertValue(value));
		});
		return propertyMap;
	}

	@FunctionDoc(
		name = "hasBlockPosition",
		desc = "This checks if the Block has a position or not",
		returns = @ReturnDoc(type = BooleanDef.class, desc = "true if the Block has a position"),
		examples = "block.hasBlockPosition();"
	)
	private Object hasBlockPosition(Arguments arguments) {
		return arguments.nextPrimitive(this).pos != null;
	}

	@FunctionDoc(
		name = "getPos",
		desc = "This gets the position of the Block",
		returns = @ReturnDoc(type = PosDef.class, desc = "the position of the Block, may be null if the Block has no position"),
		examples = "block.getPos();"
	)
	private Object getPos(Arguments arguments) {
		return arguments.nextPrimitive(this).pos;
	}

	@FunctionDoc(
		name = "getX",
		desc = "This gets the X position of the Block",
		returns = @ReturnDoc(type = NumberDef.class, desc = "the X position of the Block, may be null if the Block has no position"),
		examples = "block.getX();"
	)
	private Object getBlockX(Arguments arguments) {
		BlockPos pos = arguments.nextPrimitive(this).pos;
		return pos == null ? null : pos.getX();
	}

	@FunctionDoc(
		name = "getY",
		desc = "This gets the Y position of the Block",
		returns = @ReturnDoc(type = NumberDef.class, desc = "the Y position of the Block, may be null if the Block has no position"),
		examples = "block.getY();"
	)
	private Object getBlockY(Arguments arguments) {
		BlockPos pos = arguments.nextPrimitive(this).pos;
		return pos == null ? null : pos.getY();
	}

	@FunctionDoc(
		name = "offset",
		desc = "This gets a block with a given offset, this will throw if the block has no position",
		params = {@ParameterDoc(type = PosDef.class, name = "offset", desc = "the position offset to add to the block's current position")},
		returns = @ReturnDoc(type = BlockDef.class, desc = "the block at the offset position"),
		examples = "block.offset(new Pos(0, 1, 0));"
	)
	private Object offset(Arguments arguments) {
		warnMainThread("offset", arguments.getInterpreter());
		ScriptBlockState state = this.ensurePosition(arguments);
		BlockPos offset = arguments.nextPrimitive(PosDef.class).getBlockPos();
		BlockPos newPos = state.pos.add(offset);
		return new ScriptBlockState(EssentialUtils.getWorld().getBlockState(newPos), newPos);
	}

	@FunctionDoc(
		name = "offset",
		desc = "This gets a block with a given offset, this will throw if the block has no position",
		params = {
			@ParameterDoc(type = StringDef.class, name = "direction", desc = "the direction of the offset"),
			@ParameterDoc(type = NumberDef.class, name = "distance", desc = "the distance of the offset")
		},
		returns = @ReturnDoc(type = BlockDef.class, desc = "the block at the offset position"),
		examples = "block.offset('north', 5);"
	)
	private Object offset2(Arguments arguments) {
		warnMainThread("offset", arguments.getInterpreter());
		ScriptBlockState state = this.ensurePosition(arguments);
		Direction direction = ClientScriptUtils.stringToDirection(arguments.nextConstant(), null);
		int amount = arguments.nextPrimitive(NumberDef.class).intValue();
		BlockPos newPos = state.pos.offset(direction, amount);
		return new ScriptBlockState(EssentialUtils.getWorld().getBlockState(newPos), newPos);
	}

	@FunctionDoc(
		name = "getZ",
		desc = "This gets the Z position of the Block",
		returns = @ReturnDoc(type = NumberDef.class, desc = "the Z position of the Block, may be null if the Block has no position"),
		examples = "block.getZ();"
	)
	private Object getBlockZ(Arguments arguments) {
		BlockPos pos = arguments.nextPrimitive(this).pos;
		return pos == null ? null : pos.getZ();
	}

	@FunctionDoc(
		name = "isSolidBlock",
		desc = "This checks if the Block is a solid block",
		returns = @ReturnDoc(type = BooleanDef.class, desc = "true if the Block is a solid block"),
		examples = "block.isSolidBlock();"
	)
	private Object isSolidBlock(Arguments arguments) {
		ScriptBlockState blockState = this.ensurePosition(arguments);
		return blockState.state.isSolidBlock(EmptyBlockView.INSTANCE, BlockPos.ORIGIN);
	}

	@FunctionDoc(
		name = "rotateYClockwise",
		desc = "This rotates the Block 90 degrees clockwise",
		returns = @ReturnDoc(type = BlockDef.class, desc = "the rotated Block"),
		examples = "block.rotateYClockwise();"
	)
	private Object rotateYClockwise(Arguments arguments) {
		ScriptBlockState blockState = arguments.nextPrimitive(this);
		return blockState.state.rotate(BlockRotation.CLOCKWISE_90);
	}

	@FunctionDoc(
		name = "rotateYCounterClockwise",
		desc = "This rotates the Block 90 degrees counter-clockwise",
		returns = @ReturnDoc(type = BlockDef.class, desc = "the rotated Block"),
		examples = "block.rotateYCounterClockwise();"
	)
	private Object rotateYCounterClockwise(Arguments arguments) {
		ScriptBlockState blockState = arguments.nextPrimitive(this);
		return blockState.state.rotate(BlockRotation.COUNTERCLOCKWISE_90);
	}

	@FunctionDoc(
		name = "mirrorFrontBack",
		desc = "This mirrors the Block around the front and back",
		returns = @ReturnDoc(type = BlockDef.class, desc = "the mirrored Block"),
		examples = "block.mirrorFrontBack();"
	)
	private Object mirrorFrontBack(Arguments arguments) {
		ScriptBlockState blockState = arguments.nextPrimitive(this);
		return blockState.state.mirror(BlockMirror.FRONT_BACK);
	}

	@FunctionDoc(
		name = "mirrorLeftRight",
		desc = "This mirrors the Block around the left and right",
		returns = @ReturnDoc(type = BlockDef.class, desc = "the mirrored Block"),
		examples = "block.mirrorLeftRight();"
	)
	private Object mirrorLeftRight(Arguments arguments) {
		ScriptBlockState blockState = arguments.nextPrimitive(this);
		return blockState.state.mirror(BlockMirror.LEFT_RIGHT);
	}

	@FunctionDoc(
		name = "isFluid",
		desc = "This checks if the Block is a fluid",
		returns = @ReturnDoc(type = BooleanDef.class, desc = "true if the Block is a fluid"),
		examples = "block.isFluid();"
	)
	private Object isFluid(Arguments arguments) {
		ScriptBlockState blockState = arguments.nextPrimitive(this);
		return !blockState.state.getFluidState().isEmpty();
	}

	@FunctionDoc(
		name = "isFluidSource",
		desc = "This checks if the Block is a fluid source",
		returns = @ReturnDoc(type = BooleanDef.class, desc = "true if the Block is a fluid source"),
		examples = "block.isFluidSource();"
	)
	private Object isFluidSource(Arguments arguments) {
		ScriptBlockState blockState = arguments.nextPrimitive(this);
		return blockState.state.getFluidState().isStill();
	}

	@FunctionDoc(
		name = "isReplaceable",
		desc = "This checks if the Block is replaceable",
		returns = @ReturnDoc(type = BooleanDef.class, desc = "true if the Block is replaceable"),
		examples = "block.isReplaceable();"
	)
	private Object isReplaceable(Arguments arguments) {
		ScriptBlockState blockState = arguments.nextPrimitive(this);
		return blockState.state.isReplaceable();
	}

	@FunctionDoc(
		name = "getHardness",
		desc = "This gets the hardness of the Block",
		returns = @ReturnDoc(type = NumberDef.class, desc = "the hardness of the Block"),
		examples = "block.getHardness();"
	)
	private Object getHardness(Arguments arguments) {
		ScriptBlockState blockState = arguments.nextPrimitive(this);
		return blockState.state.getHardness(EmptyBlockView.INSTANCE, BlockPos.ORIGIN);
	}

	@FunctionDoc(
		name = "sideCoversSmallSquare",
		desc = "This checks if the Block covers a small square",
		params = {@ParameterDoc(type = StringDef.class, name = "side", desc = "the side to check, for example: 'north', 'south', 'east', 'west', 'up', 'down'")},
		returns = @ReturnDoc(type = BooleanDef.class, desc = "true if the Block covers a small square"),
		examples = "block.sideCoversSmallSquare('north');"
	)
	private Object sideCoversSmallSquare(Arguments arguments) {
		ScriptBlockState state = arguments.nextPrimitive(this);
		String stringDirection = arguments.nextConstant();
		Direction direction = ClientScriptUtils.stringToDirection(stringDirection, Direction.DOWN);
		boolean isSmallSquare = direction != Direction.DOWN || !state.state.isIn(BlockTags.UNSTABLE_BOTTOM_CENTER);
		return isSmallSquare && state.state.isSideSolid(EmptyBlockView.INSTANCE, BlockPos.ORIGIN, direction, SideShapeType.CENTER);
	}

	@FunctionDoc(
		name = "isSideSolidFullSquare",
		desc = "This checks if the Block is solid on the full square",
		params = {@ParameterDoc(type = StringDef.class, name = "side", desc = "the side to check, for example: 'north', 'south', 'east', 'west', 'up', 'down'")},
		returns = @ReturnDoc(type = BooleanDef.class, desc = "true if the Block is solid on the full square"),
		examples = "block.isSideSolidFullSquare('north');"
	)
	private Object isSideSolidFullSquare(Arguments arguments) {
		ScriptBlockState state = arguments.nextPrimitive(this);
		String stringDirection = arguments.nextConstant();
		Direction direction = ClientScriptUtils.stringToDirection(stringDirection, Direction.DOWN);
		return state.state.isSideSolidFullSquare(EmptyBlockView.INSTANCE, BlockPos.ORIGIN, direction);
	}

	@FunctionDoc(
		name = "isSpawnable",
		desc = "This checks if the Block is spawnable in the case of zombies",
		returns = @ReturnDoc(type = BooleanDef.class, desc = "true if the Block is spawnable in the case of zombies"),
		examples = "block.isSpawnable();"
	)
	private Object allowsSpawning(Arguments arguments) {
		ScriptBlockState state = arguments.nextPrimitive(this);
		return state.state.allowsSpawning(EmptyBlockView.INSTANCE, BlockPos.ORIGIN, EntityType.ZOMBIE);
	}

	@FunctionDoc(
		name = "isSpawnable",
		desc = "This checks if the Block allows spawning for given entity",
		params = {@ParameterDoc(type = EntityDef.class, name = "entity", desc = "the entity to check")},
		returns = @ReturnDoc(type = BooleanDef.class, desc = "true if the Block allows spawning for given entity"),
		examples = "block.isSpawnable(zombie);"
	)
	private Object allowsSpawningType(Arguments arguments) {
		ScriptBlockState state = arguments.nextPrimitive(this);
		Entity entity = arguments.nextPrimitive(EntityDef.class);
		return state.state.allowsSpawning(EmptyBlockView.INSTANCE, BlockPos.ORIGIN, entity.getType());
	}

	@FunctionDoc(
		name = "getLuminance",
		desc = "This gets the luminance of the Block",
		returns = @ReturnDoc(type = NumberDef.class, desc = "the luminance of the Block"),
		examples = "block.getLuminance();"
	)
	private Object getLuminance(Arguments arguments) {
		ScriptBlockState blockState = arguments.nextPrimitive(this);
		return blockState.state.getLuminance();
	}

	@FunctionDoc(
		name = "getMapColour",
		desc = "This gets the map colour of the Block, can also be called with 'getMapColor'",
		returns = @ReturnDoc(type = ListDef.class, desc = "a list with the map colour of the Block as RGB values"),
		examples = "block.getMapColour();"
	)
	private Object getMapColour(Arguments arguments) {
		ScriptBlockState blockState = arguments.nextPrimitive(this);
		int colour = blockState.state.getMapColor(EmptyBlockView.INSTANCE, BlockPos.ORIGIN).color;
		Interpreter interpreter = arguments.getInterpreter();
		return ArucasList.of(
			interpreter.create(NumberDef.class, (double) ((colour & 0xFF0000) >> 16)),
			interpreter.create(NumberDef.class, (double) ((colour & 0xFF00) >> 8)),
			interpreter.create(NumberDef.class, (double) (colour & 0xFF))
		);
	}

	@FunctionDoc(
		name = "getBlockEntityNbt",
		desc = "This gets the NBT of a block entity",
		returns = @ReturnDoc(type = MapDef.class, desc = "the NBT of a block entity, may be null if the block entity has no NBT"),
		examples = "block.getBlockEntityNbt();"
	)
	private Object getBlockNbt(Arguments arguments) {
		ScriptBlockState blockState = this.ensurePosition(arguments);
		BlockEntity blockEntity = EssentialUtils.getWorld().getBlockEntity(blockState.pos);
		if (blockEntity != null) {
			NbtCompound compound = blockEntity.createNbt();
			return ClientScriptUtils.nbtToMap(arguments.getInterpreter(), compound, 10);
		}
		return null;
	}

	private ScriptBlockState ensurePosition(Arguments arguments) {
		ScriptBlockState blockState = arguments.nextPrimitive(this);
		if (blockState.pos == null) {
			throw new RuntimeError("Block does not have position");
		}
		return blockState;
	}

	private <T extends Comparable<T>> BlockState getStateWith(BlockState blockState, Property<T> property, String value) {
		Optional<T> optional = property.parse(value);
		return optional.map(t -> blockState.with(property, t)).orElse(null);
	}
}
