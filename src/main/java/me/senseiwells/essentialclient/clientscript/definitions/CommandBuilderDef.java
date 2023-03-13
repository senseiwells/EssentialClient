package me.senseiwells.essentialclient.clientscript.definitions;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.ParsedArgument;
import me.senseiwells.arucas.api.docs.annotations.ClassDoc;
import me.senseiwells.arucas.api.docs.annotations.FunctionDoc;
import me.senseiwells.arucas.api.docs.annotations.ParameterDoc;
import me.senseiwells.arucas.api.docs.annotations.ReturnDoc;
import me.senseiwells.arucas.builtin.FunctionDef;
import me.senseiwells.arucas.builtin.ListDef;
import me.senseiwells.arucas.builtin.MapDef;
import me.senseiwells.arucas.builtin.StringDef;
import me.senseiwells.arucas.classes.CreatableDefinition;
import me.senseiwells.arucas.classes.instance.ClassInstance;
import me.senseiwells.arucas.functions.ArucasFunction;
import me.senseiwells.arucas.functions.builtin.Arguments;
import me.senseiwells.arucas.functions.builtin.BuiltInFunction;
import me.senseiwells.arucas.functions.builtin.MemberFunction;
import me.senseiwells.arucas.interpreter.Interpreter;
import me.senseiwells.arucas.utils.impl.ArucasList;
import me.senseiwells.arucas.utils.misc.Language;
import me.senseiwells.essentialclient.clientscript.core.MinecraftAPI;
import me.senseiwells.essentialclient.utils.clientscript.ClientScriptUtils;
import me.senseiwells.essentialclient.utils.command.CommandHelper;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static me.senseiwells.essentialclient.clientscript.core.MinecraftAPI.COMMAND_BUILDER;

@ClassDoc(
	name = COMMAND_BUILDER,
	desc = "This class allows you to build commands for Minecraft.",
	language = Language.Java
)
public class CommandBuilderDef extends CreatableDefinition<ArgumentBuilder<ServerCommandSource, ?>> {
	public CommandBuilderDef(Interpreter interpreter) {
		super(MinecraftAPI.COMMAND_BUILDER, interpreter);
	}

	@Override
	public List<BuiltInFunction> defineStaticMethods() {
		return List.of(
			BuiltInFunction.of("literal", 1, this::literal),
			BuiltInFunction.of("argument", 2, this::argument2),
			BuiltInFunction.of("argument", 3, this::argument3),
			BuiltInFunction.of("fromMap", 1, this::fromMap)
		);
	}

	@FunctionDoc(
		isStatic = true,
		name = "literal",
		desc = "Creates a literal argument with just a string",
		params = @ParameterDoc(type = StringDef.class, name = "argument", desc = "the literal argument"),
		returns = @ReturnDoc(type = CommandBuilderDef.class, desc = "the argument builder"),
		examples = "CommandBuilder.literal('test');"
	)
	private LiteralArgumentBuilder<ServerCommandSource> literal(Arguments arguments) {
		String name = arguments.nextPrimitive(StringDef.class);
		return CommandManager.literal(name);
	}

	@FunctionDoc(
		isStatic = true,
		name = "argument",
		desc = {
			"Creates an argument builder with a specific argument type, and a name",
			"to see all the different types refer to CommandBuilder.fromMap(...)"
		},
		params = {
			@ParameterDoc(type = StringDef.class, name = "argumentName", desc = "the name of the argument"),
			@ParameterDoc(type = StringDef.class, name = "argumentType", desc = "the type of the argument")
		},
		returns = @ReturnDoc(type = CommandBuilderDef.class, desc = "the argument builder"),
		examples = "CommandBuilder.argument('test', 'entityid');"
	)
	private RequiredArgumentBuilder<ServerCommandSource, ?> argument2(Arguments arguments) {
		String name = arguments.nextPrimitive(StringDef.class);
		String type = arguments.nextPrimitive(StringDef.class);
		return this.argument(arguments.getInterpreter(), name, type, null);
	}

	@FunctionDoc(
		isStatic = true,
		name = "argument",
		desc = {
			"Creates an argument builder with a specific argument type, a name, and a default value",
			"to see all the different types refer to CommandBuilder.fromMap(...)"
		},
		params = {
			@ParameterDoc(type = StringDef.class, name = "argumentName", desc = "the name of the argument"),
			@ParameterDoc(type = StringDef.class, name = "argumentType", desc = "the type of the argument"),
			@ParameterDoc(type = ListDef.class, name = "suggestions", desc = "a list of strings for the suggestions for the argument")
		},
		returns = @ReturnDoc(type = CommandBuilderDef.class, desc = "the argument builder"),
		examples = "CommandBuilder.argument('test', 'word', ['wow', 'suggestion']);"
	)
	private RequiredArgumentBuilder<ServerCommandSource, ?> argument3(Arguments arguments) {
		String name = arguments.nextPrimitive(StringDef.class);
		String type = arguments.nextPrimitive(StringDef.class);
		ArucasList suggestions = arguments.nextPrimitive(ListDef.class);
		return this.argument(arguments.getInterpreter(), name, type, suggestions);
	}

	@FunctionDoc(
		isStatic = true,
		name = "fromMap",
		desc = {
			"Creates an argument builder from a map.",
			"The map must contain a 'name' key as a String that is the name of the command,",
			"the map then can contain 'subcommands' as a map which contains the subcommands,",
			"the key of the subcommands is the name of the subcommand, and the value is a map,",
			"if the name is encased in '<' and '>' it will be treated as an argument, otherwise it will be treated as a literal.",
			"You can chain arguments by leaving a space in the name like: 'literal <arg>'.",
			"If the key has no name and is just an empty string the value will be used as the function",
			"which will be executed when the command is executed, the function should have the appropriate",
			"number of parameters, the number of parameters is determined by the number of arguments.",
			"Argument types are defined in the main map under the key 'arguments' with the value of a map",
			"the keys of this map should be the names of your arguments used in your subcommands,",
			"this should be a map and must have the key 'type' which should be a string that is the type of the argument.",
			"Optionally if the type is of 'integer' or 'double' you can also have the key 'min' and 'max' with numbers as the value,",
			"and if the type is of 'enum' you must have the key 'enum' with the enum class type as the value: 'enum': MyEnum.type.",
			"You can also optionally have 'suggests' which has the value of a list of strings that are suggestions for the argument.",
			"You can also optionally have 'suggester' which has the value of a function that will be called to get suggestions for the argument,",
			"this function should have arbitrary number of parameters which will be the arguments that the user has entered so far.",
			"The possible argument types are: 'PlayerName', 'Word', 'GreedyString', 'Double', 'Integer', 'Boolean', 'Enum',",
			"'ItemStack', 'Particle', 'RecipeId', 'EntityId', 'EnchantmentId'"
		},
		params = {@ParameterDoc(type = MapDef.class, name = "argumentMap", desc = "the map of arguments")},
		returns = @ReturnDoc(type = CommandBuilderDef.class, desc = "the argument builder"),
		examples = """
			effectCommandMap = {
				"name" : "effect",
				"subcommands" : {
					"give" : {
						"<targets> <effect>" : {
							"" : fun(target, effect) {
								// do something
							},
							"<seconds>" : {
								"" : fun(target, effect, second) {
									// do something
								},
								"<amplifier>" : {
									"" : fun(target, effect, second, amplifier) {
										// do something
									},
									"<hideParticle>" : {
										"" : fun(target, effect, second, amplifier, hideParticle) {
											// do something
										}
									}
								}
							}
						}
					},
					"clear" : {
						"" : fun() {
							// do something
						},
						"<targets>" : {
							"" : fun(target) {
								// do something
							},
							"<effect>" : {
								"" : fun(target, effect) {
									// do something
								}
							}
						}
					}
				},
				"arguments" : {
					"targets" : { "type" : "Entity" },
					"effect" : { "type" : "Effect", "suggests" : ["effect1", "effect2"] },
					"seconds" : { "type" : "Integer", "min" : 0, "max" : 1000000 },
					"amplifier" : { "type" : "Integer", "min" : 0, "max" : 255 },
					"hideParticle" : { "type" : "Boolean" }
				}
			};
			effectCommand = CommandBuilder.fromMap(effectCommandMap);
			"""
	)
	private ArgumentBuilder<ServerCommandSource, ?> fromMap(Arguments arguments) {
		return ClientScriptUtils.mapToCommand(arguments.nextPrimitive(MapDef.class), arguments.getInterpreter());
	}

	private RequiredArgumentBuilder<ServerCommandSource, ?> argument(Interpreter interpreter, String name, String stringArgType, ArucasList suggestions) {
		List<String> suggests = suggestions == null ? null : suggestions.stream().map(i -> i.toString(interpreter)).toList();
		return ClientScriptUtils.parseArgument(name, stringArgType, suggests, null);
	}

	@Override
	public List<MemberFunction> defineMethods() {
		return List.of(
			MemberFunction.of("then", 1, this::then),
			MemberFunction.of("executes", 1, this::executes)
		);
	}

	@FunctionDoc(
		name = "then",
		desc = "This adds a child CommandBuilder to your command builder",
		params = {@ParameterDoc(type = CommandBuilderDef.class, name = "childBuilder", desc = "the child command builder to add")},
		returns = @ReturnDoc(type = CommandBuilderDef.class, desc = "the parent command builder"),
		examples = "commandBuilder.then(CommandBuilder.literal('subcommand'));"
	)
	private ClassInstance then(Arguments arguments) {
		ClassInstance instance = arguments.next(this);
		instance.asPrimitive(this).then(arguments.nextPrimitive(this));
		return instance;
	}

	@FunctionDoc(
		name = "executes",
		desc = {
			"This sets the function to be executed when the command is executed,",
			"this should have the correct amount of parameters for the command"
		},
		params = {@ParameterDoc(type = CommandBuilderDef.class, name = "function", desc = "the function to execute")},
		returns = @ReturnDoc(type = CommandBuilderDef.class, desc = "the parent command builder"),
		examples = "commandBuilder.executes(fun() { });"
	)
	private ClassInstance executes(Arguments arguments) {
		ClassInstance instance = arguments.next(this);
		ArucasFunction function = arguments.nextPrimitive(FunctionDef.class);
		Interpreter interpreter = arguments.getInterpreter().branch();
		instance.asPrimitive(this).executes(c -> {
			Collection<ParsedArgument<?, ?>> args = CommandHelper.getArguments(c);
			if (args == null) {
				throw ClientScriptUtils.CommandParser.NO_ARGS.create();
			}
			List<ClassInstance> list = new ArrayList<>(args.size());
			for (ParsedArgument<?, ?> argument : args) {
				list.add(ClientScriptUtils.commandArgumentToValue(argument.getResult(), interpreter));
			}
			interpreter.runAsync(() -> {
				function.invoke(interpreter.branch(), list);
				return null;
			});
			return 1;
		});
		return instance;
	}
}
