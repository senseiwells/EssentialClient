package essentialclient.utils.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public abstract class MappedConfig<K, V> implements Config {
	protected final Map<K, V> map;

	public MappedConfig() {
		this.map = new HashMap<>();
	}

	protected abstract JsonElement keyToJson(K key);

	protected abstract JsonElement valueToJson(V value);

	protected abstract K jsonToKey(JsonElement keyElement);

	protected abstract V jsonToValue(JsonElement valueElement);

	public String getKeyName() {
		return "key";
	}

	public String getValueName() {
		return "value";
	}

	public V get(K key) {
		return this.map.get(key);
	}

	public void set(K key, V value) {
		this.map.put(key, value);
	}

	public V remove(K key) {
		return this.map.remove(key);
	}

	@Override
	public final JsonArray getSaveData() {
		JsonArray configArray = new JsonArray();
		this.map.forEach((k, v) -> {
			JsonObject mapObject = new JsonObject();
			mapObject.add(this.getKeyName(), this.keyToJson(k));
			mapObject.add(this.getValueName(), this.valueToJson(v));
			configArray.add(mapObject);
		});
		return configArray;
	}

	@Override
	public final void readConfig(JsonArray jsonArray) {
		jsonArray.forEach(jsonElement -> {
			JsonObject mapObject = jsonElement.getAsJsonObject();
			K key = this.jsonToKey(mapObject.get(this.getKeyName()));
			V value = this.jsonToValue(mapObject.get(this.getValueName()));
			this.map.put(key, value);
		});
	}

	@Override
	public final void readConfig() {
		Config.super.readConfig();
	}

	@Override
	public final Path getConfigRootPath() {
		return Config.super.getConfigRootPath();
	}

	@Override
	public final JsonArray getConfigData() {
		return Config.super.getConfigData();
	}

	@Override
	public final void saveConfig() {
		Config.super.saveConfig();
	}
}
