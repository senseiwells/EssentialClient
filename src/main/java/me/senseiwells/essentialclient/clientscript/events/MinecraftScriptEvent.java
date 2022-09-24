package me.senseiwells.essentialclient.clientscript.events;

import me.senseiwells.arucas.classes.ClassInstance;
import me.senseiwells.arucas.core.Interpreter;
import me.senseiwells.essentialclient.utils.clientscript.impl.ScriptEvent;

import java.util.*;
import java.util.function.Function;

public class MinecraftScriptEvent {
	private final String name;
	private final String description;
	private final String[] parameters;
	private final boolean isCancellable;

	protected final Map<UUID, Set<ScriptEvent>> REGISTERED_EVENTS = new HashMap<>();

	public MinecraftScriptEvent(String eventName, String description, String[] parameters, boolean isCancellable) {
		this.name = eventName;
		this.description = description;
		this.parameters = parameters;
		this.isCancellable = isCancellable;
		MinecraftScriptEvents.addEventToMap(eventName, this);
	}

	public MinecraftScriptEvent(String eventName, String description, String... parameters) {
		this(eventName, description, parameters, false);
	}

	public String getName() {
		return this.name;
	}

	public String getDescription() {
		return this.description;
	}

	public String[] getParameters() {
		return this.parameters;
	}

	public boolean canCancel() {
		return this.isCancellable;
	}

	public boolean isThreadDefinable() {
		return true;
	}

	public synchronized void registerEvent(ScriptEvent gameEvent) {
		if (gameEvent.getInterpreter().getThreadHandler().getRunning()) {
			Set<ScriptEvent> gameEventWrappers = this.REGISTERED_EVENTS.computeIfAbsent(gameEvent.getId(), id -> {
				gameEvent.getInterpreter().getThreadHandler().addShutdownEvent(() -> this.REGISTERED_EVENTS.remove(id));
				return new LinkedHashSet<>();
			});
			gameEventWrappers.add(gameEvent);
		}
	}

	public synchronized boolean unregisterEvent(ScriptEvent gameEvent) {
		Set<ScriptEvent> gameEventWrappers = this.REGISTERED_EVENTS.get(gameEvent.getId());
		return gameEventWrappers != null && gameEventWrappers.remove(gameEvent);
	}

	public synchronized boolean isEventRegistered(ScriptEvent gameEvent) {
		Set<ScriptEvent> gameEventWrappers = this.REGISTERED_EVENTS.get(gameEvent.getId());
		return gameEventWrappers != null && gameEventWrappers.contains(gameEvent);
	}

	public synchronized void clearRegisteredEvents(UUID uuid) {
		Set<ScriptEvent> gameEventWrappers = this.REGISTERED_EVENTS.get(uuid);
		if (gameEventWrappers != null) {
			gameEventWrappers.clear();
		}
	}

	public synchronized boolean run(Object... args) {
		return this.run(interpreter -> {
			List<ClassInstance> eventArgs = new ArrayList<>();
			for (Object arg : args) {
				eventArgs.add(interpreter.convertValue(arg));
			}
			return eventArgs;
		});
	}

	public synchronized boolean run(Function<Interpreter, List<ClassInstance>> argumentSupplier) {
		boolean shouldCancel = false;
		for (Set<ScriptEvent> gameEventWrappers : this.REGISTERED_EVENTS.values()) {
			List<ClassInstance> eventArguments = null;
			for (ScriptEvent gameEvent : gameEventWrappers) {
				if (eventArguments == null) {
					eventArguments = argumentSupplier.apply(gameEvent.getInterpreter());
				}
				if (gameEvent.invoke(eventArguments)) {
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

	/**
	 * This type of Event only gets called on a certain
	 * scripting instance, for example when a script stops
	 * the event only gets triggered for the script that
	 * is currently stopping, not any others running concurrently
	 */
	public static class Unique extends MinecraftScriptEvent {
		private final boolean isThreadDefinable;

		public Unique(String name, String description, String[] parameters, boolean isThreadDefinable) {
			super(name, description, parameters);
			this.isThreadDefinable = isThreadDefinable;
		}

		public Unique(String name, String description, String... parameters) {
			this(name, description, parameters, false);
		}

		@Override
		public boolean isThreadDefinable() {
			return this.isThreadDefinable;
		}

		public synchronized boolean run(UUID uuid, ClassInstance... arguments) {
			boolean shouldCancel = false;
			Set<ScriptEvent> gameEventWrappers = this.REGISTERED_EVENTS.get(uuid);
			if (gameEventWrappers == null) {
				return false;
			}
			for (ScriptEvent gameEvent : gameEventWrappers) {
				if (gameEvent.invoke(Arrays.asList(arguments))) {
					shouldCancel = true;
				}
			}
			return shouldCancel;
		}

		@Deprecated
		@Override
		public synchronized boolean run(Object... arguments) {
			return false;
		}
	}
}
