package me.senseiwells.essentialclient.clientscript.extensions;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.senseiwells.arucas.api.docs.ClassDoc;
import me.senseiwells.arucas.api.docs.ConstructorDoc;
import me.senseiwells.arucas.api.docs.FunctionDoc;
import me.senseiwells.arucas.api.wrappers.ArucasClass;
import me.senseiwells.arucas.api.wrappers.ArucasConstructor;
import me.senseiwells.arucas.api.wrappers.ArucasFunction;
import me.senseiwells.arucas.api.wrappers.IArucasWrappedClass;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.ExceptionUtils;
import me.senseiwells.arucas.utils.impl.ArucasList;
import me.senseiwells.arucas.values.*;
import me.senseiwells.essentialclient.clientscript.values.ConfigValue;
import me.senseiwells.essentialclient.clientscript.values.TextValue;
import me.senseiwells.essentialclient.gui.RulesScreen;
import me.senseiwells.essentialclient.rule.client.*;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import me.senseiwells.essentialclient.utils.config.Config;
import me.senseiwells.essentialclient.utils.interfaces.Rule;

import java.nio.file.Path;
import java.util.*;

import static me.senseiwells.essentialclient.clientscript.core.MinecraftAPI.*;

@SuppressWarnings("unused")
@ClassDoc(
	name = CONFIG_HANDLER,
	desc = "This class allows you to easily read and write config files.",
	importPath = "Minecraft"
)
@ArucasClass(name = CONFIG_HANDLER)
public class ConfigHandlerWrapper implements IArucasWrappedClass, Config.CList {
	private Map<String, ConfigValue> configs;
	private Path savePath;
	private String name;
	private boolean saveOnClose;

	@ConstructorDoc(
		desc = "Creates a new ConfigHandler, this is used to read and save configs",
		params = {
			STRING, "name", "The name of the config, this will also be the name of the config file",
			BOOLEAN, "read", "Whether or not to read the config on creation",
		},
		example = "new ConfigHandler('MyConfig', false);"
	)
	@ArucasConstructor
	public synchronized void constructor(Context context, StringValue name, BooleanValue read) {
		this.configs = new HashMap<>();
		this.name = name.value;
		this.saveOnClose = true;
		if (read.value) {
			this.readConfig();
		}

		context.getThreadHandler().addShutdownEvent(() -> {
			if (this.saveOnClose) {
				this.saveConfig();
			}
		});
	}

	@ConstructorDoc(
		desc = "Creates a new ConfigHandler, this is used to read and save configs",
		params = {STRING, "name", "The name of the config, this will also be the name of the config file"},
		example = "new ConfigHandler('MyConfig');"
	)
	@ArucasConstructor
	public synchronized void constructor(Context context, StringValue name) {
		this.constructor(context, name, BooleanValue.TRUE);
	}

	@FunctionDoc(
		name = "getName",
		desc = "Gets the name of the config",
		returns = {STRING, "The name of the config"},
		example = "configHandler.getName();"
	)
	@ArucasFunction
	public synchronized Value getName(Context context) {
		return StringValue.of(this.name);
	}

	@FunctionDoc(
		name = "read",
		desc = {
			"Reads the all the configs from the file",
			"If configs are already in the handler, only the values",
			"will be overwritten"
		},
		example = "configHandler.read();"
	)
	@ArucasFunction
	public synchronized void read(Context context) {
		Throwable throwable = ExceptionUtils.returnThrowable(this::readConfig);
		if (throwable != null) {
			throw new RuntimeException("Failed to read config: " + this.name, throwable);
		}
	}

	@FunctionDoc(
		name = "save",
		desc = "Saves the configs to the file",
		example = "configHandler.save();"
	)
	@ArucasFunction
	public synchronized void save(Context context) {
		this.saveConfig();
	}

	@FunctionDoc(
		name = "setSaveOnClose",
		desc = "Sets whether or not the configs should be saved when the script ends, by default this is true",
		params = {BOOLEAN, "saveOnClose", "Whether or not the configs should be saved when the script ends"},
		example = "configHandler.setSaveOnClose(false);"
	)
	@ArucasFunction
	public synchronized void setSaveOnClose(Context context, BooleanValue value) {
		this.saveOnClose = value.value;
	}

	@FunctionDoc(
		name = "willSaveOnClose",
		desc = "Gets whether or not the configs will be saved when the script ends",
		returns = {BOOLEAN, "Whether or not the configs will be saved when the script ends"},
		example = "configHandler.willSaveOnClose();"
	)
	@ArucasFunction
	public synchronized Value willSaveOnClose(Context context) {
		return BooleanValue.of(this.saveOnClose);
	}

	@FunctionDoc(
		name = "setSavePath",
		desc = "Sets the path to save the configs to, this shouldn't include the file name",
		params = {FILE, "savePath", "The path to save the configs to"},
		example = "configHandler.setSavePath(new File('/home/user/scripts/'));"
	)
	@ArucasFunction
	public void setSavePath(Context context, FileValue fileValue) {
		this.savePath = fileValue.value.toPath();
	}

	@FunctionDoc(
		name = "addConfig",
		desc = "Adds a config to the handler",
		params = {CONFIG, "config", "The config to add"},
		example = """
			config = Config.fromMap({
			    "type": "boolean",
			    "name": "My Config",
			    "description": "This is my config"
			});
			configHandler.addConfig(config);
			"""
	)
	@ArucasFunction
	public synchronized void addConfig(Context context, ConfigValue configValue) {
		this.configs.put(configValue.value.getName(), configValue);
	}

	@FunctionDoc(
		name = "addConfigs",
		desc = "Adds multiple configs to the handler",
		params = {LIST, "configs", "The configs to add"},
		example = """
			config = Config.fromMap({
			    "type": "boolean",
			    "name": "My Config",
			    "description": "This is my config"
			});
			configHandler.addConfigs([config]);
			"""
	)
	@ArucasFunction
	public synchronized void addConfigs(Context context, ListValue listValue) {
		for (Value value : listValue.value) {
			if (!(value instanceof ConfigValue configValue)) {
				throw new RuntimeException("List must contain only Configs");
			}
			this.addConfig(context, configValue);
		}
	}

	@FunctionDoc(
		name = "getConfig",
		desc = "Gets a config from the handler",
		params = {STRING, "name", "The name of the config"},
		returns = {CONFIG, "The config"},
		example = "configHandler.getConfig('MyConfig');"
	)
	@ArucasFunction
	public synchronized Value getConfig(Context context, StringValue name) {
		ConfigValue configValue = this.configs.get(name.value);
		return configValue == null ? NullValue.NULL : configValue;
	}

	@FunctionDoc(
		name = "removeConfig",
		desc = "Removes a config from the handler",
		params = {STRING, "name", "The name of the config to remove"},
		example = "configHandler.removeConfig('My Config');"
	)
	@ArucasFunction
	public synchronized Value removeConfig(Context context, StringValue name) {
		return this.configs.remove(name.value);
	}

	@FunctionDoc(
		name = "resetAllToDefault",
		desc = "Resets all configs to their default values",
		example = "configHandler.resetAllToDefault();"
	)
	@ArucasFunction
	public synchronized void resetAllToDefault(Context context) {
		for (ConfigValue config : this.configs.values()) {
			config.value.resetToDefault();
		}
	}

	@FunctionDoc(
		name = "getAllConfigs",
		desc = "Gets all the configs in the handler",
		returns = {LIST, "All the configs in the handler"},
		example = "configHandler.getAllConfigs();"
	)
	@ArucasFunction
	public synchronized Value getAllConfigs(Context context) {
		ArucasList list = new ArucasList();
		if (!this.configs.isEmpty()) {
			list.addAll(this.configs.values());
		}
		return new ListValue(list);
	}

	@FunctionDoc(
		name = "createScreen",
		desc = "Creates a new config screen containing all of the configs in the handler",
		params = {
			TEXT, "title", "The title of the screen",
			BOOLEAN, "alphabetical", "Whether or not to sort the configs alphabetically"
		},
		returns = {SCREEN, "The new config screen"},
		example = "configHandler.createScreen();"
	)
	@ArucasFunction
	public synchronized Value createScreen(Context context, TextValue screenName, BooleanValue alphabetical) throws CodeError {
		RulesScreen screen = new RulesScreen(screenName.value, EssentialUtils.getClient().currentScreen, () -> true, () -> {
			Collection<? extends Rule<?>> rules = this.configs.values().stream().map(c -> c.value).toList();
			if (alphabetical.value) {
				rules = Rule.sortRulesAlphabetically(rules);
			}
			return rules;
		});
		return context.convertValue(screen);
	}

	@FunctionDoc(
		name = "createScreen",
		desc = "Creates a new config screen containing all of the configs in the handler, in alphabetical order",
		params = {TEXT, "title", "The title of the screen"},
		returns = {SCREEN, "The new config screen"},
		example = "configHandler.createScreen();"
	)
	@ArucasFunction
	public synchronized Value createScreen(Context context, TextValue screenName) throws CodeError {
		return this.createScreen(context, screenName, BooleanValue.TRUE);
	}

	@Override
	public String getConfigName() {
		return this.name;
	}

	@Override
	public JsonElement getSaveData() {
		JsonArray array = new JsonArray();
		for (ConfigValue config : this.configs.values()) {
			array.add(config.value.serialise());
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
	public Path getConfigPath() {
		return this.savePath != null ? this.savePath.resolve(this.name + ".json") : CList.super.getConfigPath();
	}

	@SuppressWarnings("unchecked")
	private <T> void parse(JsonObject object) {
		String name = object.get("name").getAsString();
		JsonElement value = object.get("value");

		ConfigValue config = this.configs.get(name);
		if (config != null) {
			ClientRule<T> rule = (ClientRule<T>) config.value;
			rule.setValueQuietly(rule.fromJson(value));
			return;
		}

		String type = object.get("type").getAsString();

		JsonElement objectElement = object.get("description");
		String description = objectElement != null && objectElement.isJsonPrimitive() ? objectElement.getAsString() : null;

		objectElement = object.get("optional_info");
		String optionalInfo = objectElement != null && objectElement.isJsonPrimitive() ? objectElement.getAsString() : null;

		objectElement = object.get("max_length");
		int maxLength = objectElement != null && objectElement.isJsonPrimitive() ? objectElement.getAsInt() : 32;

		JsonElement defaultValue = object.get("default_value");
		ClientRule<T> rule = (ClientRule<T>) switch (type) {
			case "boolean" -> new BooleanClientRule(name, description, defaultValue != null && defaultValue.getAsBoolean());
			case "cycle" -> {
				JsonArray cycleValues = object.getAsJsonArray("cycle_values");

				List<String> cycles = new ArrayList<>();
				for (JsonElement element : cycleValues) {
					cycles.add(element.getAsString());
				}

				if (defaultValue != null) {
					yield new CycleClientRule(name, description, cycles, defaultValue.getAsString(), null);
				}
				yield new CycleClientRule(name, description, cycles);
			}
			case "double" -> new DoubleClientRule(name, description, defaultValue == null ? 0.0D : defaultValue.getAsDouble());
			case "double_slider" -> {
				double min = object.get("min").getAsDouble();
				double max = object.get("max").getAsDouble();
				yield new DoubleSliderClientRule(name, description, defaultValue == null ? 0.0D : defaultValue.getAsDouble(), min, max);
			}
			case "integer" -> new IntegerClientRule(name, description, defaultValue == null ? 0 : defaultValue.getAsInt());
			case "integer_slider" -> {
				int min = object.get("min").getAsInt();
				int max = object.get("max").getAsInt();
				yield new IntegerSliderClientRule(name, description, defaultValue == null ? 0 : defaultValue.getAsInt(), min, max);
			}
			case "list" -> {
				List<String> configData = new ArrayList<>();
				if (defaultValue != null && defaultValue.isJsonArray()) {
					for (JsonElement element : defaultValue.getAsJsonArray()) {
						configData.add(element.getAsString());
					}
				}
				ListClientRule listClientRule = new ListClientRule(name, description, configData);
				listClientRule.setMaxLength(maxLength);
				yield listClientRule;
			}
			case "string" -> {
				StringClientRule stringClientRule = new StringClientRule(name, description, defaultValue == null ? "" : defaultValue.getAsString());
				stringClientRule.setMaxLength(maxLength);
				yield stringClientRule;
			}
			default -> throw new RuntimeException("Invalid config type '%s'".formatted(type));
		};

		if (value != null) {
			rule.setValueQuietly(rule.fromJson(value));
		}

		rule.setOptionalInfo(optionalInfo);

		this.configs.put(name, new ConfigValue(rule));
	}
}
