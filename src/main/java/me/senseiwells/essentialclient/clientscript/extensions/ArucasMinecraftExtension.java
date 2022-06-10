package me.senseiwells.essentialclient.clientscript.extensions;

import me.senseiwells.arucas.api.IArucasExtension;
import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.api.docs.FunctionDoc;
import me.senseiwells.arucas.throwables.BuiltInException;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.Arguments;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.impl.ArucasThread;
import me.senseiwells.arucas.values.ListValue;
import me.senseiwells.arucas.values.NullValue;
import me.senseiwells.arucas.values.ThreadValue;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.functions.BuiltInFunction;
import me.senseiwells.arucas.values.functions.FunctionValue;
import me.senseiwells.essentialclient.clientscript.values.MinecraftClientValue;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;

import java.util.List;

import static me.senseiwells.arucas.utils.ValueTypes.THREAD;
import static me.senseiwells.essentialclient.clientscript.core.MinecraftAPI.MINECRAFT_CLIENT;

public class ArucasMinecraftExtension implements IArucasExtension {
	@Override
	public String getName() {
		return "MinecraftExtension";
	}

	@Override
	public ArucasFunctionMap<BuiltInFunction> getDefinedFunctions() {
		return ArucasFunctionMap.of(
			BuiltInFunction.of("getMinecraftClient", this::getMinecraftClient, "Use 'MinecraftClient.getClient()'"),
			BuiltInFunction.of("runThreaded", 2, this::runThreaded, "Use 'Thread.runThreaded(function)'"),
			BuiltInFunction.of("hold", this::hold)
		);
	}

	@FunctionDoc(
		deprecated = "Use 'MinecraftClient.getClient()'",
		name = "getMinecraftClient",
		desc = "This gets the MinecraftClient instance",
		returns = {MINECRAFT_CLIENT, "The MinecraftClient instance"},
		example = "getMinecraftClient();"
	)
	private Value getMinecraftClient(Arguments arguments) {
		return MinecraftClientValue.INSTANCE;
	}

	@FunctionDoc(
		deprecated = "Use 'Thread.runThreaded(function)'",
		name = "runThreaded",
		desc = "This runs a function in a new thread",
		returns = {THREAD, "The new thread that your function is running on"},
		example = """
			runThreaded(fun(parameter) {
			    print(parameter);
			}, ['hi']);
			"""
	)
	private Value runThreaded(Arguments arguments) throws CodeError {
		Context context = arguments.getContext();
		FunctionValue functionValue = arguments.getNextFunction();
		List<Value> list = arguments.getNextGeneric(ListValue.class);
		ArucasThread thread = context.getThreadHandler().runAsyncFunctionInContext(context.createBranch(), branchContext -> functionValue.call(branchContext, list));
		return ThreadValue.of(thread);
	}

	@FunctionDoc(
		name = "hold",
		desc = "This freezes the current thread and halts execution, same functionality as 'Thread.freeze()'",
		example = "hold();"
	)
	private Value hold(Arguments arguments) throws CodeError {
		try {
			Thread.sleep(Long.MAX_VALUE);
		}
		catch (InterruptedException e) {
			throw new CodeError(CodeError.ErrorType.INTERRUPTED_ERROR, "", arguments.getPosition());
		}
		return NullValue.NULL;
	}

	public static MinecraftClient getClient() throws CodeError {
		MinecraftClient client = EssentialUtils.getClient();
		if (client == null) {
			throw new BuiltInException("MinecraftClient was null");
		}
		return client;
	}

	public static ClientPlayerEntity getPlayer() throws CodeError {
		ClientPlayerEntity player = getClient().player;
		if (player == null) {
			throw new BuiltInException("MinecraftClient.player was null");
		}
		return player;
	}

	public static ClientPlayerEntity getPlayer(MinecraftClient client) throws CodeError {
		ClientPlayerEntity player = client.player;
		if (player == null) {
			throw new BuiltInException("MinecraftClient.player was null");
		}
		return player;
	}

	public static ClientWorld getWorld() throws CodeError {
		ClientWorld world = getClient().world;
		if (world == null) {
			throw new BuiltInException("MinecraftClient.world was null");
		}
		return world;
	}

	public static ClientWorld getWorld(MinecraftClient client) throws CodeError {
		ClientWorld world = client.world;
		if (world == null) {
			throw new BuiltInException("MinecraftClient.world was null");
		}
		return world;
	}

	public static ClientPlayNetworkHandler getNetworkHandler() throws CodeError {
		ClientPlayNetworkHandler networkHandler = getClient().getNetworkHandler();
		if (networkHandler == null) {
			throw new BuiltInException("MinecraftClient.networkHandler was null");
		}
		return networkHandler;
	}

	public static ClientPlayerInteractionManager getInteractionManager() throws CodeError {
		ClientPlayerInteractionManager interactionManager = getClient().interactionManager;
		if (interactionManager == null) {
			throw new BuiltInException("MinecraftClient.interactionManager was null");
		}
		return interactionManager;
	}

	public static Identifier getId(Arguments arguments, String name) throws RuntimeError {
		return getId(arguments.getContext(), arguments.getPosition(), name);
	}

	public static Identifier getId(Context context, ISyntax syntaxHandler, String name) throws RuntimeError {
		try {
			return new Identifier(name);
		}
		catch (InvalidIdentifierException e) {
			throw new RuntimeError("Invalid identifier name", syntaxHandler, context);
		}
	}
}
