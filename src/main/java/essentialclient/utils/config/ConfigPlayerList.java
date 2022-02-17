package essentialclient.utils.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ConfigPlayerList extends MappedConfig<String, List<String>> {
	public static final ConfigPlayerList INSTANCE = new ConfigPlayerList();

	private ConfigPlayerList() { }

	public CompletableFuture<Suggestions> suggestList(SuggestionsBuilder builder) {
		return this.map.isEmpty() ? builder.buildFuture() : CommandSource.suggestMatching(this.map.keySet(), builder);
	}

	@Override
	public String getConfigName() {
		return "PlayerList";
	}

	@Override
	public Path getConfigPath() {
		return this.getConfigRootPath().resolve("PlayerList.json");
	}

	@Override
	protected JsonElement keyToJson(String key) {
		return new JsonPrimitive(key);
	}

	@Override
	protected JsonElement valueToJson(List<String> value) {
		JsonArray playerList = new JsonArray();
		value.forEach(playerList::add);
		return playerList;
	}

	@Override
	protected String jsonToKey(JsonElement keyElement) {
		return keyElement.getAsString();
	}

	@Override
	protected List<String> jsonToValue(JsonElement valueElement) {
		List<String> playerList = new ArrayList<>();
		valueElement.getAsJsonArray().forEach(jsonElement -> {
			playerList.add(jsonElement.getAsString());
		});
		return playerList;
	}

	@Override
	public String getKeyName() {
		return "name";
	}

	@Override
	public String getValueName() {
		return "players";
	}
}
