package me.senseiwells.essentialclient.utils.clientscript;

import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.impl.ArucasThread;
import me.senseiwells.essentialclient.utils.misc.Events;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ClientTickSyncer {
	private static final Set<CountDownLatch> OBJECTS_TO_SYNC = new HashSet<>();

	static {
		Events.ON_TICK_POST.register(c -> ClientTickSyncer.triggerSync());
	}

	public synchronized static void syncToTick() throws CodeError {
		CountDownLatch countDownLatch = new CountDownLatch(1);
		OBJECTS_TO_SYNC.add(countDownLatch);

		if (Thread.currentThread() instanceof ArucasThread) {
			try {
				@SuppressWarnings("unused")
				boolean timeOut = countDownLatch.await(50, TimeUnit.MILLISECONDS);
				return;
			}
			catch (InterruptedException e) {
				throw new CodeError(CodeError.ErrorType.INTERRUPTED_ERROR, "", ISyntax.EMPTY);
			}
		}
		throw new RuntimeException("Tried to sync non Arucas Thread");
	}

	public synchronized static void triggerSync() {
		if (!OBJECTS_TO_SYNC.isEmpty()) {
			for (CountDownLatch countDownLatch : OBJECTS_TO_SYNC.toArray(CountDownLatch[]::new)) {
				countDownLatch.countDown();
			}
			OBJECTS_TO_SYNC.clear();
		}
	}
}
