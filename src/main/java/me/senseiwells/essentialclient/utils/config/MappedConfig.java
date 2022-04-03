package me.senseiwells.essentialclient.utils.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@SuppressWarnings("unused")
public abstract class MappedConfig<K, V> extends AbstractMappedConfig<JsonArray, K, V> implements Config.CList {
	public String getKeyName() {
		return "key";
	}

	public String getValueName() {
		return "value";
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
	public final void readConfig(JsonArray jsonElement) {
		jsonElement.getAsJsonArray().forEach(element -> {
			JsonObject mapObject = element.getAsJsonObject();
			K key = this.jsonToKey(mapObject.get(this.getKeyName()));
			V value = this.jsonToValue(key, mapObject.get(this.getValueName()));
			this.map.put(key, value);
		});
	}
}
