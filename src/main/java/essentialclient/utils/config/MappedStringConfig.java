package essentialclient.utils.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public abstract class MappedStringConfig<V> extends AbstractMappedConfig<JsonObject, String, V> implements Config.CMap {
	@Override
	protected final JsonElement keyToJson(String key) {
		throw new UnsupportedOperationException("Key should not be converted to json");
	}

	@Override
	protected final String jsonToKey(JsonElement keyElement) {
		throw new UnsupportedOperationException("Key should not be converted from json");
	}

	@Override
	public final JsonObject getSaveData() {
		JsonObject jsonObject = new JsonObject();
		this.map.forEach((s, v) -> jsonObject.add(s, this.valueToJson(v)));
		return jsonObject;
	}

	@Override
	public final void readConfig(JsonObject element) {
		element.entrySet().forEach(entry -> {
			V value = this.jsonToValue(entry.getKey(), entry.getValue());
			if (value != null) {
				this.map.put(entry.getKey(), value);
			}
		});
	}
}
