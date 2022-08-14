package me.senseiwells.essentialclient.clientscript.events;

import me.senseiwells.arucas.classes.ClassInstance;
import me.senseiwells.arucas.core.Interpreter;
import me.senseiwells.arucas.utils.impl.ArucasList;
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

	public boolean isThreadDefinable() {
		return true;
	}

	public synchronized void registerEvent(Interpreter interpreter, GameEventWrapper gameEvent) {
		Set<GameEventWrapper> gameEventWrappers = this.REGISTERED_EVENTS.computeIfAbsent(interpreter.getProperties().getId(), id -> {
			interpreter.getThreadHandler().addShutdownEvent(() -> this.REGISTERED_EVENTS.remove(id));
			return new LinkedHashSet<>();
		});
		gameEventWrappers.add(gameEvent);
	}

	public synchronized boolean unregisterEvent(Interpreter interpreter, GameEventWrapper gameEvent) {
		Set<GameEventWrapper> gameEventWrappers = this.REGISTERED_EVENTS.get(interpreter.getProperties().getId());
		return gameEventWrappers != null && gameEventWrappers.remove(gameEvent);
	}

	public synchronized boolean isEventRegistered(Interpreter interpreter, GameEventWrapper gameEvent) {
		Set<GameEventWrapper> gameEventWrappers = this.REGISTERED_EVENTS.get(interpreter.getProperties().getId());
		return gameEventWrappers != null && gameEventWrappers.contains(gameEvent);
	}

	public synchronized void clearRegisteredEvents(Interpreter interpreter) {
		Set<GameEventWrapper> gameEventWrappers = this.REGISTERED_EVENTS.get(interpreter.getProperties().getId());
		if (gameEventWrappers != null) {
			gameEventWrappers.clear();
		}
	}

	public synchronized boolean run(ClassInstance... arguments) {
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
		List<ClassInstance> values = null;
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
		List<ClassInstance> apply(Interpreter interpreter);

		default List<ClassInstance> applySafe(Interpreter interpreter) {
			try {
				return this.apply(interpreter);
			}
			catch (Exception exception) {
				interpreter.getThreadHandler().handleError(exception);
				return null;
			}
		}
	}

	/**
	 * This type of Event only gets called on a certain
	 * scripting instance, for example when a script stops
	 * the event only gets triggered for the script that
	 * is currently stopping, not any others running concurrently
	 */
	public static class Unique extends MinecraftScriptEvent {
		private final boolean isThreadDefinable;

		public Unique(String eventName, boolean isThreadDefinable) {
			super(eventName);
			this.isThreadDefinable = isThreadDefinable;
		}

		public Unique(String eventName) {
			this(eventName, false);
		}

		@Override
		public boolean isThreadDefinable() {
			return this.isThreadDefinable;
		}

		public synchronized boolean run(UUID uuid, ClassInstance... arguments) {
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
		public synchronized boolean run(ClassInstance... arguments) {
			return false;
		}

		@Deprecated
		@Override
		public synchronized boolean run(ArgumentSupplier argumentSupplier) {
			return false;
		}
	}
}
