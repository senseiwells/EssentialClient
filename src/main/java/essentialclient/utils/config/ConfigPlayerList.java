package essentialclient.utils.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ConfigPlayerList extends MappedStringConfig<List<String>> {
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
	protected JsonElement valueToJson(List<String> value) {
		JsonArray playerList = new JsonArray();
		value.forEach(playerList::add);
		return playerList;
	}

	@Override
	protected List<String> jsonToValue(String key, JsonElement valueElement) {
		List<String> playerList = new ArrayList<>();
		valueElement.getAsJsonArray().forEach(jsonElement -> {
			playerList.add(jsonElement.getAsString());
		});
		return playerList;
	}
}
