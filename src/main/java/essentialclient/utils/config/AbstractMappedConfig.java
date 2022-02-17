package essentialclient.utils.config;

import com.google.gson.JsonElement;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class AbstractMappedConfig<T extends JsonElement, K, V> implements Config<T> {
	protected final Map<K, V> map;

	public AbstractMappedConfig() {
		this.map = new LinkedHashMap<>();
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

	protected abstract JsonElement keyToJson(K key);

	protected abstract JsonElement valueToJson(V value);

	protected abstract K jsonToKey(JsonElement keyElement);

	protected abstract V jsonToValue(K key, JsonElement valueElement);

	@Override
	public final void readConfig() {
		Config.super.readConfig();
	}

	@Override
	public final Path getConfigRootPath() {
		return Config.super.getConfigRootPath();
	}

	@Override
	public final T getConfigData() {
		return Config.super.getConfigData();
	}

	@Override
	public final void saveConfig() {
		Config.super.saveConfig();
	}
}
