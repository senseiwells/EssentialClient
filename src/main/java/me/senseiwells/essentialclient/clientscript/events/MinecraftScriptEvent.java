package me.senseiwells.essentialclient.clientscript.events;

import me.senseiwells.arucas.api.ArucasThreadHandler;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.impl.ArucasList;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.essentialclient.clientscript.extensions.GameEventWrapper;

import java.util.*;

public class MinecraftScriptEvent {
	private final String name;
	private final boolean isCancellable;

	protected final Map<UUID, Set<GameEventWrapper>> REGISTERED_EVENTS = new HashMap<>();

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

	public synchronized void registerEvent(Context context, GameEventWrapper gameEvent) {
		Set<GameEventWrapper> gameEventWrappers = this.REGISTERED_EVENTS.computeIfAbsent(context.getContextId(), id -> {
			context.getThreadHandler().addShutdownEvent(() -> this.REGISTERED_EVENTS.remove(id));
			return new LinkedHashSet<>();
		});
		gameEventWrappers.add(gameEvent);
	}

	public synchronized boolean unregisterEvent(Context context, GameEventWrapper gameEvent) {
		Set<GameEventWrapper> gameEventWrappers = this.REGISTERED_EVENTS.get(context.getContextId());
		return gameEventWrappers != null && gameEventWrappers.remove(gameEvent);
	}

	public synchronized boolean isEventRegistered(Context context, GameEventWrapper gameEvent) {
		Set<GameEventWrapper> gameEventWrappers = this.REGISTERED_EVENTS.get(context.getContextId());
		return gameEventWrappers != null && gameEventWrappers.contains(gameEvent);
	}

	public synchronized void clearRegisteredEvents(Context context) {
		Set<GameEventWrapper> gameEventWrappers = this.REGISTERED_EVENTS.get(context.getContextId());
		if (gameEventWrappers != null) {
			gameEventWrappers.clear();
		}
	}

	public synchronized boolean run(Value<?>... arguments) {
		ArucasList argumentList = new ArucasList();
		argumentList.addAll(List.of(arguments));
		boolean shouldCancel = false;
		for (Set<GameEventWrapper> gameEventWrappers : this.REGISTERED_EVENTS.values()) {
			for (GameEventWrapper gameEvent : gameEventWrappers) {
				if (gameEvent.callFunction(argumentList)) {
					shouldCancel = true;
				}
			}
		}
		return shouldCancel;
	}

	public synchronized boolean run(ArgumentSupplier argumentSupplier) {
		List<Value<?>> values = null;
		boolean shouldCancel = false;
		for (Set<GameEventWrapper> gameEventWrappers : this.REGISTERED_EVENTS.values()) {
			for (GameEventWrapper gameEvent : gameEventWrappers) {
				if (values == null) {
					values = argumentSupplier.applySafe(gameEvent.getEventContext());
					if (values == null) {
						break;
					}
				}
				if (gameEvent.callFunction(values)) {
					shouldCancel = true;
				}
			}
		}
		return shouldCancel;
	}

	@Override
	public String toString() {
		return this.name;
	}

	@FunctionalInterface
	public interface ArgumentSupplier {
		List<Value<?>> apply(Context context) throws CodeError;

		default List<Value<?>> applySafe(Context context) {
			try {
				return this.apply(context);
			}
			catch (CodeError codeError) {
				ArucasThreadHandler threadHandler = context.getThreadHandler();
				threadHandler.tryError(context, codeError);
				return null;
			}
		}
	}

	public static class Unique extends MinecraftScriptEvent {
		public Unique(String eventName) {
			super(eventName);
		}

		public synchronized boolean run(UUID uuid, Value<?>... arguments) {
			ArucasList argumentList = new ArucasList();
			argumentList.addAll(List.of(arguments));
			boolean shouldCancel = false;
			Set<GameEventWrapper> gameEventWrappers = this.REGISTERED_EVENTS.get(uuid);
			if (gameEventWrappers == null) {
				return false;
			}
			for (GameEventWrapper gameEvent : gameEventWrappers) {
				if (gameEvent.callFunction(argumentList)) {
					shouldCancel = true;
				}
			}
			return shouldCancel;
		}

		@Deprecated
		@Override
		public synchronized boolean run(Value<?>... arguments) {
			return false;
		}

		@Deprecated
		@Override
		public synchronized boolean run(ArgumentSupplier argumentSupplier) {
			return false;
		}
	}
}
