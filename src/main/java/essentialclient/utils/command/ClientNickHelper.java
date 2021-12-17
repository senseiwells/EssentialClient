package essentialclient.utils.command;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import essentialclient.EssentialClient;
import essentialclient.utils.EssentialUtils;
import net.minecraft.command.CommandSource;
import net.minecraft.util.JsonHelper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ClientNickHelper {
	private static Map<String, String> renamePlayerMap = new HashMap<>();

	public static final Codec<String> CODEC = Codec.STRING;
	public static final Codec<Map<String, String>> MAP_CODEC = Codec.unboundedMap(Codec.STRING, CODEC);
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

	public static String getRename(String name) {
		return renamePlayerMap.get(name);
	}

	public static void setRename(String name, String rename) {
		renamePlayerMap.put(name, rename);
	}

	public static String deleteRename(String name) {
		return renamePlayerMap.remove(name);
	}

	public static void writeSaveFile() {
		Path file = getFile();
		try(BufferedWriter writer = Files.newBufferedWriter(file)) {
			MAP_CODEC.encodeStart(JsonOps.INSTANCE, renamePlayerMap)
				.resultOrPartial(e -> EssentialClient.LOGGER.error("Could not write player rename data: {}", e))
				.ifPresent(obj -> GSON.toJson(obj, writer));
		}
		catch (IOException e) {
			e.printStackTrace();
			EssentialClient.LOGGER.error("Failed to save player rename data");
		}
	}

	public static void readSaveFile() {
		Path file = getFile();
		renamePlayerMap = new HashMap<>();
		if (!Files.isRegularFile(file)) {
			return;
		}
		try (BufferedReader reader = Files.newBufferedReader(file)) {
			renamePlayerMap = new HashMap<>(MAP_CODEC.decode(JsonOps.INSTANCE, JsonHelper.deserialize(reader))
				.getOrThrow(false, e -> EssentialClient.LOGGER.error("Could not read player rename data: {}", e))
				.getFirst());
		}
		//many exceptions
		catch (Exception e) {
			try {
				Files.deleteIfExists(file);
				EssentialClient.LOGGER.warn("Removed the outdated/corrupt config file");
			}
			catch (IOException ioException) {
				EssentialClient.LOGGER.error("Something went very wrong, please delete your config file manually");
			}
		}
	}

	public static CompletableFuture<Suggestions> suggestPlayerRename(SuggestionsBuilder builder) {
		if (renamePlayerMap.isEmpty())
			return CommandSource.suggestMatching(new String[]{}, builder);
		return CommandSource.suggestMatching(renamePlayerMap.keySet(), builder);
	}

	private static Path getFile() {
		return EssentialUtils.getEssentialConfigFile().resolve("PlayerRename.json");
	}
}
