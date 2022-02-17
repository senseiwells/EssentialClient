package essentialclient.utils.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public class ConfigClientNick extends MappedConfig<String, String> {
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
	public Path getConfigPath() {
		return this.getConfigRootPath().resolve("ClientNick.json");
	}

	@Override
	protected JsonElement keyToJson(String key) {
		return new JsonPrimitive(key);
	}

	@Override
	protected JsonElement valueToJson(String value) {
		return new JsonPrimitive(value);
	}

	@Override
	protected String jsonToKey(JsonElement keyElement) {
		return keyElement.getAsString();
	}

	@Override
	protected String jsonToValue(JsonElement valueElement) {
		return valueElement.getAsString();
	}

	@Override
	public String getKeyName() {
		return "name";
	}

	@Override
	public String getValueName() {
		return "rename";
	}
}
