package me.senseiwells.essentialclient.clientscript.values;

import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.api.docs.ClassDoc;
import me.senseiwells.arucas.api.docs.FunctionDoc;
import me.senseiwells.arucas.extensions.util.JsonValue;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.Arguments;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.impl.ArucasList;
import me.senseiwells.arucas.utils.impl.ArucasMap;
import me.senseiwells.arucas.values.*;
import me.senseiwells.arucas.values.functions.BuiltInFunction;
import me.senseiwells.arucas.values.functions.FunctionValue;
import me.senseiwells.arucas.values.functions.MemberFunction;
import me.senseiwells.essentialclient.rule.client.ClientRule;
import me.senseiwells.essentialclient.utils.clientscript.ClientScriptUtils;

import static me.senseiwells.essentialclient.clientscript.core.MinecraftAPI.*;

public class ConfigValue extends GenericValue<ClientRule<?>> {
	public ConfigValue(ClientRule<?> value) {
		super(value);
	}

	@Override
	public GenericValue<ClientRule<?>> copy(Context context) throws CodeError {
		return this;
	}

	@Override
	public String getAsString(Context context) throws CodeError {
		return "Config{name=" + this.value.getName() + ", value=" + this.value.getValue() + "}";
	}

	@Override
	public int getHashCode(Context context) throws CodeError {
		return this.value.hashCode();
	}

	@Override
	public boolean isEquals(Context context, Value other) throws CodeError {
		return this.value == other.getValue();
	}

	@Override
	public String getTypeName() {
		return CONFIG;
	}

	@ClassDoc(
		name = CONFIG,
		desc = "This class allows you to create configs for your scripts",
		importPath = "Minecraft"
	)
	public static class ArucasConfigValue extends ArucasClassExtension {
		public ArucasConfigValue() {
			super(CONFIG);
		}

		@Override
		public Class<? extends Value> getValueClass() {
			return ConfigValue.class;
		}

		@Override
		public ArucasFunctionMap<BuiltInFunction> getDefinedStaticMethods() {
			return ArucasFunctionMap.of(
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
				"'value' which is the current value of the config, ",
				"'listener' which is a function that will be called when the config changes, this must have 1 parameter which is the rule that was changed,",
				"'max_length' which is the max length for the input of the config, this must be an integer > 0, default is 32",
				"And 'cycle' types must contain the following keys:",
				"'cycle_values' which is a list of values that the config can cycle through.",
				"And slider types must contain the following keys:",
				"'min' which is the minimum value of the slider,",
				"'max' which is the maximum value of the slider",
			},
			params = {MAP, "map", "The map to create the config from"},
			returns = {CONFIG, "The config created from the map"},
			throwMsgs = {
				"Config map must contain a type that is a string",
				"Config map must contain a name that is a string",
				"'cycle' type must have 'cycle_values' as a list",
				"... type must have 'min' as a number",
				"... type must have 'max' as a number",
				"Invalid config type ..."
			},
			example = """
				configMap = {
					"type": "string",
					"name": "My Config",
					"description": "This is my config",
					"optional_info": "This is an optional info",
					"default_value": "foo",
					"value": "bar",
					"listener": fun(newValue) { },
					"max_length": 64
				};
				config = Config.fromMap(configMap);
				"""
		)
		private Value fromMap(Arguments arguments) throws CodeError {
			ArucasMap map = arguments.getNextGeneric(MapValue.class);
			return ClientScriptUtils.mapToRule(map, arguments.getContext(), arguments.getPosition());
		}

		@FunctionDoc(
			isStatic = true,
			name = "fromListOfMap",
			desc = "Creates a config from a list of config maps",
			params = {LIST, "list", "The list of config maps"},
			returns = {LIST, "A list of configs created from the list of config maps"},
			example = """
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
		private Value fromListOfMap(Arguments arguments) throws CodeError {
			ArucasList list = arguments.getNextGeneric(ListValue.class);
			ArucasList configList = new ArucasList();
			for (Value value : list.toArray()) {
				if (!(value instanceof MapValue map)) {
					throw arguments.getError("List must only contains maps");
				}
				configList.add(ClientScriptUtils.mapToRule(map.value, arguments.getContext(), arguments.getPosition()));
			}
			return new ListValue(configList);
		}

		@Override
		public ArucasFunctionMap<MemberFunction> getDefinedMethods() {
			return ArucasFunctionMap.of(
				MemberFunction.of("getName", this::getName),
				MemberFunction.of("getType", this::getType),
				MemberFunction.of("getDescription", this::getDescription),
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
			returns = {STRING, "The name of the config"},
			example = "config.getName();"
		)
		private Value getName(Arguments arguments) throws RuntimeError {
			ClientRule<?> rule = arguments.getNextGeneric(ConfigValue.class);
			return StringValue.of(rule.getName());
		}

		@FunctionDoc(
			name = "getType",
			desc = "Gets the type of the config",
			returns = {STRING, "The type of the config"},
			example = "config.getType();"
		)
		private Value getType(Arguments arguments) throws RuntimeError {
			ClientRule<?> rule = arguments.getNextGeneric(ConfigValue.class);
			return StringValue.of(rule.getTypeAsString());
		}

		@FunctionDoc(
			name = "getDescription",
			desc = "Gets the description of the config",
			returns = {STRING, "The description of the config"},
			example = "config.getDescription();"
		)
		private Value getDescription(Arguments arguments) throws RuntimeError {
			ClientRule<?> rule = arguments.getNextGeneric(ConfigValue.class);
			return StringValue.of(rule.getDescription());
		}

		@FunctionDoc(
			name = "getOptionalInfo",
			desc = "Gets the optional info of the config",
			returns = {STRING, "The optional info of the config"},
			example = "config.getOptionalInfo();"
		)
		private Value getOptionalInfo(Arguments arguments) throws RuntimeError {
			ClientRule<?> rule = arguments.getNextGeneric(ConfigValue.class);
			return StringValue.of(rule.getOptionalInfo());
		}

		@FunctionDoc(
			name = "getDefaultValue",
			desc = "Gets the default value of the config",
			returns = {ANY, "The default value of the config"},
			example = "config.getDefaultValue();"
		)
		private Value getDefaultValue(Arguments arguments) throws CodeError {
			ClientRule<?> rule = arguments.getNextGeneric(ConfigValue.class);
			return arguments.getContext().convertValue(rule.getDefaultValue());
		}

		@FunctionDoc(
			name = "getValue",
			desc = "Gets the value of the config",
			returns = {ANY, "The value of the config"},
			example = "config.getValue();"
		)
		private Value getValue(Arguments arguments) throws CodeError {
			ClientRule<?> rule = arguments.getNextGeneric(ConfigValue.class);
			return arguments.getContext().convertValue(rule.getValue());
		}

		@FunctionDoc(
			name = "toJson",
			desc = "Converts the config into a json value, this will not keep the listeners",
			returns = {JSON, "The config as a json value"},
			example = "config.toJson();"
		)
		private Value toJson(Arguments arguments) throws CodeError {
			ClientRule<?> rule = arguments.getNextGeneric(ConfigValue.class);
			return new JsonValue(rule.serialise());
		}

		@FunctionDoc(
			name = "addListener",
			desc = {
				"Adds a listener to the config, the listener will be called when the config is changed",
				"The listener must have one parameter, this is the rule that was changed"
			},
			params = {FUNCTION, "listener", "The listener to add"},
			example = """
				config.addListener(function(newValue) {
				    print(newValue);
				});
				"""
		)
		private Value addListener(Arguments arguments) throws CodeError {
			ConfigValue rule = arguments.getNext(ConfigValue.class);
			FunctionValue listener = arguments.getNext(FunctionValue.class);
			Context branch = arguments.getContext().createBranch();
			rule.value.addListener(object -> {
				Context branchContext = branch.createBranch();
				listener.callSafe(branchContext, () -> ArucasList.arrayListOf(rule));
			});
			return NullValue.NULL;
		}

		@FunctionDoc(
			name = "resetToDefault",
			desc = "Resets the config to the default value",
			example = "config.resetToDefault();"
		)
		private Value resetToDefault(Arguments arguments) throws CodeError {
			ClientRule<?> rule = arguments.getNextGeneric(ConfigValue.class);
			rule.resetToDefault();
			return NullValue.NULL;
		}

		@FunctionDoc(
			name = "setValue",
			desc = "Sets the value of the config, if the value is invalid it will not be changed",
			params = {ANY, "value", "The new value of the config"},
			example = "config.setValue(10);"
		)
		private Value setValue(Arguments arguments) throws CodeError {
			ClientRule<?> rule = arguments.getNextGeneric(ConfigValue.class);
			Value value = arguments.getNext();
			rule.setValueFromString(value.getAsString(arguments.getContext()));
			return NullValue.NULL;
		}
	}
}
