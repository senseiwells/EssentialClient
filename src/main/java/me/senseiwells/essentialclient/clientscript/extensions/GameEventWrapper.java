package me.senseiwells.essentialclient.clientscript.extensions;

import me.senseiwells.arucas.api.ArucasThreadHandler;
import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.api.wrappers.ArucasClass;
import me.senseiwells.arucas.api.wrappers.ArucasConstructor;
import me.senseiwells.arucas.api.wrappers.ArucasFunction;
import me.senseiwells.arucas.api.wrappers.IArucasWrappedClass;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.BooleanValue;
import me.senseiwells.arucas.values.NullValue;
import me.senseiwells.arucas.values.StringValue;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.functions.FunctionValue;
import me.senseiwells.essentialclient.clientscript.events.CancelEvent;
import me.senseiwells.essentialclient.clientscript.events.MinecraftScriptEvent;
import me.senseiwells.essentialclient.clientscript.events.MinecraftScriptEvents;
import me.senseiwells.essentialclient.utils.EssentialUtils;

import java.util.List;

@SuppressWarnings("unused")
@ArucasClass(name = "GameEvent")
public class GameEventWrapper implements IArucasWrappedClass {
	private Context eventContext;
	private MinecraftScriptEvent minecraftEvent;
	private FunctionValue function;
	private boolean runOnMainThread;

	@ArucasConstructor
	public void constructor(Context eventContext, StringValue eventName, FunctionValue function, BooleanValue cancellable) {
		this.minecraftEvent = MinecraftScriptEvents.getEvent(eventName.value);
		if (this.minecraftEvent == null) {
			throw new RuntimeException("No such event '%s'".formatted(eventName.value));
		}
		this.eventContext = eventContext;
		this.function = function;
		this.runOnMainThread = cancellable.value;
	}

	@ArucasConstructor
	public void constructor(Context eventContext, StringValue eventName, FunctionValue function) {
		this.constructor(eventContext, eventName, function, BooleanValue.FALSE);
	}

	@ArucasFunction
	public BooleanValue isRegistered(Context context) {
		return BooleanValue.of(this.minecraftEvent.isEventRegistered(context, this));
	}

	@ArucasFunction
	public NullValue register(Context context) {
		this.minecraftEvent.registerEvent(context, this);
		return NullValue.NULL;
	}

	@ArucasFunction
	public BooleanValue unregister(Context context) {
		return BooleanValue.of(this.minecraftEvent.unregisterEvent(context, this));
	}

	@ArucasFunction
	public static NullValue cancel(Context context) throws CancelEvent {
		throw CancelEvent.INSTANCE;
	}

	@ArucasFunction
	public static NullValue unregisterAll(Context context) {
		MinecraftScriptEvents.clearEventFunctions(context);
		return NullValue.NULL;
	}

	public Context getEventContext() {
		return this.eventContext;
	}

	public boolean callFunction(List<Value<?>> arguments) {
		Context branchContext = this.eventContext.createBranch();
		ArucasThreadHandler threadHandler = this.eventContext.getThreadHandler();
		if (this.minecraftEvent.isThreadDefinable() && !this.runOnMainThread && EssentialUtils.getClient().isOnThread()) {
			threadHandler.runAsyncFunctionInThreadPool(
				branchContext,
				context -> this.function.call(context, arguments)
			);
			return false;
		}
		try {
			this.function.call(branchContext, arguments);
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
		catch (CodeError codeError) {
			threadHandler.tryError(branchContext, codeError);
		}
		return false;
	}
}
