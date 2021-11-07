package essentialclient.clientscript.extensions;

import essentialclient.clientscript.ClientScript;
import essentialclient.clientscript.MinecraftEventFunction;
import essentialclient.clientscript.values.BlockStateValue;
import essentialclient.clientscript.values.ItemStackValue;
import essentialclient.clientscript.values.MinecraftClientValue;
import me.senseiwells.arucas.api.IArucasExtension;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.Position;
import me.senseiwells.arucas.values.*;
import me.senseiwells.arucas.values.functions.AbstractBuiltInFunction;
import me.senseiwells.arucas.values.functions.BuiltInFunction;
import me.senseiwells.arucas.values.functions.FunctionValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.List;
import java.util.Set;

public class ArucasMinecraftExtension implements IArucasExtension {

	@Override
	public Set<? extends AbstractBuiltInFunction<?>> getDefinedFunctions() {
		return this.minecraftFunctions;
	}

	@Override
	public String getName() {
		return "MinecraftExtension";
	}

	private final Set<? extends AbstractBuiltInFunction<?>> minecraftFunctions = Set.of(
			new BuiltInFunction("getMinecraftClient", this::getMinecraftClient),
			new BuiltInFunction("runThreaded", List.of("function", "parameters"), this::runThreaded),
			new BuiltInFunction("schedule", List.of("milliseconds", "function"), this::schedule, true),
			new BuiltInFunction("addGameEvent", List.of("eventName", "function"), this::addGameEvent),
			new BuiltInFunction("itemFromString", "name", this::itemFromString),
			new BuiltInFunction("blockFromString", "name", this::blockFromString),
			new BuiltInFunction("getScriptsPath", (context, function) -> new StringValue(ClientScript.getDir().toString())),
			new BuiltInFunction("throwUncatchableError", (context, function) -> { throw new NullPointerException(); }),
			new BuiltInFunction("hold", this::hold)
	);

	private Value<?> getMinecraftClient(Context context, BuiltInFunction function) throws CodeError {
		return new MinecraftClientValue(getClient());
	}

	private Value<?> runThreaded(Context context, BuiltInFunction function) throws CodeError {
		FunctionValue functionValue = function.getParameterValueOfType(context, FunctionValue.class, 0);
		List<Value<?>> list = function.getParameterValueOfType(context, ListValue.class, 1).value;
		ClientScript.getInstance().runBranchAsyncFunction((branchContext) -> functionValue.call(branchContext, list));
		return new NullValue();
	}

	@Deprecated
	private Value<?> schedule(Context context, BuiltInFunction function) throws CodeError {
		NumberValue numberValue = function.getParameterValueOfType(context, NumberValue.class, 0);
		FunctionValue functionValue = function.getParameterValueOfType(context, FunctionValue.class, 1);

		ClientScript.getInstance().runBranchAsyncFunction((branchContext) -> {
			try {
				Thread.sleep(numberValue.value.longValue());
				functionValue.call(branchContext, List.of());
			}
			catch (InterruptedException e) {
				throw new CodeError(CodeError.ErrorType.INTERRUPTED_ERROR, "", function.startPos, function.endPos);
			}
		});
		return new NullValue();
	}

	private Value<?> addGameEvent(Context context, BuiltInFunction function) throws CodeError {
		String eventName = function.getParameterValueOfType(context, StringValue.class, 0).value;
		FunctionValue functionValue = function.getParameterValueOfType(context, FunctionValue.class, 1);
		eventName = eventName.startsWith("_") ? eventName : "_" + eventName;
		eventName = eventName.endsWith("_") ? eventName : eventName + "_";
		if (!MinecraftEventFunction.isEvent(eventName)) {
			throw new RuntimeError("The event name must be a predefined event", function.startPos, function.endPos, context);
		}
		context.getSymbolTable().getRoot().set(eventName, functionValue);
		return new NullValue();
	}

	private Value<?> itemFromString(Context context, BuiltInFunction function) throws CodeError {
		StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 0);
		return new ItemStackValue(Registry.ITEM.get(new Identifier(stringValue.value)).getDefaultStack());
	}

	private Value<?> blockFromString(Context context, BuiltInFunction function) throws CodeError {
		StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 0);
		return new BlockStateValue(Registry.BLOCK.get(new Identifier(stringValue.value)).getDefaultState());
	}

	private Value<?> hold(Context context, BuiltInFunction function) throws CodeError {
		try {
			Thread.sleep(Long.MAX_VALUE);
		}
		catch (InterruptedException e) {
			throw new CodeError(CodeError.ErrorType.INTERRUPTED_ERROR, "", function.startPos, function.endPos);
		}
		return new NullValue();
	}

	private static final Position newPos = new Position(0, 0, 0, "");

	public static MinecraftClient getClient() throws CodeError {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client == null) {
			throw new RuntimeError("MinecraftClient was null", newPos, newPos);
		}
		return client;
	}

	public static ClientPlayerEntity getPlayer() throws CodeError {
		ClientPlayerEntity player = getClient().player;
		if (player == null) {
			throw new RuntimeError("MinecraftClient.player was null", newPos, newPos);
		}
		return player;
	}

	public static ClientPlayerEntity getPlayer(MinecraftClient client) throws CodeError {
		ClientPlayerEntity player = client.player;
		if (player == null) {
			throw new RuntimeError("MinecraftClient.player was null", newPos, newPos);
		}
		return player;
	}

	public static ClientWorld getWorld() throws CodeError {
		ClientWorld world = getClient().world;
		if (world == null) {
			throw new RuntimeError("MinecraftClient.world was null", newPos, newPos);
		}
		return world;
	}

	public static ClientWorld getWorld(MinecraftClient client) throws CodeError {
		ClientWorld world = client.world;
		if (world == null) {
			throw new RuntimeError("MinecraftClient.world was null", newPos, newPos);
		}
		return world;
	}

	public static ClientPlayNetworkHandler getNetworkHandler() throws CodeError {
		ClientPlayNetworkHandler networkHandler = getClient().getNetworkHandler();
		if (networkHandler == null) {
			throw new RuntimeError("MinecraftClient.networkHandler was null", newPos, newPos);
		}
		return networkHandler;
	}

	public static ClientPlayerInteractionManager getInteractionManager() throws CodeError {
		ClientPlayerInteractionManager interactionManager = getClient().interactionManager;
		if (interactionManager == null) {
			throw new RuntimeError("MinecraftClient.interactionManager was null", newPos, newPos);
		}
		return interactionManager;
	}


}
