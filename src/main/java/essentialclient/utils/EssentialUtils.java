package essentialclient.utils;

import essentialclient.EssentialClient;
import essentialclient.clientscript.ClientScript;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModDependency;
import net.minecraft.MinecraftVersion;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class EssentialUtils {

	public static void sendMessageToActionBar(String message) {
		MinecraftClient client = getClient();
		ClientPlayerEntity playerEntity = getPlayer();
		if (playerEntity != null) {
			client.execute(() -> playerEntity.sendMessage(new LiteralText(message), true));
		}
	}
	
	public static void sendMessage(String message) {
		MinecraftClient client = getClient();
		ClientPlayerEntity playerEntity = getPlayer();
		if (playerEntity != null) {
			client.execute(() -> playerEntity.sendMessage(new LiteralText(message), false));
		}
	}
	
	public static void sendMessage(Text text) {
		MinecraftClient client = getClient();
		ClientPlayerEntity playerEntity = getPlayer();
		if (playerEntity != null) {
			client.execute(() -> playerEntity.sendMessage(text, false));
		}
	}

	public static Path getEssentialConfigFile() {
		return FabricLoader.getInstance().getConfigDir().resolve("EssentialClient");
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

	public static ModContainer getModContainer() {
		Optional<ModContainer> modContainer = FabricLoader.getInstance().getModContainer("essential-client");
		return modContainer.isEmpty() ? null : modContainer.get();
	}

	public static String getVersion() {
		ModContainer modContainer = getModContainer();
		if (modContainer == null) {
			return "unknown";
		}
		return modContainer.getMetadata().getVersion().getFriendlyString();
	}

	public static String getArucasVersion() {
		ModContainer modContainer = getModContainer();
		if (modContainer != null) {
			for (ModDependency modDependency : modContainer.getMetadata().getSuggests()) {
				if (modDependency.getModId().equals("arucas")) {
					return modDependency.getVersionRequirements().toString()
						.replaceAll("\\[=", "")
						.replaceAll("]", "");
				}
			}
		}
		return "unknown";
	}

	public static String getMinecraftVersion() {
		return MinecraftVersion.CURRENT.getName();
	}

	public static void checkIfEssentialClientDirExists() {
		Path configFile = getEssentialConfigFile();
		if (!Files.exists(configFile)) {
			try {
				Files.createDirectory(configFile);
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void checkifScriptFileExists() {
		Path macroDir = ClientScript.getDir();
		Path macroFile = ClientScript.getFile();
		if (!Files.exists(macroDir)) {
			try {
				Files.createDirectory(macroDir);
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (!Files.exists(macroFile)) {
			try {
				Files.createFile(macroFile);
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void tryUpdateClient() {
		sendMessage("Trying to update Essential Client...");
		try {
			String version = getLatestVersionUrl();
			if (version.equals(EssentialClient.VERSION)) {
				sendMessage("You already have the latest version!");
				return;
			}
			FileUtils.copyURLToFile(new URL(
				"https://github.com/senseiwells/EssentialClient/releases/download/v%s/essential-client-%s-%s.jar".formatted(
					version,
					getMinecraftVersion(),
					version
				)
			), FabricLoader.getInstance().getGameDir().resolve("mods").resolve(
				"essential-client-%s-%s.jar".formatted(
					getMinecraftVersion(),
					version
				)
			).toFile(), 1000, 1000);
			sendMessage("Downloaded version %s to mods folder".formatted(version));
			try {
				tryDeleteOldVersion();
			}
			catch (IOException e) {
				sendMessage(new LiteralText("Could not delete the old version, please delete it manually immediately!").formatted(Formatting.RED));
			}
			sendMessage(new LiteralText("[Click here to open mods folder]").formatted(Formatting.GOLD, Formatting.BOLD).styled(style -> style.withClickEvent(
				new ClickEvent(ClickEvent.Action.OPEN_FILE, FabricLoader.getInstance().getGameDir().resolve("mods").toString()))
			));
			sendMessage("Please restart your game!");
		}
		catch (FileNotFoundException e) {
			sendMessage(new LiteralText("An error occurred trying to update, maybe you are using an unsupported version?").formatted(Formatting.RED));
		}
		catch (IOException e) {
			e.printStackTrace();
			sendMessage(new LiteralText("An error occurred trying to update...").formatted(Formatting.RED));
		}
	}

	private static String getLatestVersionUrl() throws IOException{
		int versionIndex = 0;
		String lastWorkingVersion = EssentialClient.VERSION;
		String essentialClientVersion = incrementVersion(lastWorkingVersion, versionIndex);
		while (true) {
			URL url = new URL("https://github.com/senseiwells/EssentialClient/releases/tag/v%s".formatted(essentialClientVersion));
			HttpURLConnection huc = (HttpURLConnection) url.openConnection();
			if (huc.getResponseCode() != HttpURLConnection.HTTP_OK) {
				if (versionIndex == 2) {
					return lastWorkingVersion;
				}
				versionIndex++;
			}
			else {
				lastWorkingVersion = essentialClientVersion;
			}
			essentialClientVersion = incrementVersion(lastWorkingVersion, versionIndex);
		}
	}

	private static String incrementVersion(String input, int versionIndex) throws IOException {
		String[] delimitedInput = input.split("\\.");
		try {
			delimitedInput[versionIndex] = String.valueOf(Integer.parseInt(delimitedInput[versionIndex]) + 1);
			switch (versionIndex) {
				case 0: delimitedInput[1] = "0";
				case 1: delimitedInput[2] = "0";
			}
		}
		catch (NumberFormatException e) {
			throw new IOException("Invalid Version");
		}
		return String.join(".", delimitedInput);
	}

	private static void tryDeleteOldVersion() throws IOException {
		String minecraftVersion = getMinecraftVersion();
		String clientVersion = EssentialClient.VERSION;
		File modDirectory = FabricLoader.getInstance().getGameDir().resolve("mods").toFile();
		if (!modDirectory.isDirectory()) {
			throw new FileNotFoundException("Could not find mod directory");
		}
		File[] files = modDirectory.listFiles((dir, name) -> name.contains("essential-client-%s-%s".formatted(minecraftVersion, clientVersion)));
		if (files == null || files.length != 1) {
			throw new FileNotFoundException();
		}
		FileUtils.forceDelete(files[0]);
	}
}
