package me.senseiwells.essentialclient.clientscript.values;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.api.docs.ClassDoc;
import me.senseiwells.arucas.api.docs.FunctionDoc;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.Arguments;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.impl.ArucasList;
import me.senseiwells.arucas.values.*;
import me.senseiwells.arucas.values.functions.BuiltInFunction;
import me.senseiwells.arucas.values.functions.FunctionValue;
import me.senseiwells.arucas.values.functions.MemberFunction;
import me.senseiwells.essentialclient.utils.clientscript.ClientScriptUtils;
import me.senseiwells.essentialclient.utils.command.CommandHelper;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.EntitySummonArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static me.senseiwells.arucas.utils.ValueTypes.*;
import static me.senseiwells.essentialclient.clientscript.core.MinecraftAPI.COMMAND_BUILDER;

public class CommandBuilderValue extends GenericValue<ArgumentBuilder<ServerCommandSource, ?>> {
	public CommandBuilderValue(ArgumentBuilder<ServerCommandSource, ?> value) {
		super(value);
	}

	@Override
	public CommandBuilderValue copy(Context context) {
		return this;
	}

	@Override
	public String getAsString(Context context) {
		return "CommandBuilder@" + this.getHashCode(context);
	}

	@Override
	public int getHashCode(Context context) {
		return this.value.hashCode();
	}

	@Override
	public boolean isEquals(Context context, Value other) {
		return this == other;
	}

	@Override
	public String getTypeName() {
		return COMMAND_BUILDER;
	}

	@ClassDoc(
		name = COMMAND_BUILDER,
		desc = "This class allows you to build commands for Minecraft.",
		importPath = "Minecraft"
	)
	public static class CommandBuilderClass extends ArucasClassExtension {
		public CommandBuilderClass() {
			super(COMMAND_BUILDER);
		}

		@Override
		public ArucasFunctionMap<BuiltInFunction> getDefinedStaticMethods() {
			return ArucasFunctionMap.of(
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
			params = {STRING, "argument", "the literal argument"},
			returns = {COMMAND_BUILDER, "the argument builder"},
			example = "CommandBuilder.literal('test');"
		)
		private Value literal(Arguments arguments) {
			StringValue stringValue = arguments.getNextString();
			LiteralArgumentBuilder<ServerCommandSource> literalBuilder = CommandManager.literal(stringValue.value);
			return new CommandBuilderValue(literalBuilder);
		}

		@FunctionDoc(
			isStatic = true,
			name = "argument",
			desc = {
				"Creates an argument builder with a specific argument type, and a name",
				"to see all the different types refer to CommandBuilder.fromMap(...)"
			},
			params = {
				STRING, "argumentName", "the name of the argument",
				STRING, "argumentType", "the type of the argument"
			},
			returns = {COMMAND_BUILDER, "the argument builder"},
			example = "CommandBuilder.argument('test', 'entityid');"
		)
		private Value argument2(Arguments arguments) {
			StringValue stringValue = arguments.getNextString();
			StringValue argumentTypeString = arguments.getNextString();
			return this.argument(arguments.getContext(), arguments.getPosition(), stringValue.value, argumentTypeString.value, NullValue.NULL);
		}

		@FunctionDoc(
			isStatic = true,
			name = "argument",
			desc = {
				"Creates an argument builder with a specific argument type, a name, and a default value",
				"to see all the different types refer to CommandBuilder.fromMap(...)"
			},
			params = {
				STRING, "argumentName", "the name of the argument",
				STRING, "argumentType", "the type of the argument",
				LIST, "suggestions", "a list of strings for the suggestions for the argument"
			},
			returns = {COMMAND_BUILDER, "the argument builder"},
			example = "CommandBuilder.argument('test', 'word', ['wow', 'suggestion']);"
		)
		private Value argument3(Arguments arguments) {
			StringValue stringValue = arguments.getNextString();
			StringValue argumentTypeString = arguments.getNextString();
			Value suggestions = arguments.getNext();
			return this.argument(arguments.getContext(), arguments.getPosition(), stringValue.value, argumentTypeString.value, suggestions);
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
			params = {MAP, "argumentMap", "the map of arguments"},
			returns = {COMMAND_BUILDER, "the argument builder"},
			example = """
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
		private Value fromMap(Arguments arguments) {
			MapValue mapValue = arguments.getNextMap();
			return new CommandBuilderValue(ClientScriptUtils.mapToCommand(mapValue.value, arguments.getContext(), arguments.getPosition()));
		}

		private Value argument(Context context, ISyntax syntaxPosition, String name, String stringArgType, Value suggestions) {
			SuggestionProvider<ServerCommandSource> extraSuggestion = null;
			ArgumentType<?> argumentType = switch (stringArgType.toLowerCase()) {
				case "playername" -> {
					extraSuggestion = (c, b) -> CommandHelper.suggestOnlinePlayers(b);
					yield StringArgumentType.word();
				}
				case "recipeid" -> {
					extraSuggestion = SuggestionProviders.ALL_RECIPES;
					yield IdentifierArgumentType.identifier();
				}
				case "entityid" -> {
					extraSuggestion = SuggestionProviders.SUMMONABLE_ENTITIES;
					yield EntitySummonArgumentType.entitySummon();
				}
				default -> ClientScriptUtils.parseArgumentType(stringArgType, context, syntaxPosition);
			};
			RequiredArgumentBuilder<ServerCommandSource, ?> argumentBuilder = CommandManager.argument(name, argumentType);
			if (extraSuggestion != null) {
				argumentBuilder.suggests(extraSuggestion);
			}
			if (suggestions != NullValue.NULL) {
				if (suggestions instanceof ListValue listValue) {
					List<String> stringSuggestions = new ArrayList<>();
					for (Value value : listValue.value.toArray()) {
						stringSuggestions.add(value.getAsString(context));
					}
					argumentBuilder.suggests((c, b) -> CommandSource.suggestMatching(stringSuggestions, b));
				}
				else {
					throw new RuntimeError("Expected 'null' or a list", syntaxPosition, context);
				}
			}
			return new CommandBuilderValue(argumentBuilder);
		}

		@Override
		public ArucasFunctionMap<MemberFunction> getDefinedMethods() {
			return ArucasFunctionMap.of(
				MemberFunction.of("then", 1, this::then),
				MemberFunction.of("executes", 1, this::executes)
			);
		}

		@FunctionDoc(
			name = "then",
			desc = "This adds a child CommandBuilder to your command builder",
			params = {COMMAND_BUILDER, "childBuilder", "the child command builder to add"},
			returns = {COMMAND_BUILDER, "the parent command builder"},
			example = "commandBuilder.then(CommandBuilder.literal('subcommand'));"
		)
		private Value then(Arguments arguments) {
			CommandBuilderValue commandBuilderValue = arguments.getNext(CommandBuilderValue.class);
			CommandBuilderValue nextCommandBuilder = arguments.getNext(CommandBuilderValue.class);
			commandBuilderValue.value.then(nextCommandBuilder.value);
			return commandBuilderValue;
		}

		@FunctionDoc(
			name = "executes",
			desc = {
				"This sets the function to be executed when the command is executed,",
				"this should have the correct amount of parameters for the command"
			},
			params = {COMMAND_BUILDER, "function", "the function to execute"},
			returns = {COMMAND_BUILDER, "the parent command builder"},
			example = "commandBuilder.executes(fun() { });"
		)
		private Value executes(Arguments arguments) {
			CommandBuilderValue commandBuilderValue = arguments.getNext(CommandBuilderValue.class);
			FunctionValue functionValue = arguments.getNextFunction();
			Context context = arguments.getContext().createBranch();
			ISyntax position = arguments.getPosition();
			commandBuilderValue.value.executes(c -> {
				context.getThreadHandler().runAsyncFunctionInThreadPool(context.createBranch(), ctx -> {
					Collection<ParsedArgument<?, ?>> commandArgs = CommandHelper.getArguments(c);
					if (commandArgs == null) {
						throw new RuntimeError("Couldn't get arguments", position, ctx);
					}
					ArucasList arucasList = new ArucasList();
					for (ParsedArgument<?, ?> argument : commandArgs) {
						arucasList.add(ClientScriptUtils.commandArgumentToValue(argument.getResult(), ctx));
					}
					functionValue.call(ctx, arucasList);
				});
				return 1;
			});
			return commandBuilderValue;
		}

		@Override
		public Class<CommandBuilderValue> getValueClass() {
			return CommandBuilderValue.class;
		}
	}
}
