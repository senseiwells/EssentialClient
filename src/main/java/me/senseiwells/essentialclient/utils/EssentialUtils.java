package me.senseiwells.essentialclient.utils;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.MinecraftVersion;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class EssentialUtils {
	private static final Path ESSENTIAL_CLIENT_PATH;
	private static final boolean DEV;

	public static URL WIKI_URL;
	public static URL SCRIPT_WIKI_URL;

	static {
		ESSENTIAL_CLIENT_PATH = FabricLoader.getInstance().getConfigDir().resolve("EssentialClient");
		DEV = FabricLoader.getInstance().isDevelopmentEnvironment();

		throwAsRuntime(() -> {
			WIKI_URL = new URL("https://github.com/senseiwells/EssentialClient/wiki");
			SCRIPT_WIKI_URL = new URL("https://github.com/senseiwells/EssentialClient/wiki/ClientScript");
			Files.createDirectories(ESSENTIAL_CLIENT_PATH);
		});
	}

	public static void sendMessageToActionBar(String message) {
		MinecraftClient client = getClient();
		if (client.player != null) {
			client.execute(() -> client.player.sendMessage(new LiteralText(message), true));
		}
	}

	public static void sendMessage(String message) {
		MinecraftClient client = getClient();
		if (client.player != null) {
			client.execute(() -> client.player.sendMessage(new LiteralText(message), false));
		}
	}

	public static void sendMessage(Text text) {
		MinecraftClient client = getClient();
		if (client.player != null) {
			client.execute(() -> client.player.sendMessage(text, false));
		}
	}

	public static void sendChatMessage(String message) {
		MinecraftClient client = getClient();
		if (client.player != null) {
			client.execute(() -> client.player.sendChatMessage(message));
		}
	}

	public static Path getEssentialConfigFile() {
		return ESSENTIAL_CLIENT_PATH;
	}

	public static MinecraftClient getClient() {
		return MinecraftClient.getInstance();
	}

	public static ClientPlayerEntity getPlayer() {
		return getClient().player;
	}

	public static ClientWorld getWorld() {
		return getClient().world;
	}

	public static ClientPlayerInteractionManager getInteractionManager() {
		return getClient().interactionManager;
	}

	public static ClientPlayNetworkHandler getNetworkHandler() {
		return getClient().getNetworkHandler();
	}

	public static PlayerListEntry getPlayerListEntry() {
		return EssentialUtils.getNetworkHandler().getPlayerListEntry(EssentialUtils.getPlayer().getUuid());
	}

	public static boolean playerHasOp() {
		ClientPlayerEntity player = getPlayer();
		return player != null && player.hasPermissionLevel(2);
	}

	public static String getMinecraftVersion() {
		return MinecraftVersion.GAME_VERSION.getName();
	}

	public static boolean isDev() {
		return DEV;
	}

	public static boolean isModInstalled(String modId) {
		return FabricLoader.getInstance().isModLoaded(modId);
	}

	public static void throwAsRuntime(ThrowableRunnable runnable) {
		try {
			runnable.run();
		}
		catch (Throwable throwable) {
			throw new RuntimeException(throwable);
		}
	}

	public static <T> T throwAsRuntime(ThrowableSupplier<T> supplier) {
		try {
			return supplier.get();
		}
		catch (Throwable throwable) {
			throw new RuntimeException(throwable);
		}
	}

	public static <T> T catchAsNull(ThrowableSupplier<T> throwableSupplier) {
		try {
			return throwableSupplier.get();
		}
		catch (Throwable throwable) {
			return null;
		}
	}

	@FunctionalInterface
	public interface ThrowableRunnable {
		void run() throws Throwable;
	}

	@FunctionalInterface
	public interface ThrowableSupplier<T> {
		T get() throws Throwable;
	}
}
