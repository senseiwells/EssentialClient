package me.senseiwells.essentialclient.utils.misc;

import net.minecraft.client.MinecraftClient;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Events {
	public static final Event<MinecraftClient>
		ON_TICK_POST = new Event<>(),
		ON_CLOSE = new Event<>();
	public static final Event<Void>
		ON_DISCONNECT = new Event<>(),
		ON_DISCONNECT_POST = new Event<>();

	public static class Event<T> {
		private final List<Consumer<T>> listeners;

		private Event() {
			this.listeners = new ArrayList<>();
		}

		public void trigger() {
			this.trigger(null);
		}

		public void trigger(T value) {
			for (Consumer<T> listener : this.listeners) {
				listener.accept(value);
			}
		}

		public void register(Consumer<T> listener) {
			this.listeners.add(listener);
		}
	}
}
