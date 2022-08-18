package me.senseiwells.essentialclient.utils.clientscript;

import me.senseiwells.arucas.utils.impl.ArucasThread;
import me.senseiwells.essentialclient.utils.misc.Events;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ClientTickSyncer {
	private static final Set<ArucasThread> FROZEN_THREADS = ConcurrentHashMap.newKeySet();

	static {
		Events.ON_TICK_POST.register(c -> ClientTickSyncer.triggerSync());
	}

	public synchronized static void syncToTick() {
		if (Thread.currentThread() instanceof ArucasThread arucasThread) {
			FROZEN_THREADS.add(arucasThread);
			arucasThread.freeze();
			return;
		}
		throw new RuntimeException("Tried to sync non Arucas Thread");
	}

	public synchronized static void triggerSync() {
		if (!FROZEN_THREADS.isEmpty()) {
			for (ArucasThread thread : FROZEN_THREADS) {
				thread.thaw();
			}
			FROZEN_THREADS.clear();
		}
	}
}
