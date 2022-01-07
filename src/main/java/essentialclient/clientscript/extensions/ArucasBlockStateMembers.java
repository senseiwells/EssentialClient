package essentialclient.clientscript.extensions;

import essentialclient.clientscript.values.BlockStateValue;
import essentialclient.clientscript.values.ItemStackValue;
import me.senseiwells.arucas.api.IArucasValueExtension;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.ArucasValueList;
import me.senseiwells.arucas.utils.ArucasValueMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.*;
import me.senseiwells.arucas.values.functions.MemberFunction;
import net.minecraft.block.*;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.state.property.Property;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ArucasBlockStateMembers implements IArucasValueExtension {

	@Override
	public Set<MemberFunction> getDefinedFunctions() {
		return this.blockStateFunctions;
	}

	@Override
	public Class<BlockStateValue> getValueType() {
		return BlockStateValue.class;
	}

	@Override
	public String getName() {
		return "BlockStateMemberFunctions";
	}

	private final Set<MemberFunction> blockStateFunctions = Set.of(
		new MemberFunction("getId", (context, function) -> new StringValue(Registry.BLOCK.getId(this.getBlockState(context, function).getBlock()).getPath())),
		new MemberFunction("getBlockId", List.of(), (context, function) -> new StringValue(Registry.BLOCK.getId(this.getBlockState(context, function).getBlock()).getPath()), true),
		new MemberFunction("isBlockEntity", (context, function) -> BooleanValue.of(this.getBlockState(context, function).getBlock() instanceof BlockEntityProvider)),
		new MemberFunction("isTransparent", (context, function) -> BooleanValue.of(!this.getBlockState(context, function).isOpaque())),
		new MemberFunction("isSolidBlock", this::isSolidBlock),
		new MemberFunction("asItemStack", (context, function) -> new ItemStackValue(this.getBlockState(context, function).getBlock().asItem().getDefaultStack())),
		new MemberFunction("getBlastResistance", (context, function) -> new NumberValue(this.getBlockState(context, function).getBlock().getBlastResistance())),
		new MemberFunction("getBlockProperties", this::getBlockProperties),
		new MemberFunction("rotateYClockwise", this::rotateYClockwise),
		new MemberFunction("rotateYCounterClockwise", this::rotateYCounterClockwise),
		new MemberFunction("mirrorFrontBack", this::mirrorFrontBack),
		new MemberFunction("mirrorLeftRight", this::mirrorLeftRight),
		new MemberFunction("hasBlockPosition", this::hasBlockPosition),
		new MemberFunction("isFluid", this::isFluid),
		new MemberFunction("isFluidSource", this::isFluidSource),
		new MemberFunction("getBlockX", this::getBlockX),
		new MemberFunction("getBlockZ", this::getBlockZ),
		new MemberFunction("getBlockY", this::getBlockY),
		new MemberFunction("getBlockPos", this::getBlockPos),
		new MemberFunction("getAddPos","otherBlock", this::getAddPos),
		new MemberFunction("getOffsetDirectionPos",List.of("direction","int"), this::getOffsetDirectionPos),
		new MemberFunction("getRotateClockwisePos", this::getRotateClockwisePos),
		new MemberFunction("getRotateCounterClockwisePos", this::getRotateCounterClockwisePos),
		new MemberFunction("getUnitVectorFromDirection","direction", this::getUnitVectorFromDirection),
		new MemberFunction("refresh", this::refresh),
		new MemberFunction("getX", this::getBlockX),
		new MemberFunction("getZ", this::getBlockZ),
		new MemberFunction("getY", this::getBlockY),
		new MemberFunction("isReplaceable", this::isReplaceable),
		new MemberFunction("getHardness", this::getHardness),
		new MemberFunction("sideCoversSmallSquare","direction", this::sideCoversSmallSquare),
		new MemberFunction("isSideSolidFullSquare","direction", this::isSideSolidFullSquare)
	);
	private Value<?> sideCoversSmallSquare(Context context, MemberFunction function) throws  CodeError {
		BlockStateValue blockStateValue = function.getParameterValueOfType(context, BlockStateValue.class, 0);
		Direction direction = Direction.byName(function.getParameterValueOfType(context, StringValue.class, 1).value);
		if (direction == null){direction = Direction.DOWN;}
		return BooleanValue.of(Block.sideCoversSmallSquare(ArucasMinecraftExtension.getWorld(), blockStateValue.blockPos, direction));
	}
	private Value<?> isSideSolidFullSquare(Context context, MemberFunction function) throws  CodeError {
		BlockStateValue blockStateValue = function.getParameterValueOfType(context, BlockStateValue.class, 0);
		Direction direction = Direction.byName(function.getParameterValueOfType(context, StringValue.class, 1).value);
		if (direction == null){direction = Direction.DOWN;}
		return BooleanValue.of(blockStateValue.value.isSideSolidFullSquare(ArucasMinecraftExtension.getWorld(), blockStateValue.blockPos, direction));
	}
	private Value<?> isReplaceable(Context context, MemberFunction function) throws  CodeError {
		BlockState blockState = this.getBlockState(context, function);
		boolean replaceable = blockState.getMaterial().isReplaceable();
		return BooleanValue.of(replaceable);
	}
	private Value<?> isSolidBlock(Context context, MemberFunction function) throws  CodeError {
		BlockStateValue blockStateValue = function.getParameterValueOfType(context, BlockStateValue.class, 0);
		BlockState blockState = blockStateValue.value;
		boolean isSolid = blockState.isSolidBlock(ArucasMinecraftExtension.getWorld(), blockStateValue.blockPos);
		return BooleanValue.of(isSolid);
	}
	private Value<?> getHardness(Context context, MemberFunction function) throws  CodeError {
		BlockState blockState = this.getBlockState(context, function);
		float hardness = blockState.getHardness(ArucasMinecraftExtension.getWorld(), BlockPos.ORIGIN); //requires dummy inputs, why?
		return new NumberValue(hardness);
	}
	private Value<?> getBlockProperties(Context context, MemberFunction function) throws CodeError {
		BlockState blockState = this.getBlockState(context, function);
		ArucasValueMap propertyMap = new ArucasValueMap();
		for (Map.Entry<Property<?>, Comparable<?>> entry : blockState.getEntries().entrySet()) {
			Value<?> mapValue;
			Comparable<?> comparable = entry.getValue();
			if (comparable instanceof Number value) {
				mapValue = new NumberValue(value.doubleValue());
			}
			else if (comparable instanceof Boolean value) {
				mapValue = BooleanValue.of(value);
			}
			else {
				mapValue = new StringValue(comparable.toString());
			}
			propertyMap.put(new StringValue(entry.getKey().getName()), mapValue);
		}
		return new MapValue(propertyMap);
	}

	private Value<?> rotateYClockwise(Context context, MemberFunction function) throws CodeError{
		BlockStateValue blockStateValue = function.getParameterValueOfType(context, BlockStateValue.class, 0);
		return new BlockStateValue(blockStateValue.value.rotate(BlockRotation.CLOCKWISE_90));
	}
	private Value<?> rotateYCounterClockwise(Context context, MemberFunction function) throws CodeError{
		BlockStateValue blockStateValue = function.getParameterValueOfType(context, BlockStateValue.class, 0);
		return new BlockStateValue(blockStateValue.value.rotate(BlockRotation.COUNTERCLOCKWISE_90));
	}
	private Value<?> mirrorFrontBack(Context context, MemberFunction function) throws CodeError{
		BlockStateValue blockStateValue = function.getParameterValueOfType(context, BlockStateValue.class, 0);
		return new BlockStateValue(blockStateValue.value.mirror(BlockMirror.FRONT_BACK));
	}
	private Value<?> mirrorLeftRight(Context context, MemberFunction function) throws CodeError{
		BlockStateValue blockStateValue = function.getParameterValueOfType(context, BlockStateValue.class, 0);
		return new BlockStateValue(blockStateValue.value.mirror(BlockMirror.LEFT_RIGHT));
	}
	private Value<?> isFluid(Context context, MemberFunction function) throws CodeError{
		BlockStateValue blockStateValue = function.getParameterValueOfType(context, BlockStateValue.class, 0);
		return BooleanValue.of(blockStateValue.value.getBlock() instanceof FluidDrainable || blockStateValue.value.getFluidState().getFluid() instanceof FlowableFluid);
	}
	private Value<?> isFluidSource(Context context, MemberFunction function) throws CodeError{
		BlockStateValue blockStateValue = function.getParameterValueOfType(context, BlockStateValue.class, 0);
		return BooleanValue.of(blockStateValue.value.getBlock() instanceof BubbleColumnBlock ||
			blockStateValue.value.getBlock() instanceof FluidBlock && blockStateValue.value.get(FluidBlock.LEVEL) ==0 );
	}
	private Value<?> hasBlockPosition(Context context, MemberFunction function) throws CodeError {
		BlockStateValue blockStateValue = function.getParameterValueOfType(context, BlockStateValue.class, 0);
		return BooleanValue.of(blockStateValue.getBlockX().value != null);
	}

	private Value<?> getBlockX(Context context, MemberFunction function) throws CodeError {
		BlockStateValue blockStateValue = function.getParameterValueOfType(context, BlockStateValue.class, 0);
		return blockStateValue.getBlockX();
	}

	private Value<?> getBlockY(Context context, MemberFunction function) throws CodeError {
		BlockStateValue blockStateValue = function.getParameterValueOfType(context, BlockStateValue.class, 0);
		return blockStateValue.getBlockY();
	}

	private Value<?> getBlockZ(Context context, MemberFunction function) throws CodeError {
		BlockStateValue blockStateValue = function.getParameterValueOfType(context, BlockStateValue.class, 0);
		return blockStateValue.getBlockZ();
	}
	private Value<?> getBlockPos(Context context, MemberFunction function) throws CodeError {
		BlockStateValue blockStateValue = function.getParameterValueOfType(context, BlockStateValue.class, 0);
		ArucasValueList posList = new ArucasValueList();
		posList.add(new NumberValue(blockStateValue.blockPos.getX()));
		posList.add(new NumberValue(blockStateValue.blockPos.getY()));
		posList.add(new NumberValue(blockStateValue.blockPos.getZ()));
		return new ListValue(posList);
	}
	private Value<?> getAddPos(Context context, MemberFunction function) throws CodeError {
		BlockPos blockPos = function.getParameterValueOfType(context, BlockStateValue.class, 0).blockPos;
		BlockPos blockPos2 = function.getParameterValueOfType(context, BlockStateValue.class, 1).blockPos;
		BlockPos blockPosAdd = blockPos.add(blockPos2);
		final BlockState blockStateDefault = Blocks.AIR.getDefaultState();
		return new BlockStateValue(blockStateDefault, blockPosAdd);
	}
	private Value<?> getOffsetDirectionPos(Context context, MemberFunction function) throws CodeError {
		final BlockState blockStateDefault = Blocks.AIR.getDefaultState();
		BlockPos blockPos = function.getParameterValueOfType(context, BlockStateValue.class, 0).blockPos;
		Direction offsetDir = Direction.byName(function.getParameterValueOfType(context, StringValue.class, 1).value);
		int intValue = function.getParameterValueOfType(context, NumberValue.class, 2).value.intValue();
		if (offsetDir == null){ return new BlockStateValue(blockStateDefault, blockPos);}
		return new BlockStateValue(blockStateDefault, blockPos.offset(offsetDir, intValue));
	}
	private Value<?> getRotateClockwisePos(Context context, MemberFunction function) throws CodeError {
		final BlockState blockStateDefault = Blocks.AIR.getDefaultState();
		BlockPos blockPos = function.getParameterValueOfType(context, BlockStateValue.class, 0).blockPos;
		return new BlockStateValue(blockStateDefault, blockPos.rotate(BlockRotation.CLOCKWISE_90));
	}
	private Value<?> getRotateCounterClockwisePos(Context context, MemberFunction function) throws CodeError {
		final BlockState blockStateDefault = Blocks.AIR.getDefaultState();
		BlockPos blockPos = function.getParameterValueOfType(context, BlockStateValue.class, 0).blockPos;
		return new BlockStateValue(blockStateDefault, blockPos.rotate(BlockRotation.COUNTERCLOCKWISE_90));
	}
	private Value<?> getUnitVectorFromDirection(Context context, MemberFunction function) throws CodeError {
		final BlockState blockStateDefault = Blocks.AIR.getDefaultState();
		Direction direction = Direction.byName(function.getParameterValueOfType(context, StringValue.class, 1).value);
		if(direction == null){
			throw new RuntimeError("Direction name is not valid", function.syntaxPosition, context);
		}
		return new BlockStateValue(blockStateDefault, new BlockPos(direction.getVector()));
	}
	private Value<?> refresh(Context context, MemberFunction function) throws CodeError {
		BlockPos blockPos = function.getParameterValueOfType(context, BlockStateValue.class, 0).blockPos;
		BlockState blockState = ArucasMinecraftExtension.getWorld().getBlockState(blockPos);
		if (blockState == null) {
			throw new RuntimeError("Block was null", function.syntaxPosition, context);
		}
		return new BlockStateValue(blockState, blockPos);
	}
	private BlockState getBlockState(Context context, MemberFunction function) throws CodeError {
		BlockState block = function.getParameterValueOfType(context, BlockStateValue.class, 0).value;
		if (block == null) {
			throw new RuntimeError("Block was null", function.syntaxPosition, context);
		}
		return block;
	}
}
