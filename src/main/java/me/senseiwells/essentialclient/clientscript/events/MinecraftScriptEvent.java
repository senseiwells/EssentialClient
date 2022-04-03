package me.senseiwells.essentialclient.clientscript.events;

import me.senseiwells.essentialclient.clientscript.extensions.GameEventWrapper;
import me.senseiwells.arucas.api.ArucasThreadHandler;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.impl.ArucasList;
import me.senseiwells.arucas.values.Value;

import java.util.*;

public class MinecraftScriptEvent {
	private final Map<ArucasThreadHandler, Set<GameEventWrapper>> registeredEvents = new HashMap<>();
	private final String name;
	private final boolean isCancellable;

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
		Set<GameEventWrapper> gameEventWrappers = this.registeredEvents.getOrDefault(context.getThreadHandler(), new LinkedHashSet<>());
		this.registeredEvents.putIfAbsent(context.getThreadHandler(), gameEventWrappers);
		gameEventWrappers.add(gameEvent);
	}

	public synchronized boolean unregisterEvent(Context context, GameEventWrapper gameEvent) {
		Set<GameEventWrapper> gameEventWrappers = this.registeredEvents.get(context.getThreadHandler());
		return gameEventWrappers != null && gameEventWrappers.remove(gameEvent);
	}

	public synchronized boolean isEventRegistered(Context context, GameEventWrapper gameEvent) {
		Set<GameEventWrapper> gameEventWrappers = this.registeredEvents.get(context.getThreadHandler());
		return gameEventWrappers != null && gameEventWrappers.contains(gameEvent);
	}

	public synchronized void clearRegisteredEvents(Context context) {
		Set<GameEventWrapper> gameEventWrappers = this.registeredEvents.get(context.getThreadHandler());
		if (gameEventWrappers != null) {
			gameEventWrappers.clear();
		}
	}

	public synchronized boolean run(Value<?>... arguments) {
		ArucasList argumentList = new ArucasList();
		argumentList.addAll(List.of(arguments));
		boolean shouldCancel = false;
		for (Set<GameEventWrapper> gameEventWrappers : this.registeredEvents.values()) {
			for (GameEventWrapper gameEvent : gameEventWrappers) {
				if (gameEvent.callFunction(argumentList)) {
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
}
