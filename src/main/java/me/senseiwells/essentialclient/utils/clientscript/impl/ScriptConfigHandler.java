package me.senseiwells.essentialclient.utils.clientscript.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.senseiwells.arucas.classes.ClassInstance;
import me.senseiwells.arucas.core.Interpreter;
import me.senseiwells.arucas.utils.impl.ArucasList;
import me.senseiwells.essentialclient.clientscript.definitions.ConfigDef;
import me.senseiwells.essentialclient.gui.RulesScreen;
import me.senseiwells.essentialclient.gui.entries.BaseListEntry;
import me.senseiwells.essentialclient.rule.client.*;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import me.senseiwells.essentialclient.utils.config.Config;
import me.senseiwells.essentialclient.utils.interfaces.Rule;
import net.minecraft.text.Text;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class ScriptConfigHandler implements Config.CList {
	private final Map<String, ClassInstance> configs;
	private final Interpreter interpreter;
	private final String name;
	private Path path;
	private boolean save;

	public ScriptConfigHandler(Interpreter interpreter, String name, boolean read) {
		this.configs = new ConcurrentHashMap<>();
		this.interpreter = interpreter;
		this.name = name;

		if (read) {
			this.readConfig();
		}

		interpreter.getThreadHandler().addShutdownEvent(() -> {
			if (this.save) {
				this.saveConfig();
			}
		});
	}

	public RulesScreen createScreen(Text title, boolean alphabetical) {
		return new RulesScreen(title, EssentialUtils.getClient().currentScreen) {
			@Override
			public Collection<? extends Rule<?>> getRules() {
				return ScriptConfigHandler.this.configs.values().stream().map(i -> i.getPrimitive(ConfigDef.class)).toList();
			}

			@Override
			public Comparator<BaseListEntry<?>> entryComparator() {
				return alphabetical ? super.entryComparator() : (a, b) -> 0;
			}

			@Override
			public boolean shouldCategorise() {
				return true;
			}
		};
	}

	public void addConfig(ClassInstance instance) {
		ClientRule<?> rule = instance.getPrimitive(ConfigDef.class);
		if (rule != null) {
			this.configs.put(rule.getName(), instance);
		}
	}

	public void resetAllToDefault() {
		this.forEachConfig(Rule::resetToDefault);
	}

	public void removeConfig(String name) {
		this.configs.remove(name);
	}

	public ClassInstance getConfig(String name) {
		return this.configs.get(name);
	}

	public ArucasList getAllConfigs() {
		return new ArucasList(this.configs.values());
	}

	public void setSaveOnClose(boolean save) {
		this.save = save;
	}

	public boolean willSaveOnClose() {
		return this.save;
	}

	public void setSavePath(Path path) {
		this.path = path;
	}

	@Override
	public String getConfigName() {
		return this.name;
	}

	@Override
	public JsonElement getSaveData() {
		JsonArray array = new JsonArray();
		for (ClassInstance instance : this.configs.values()) {
			array.add(instance.getPrimitive(ConfigDef.class).serialise());
		}
		return array;
	}

	@Override
	public void readConfig(JsonArray array) {
		for (JsonElement element : array) {
			this.parse(element.getAsJsonObject());
		}
	}

	@Override
	public Path getConfigRootPath() {
		return this.path == null ? CList.super.getConfigRootPath() : this.path;
	}

	private void forEachConfig(Consumer<ClientRule<?>> consumer) {
		this.configs.values().forEach(i -> {
			ClientRule<?> rule = i.getPrimitive(ConfigDef.class);
			consumer.accept(rule);
		});
	}

	@SuppressWarnings("unchecked")
	private <T> void parse(JsonObject object) {
		String name = object.get("name").getAsString();
		JsonElement value = object.get("value");

		ClientRule<T> config = (ClientRule<T>) this.configs.get(name).getPrimitive(ConfigDef.class);
		if (config != null) {
			config.setValueQuietly(config.fromJson(value));
			return;
		}

		String type = object.get("type").getAsString();

		JsonElement objectElement = object.get("description");
		String description = objectElement != null && objectElement.isJsonPrimitive() ? objectElement.getAsString() : null;

		objectElement = object.get("optional_info");
		String optionalInfo = objectElement != null && objectElement.isJsonPrimitive() ? objectElement.getAsString() : null;

		objectElement = object.get("max_length");
		int maxLength = objectElement != null && objectElement.isJsonPrimitive() ? objectElement.getAsInt() : 32;

		objectElement = object.get("category");
		String category = objectElement != null && objectElement.isJsonPrimitive() ? objectElement.getAsString() : null;

		JsonElement defaultValue = object.get("default_value");
		ClientRule<T> rule = (ClientRule<T>) switch (type) {
			case "boolean" -> new BooleanClientRule(name, description, defaultValue != null && defaultValue.getAsBoolean(), category);
			case "cycle" -> {
				JsonArray cycleValues = object.getAsJsonArray("cycle_values");

				List<String> cycles = new ArrayList<>();
				for (JsonElement element : cycleValues) {
					cycles.add(element.getAsString());
				}

				if (defaultValue != null) {
					yield new CycleClientRule(name, description, cycles, defaultValue.getAsString(), category, null);
				}
				yield new CycleClientRule(name, description, cycles, category);
			}
			case "double" -> new DoubleClientRule(name, description, defaultValue == null ? 0.0D : defaultValue.getAsDouble(), category);
			case "double_slider" -> {
				double min = object.get("min").getAsDouble();
				double max = object.get("max").getAsDouble();
				yield new DoubleSliderClientRule(name, description, defaultValue == null ? 0.0D : defaultValue.getAsDouble(), category, min, max);
			}
			case "integer" -> new IntegerClientRule(name, description, defaultValue == null ? 0 : defaultValue.getAsInt(), category);
			case "integer_slider" -> {
				int min = object.get("min").getAsInt();
				int max = object.get("max").getAsInt();
				yield new IntegerSliderClientRule(name, description, defaultValue == null ? 0 : defaultValue.getAsInt(), category, min, max);
			}
			case "list" -> {
				List<String> configData = new ArrayList<>();
				if (defaultValue != null && defaultValue.isJsonArray()) {
					for (JsonElement element : defaultValue.getAsJsonArray()) {
						configData.add(element.getAsString());
					}
				}
				ListClientRule listClientRule = new ListClientRule(name, description, configData, category);
				listClientRule.setMaxLength(maxLength);
				yield listClientRule;
			}
			case "string" -> {
				StringClientRule stringClientRule = new StringClientRule(name, description, defaultValue == null ? "" : defaultValue.getAsString(), category);
				stringClientRule.setMaxLength(maxLength);
				yield stringClientRule;
			}
			default -> throw new IllegalArgumentException("Invalid config type '%s'".formatted(type));
		};

		if (value != null) {
			rule.setValueQuietly(rule.fromJson(value));
		}

		rule.setOptionalInfo(optionalInfo);

		this.configs.put(name, this.interpreter.create(ConfigDef.class, rule));
	}
}
