package me.senseiwells.essentialclient.utils.clientscript;

import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.ValuePair;
import me.senseiwells.arucas.utils.impl.ArucasList;
import me.senseiwells.arucas.utils.impl.ArucasMap;
import me.senseiwells.arucas.values.*;
import me.senseiwells.arucas.values.classes.ArucasEnumDefinition;
import me.senseiwells.arucas.values.functions.FunctionValue;
import me.senseiwells.essentialclient.clientscript.values.PosValue;
import me.senseiwells.essentialclient.utils.command.ClientEntityArgumentType;
import me.senseiwells.essentialclient.utils.command.ClientEntitySelector;
import me.senseiwells.essentialclient.utils.command.CommandHelper;
import me.senseiwells.essentialclient.utils.command.DefinitionArgumentType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.*;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import java.util.Collection;

public class ClientScriptUtils {
	public static final StringValue
		NAME = StringValue.of("name"),
		SUBCOMMANDS = StringValue.of("subcommands"),
		ARGUMENTS = StringValue.of("arguments"),
		TYPE = StringValue.of("type"),
		MIN = StringValue.of("min"),
		MAX = StringValue.of("max"),
		SUGGESTS = StringValue.of("suggests"),
		ENUM = StringValue.of("enum");

	public static ArgumentBuilder<ServerCommandSource, ?> mapToCommand(ArucasMap arucasMap, Context context, ISyntax syntaxPosition) throws CodeError {
		return new CommandParser(arucasMap, context, syntaxPosition).parse();
	}

	public static ArgumentType<?> parseArgumentType(String string, Context context, ISyntax syntaxPosition) throws RuntimeError {
		return switch (string.toLowerCase()) {
			case "playername", "word" -> StringArgumentType.word();
			case "double" -> DoubleArgumentType.doubleArg();
			case "integer" -> IntegerArgumentType.integer();
			case "boolean" -> BoolArgumentType.bool();
			case "itemstack" -> ItemStackArgumentType.itemStack();
			case "block" -> BlockStateArgumentType.blockState();
			case "greedystring" -> StringArgumentType.greedyString();
			case "entity" -> ClientEntityArgumentType.entity();
			case "entities" -> ClientEntityArgumentType.entities();
			case "blockpos" -> BlockPosArgumentType.blockPos();
			case "pos" -> Vec3ArgumentType.vec3();
			case "effect" -> StatusEffectArgumentType.statusEffect();
			case "particle" -> ParticleEffectArgumentType.particleEffect();
			case "recipeid" -> IdentifierArgumentType.identifier();
			case "entityid" -> EntityArgumentType.entity();
			case "enchantmentid" -> EnchantmentArgumentType.enchantment();
			default -> throw new RuntimeError("Invalid argument type", syntaxPosition, context);
		};
	}

	public static Value<?> commandArgumentToValue(Object object, Context context, CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException, CodeError {
		// We check for these two here since they require CommandContext
		if (object instanceof PosArgument posArgument) {
			return new PosValue(posArgument.toAbsolutePos(commandContext.getSource()));
		}
		if (object instanceof ClientEntitySelector selector) {
			object = selector.isSingleTarget() ? selector.getEntity(commandContext.getSource()) : selector.getEntities(commandContext.getSource());
		}
		return context.convertValue(object);
	}

	private static class CommandParser {
		private final Context context;
		private final ISyntax syntaxPosition;
		private final String commandName;
		private final ArucasMap subCommandMap;
		private final ArucasMap argumentMap;

		CommandParser(ArucasMap commandMap, Context context, ISyntax syntaxPosition) throws CodeError {
			this.context = context.createBranch();
			this.syntaxPosition = syntaxPosition;

			if (!(commandMap.get(context, NAME) instanceof StringValue string)) {
				throw new RuntimeError("Command map must contain 'name: <String>'", syntaxPosition, context);
			}
			this.commandName = string.value;

			this.subCommandMap = commandMap.get(context, SUBCOMMANDS) instanceof MapValue map ? map.value : null;
			this.argumentMap = commandMap.get(context, ARGUMENTS) instanceof MapValue map ? map.value : null;
		}

		ArgumentBuilder<ServerCommandSource, ?> parse() throws CodeError {
			LiteralArgumentBuilder<ServerCommandSource> baseCommand = CommandManager.literal(this.commandName);
			if (this.subCommandMap == null) {
				return baseCommand;
			}
			return this.command(baseCommand, this.subCommandMap);
		}

		private ArgumentBuilder<ServerCommandSource, ?> command(ArgumentBuilder<ServerCommandSource, ?> parent, ArucasMap childMap) throws CodeError {
			for (ValuePair pair : childMap.pairSet()) {
				if (!(pair.getKey() instanceof StringValue stringValue)) {
					throw new RuntimeError("Expected string value in subcommand map", this.syntaxPosition, this.context);
				}
				if (stringValue.value.isBlank()) {
					if (!(pair.getValue() instanceof FunctionValue functionValue)) {
						throw new RuntimeError("Expected function value", this.syntaxPosition, this.context);
					}
					parent.executes(c -> {
						this.context.getThreadHandler().runAsyncFunctionInThreadPool(this.context.createBranch(), ctx -> {
							Collection<ParsedArgument<?, ?>> arguments = CommandHelper.getArguments(c);
							if (arguments == null) {
								throw new RuntimeError("Couldn't get arguments for '%s'".formatted(stringValue.value), this.syntaxPosition, ctx);
							}
							ArucasList arucasList = new ArucasList();
							for (ParsedArgument<?, ?> argument : arguments) {
								arucasList.add(commandArgumentToValue(argument.getResult(), ctx, c));
							}
							functionValue.call(ctx, arucasList);
						});
						return 1;
					});
					continue;
				}
				if (!(pair.getValue() instanceof MapValue map)) {
					throw new RuntimeError("Expected map value for '%s'".formatted(stringValue.value), this.syntaxPosition, this.context);
				}
				ArucasMap subMap = map.value;
				String[] arguments = stringValue.value.split(" ");
				if (arguments.length > 1) {
					ArgumentBuilder<ServerCommandSource, ?> current = this.subCommand(arguments[arguments.length - 1], subMap);
					for (int i = arguments.length - 2; i >= 0; i--) {
						String name = arguments[i];
						current = this.connectedCommand(name).then(current);
					}
					parent.then(current);
					continue;
				}
				parent.then(this.subCommand(stringValue.value, subMap));
			}
			return parent;
		}

		private ArgumentBuilder<ServerCommandSource, ?> subCommand(String string, ArucasMap childMap) throws CodeError {
			if (string.charAt(0) != '<' || string.charAt(string.length() - 1) != '>') {
				return this.command(CommandManager.literal(string), childMap);
			}
			string = string.substring(1, string.length() - 1);
			if (this.argumentMap != null && this.argumentMap.get(this.context, StringValue.of(string)) instanceof MapValue map) {
				return this.command(this.getArgument(string, map.value), childMap);
			}
			throw new RuntimeError("Expected map value for argument '%s'".formatted(string), this.syntaxPosition, this.context);
		}

		private ArgumentBuilder<ServerCommandSource, ?> connectedCommand(String string) throws CodeError {
			if (string.charAt(0) != '<' || string.charAt(string.length() - 1) != '>') {
				return CommandManager.literal(string);
			}
			string = string.substring(1, string.length() - 1);
			if (this.argumentMap != null && this.argumentMap.get(this.context, StringValue.of(string)) instanceof MapValue map) {
				return this.getArgument(string, map.value);
			}
			throw new RuntimeError("Expected map value for argument '%s'".formatted(string), this.syntaxPosition, this.context);
		}

		private ArgumentBuilder<ServerCommandSource, ?> getArgument(String name, ArucasMap arguments) throws CodeError {
			Value<?> argumentValue = arguments.get(this.context, TYPE);
			if (!(argumentValue instanceof StringValue)) {
				throw new RuntimeError("Expected string for 'type' for argument '%s'".formatted(name), this.syntaxPosition, this.context);
			}
			SuggestionProvider<ServerCommandSource> extraSuggestion = null;
			String argumentTypeName = (String) argumentValue.value;
			ArgumentType<?> argumentType = switch (argumentTypeName.toLowerCase()) {
				case "playername" -> {
					extraSuggestion = (c, b) -> CommandHelper.suggestOnlinePlayers(b);
					yield StringArgumentType.word();
				}
				case "double" -> {
					if (arguments.get(this.context, MIN) instanceof NumberValue minValue) {
						if (arguments.get(this.context, MAX) instanceof NumberValue maxValue) {
							yield DoubleArgumentType.doubleArg(minValue.value, maxValue.value);
						}
						yield DoubleArgumentType.doubleArg(minValue.value);
					}
					yield DoubleArgumentType.doubleArg();
				}
				case "integer" -> {
					if (arguments.get(this.context, MIN) instanceof NumberValue minValue) {
						if (arguments.get(this.context, MAX) instanceof NumberValue maxValue) {
							yield IntegerArgumentType.integer(minValue.value.intValue(), maxValue.value.intValue());
						}
						yield IntegerArgumentType.integer(minValue.value.intValue());
					}
					yield IntegerArgumentType.integer();
				}
				case "recipeid" -> {
					extraSuggestion = SuggestionProviders.ALL_RECIPES;
					yield IdentifierArgumentType.identifier();
				}
				case "entityid" -> {
					extraSuggestion = SuggestionProviders.SUMMONABLE_ENTITIES;
					yield EntitySummonArgumentType.entitySummon();
				}
				case "enum" -> {
					if (arguments.get(this.context, ENUM) instanceof TypeValue type && type.value instanceof ArucasEnumDefinition definition) {
						yield DefinitionArgumentType.enumeration(definition);
					}
					throw new RuntimeError("Enum argument type must contain 'enum: <Type>'", this.syntaxPosition, this.context);
				}
				default -> parseArgumentType(argumentTypeName, this.context, this.syntaxPosition);
			};
			RequiredArgumentBuilder<ServerCommandSource, ?> argumentBuilder = CommandManager.argument(name, argumentType);
			if (extraSuggestion != null) {
				argumentBuilder.suggests(extraSuggestion);
			}
			Value<?> suggestion = arguments.get(this.context, SUGGESTS);
			if (suggestion instanceof ListValue listValue) {
				int size = listValue.value.size();
				String[] suggestions = new String[size];
				Value<?>[] values = listValue.value.toArray();
				for (int i = 0; i < size; i++) {
					suggestions[i] = values[i].getAsString(this.context);
				}
				argumentBuilder.suggests((c, b) -> CommandSource.suggestMatching(suggestions, b));
			}
			else if (suggestion != null) {
				throw new RuntimeError("Suggestion should be a list", this.syntaxPosition, this.context);
			}
			return argumentBuilder;
		}
	}
}
