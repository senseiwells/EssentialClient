package essentialclient.clientscript.extensions;

import essentialclient.clientscript.values.BlockStateValue;
import essentialclient.clientscript.values.ItemStackValue;
import me.senseiwells.arucas.api.IArucasExtension;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.ArucasValueMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.*;
import me.senseiwells.arucas.values.functions.AbstractBuiltInFunction;
import me.senseiwells.arucas.values.functions.MemberFunction;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.Property;
import net.minecraft.util.registry.Registry;

import java.util.Map;
import java.util.Set;

public class ArucasBlockStateMembers implements IArucasExtension {

	@Override
	public Set<? extends AbstractBuiltInFunction<?>> getDefinedFunctions() {
		return this.blockStateFunctions;
	}

	@Override
	public String getName() {
		return "BlockStateMemberFunctions";
	}

	private final Set<? extends AbstractBuiltInFunction<?>> blockStateFunctions = Set.of(
		new MemberFunction("getBlockId", (context, function) -> new StringValue(Registry.BLOCK.getId(this.getBlockState(context, function).getBlock()).getPath())),
		new MemberFunction("isBlockEntity", (context, function) -> new BooleanValue(this.getBlockState(context, function).getBlock() instanceof BlockEntityProvider)),
		new MemberFunction("isTransparent", (context, function) -> new BooleanValue(!this.getBlockState(context, function).isOpaque())),
		new MemberFunction("asItemStack", (context, function) -> new ItemStackValue(this.getBlockState(context, function).getBlock().asItem().getDefaultStack())),
		new MemberFunction("getBlastResistance", (context, function) -> new NumberValue(this.getBlockState(context, function).getBlock().getBlastResistance())),
		new MemberFunction("getBlockProperties", this::getBlockProperties),
		new MemberFunction("hasBlockPosition", this::hasBlockPosition),
		new MemberFunction("getBlockX", this::getBlockX),
		new MemberFunction("getBlockZ", this::getBlockZ),
		new MemberFunction("getBlockY", this::getBlockY)
	);

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
				mapValue = new BooleanValue(value);
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
		return new BooleanValue(blockStateValue.getBlockX().value != null);
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
