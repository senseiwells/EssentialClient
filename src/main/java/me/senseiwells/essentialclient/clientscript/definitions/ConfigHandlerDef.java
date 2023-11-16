package me.senseiwells.essentialclient.clientscript.definitions;

import kotlin.Unit;
import me.senseiwells.arucas.api.docs.annotations.*;
import me.senseiwells.arucas.builtin.BooleanDef;
import me.senseiwells.arucas.builtin.FileDef;
import me.senseiwells.arucas.builtin.ListDef;
import me.senseiwells.arucas.builtin.StringDef;
import me.senseiwells.arucas.classes.CreatableDefinition;
import me.senseiwells.arucas.classes.instance.ClassInstance;
import me.senseiwells.arucas.functions.builtin.Arguments;
import me.senseiwells.arucas.functions.builtin.ConstructorFunction;
import me.senseiwells.arucas.functions.builtin.MemberFunction;
import me.senseiwells.arucas.interpreter.Interpreter;
import me.senseiwells.arucas.utils.impl.ArucasList;
import me.senseiwells.arucas.utils.misc.Language;
import me.senseiwells.essentialclient.gui.RulesScreen;
import me.senseiwells.essentialclient.utils.clientscript.impl.ScriptConfigHandler;
import net.minecraft.text.Text;

import java.util.List;

import static me.senseiwells.essentialclient.clientscript.core.MinecraftAPI.CONFIG_HANDLER;

@ClassDoc(
	name = CONFIG_HANDLER,
	desc = "This class allows you to easily read and write config files.",
	language = Language.Java
)
public class ConfigHandlerDef extends CreatableDefinition<ScriptConfigHandler> {
	public ConfigHandlerDef(Interpreter interpreter) {
		super(CONFIG_HANDLER, interpreter);
	}

	@Override
	public List<ConstructorFunction> defineConstructors() {
		return List.of(
			ConstructorFunction.of(1, this::constructor1),
			ConstructorFunction.of(2, this::constructor2)
		);
	}

	@ConstructorDoc(
		desc = "Creates a new ConfigHandler, this is used to read and save configs",
		params = {@ParameterDoc(type = StringDef.class, name = "name", desc = "The name of the config, this will also be the name of the config file")},
		examples = "new ConfigHandler('MyConfig');"
	)
	private Unit constructor1(Arguments arguments) {
		ClassInstance instance = arguments.next();
		String name = arguments.nextPrimitive(StringDef.class);
		instance.setPrimitive(this, new ScriptConfigHandler(arguments.getInterpreter(), name, true));
		return null;
	}

	@ConstructorDoc(
		desc = "Creates a new ConfigHandler, this is used to read and save configs",
		params = {
			@ParameterDoc(type = StringDef.class, name = "name", desc = "The name of the config, this will also be the name of the config file"),
			@ParameterDoc(type = BooleanDef.class, name = "read", desc = "Whether or not to read the config on creation")
		},
		examples = "new ConfigHandler('MyConfig', false);"
	)
	private Unit constructor2(Arguments arguments) {
		ClassInstance instance = arguments.next();
		String name = arguments.nextPrimitive(StringDef.class);
		boolean read = arguments.nextPrimitive(BooleanDef.class);
		instance.setPrimitive(this, new ScriptConfigHandler(arguments.getInterpreter(), name, read));
		return null;
	}

	@Override
	public List<MemberFunction> defineMethods() {
		return List.of(
			MemberFunction.of("getName", this::getName),
			MemberFunction.of("read", this::read),
			MemberFunction.of("save", this::save),
			MemberFunction.of("setSaveOnClose", 1, this::setSaveOnClose),
			MemberFunction.of("willSaveOnClose", this::willSaveOnClose),
			MemberFunction.of("setSavePath", 1, this::setSavePath),
			MemberFunction.of("addConfig", 1, this::addConfig),
			MemberFunction.arb("addConfigs", this::addConfigs),
			MemberFunction.of("getConfig", 1, this::getConfig),
			MemberFunction.of("removeConfig", 1, this::removeConfig),
			MemberFunction.of("resetAllToDefault", this::resetAllToDefault),
			MemberFunction.of("getAllConfigs", this::getAllConfigs),
			MemberFunction.of("createScreen", this::createScreen0),
			MemberFunction.of("createScreen", 1, this::createScreen1),
			MemberFunction.of("createScreen", 2, this::createScreen2)
		);
	}

	@FunctionDoc(
		name = "getName",
		desc = "Gets the name of the config",
		returns = @ReturnDoc(type = StringDef.class, desc = "The name of the config"),
		examples = "configHandler.getName();"
	)
	private String getName(Arguments arguments) {
		return arguments.nextPrimitive(this).getConfigName();
	}

	@FunctionDoc(
		name = "read",
		desc = {
			"Reads the all the configs from the file",
			"If configs are already in the handler, only the values",
			"will be overwritten"
		},
		examples = "configHandler.read();"
	)
	private Void read(Arguments arguments) {
		arguments.nextPrimitive(this).readConfig();
		return null;
	}

	@FunctionDoc(
		name = "save",
		desc = "Saves the configs to the file",
		examples = "configHandler.save();"
	)
	private Void save(Arguments arguments) {
		arguments.nextPrimitive(this).saveConfig();
		return null;
	}

	@FunctionDoc(
		name = "setSaveOnClose",
		desc = "Sets whether or not the configs should be saved when the script ends, by default this is true",
		params = {@ParameterDoc(type = BooleanDef.class, name = "saveOnClose", desc = "Whether or not the configs should be saved when the script ends")},
		examples = "configHandler.setSaveOnClose(false);"
	)
	private Void setSaveOnClose(Arguments arguments) {
		arguments.nextPrimitive(this).setSaveOnClose(arguments.nextPrimitive(BooleanDef.class));
		return null;
	}

	@FunctionDoc(
		name = "willSaveOnClose",
		desc = "Gets whether or not the configs will be saved when the script ends",
		returns = @ReturnDoc(type = BooleanDef.class, desc = "Whether or not the configs will be saved when the script ends"),
		examples = "configHandler.willSaveOnClose();"
	)
	private boolean willSaveOnClose(Arguments arguments) {
		return arguments.nextPrimitive(this).willSaveOnClose();
	}

	@FunctionDoc(
		name = "setSavePath",
		desc = "Sets the path to save the configs to, this shouldn't include the file name",
		params = {@ParameterDoc(type = FileDef.class, name = "savePath", desc = "The path to save the configs to")},
		examples = "configHandler.setSavePath(new File('/home/user/scripts/'));"
	)
	private Void setSavePath(Arguments arguments) {
		arguments.nextPrimitive(this).setSavePath(arguments.nextPrimitive(FileDef.class).toPath());
		return null;
	}

	@FunctionDoc(
		name = "addConfig",
		desc = "Adds a config to the handler",
		params = {@ParameterDoc(type = ConfigDef.class, name = "config", desc = "The config to add")},
		examples =
			"""
				config = Config.fromMap({
					"type": "boolean",
					"name": "My Config",
					"description": "This is my config"
				});
				configHandler.addConfig(config);
				"""
	)
	private Void addConfig(Arguments arguments) {
		arguments.nextPrimitive(this).addConfig(arguments.next(ConfigDef.class));
		return null;
	}

	@FunctionDoc(
		name = "addConfigs",
		desc = {
			"Adds multiple configs to the handler, you can pass in a list of configs",
			"or a varargs of configs, this is for compatability with older scripts"
		},
		params = @ParameterDoc(type = ConfigDef.class, name = "configs", desc = "The configs to add", isVarargs = true),
		examples =
			"""
				config = Config.fromMap({
					"type": "boolean",
					"name": "My Config",
					"description": "This is my config"
				});
				configHandler.addConfigs(config, config);
				"""
	)
	private Void addConfigs(Arguments arguments) {
		ScriptConfigHandler handler = arguments.nextPrimitive(this);
		if (arguments.size() == 2 && arguments.isNext(ListDef.class)) {
			ArucasList list = arguments.nextPrimitive(ListDef.class);
			for (ClassInstance instance : list) {
				handler.addConfig(instance);
			}
		} else {
			for (ClassInstance instance : arguments.getRemaining()) {
				handler.addConfig(instance);
			}
		}
		return null;
	}

	@FunctionDoc(
		name = "getConfig",
		desc = "Gets a config from the handler",
		params = {@ParameterDoc(type = StringDef.class, name = "name", desc = "The name of the config")},
		returns = @ReturnDoc(type = ConfigDef.class, desc = "The config"),
		examples = "configHandler.getConfig('MyConfig');"
	)
	private ClassInstance getConfig(Arguments arguments) {
		return arguments.nextPrimitive(this).getConfig(arguments.nextPrimitive(StringDef.class));
	}

	@FunctionDoc(
		name = "removeConfig",
		desc = "Removes a config from the handler",
		params = {@ParameterDoc(type = StringDef.class, name = "name", desc = "The name of the config to remove")},
		examples = "configHandler.removeConfig('My Config');"
	)
	private Void removeConfig(Arguments arguments) {
		arguments.nextPrimitive(this).removeConfig(arguments.nextPrimitive(StringDef.class));
		return null;
	}

	@FunctionDoc(
		name = "resetAllToDefault",
		desc = "Resets all configs to their default values",
		examples = "configHandler.resetAllToDefault();"
	)
	private Void resetAllToDefault(Arguments arguments) {
		arguments.nextPrimitive(this).resetAllToDefault();
		return null;
	}

	@FunctionDoc(
		name = "getAllConfigs",
		desc = "Gets all the configs in the handler",
		returns = @ReturnDoc(type = ListDef.class, desc = "All the configs in the handler"),
		examples = "configHandler.getAllConfigs();"
	)
	private ArucasList getAllConfigs(Arguments arguments) {
		return arguments.nextPrimitive(this).getAllConfigs();
	}

	@FunctionDoc(
		name = "createScreen",
		desc = {
			"Creates a new config screen containing all of the configs in the handler, in alphabetical order.",
			"The screen name will be the default, the same as the name of the config handler"
		},
		returns = @ReturnDoc(type = ScreenDef.class, desc = "The new config screen"),
		examples = "configHandler.createScreen();"
	)
	private RulesScreen createScreen0(Arguments arguments) {
		ScriptConfigHandler handler = arguments.nextPrimitive(this);
		return handler.createScreen(Text.literal(handler.getConfigName()), true);
	}

	@FunctionDoc(
		name = "createScreen",
		desc = "Creates a new config screen containing all of the configs in the handler, in alphabetical order",
		params = {@ParameterDoc(type = TextDef.class, name = "title", desc = "The title of the screen")},
		returns = @ReturnDoc(type = ScreenDef.class, desc = "The new config screen"),
		examples = "configHandler.createScreen(Text.of('wow'));"
	)
	private RulesScreen createScreen1(Arguments arguments) {
		return arguments.nextPrimitive(this).createScreen(arguments.nextPrimitive(TextDef.class), true);
	}

	@FunctionDoc(
		name = "createScreen",
		desc = "Creates a new config screen containing all of the configs in the handler",
		params = {
			@ParameterDoc(type = TextDef.class, name = "title", desc = "The title of the screen"),
			@ParameterDoc(type = BooleanDef.class, name = "alphabetical", desc = "Whether or not to sort the configs alphabetically")
		},
		returns = @ReturnDoc(type = ScreenDef.class, desc = "The new config screen"),
		examples = "configHandler.createScreen(Text.of('wow'), false);"
	)
	private RulesScreen createScreen2(Arguments arguments) {
		return arguments.nextPrimitive(this).createScreen(
			arguments.nextPrimitive(TextDef.class),
			arguments.nextPrimitive(BooleanDef.class)
		);
	}
}
