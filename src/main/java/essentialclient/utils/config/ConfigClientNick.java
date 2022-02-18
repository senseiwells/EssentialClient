package essentialclient.utils.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;

import java.util.concurrent.CompletableFuture;

public class ConfigClientNick extends MappedStringConfig<String> {
	public static final ConfigClientNick INSTANCE = new ConfigClientNick();

	private ConfigClientNick() { }

	public CompletableFuture<Suggestions> suggestPlayerRename(SuggestionsBuilder builder) {
		return this.map.isEmpty() ? builder.buildFuture() : CommandSource.suggestMatching(this.map.keySet(), builder);
	}

	@Override
	public String getConfigName() {
		return "ClientNick";
	}

	@Override
	protected JsonElement valueToJson(String value) {
		return new JsonPrimitive(value);
	}

	@Override
	protected String jsonToValue(String key, JsonElement valueElement) {
		return valueElement.getAsString();
	}
}
