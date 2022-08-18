package me.senseiwells.essentialclient.utils.misc;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.util.ArrayDeque;
import java.util.Queue;

public class Scheduler {
	private static final Int2ObjectOpenHashMap<Queue<Runnable>> TASKS = new Int2ObjectOpenHashMap<>();
	private static final Object LOCK = new Object();
	private static int tickCount = 0;

	static {
		Events.ON_TICK_POST.register(client -> {
			synchronized (LOCK) {
				Queue<Runnable> queue = TASKS.remove(tickCount++);
				if (queue != null) {
					queue.forEach(Runnable::run);
					queue.clear();
				}
			}
		});
	}

	public static void schedule(int ticks, Runnable runnable) {
		synchronized (LOCK) {
			if (ticks < 0) {
				throw new IllegalArgumentException("Cannot schedule a task in the past");
			}
			TASKS.computeIfAbsent(tickCount + ticks, k -> new ArrayDeque<>()).add(runnable);
		}
	}

	public static void scheduleLoop(int delay, int interval, int until, Runnable runnable) {
		synchronized (LOCK) {
			if (delay < 0 || interval < 0 || until < 0) {
				throw new IllegalArgumentException("Delay, interval or until ticks cannot be negative");
			}
			for (int delayModifier = tickCount + delay; delayModifier <= tickCount + until; delayModifier += interval) {
				TASKS.computeIfAbsent(delayModifier, k -> new ArrayDeque<>()).add(runnable);
			}
		}
	}
}
