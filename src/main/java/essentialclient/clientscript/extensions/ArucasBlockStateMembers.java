package essentialclient.clientscript.extensions;

import com.sun.jdi.FloatValue;
import essentialclient.clientscript.values.BlockStateValue;
import essentialclient.clientscript.values.ItemStackValue;
import me.senseiwells.arucas.api.IArucasValueExtension;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.ArucasValueMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.*;
import me.senseiwells.arucas.values.functions.MemberFunction;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
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
		new MemberFunction("asItemStack", (context, function) -> new ItemStackValue(this.getBlockState(context, function).getBlock().asItem().getDefaultStack())),
		new MemberFunction("getBlastResistance", (context, function) -> new NumberValue(this.getBlockState(context, function).getBlock().getBlastResistance())),
		new MemberFunction("getBlockProperties", this::getBlockProperties),
		new MemberFunction("hasBlockPosition", this::hasBlockPosition),
		new MemberFunction("getBlockX", this::getBlockX),
		new MemberFunction("getBlockZ", this::getBlockZ),
		new MemberFunction("getBlockY", this::getBlockY),
		new MemberFunction("getX", this::getBlockX),
		new MemberFunction("getZ", this::getBlockZ),
		new MemberFunction("getY", this::getBlockY),
		new MemberFunction("isReplaceable", this::isReplaceable),
		new MemberFunction("getHardness", this::getHardness)
	);
	private Value<?> isReplaceable(Context context, MemberFunction function) throws  CodeError {
		BlockState blockState = this.getBlockState(context, function);
		boolean replaceable = blockState.getMaterial().isReplaceable();
		return BooleanValue.of(replaceable);
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

	private BlockState getBlockState(Context context, MemberFunction function) throws CodeError {
		BlockState block = function.getParameterValueOfType(context, BlockStateValue.class, 0).value;
		if (block == null) {
			throw new RuntimeError("Block was null", function.syntaxPosition, context);
		}
		return block;
	}
}
