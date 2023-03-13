package me.senseiwells.essentialclient.clientscript.definitions;

import com.google.gson.JsonElement;
import me.senseiwells.arucas.api.docs.annotations.ClassDoc;
import me.senseiwells.arucas.api.docs.annotations.FunctionDoc;
import me.senseiwells.arucas.api.docs.annotations.ParameterDoc;
import me.senseiwells.arucas.api.docs.annotations.ReturnDoc;
import me.senseiwells.arucas.builtin.*;
import me.senseiwells.arucas.classes.CreatableDefinition;
import me.senseiwells.arucas.classes.instance.ClassInstance;
import me.senseiwells.arucas.compiler.LocatableTrace;
import me.senseiwells.arucas.exceptions.RuntimeError;
import me.senseiwells.arucas.extensions.JsonDef;
import me.senseiwells.arucas.functions.ArucasFunction;
import me.senseiwells.arucas.functions.builtin.Arguments;
import me.senseiwells.arucas.functions.builtin.BuiltInFunction;
import me.senseiwells.arucas.functions.builtin.MemberFunction;
import me.senseiwells.arucas.interpreter.Interpreter;
import me.senseiwells.arucas.utils.impl.ArucasList;
import me.senseiwells.arucas.utils.impl.ArucasMap;
import me.senseiwells.arucas.utils.misc.Language;
import me.senseiwells.essentialclient.clientscript.core.MinecraftAPI;
import me.senseiwells.essentialclient.rule.client.ClientRule;
import me.senseiwells.essentialclient.utils.clientscript.ClientScriptUtils;
import me.senseiwells.essentialclient.utils.interfaces.Rule;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static me.senseiwells.essentialclient.clientscript.core.MinecraftAPI.CONFIG;

@ClassDoc(
	name = CONFIG,
	desc = "This class allows you to create configs for your scripts",
	language = Language.Java
)
public class ConfigDef extends CreatableDefinition<ClientRule<?>> {
	public ConfigDef(Interpreter interpreter) {
		super(MinecraftAPI.CONFIG, interpreter);
	}

	@NotNull
	@Override
	public String toString(@NotNull ClassInstance instance, @NotNull Interpreter interpreter, @NotNull LocatableTrace trace) {
		ClientRule<?> rule = instance.asPrimitive(this);
		return "Config{name=" + rule.getName() + ", value=" + rule.getValue() + "}";
	}

	@Override
	public List<BuiltInFunction> defineStaticMethods() {
		return List.of(
			BuiltInFunction.of("fromMap", 1, this::fromMap),
			BuiltInFunction.of("fromListOfMap", 1, this::fromListOfMap)
		);
	}

	@FunctionDoc(
		isStatic = true,
		name = "fromMap",
		desc = {
			"Creates a config from a map",
			"The map must contain the following keys:",
			"'type' which is the type of the config which can be 'boolean', 'cycle', 'double', 'double_slider', 'integer', 'integer_slider', 'list', or 'string',",
			"'name' which is the name of the config",
			"And can optionally contain the following keys:",
			"'description' which is a description of the config,",
			"'optional_info' which is an optional info for the config,",
			"'default_value' which is the default value of the config,",
			"'category' which is the category of the config,",
			"'value' which is the current value of the config, ",
			"'listener' which is a function that will be called when the config changes, this must have 1 parameter which is the rule that was changed,",
			"'max_length' which is the max length for the input of the config, this must be an integer > 0, default is 32",
			"And 'cycle' types must contain the following keys:",
			"'cycle_values' which is a list of values that the config can cycle through.",
			"And slider types must contain the following keys:",
			"'min' which is the minimum value of the slider,",
			"'max' which is the maximum value of the slider"
		},
		params = {@ParameterDoc(type = MapDef.class, name = "map", desc = "The map to create the config from")},
		returns = @ReturnDoc(type = ConfigDef.class, desc = "The config created from the map"),
		examples = """
			configMap = {
				"type": "string",
				"name": "My Config",
				"description": "This is my config",
				"category": "Useful",
				"optional_info": "This is an optional info",
				"default_value": "foo",
				"value": "bar",
				"listener": fun(newValue) { },
				"max_length": 64
			};
			config = Config.fromMap(configMap);
			"""
	)
	private ClassInstance fromMap(Arguments arguments) {
		ArucasMap map = arguments.nextPrimitive(MapDef.class);
		return ClientScriptUtils.mapToRule(map, arguments.getInterpreter());
	}

	@FunctionDoc(
		isStatic = true,
		name = "fromListOfMap",
		desc = "Creates a config from a list of config maps",
		params = {@ParameterDoc(type = ListDef.class, name = "list", desc = "The list of config maps")},
		returns = @ReturnDoc(type = ListDef.class, desc = "A list of configs created from the list of config maps"),
		examples = """
			configs = [
				{
					"type": "boolean",
					"name": "My Config",
					"description": "This is my config"
				},
				{
					"type": "cycle",
					"name": "My Cycle Config",
					"description": "This is my cycle config",
					"cycle_values": ["one", "two", "three"],
					"default_value": "two"
				}
			];
			configs = Config.fromListOfMap(configs);
			"""
	)
	private ArucasList fromListOfMap(Arguments arguments) {
		ArucasList list = arguments.nextPrimitive(ListDef.class);
		ArucasList configList = new ArucasList();
		for (ClassInstance value : list.toArray()) {
			ArucasMap configMap = value.getPrimitive(MapDef.class);
			if (configMap == null) {
				throw new RuntimeError("List must only contains maps");
			}
			configList.add(ClientScriptUtils.mapToRule(configMap, arguments.getInterpreter()));
		}
		return configList;
	}

	@Override
	public List<MemberFunction> defineMethods() {
		return List.of(
			MemberFunction.of("getName", this::getName),
			MemberFunction.of("getType", this::getType),
			MemberFunction.of("getDescription", this::getDescription),
			MemberFunction.of("getCategory", this::getCategory),
			MemberFunction.of("getOptionalInfo", this::getOptionalInfo),
			MemberFunction.of("getDefaultValue", this::getDefaultValue),
			MemberFunction.of("getValue", this::getValue),
			MemberFunction.of("toJson", this::toJson),
			MemberFunction.of("resetToDefault", this::resetToDefault),
			MemberFunction.of("addListener", 1, this::addListener),
			MemberFunction.of("setValue", 1, this::setValue)
		);
	}

	@FunctionDoc(
		name = "getName",
		desc = "Gets the name of the config",
		returns = @ReturnDoc(type = StringDef.class, desc = "The name of the config"),
		examples = "config.getName();"
	)
	private String getName(Arguments arguments) {
		return arguments.nextPrimitive(this).getName();
	}

	@FunctionDoc(
		name = "getType",
		desc = "Gets the type of the config",
		returns = @ReturnDoc(type = StringDef.class, desc = "The type of the config"),
		examples = "config.getType();"
	)
	private String getType(Arguments arguments) {
		return arguments.nextPrimitive(this).getTypeAsString();
	}

	@FunctionDoc(
		name = "getDescription",
		desc = "Gets the description of the config",
		returns = @ReturnDoc(type = StringDef.class, desc = "The description of the config"),
		examples = "config.getDescription();"
	)
	private String getDescription(Arguments arguments) {
		return arguments.nextPrimitive(this).getDescription();
	}

	@FunctionDoc(
		name = "getCategory",
		desc = "Gets the category of the config",
		returns = @ReturnDoc(type = StringDef.class, desc = "The category of the config"),
		examples = "config.getCategory();"
	)
	private String getCategory(Arguments arguments) {
		return arguments.nextPrimitive(this).getCategory();
	}

	@FunctionDoc(
		name = "getOptionalInfo",
		desc = "Gets the optional info of the config",
		returns = @ReturnDoc(type = StringDef.class, desc = "The optional info of the config"),
		examples = "config.getOptionalInfo();"
	)
	private String getOptionalInfo(Arguments arguments) {
		return arguments.nextPrimitive(this).getOptionalInfo();
	}

	@FunctionDoc(
		name = "getDefaultValue",
		desc = "Gets the default value of the config",
		returns = @ReturnDoc(type = ObjectDef.class, desc = "The default value of the config"),
		examples = "config.getDefaultValue();"
	)
	private Object getDefaultValue(Arguments arguments) {
		return arguments.nextPrimitive(this).getDefaultValue();
	}

	@FunctionDoc(
		name = "getValue",
		desc = "Gets the value of the config",
		returns = @ReturnDoc(type = ObjectDef.class, desc = "The value of the config"),
		examples = "config.getValue();"
	)
	private Object getValue(Arguments arguments) {
		return arguments.nextPrimitive(this).getValue();
	}

	@FunctionDoc(
		name = "toJson",
		desc = "Converts the config into a json value, this will not keep the listeners",
		returns = @ReturnDoc(type = JsonDef.class, desc = "The config as a json value"),
		examples = "config.toJson();"
	)
	private JsonElement toJson(Arguments arguments) {
		return arguments.nextPrimitive(this).serialise();
	}

	@FunctionDoc(
		name = "addListener",
		desc = {
			"Adds a listener to the config, the listener will be called when the config is changed",
			"The listener must have one parameter, this is the new value that was set"
		},
		params = {@ParameterDoc(type = FunctionDef.class, name = "listener", desc = "The listener to add")},
		examples = """
			config.addListener(function(newValue) {
				print(newValue);
			});
			"""
	)
	private Void addListener(Arguments arguments) {
		ClientRule<?> rule = arguments.nextPrimitive(this);
		ArucasFunction listener = arguments.nextPrimitive(FunctionDef.class);
		Interpreter interpreter = arguments.getInterpreter().branch();
		rule.addListener(object -> {
			Interpreter branch = interpreter.branch();
			listener.invoke(branch, List.of(branch.convertValue(object)));
		});
		return null;
	}

	@FunctionDoc(
		name = "resetToDefault",
		desc = "Resets the config to the default value",
		examples = "config.resetToDefault();"
	)
	private Void resetToDefault(Arguments arguments) {
		arguments.nextPrimitive(this).resetToDefault();
		return null;
	}

	@FunctionDoc(
		name = "setValue",
		desc = {
			"Sets the value of the config, if the value is invalid it will not be changed",
			"If you are modifying a list rule you must pass in a list to this method"
		},
		params = {@ParameterDoc(type = ObjectDef.class, name = "value", desc = "The new value of the config")},
		examples = "config.setValue(10);"
	)
	private Void setValue(Arguments arguments) {
		ClientRule<?> rule = arguments.nextPrimitive(this);
		if (rule instanceof Rule.ListRule listRule) {
			List<String> values = arguments.nextPrimitive(ListDef.class).stream()
				.map(e -> e.toString(arguments.getInterpreter()))
				.toList();
			listRule.setValue(values);
			return null;
		}
		ClassInstance value = arguments.next();
		rule.setValueFromString(value.toString(arguments.getInterpreter()));
		return null;
	}
}
