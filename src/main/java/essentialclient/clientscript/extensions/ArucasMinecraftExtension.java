package essentialclient.clientscript.extensions;

import essentialclient.clientscript.ClientScript;
import essentialclient.clientscript.values.MinecraftClientValue;
import me.senseiwells.arucas.api.IArucasExtension;
import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.ArucasThreadUtils;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.ListValue;
import me.senseiwells.arucas.values.NullValue;
import me.senseiwells.arucas.values.NumberValue;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.functions.AbstractBuiltInFunction;
import me.senseiwells.arucas.values.functions.BuiltInFunction;
import me.senseiwells.arucas.values.functions.FunctionValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;

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
		new BuiltInFunction("throwUncatchableError", (context, function) -> { throw new NullPointerException(); }),
		new BuiltInFunction("hold", this::hold)
	);

	private Value<?> getMinecraftClient(Context context, BuiltInFunction function) throws CodeError {
		return new MinecraftClientValue(getClient());
	}

	private Value<?> runThreaded(Context context, BuiltInFunction function) throws CodeError {
		FunctionValue functionValue = function.getParameterValueOfType(context, FunctionValue.class, 0);
		List<Value<?>> list = function.getParameterValueOfType(context, ListValue.class, 1).value;
		Thread thread = ClientScript.getInstance().runBranchAsyncFunction((branchContext) -> functionValue.call(branchContext, list));
		long threadId = thread.getId();
		ArucasThreadUtils.addThreadToMap(threadId, thread);
		return new NumberValue(threadId);
	}

	private Value<?> hold(Context context, BuiltInFunction function) throws CodeError {
		try {
			Thread.sleep(Long.MAX_VALUE);
		}
		catch (InterruptedException e) {
			throw new CodeError(CodeError.ErrorType.INTERRUPTED_ERROR, "", function.syntaxPosition);
		}
		return NullValue.NULL;
	}

	public static MinecraftClient getClient() throws CodeError {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client == null) {
			throw new RuntimeError("MinecraftClient was null", ISyntax.empty());
		}
		return client;
	}

	public static ClientPlayerEntity getPlayer() throws CodeError {
		ClientPlayerEntity player = getClient().player;
		if (player == null) {
			throw new RuntimeError("MinecraftClient.player was null", ISyntax.empty());
		}
		return player;
	}

	public static ClientPlayerEntity getPlayer(MinecraftClient client) throws CodeError {
		ClientPlayerEntity player = client.player;
		if (player == null) {
			throw new RuntimeError("MinecraftClient.player was null", ISyntax.empty());
		}
		return player;
	}

	public static ClientWorld getWorld() throws CodeError {
		ClientWorld world = getClient().world;
		if (world == null) {
			throw new RuntimeError("MinecraftClient.world was null", ISyntax.empty());
		}
		return world;
	}

	public static ClientWorld getWorld(MinecraftClient client) throws CodeError {
		ClientWorld world = client.world;
		if (world == null) {
			throw new RuntimeError("MinecraftClient.world was null", ISyntax.empty());
		}
		return world;
	}

	public static ClientPlayNetworkHandler getNetworkHandler() throws CodeError {
		ClientPlayNetworkHandler networkHandler = getClient().getNetworkHandler();
		if (networkHandler == null) {
			throw new RuntimeError("MinecraftClient.networkHandler was null", ISyntax.empty());
		}
		return networkHandler;
	}

	public static ClientPlayerInteractionManager getInteractionManager() throws CodeError {
		ClientPlayerInteractionManager interactionManager = getClient().interactionManager;
		if (interactionManager == null) {
			throw new RuntimeError("MinecraftClient.interactionManager was null", ISyntax.empty());
		}
		return interactionManager;
	}

	public static Identifier getIdentifier(Context context, ISyntax syntaxHandler, String name) throws RuntimeError {
		try {
			return new Identifier(name);
		}
		catch (InvalidIdentifierException e) {
			throw new RuntimeError("Invalid identifier name", syntaxHandler, context);
		}
	}
}
