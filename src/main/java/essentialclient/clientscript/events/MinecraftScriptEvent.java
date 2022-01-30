package essentialclient.clientscript.events;

import essentialclient.clientscript.extensions.GameEventWrapper;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.impl.ArucasList;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.functions.FunctionValue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MinecraftScriptEvent {
	private final Set<EventFunction> functionValues = new HashSet<>();
	private final Set<GameEventWrapper> registeredEvents = new HashSet<>();
	private final String name;
	private final boolean isCancellable;
	private int functionId = 0;

	public MinecraftScriptEvent(String eventName, boolean isCancellable) {
		this.name = eventName;
		this.isCancellable = isCancellable;
		MinecraftScriptEvents.addEventToMap(eventName, this);
	}

	public MinecraftScriptEvent(String eventName) {
		this(eventName, false);
	}

	public boolean canCancel() {
		return this.isCancellable;
	}

	public synchronized int addFunction(Context context, FunctionValue functionValue) {
		this.functionId++;
		this.functionValues.add(new EventFunction(context, functionValue, this.functionId));
		return this.functionId;
	}

	public synchronized void registerEvent(GameEventWrapper gameEvent) {
		this.registeredEvents.add(gameEvent);
	}

	public synchronized boolean removeFunction(int id) {
		return this.functionValues.removeIf(eventFunction -> eventFunction.id == id);
	}

	public synchronized boolean unregisterEvent(GameEventWrapper gameEvent) {
		return this.registeredEvents.remove(gameEvent);
	}

	public synchronized boolean isEventRegistered(GameEventWrapper gameEvent) {
		return this.registeredEvents.contains(gameEvent);
	}

	public synchronized void clearRegisteredEvents() {
		this.functionValues.clear();
		this.registeredEvents.clear();
	}

	public synchronized boolean run(Value<?>... arguments) {
		ArucasList argumentList = new ArucasList();
		argumentList.addAll(List.of(arguments));
		this.functionValues.forEach(eventFunction -> {
			Context fileContext = eventFunction.context.createBranch();
			FunctionValue functionValue = eventFunction.functionValue;
			fileContext.getThreadHandler().runAsyncFunctionInContext(fileContext, context -> functionValue.call(context, argumentList), "ClientScript Event");
		});
		boolean shouldCancel = false;
		for (GameEventWrapper registeredEvent : this.registeredEvents) {
			if (registeredEvent.callFunction(argumentList)) {
				shouldCancel = true;
			}
		}
		return shouldCancel;
	}

	@Override
	public String toString() {
		return this.name;
	}

	private record EventFunction(Context context, FunctionValue functionValue, int id) { }
}
