package me.senseiwells.essentialclient.clientscript.definitions;

import me.senseiwells.arucas.api.docs.ClassDoc;
import me.senseiwells.arucas.api.docs.FunctionDoc;
import me.senseiwells.arucas.builtin.NumberDef;
import me.senseiwells.arucas.builtin.StringDef;
import me.senseiwells.arucas.classes.ClassInstance;
import me.senseiwells.arucas.classes.CreatableDefinition;
import me.senseiwells.arucas.classes.PrimitiveDefinition;
import me.senseiwells.arucas.core.Interpreter;
import me.senseiwells.arucas.exceptions.RuntimeError;
import me.senseiwells.arucas.utils.*;
import me.senseiwells.arucas.utils.impl.ArucasList;
import me.senseiwells.arucas.utils.impl.ArucasMap;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import me.senseiwells.essentialclient.utils.clientscript.ClientScriptUtils;
import me.senseiwells.essentialclient.utils.clientscript.impl.ScriptBlockState;
import me.senseiwells.essentialclient.utils.clientscript.impl.ScriptMaterial;
import me.senseiwells.essentialclient.utils.clientscript.impl.ScriptPos;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.EmptyBlockView;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static me.senseiwells.arucas.utils.Util.Types.*;
import static me.senseiwells.essentialclient.clientscript.core.MinecraftAPI.*;

@ClassDoc(
	name = BLOCK,
	desc = "This class allows interactions with blocks in Minecraft.",
	importPath = "Minecraft",
	superclass = MaterialDef.class,
	language = Util.Language.Java
)
public class BlockDef extends CreatableDefinition<ScriptBlockState> {
	public BlockDef(Interpreter interpreter) {
		super(BLOCK, interpreter);
	}

	@Override
	public PrimitiveDefinition<? super ScriptBlockState> superclass() {
		return this.getPrimitiveDef(MaterialDef.class);
	}

	@Override
	public Object asJavaValue(ClassInstance instance) {
		return instance.asPrimitive(this).asDefault();
	}

	@Override
	public boolean equals$Arucas(ClassInstance instance, Interpreter interpreter, ClassInstance other, LocatableTrace trace) {
		ScriptBlockState state = other.getPrimitive(this);
		return state != null && instance.asPrimitive(this).state.equals(state.state);
	}

	@Override
	public int hashCode$Arucas(ClassInstance instance, Interpreter interpreter, LocatableTrace trace) {
		return instance.asPrimitive(this).state.hashCode();
	}

	@Override
	public String toString$Arucas(ClassInstance instance, Interpreter interpreter, LocatableTrace trace) {
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
		params = {MATERIAL, "material", "the material, item stack, block, or string to create the Block from"},
		returns = {BLOCK, "the Block created from the material or string"},
		examples = "Block.of(Material.STONE);"
	)
	private BlockState of(Arguments arguments) {
		if (arguments.isNext(StringDef.class)) {
			String id = arguments.nextPrimitive(StringDef.class);
			return Registry.BLOCK.getOrEmpty(ClientScriptUtils.stringToIdentifier(id)).orElseThrow(
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
		returns = {MATERIAL, "the material of the Block"},
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
		returns = {BLOCK, "default state of the Block"},
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
			STRING, "property", "property name, such as 'facing', 'extended'",
			STRING, "value", "value name, such as 'north', 'true'"
		},
		returns = {BLOCK, "new state of the Block"},
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
		returns = {BOOLEAN, "true if the Block is a BlockEntity"},
		examples = "block.isBlockEntity();"
	)
	private Object isBlockEntity(Arguments arguments) {
		return arguments.nextPrimitive(this).asBlock() instanceof BlockEntityProvider;
	}

	@FunctionDoc(
		name = "isTransparent",
		desc = "This checks if the Block is transparent",
		returns = {BOOLEAN, "true if the Block is transparent"},
		examples = "block.isTransparent();"
	)
	private Object isTransparent(Arguments arguments) {
		return !arguments.nextPrimitive(this).state.isOpaque();
	}

	@FunctionDoc(
		name = "getBlastResistance",
		desc = "This gets the blast resistance of the Block",
		returns = {NUMBER, "the blast resistance of the Block"},
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
		returns = {MAP, "the properties of the Block, may be empty if there are no properties"},
		examples = "block.getBlockProperties();"
	)
	private Object getBlockProperties(Arguments arguments) {
		ScriptBlockState blockState = arguments.nextPrimitive(this);
		ArucasMap propertyMap = new ArucasMap();
		Interpreter interpreter = arguments.getInterpreter();
		for (Map.Entry<Property<?>, Comparable<?>> entry : blockState.state.getEntries().entrySet()) {
			propertyMap.put(interpreter, interpreter.create(StringDef.class, entry.getKey().getName()), interpreter.convertValue(entry.getValue()));
		}
		return propertyMap;
	}

	@FunctionDoc(
		name = "hasBlockPosition",
		desc = "This checks if the Block has a position or not",
		returns = {BOOLEAN, "true if the Block has a position"},
		examples = "block.hasBlockPosition();"
	)
	private Object hasBlockPosition(Arguments arguments) {
		return arguments.nextPrimitive(this).pos != null;
	}

	@FunctionDoc(
		name = "getPos",
		desc = "This gets the position of the Block",
		returns = {POS, "the position of the Block, may be null if the Block has no position"},
		examples = "block.getPos();"
	)
	private Object getPos(Arguments arguments) {
		return arguments.nextPrimitive(this).pos;
	}

	@FunctionDoc(
		name = "getX",
		desc = "This gets the X position of the Block",
		returns = {NUMBER, "the X position of the Block, may be null if the Block has no position"},
		examples = "block.getX();"
	)
	private Object getBlockX(Arguments arguments) {
		BlockPos pos = arguments.nextPrimitive(this).pos;
		return pos == null ? null : pos.getX();
	}

	@FunctionDoc(
		name = "getY",
		desc = "This gets the Y position of the Block",
		returns = {NUMBER, "the Y position of the Block, may be null if the Block has no position"},
		examples = "block.getY();"
	)
	private Object getBlockY(Arguments arguments) {
		BlockPos pos = arguments.nextPrimitive(this).pos;
		return pos == null ? null : pos.getY();
	}

	@FunctionDoc(
		name = "getZ",
		desc = "This gets the Z position of the Block",
		returns = {NUMBER, "the Z position of the Block, may be null if the Block has no position"},
		examples = "block.getZ();"
	)
	private Object getBlockZ(Arguments arguments) {
		BlockPos pos = arguments.nextPrimitive(this).pos;
		return pos == null ? null : pos.getZ();
	}

	@FunctionDoc(
		name = "isSolidBlock",
		desc = "This checks if the Block is a solid block",
		returns = {BOOLEAN, "true if the Block is a solid block"},
		examples = "block.isSolidBlock();"
	)
	private Object isSolidBlock(Arguments arguments) {
		ScriptBlockState blockState = this.ensurePosition(arguments);
		return blockState.state.isSolidBlock(EmptyBlockView.INSTANCE, BlockPos.ORIGIN);
	}

	@FunctionDoc(
		name = "rotateYClockwise",
		desc = "This rotates the Block 90 degrees clockwise",
		returns = {BLOCK, "the rotated Block"},
		examples = "block.rotateYClockwise();"
	)
	private Object rotateYClockwise(Arguments arguments) {
		ScriptBlockState blockState = arguments.nextPrimitive(this);
		return blockState.state.rotate(BlockRotation.CLOCKWISE_90);
	}

	@FunctionDoc(
		name = "rotateYCounterClockwise",
		desc = "This rotates the Block 90 degrees counter-clockwise",
		returns = {BLOCK, "the rotated Block"},
		examples = "block.rotateYCounterClockwise();"
	)
	private Object rotateYCounterClockwise(Arguments arguments) {
		ScriptBlockState blockState = arguments.nextPrimitive(this);
		return blockState.state.rotate(BlockRotation.COUNTERCLOCKWISE_90);
	}

	@FunctionDoc(
		name = "mirrorFrontBack",
		desc = "This mirrors the Block around the front and back",
		returns = {BLOCK, "the mirrored Block"},
		examples = "block.mirrorFrontBack();"
	)
	private Object mirrorFrontBack(Arguments arguments) {
		ScriptBlockState blockState = arguments.nextPrimitive(this);
		return blockState.state.mirror(BlockMirror.FRONT_BACK);
	}

	@FunctionDoc(
		name = "mirrorLeftRight",
		desc = "This mirrors the Block around the left and right",
		returns = {BLOCK, "the mirrored Block"},
		examples = "block.mirrorLeftRight();"
	)
	private Object mirrorLeftRight(Arguments arguments) {
		ScriptBlockState blockState = arguments.nextPrimitive(this);
		return blockState.state.mirror(BlockMirror.LEFT_RIGHT);
	}

	@FunctionDoc(
		name = "isFluid",
		desc = "This checks if the Block is a fluid",
		returns = {BOOLEAN, "true if the Block is a fluid"},
		examples = "block.isFluid();"
	)
	private Object isFluid(Arguments arguments) {
		ScriptBlockState blockState = arguments.nextPrimitive(this);
		return blockState.state.contains(FluidBlock.LEVEL);
	}

	@FunctionDoc(
		name = "isFluidSource",
		desc = "This checks if the Block is a fluid source",
		returns = {BOOLEAN, "true if the Block is a fluid source"},
		examples = "block.isFluidSource();"
	)
	private Object isFluidSource(Arguments arguments) {
		ScriptBlockState blockState = arguments.nextPrimitive(this);
		Block block = blockState.asBlock();
		if ((block instanceof FluidBlock && blockState.state.get(FluidBlock.LEVEL) == 0) || block instanceof BubbleColumnBlock) {
			return true;
		}
		return block instanceof Waterloggable && blockState.state.get(Properties.WATERLOGGED);
	}

	@FunctionDoc(
		name = "isReplaceable",
		desc = "This checks if the Block is replaceable",
		returns = {BOOLEAN, "true if the Block is replaceable"},
		examples = "block.isReplaceable();"
	)
	private Object isReplaceable(Arguments arguments) {
		ScriptBlockState blockState = arguments.nextPrimitive(this);
		return blockState.state.getMaterial().isReplaceable();
	}

	@FunctionDoc(
		name = "getHardness",
		desc = "This gets the hardness of the Block",
		returns = {NUMBER, "the hardness of the Block"},
		examples = "block.getHardness();"
	)
	private Object getHardness(Arguments arguments) {
		ScriptBlockState blockState = arguments.nextPrimitive(this);
		return blockState.state.getHardness(EmptyBlockView.INSTANCE, BlockPos.ORIGIN);
	}

	@FunctionDoc(
		name = "sideCoversSmallSquare",
		desc = "This checks if the Block covers a small square",
		params = {STRING, "side", "the side to check, for example: 'north', 'south', 'east', 'west', 'up', 'down'"},
		returns = {BOOLEAN, "true if the Block covers a small square"},
		examples = "block.sideCoversSmallSquare('north');"
	)
	private Object sideCoversSmallSquare(Arguments arguments) {
		ScriptBlockState state = arguments.nextPrimitive(this);
		String stringDirection = arguments.nextPrimitive(StringDef.class);
		Direction direction = ClientScriptUtils.stringToDirection(stringDirection, Direction.DOWN);
		boolean isSmallSquare = direction != Direction.DOWN || !state.state.isIn(BlockTags.UNSTABLE_BOTTOM_CENTER);
		return isSmallSquare && state.state.isSideSolid(EmptyBlockView.INSTANCE, BlockPos.ORIGIN, direction, SideShapeType.CENTER);
	}

	@FunctionDoc(
		name = "isSideSolidFullSquare",
		desc = "This checks if the Block is solid on the full square",
		params = {STRING, "side", "the side to check, for example: 'north', 'south', 'east', 'west', 'up', 'down'"},
		returns = {BOOLEAN, "true if the Block is solid on the full square"},
		examples = "block.isSideSolidFullSquare('north');"
	)
	private Object isSideSolidFullSquare(Arguments arguments) {
		ScriptBlockState state = arguments.nextPrimitive(this);
		String stringDirection = arguments.nextPrimitive(StringDef.class);
		Direction direction = ClientScriptUtils.stringToDirection(stringDirection, Direction.DOWN);
		return state.state.isSideSolidFullSquare(EmptyBlockView.INSTANCE, BlockPos.ORIGIN, direction);
	}

	@FunctionDoc(
		name = "isSpawnable",
		desc = "This checks if the Block is spawnable in the case of zombies",
		returns = {BOOLEAN, "true if the Block is spawnable in the case of zombies"},
		examples = "block.isSpawnable();"
	)
	private Object allowsSpawning(Arguments arguments) {
		ScriptBlockState state = arguments.nextPrimitive(this);
		return state.state.allowsSpawning(EmptyBlockView.INSTANCE, BlockPos.ORIGIN, EntityType.ZOMBIE);
	}

	@FunctionDoc(
		name = "isSpawnable",
		desc = "This checks if the Block allows spawning for given entity",
		params = {ENTITY, "entity", "the entity to check"},
		returns = {BOOLEAN, "true if the Block allows spawning for given entity"},
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
		returns = {NUMBER, "the luminance of the Block"},
		examples = "block.getLuminance();"
	)
	private Object getLuminance(Arguments arguments) {
		ScriptBlockState blockState = arguments.nextPrimitive(this);
		return blockState.state.getLuminance();
	}

	@FunctionDoc(
		name = "getMapColour",
		desc = "This gets the map colour of the Block, can also be called with 'getMapColor'",
		returns = {LIST, "a list with the map colour of the Block as RGB values"},
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
		name = "getBlockNbt",
		desc = "This gets the NBT of the Block",
		returns = {MAP, "the NBT of the Block, may be null if the Block has no NBT"},
		examples = "block.getBlockNbt();"
	)
	private Object getBlockNbt(Arguments arguments) {
		ScriptBlockState blockState = this.ensurePosition(arguments);
		BlockEntity blockEntity = EssentialUtils.getWorld().getBlockEntity(blockState.pos);
		if (blockEntity != null) {
			//#if MC >= 11800
			NbtCompound compound = blockEntity.createNbt();
			//#else
			//$$NbtCompound compound = blockEntity.writeNbt(new NbtCompound());
			//#endif
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
