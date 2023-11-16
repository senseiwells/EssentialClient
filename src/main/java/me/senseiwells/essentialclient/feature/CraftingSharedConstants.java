package me.senseiwells.essentialclient.feature;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;

public final class CraftingSharedConstants {
	public static final AtomicBoolean IS_VANILLA_CLICK = new AtomicBoolean(false);
	public static final AtomicBoolean IS_SCRIPT_CLICK = new AtomicBoolean(false);
	public static final ScheduledExecutorService EXECUTOR = new ScheduledThreadPoolExecutor(2);

	static {
		ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
			EXECUTOR.shutdownNow();
		});
	}

	public static void load() { }
}
