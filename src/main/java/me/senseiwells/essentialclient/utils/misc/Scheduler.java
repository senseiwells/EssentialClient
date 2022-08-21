package me.senseiwells.essentialclient.utils.misc;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public class Scheduler {
	private static final Int2ObjectOpenHashMap<Queue<FutureTask<?>>> TASKS = new Int2ObjectOpenHashMap<>();
	private static final Object LOCK = new Object();
	private static int tickCount = 0;

	static {
		Events.ON_TICK_POST.register(client -> {
			synchronized (LOCK) {
				Queue<FutureTask<?>> queue = TASKS.remove(tickCount++);
				if (queue != null) {
					queue.forEach(Runnable::run);
					queue.clear();
				}
			}
		});
	}

	public static void schedule(int ticks, Runnable runnable) {
		schedule(ticks, Executors.callable(runnable));
	}

	public static <V> Future<V> schedule(int ticks, Callable<V> callable) {
		synchronized (LOCK) {
			if (ticks < 0) {
				throw new IllegalArgumentException("Cannot schedule a task in the past");
			}
			FutureTask<V> task = new FutureTask<>(callable);
			addTask(tickCount + ticks, task);
			return task;
		}
	}

	public static void scheduleLoop(int delay, int interval, int until, Runnable runnable) {
		synchronized (LOCK) {
			if (delay < 0 || interval < 0 || until < 0) {
				throw new IllegalArgumentException("Delay, interval or until ticks cannot be negative");
			}
			for (int delayModifier = tickCount + delay; delayModifier <= tickCount + until; delayModifier += interval) {
				addTask(delayModifier, new FutureTask<>(runnable, null));
			}
		}
	}

	private static void addTask(int ticks, FutureTask<?> task) {
		synchronized (LOCK) {
			TASKS.computeIfAbsent(ticks, k -> new ArrayDeque<>()).add(task);
		}
	}
}
