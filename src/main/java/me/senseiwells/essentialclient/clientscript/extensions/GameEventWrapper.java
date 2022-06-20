package me.senseiwells.essentialclient.clientscript.extensions;

import me.senseiwells.arucas.api.ArucasThreadHandler;
import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.api.docs.ClassDoc;
import me.senseiwells.arucas.api.docs.ConstructorDoc;
import me.senseiwells.arucas.api.docs.FunctionDoc;
import me.senseiwells.arucas.api.wrappers.ArucasClass;
import me.senseiwells.arucas.api.wrappers.ArucasConstructor;
import me.senseiwells.arucas.api.wrappers.ArucasFunction;
import me.senseiwells.arucas.api.wrappers.IArucasWrappedClass;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.BooleanValue;
import me.senseiwells.arucas.values.StringValue;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.functions.FunctionValue;
import me.senseiwells.essentialclient.clientscript.events.CancelEvent;
import me.senseiwells.essentialclient.clientscript.events.MinecraftScriptEvent;
import me.senseiwells.essentialclient.clientscript.events.MinecraftScriptEvents;
import me.senseiwells.essentialclient.utils.EssentialUtils;

import java.util.List;

import static me.senseiwells.arucas.utils.ValueTypes.*;
import static me.senseiwells.essentialclient.clientscript.core.MinecraftAPI.GAME_EVENT;

@SuppressWarnings("unused")
@ClassDoc(
	name = GAME_EVENT,
	desc = "This class allows you to register listeners for game events in Minecraft.",
	importPath = "Minecraft"
)
@ArucasClass(name = GAME_EVENT)
public class GameEventWrapper implements IArucasWrappedClass {
	private Context eventContext;
	private MinecraftScriptEvent minecraftEvent;
	private FunctionValue function;
	private boolean runOnMainThread;

	@ConstructorDoc(
		desc = "This creates a new GameEvent",
		params = {
			STRING, "eventName", "The name of the event, you can find these on the GameEvents page",
			FUNCTION, "onEvent", "The function to run when the event is called, some events may have parameters",
			BOOLEAN, "cancellable", "Whether or not the event is cancellable, if it is then it will run on the main thread"
		},
		example = "new GameEvent('onClientTick', fun() { }, true);"
	)
	@ArucasConstructor
	public void constructor(Context eventContext, StringValue eventName, FunctionValue function, BooleanValue cancellable) {
		this.minecraftEvent = MinecraftScriptEvents.getEvent(eventName.value);
		if (this.minecraftEvent == null) {
			throw new RuntimeException("No such event '%s'".formatted(eventName.value));
		}
		this.eventContext = eventContext.createBranch();
		this.function = function;
		this.runOnMainThread = cancellable.value;
		this.register(eventContext);
	}

	@ConstructorDoc(
		desc = "This creates a new GameEvent, that is not cancellable",
		params = {
			STRING, "eventName", "The name of the event, you can find these on the GameEvents page",
			FUNCTION, "onEvent", "The function to run when the event is called, some events may have parameters",
		},
		example = "new GameEvent('onClientTick', fun() { });"
	)
	@ArucasConstructor
	public void constructor(Context eventContext, StringValue eventName, FunctionValue function) {
		this.constructor(eventContext, eventName, function, BooleanValue.FALSE);
	}

	@FunctionDoc(
		name = "isRegistered",
		desc = "This returns whether or not the event is registered",
		returns = {BOOLEAN, "Whether or not the event is registered"},
		example = "gameEvent.isRegistered();"
	)
	@ArucasFunction
	public BooleanValue isRegistered(Context context) {
		return BooleanValue.of(this.minecraftEvent.isEventRegistered(context, this));
	}

	@FunctionDoc(
		name = "register",
		desc = "This registers the event",
		example = "gameEvent.register();"
	)
	@ArucasFunction
	public void register(Context context) {
		this.minecraftEvent.registerEvent(context, this);
	}

	@FunctionDoc(
		name = "unregister",
		desc = "This unregisters the event",
		example = "gameEvent.unregister();"
	)
	@ArucasFunction
	public BooleanValue unregister(Context context) {
		return BooleanValue.of(this.minecraftEvent.unregisterEvent(context, this));
	}

	@FunctionDoc(
		isStatic = true,
		name = "cancel",
		desc = {
			"If called on a cancellable event, this will stop execution and cancel the event,",
			"if called on a non-cancellable event, or not on an event, this will throw an error"
		},
		example = "GameEvent.cancel();"
	)
	@ArucasFunction
	public static void cancel(Context context) throws CancelEvent {
		throw CancelEvent.INSTANCE;
	}

	@FunctionDoc(
		isStatic = true,
		name = "unregisterAll",
		desc = "This unregisters all events registered by this script",
		example = "GameEvent.unregisterAll();"
	)
	@ArucasFunction
	public static void unregisterAll(Context context) {
		MinecraftScriptEvents.clearEventFunctions(context);
	}

	public Context getEventContext() {
		return this.eventContext;
	}

	public boolean callFunction(List<Value> arguments) {
		Context branchContext = this.eventContext.createBranch();
		ArucasThreadHandler threadHandler = this.eventContext.getThreadHandler();

		int count = this.function.getCount();
		List<Value> newArguments = count < arguments.size() ? arguments.subList(0, count) : arguments;

		if (this.minecraftEvent.isThreadDefinable() && !this.runOnMainThread && EssentialUtils.getClient().isOnThread()) {
			threadHandler.runAsyncFunctionInThreadPool(
				branchContext,
				context -> this.function.call(context, newArguments)
			);
			return false;
		}
		try {
			this.function.call(branchContext, newArguments);
			return false;
		}
		catch (CancelEvent cancelEvent) {
			if (this.minecraftEvent.canCancel()) {
				if (!EssentialUtils.getClient().isOnThread()) {
					threadHandler.tryError(branchContext, new RuntimeError(
						"Cannot cancel event, not on main thread",
						ISyntax.empty(), branchContext
					));
				}
				return true;
			}
			threadHandler.tryError(branchContext, new RuntimeError(
				"Cannot cancel event '%s'".formatted(this.minecraftEvent),
				ISyntax.empty(), branchContext
			));
		}
		catch (Throwable throwable) {
			threadHandler.tryError(branchContext, throwable);
		}
		return false;
	}
}
