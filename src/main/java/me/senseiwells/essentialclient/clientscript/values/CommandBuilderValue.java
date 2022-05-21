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

import static me.senseiwells.essentialclient.clientscript.core.MinecraftAPI.COMMAND_BUILDER;

public class CommandBuilderValue extends GenericValue<ArgumentBuilder<ServerCommandSource, ?>> {
	public CommandBuilderValue(ArgumentBuilder<ServerCommandSource, ?> value) {
		super(value);
	}

	@Override
	public CommandBuilderValue copy(Context context) throws CodeError {
		return this;
	}

	@Override
	public String getAsString(Context context) throws CodeError {
		return "CommandBuilder@" + this.getHashCode(context);
	}

	@Override
	public int getHashCode(Context context) throws CodeError {
		return this.value.hashCode();
	}

	@Override
	public boolean isEquals(Context context, Value other) throws CodeError {
		return this == other;
	}

	@Override
	public String getTypeName() {
		return COMMAND_BUILDER;
	}

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

		private Value literal(Arguments arguments) throws CodeError {
			StringValue stringValue = arguments.getNextString();
			LiteralArgumentBuilder<ServerCommandSource> literalBuilder = CommandManager.literal(stringValue.value);
			return new CommandBuilderValue(literalBuilder);
		}

		private Value argument2(Arguments arguments) throws CodeError {
			StringValue stringValue = arguments.getNextString();
			StringValue argumentTypeString = arguments.getNextString();
			return this.argument(arguments.getContext(), arguments.getPosition(), stringValue.value, argumentTypeString.value, NullValue.NULL);
		}

		private Value argument3(Arguments arguments) throws CodeError {
			StringValue stringValue = arguments.getNextString();
			StringValue argumentTypeString = arguments.getNextString();
			Value suggestions = arguments.getNext();
			return this.argument(arguments.getContext(), arguments.getPosition(), stringValue.value, argumentTypeString.value, suggestions);
		}

		private Value fromMap(Arguments arguments) throws CodeError {
			MapValue mapValue = arguments.getNextMap();
			return new CommandBuilderValue(ClientScriptUtils.mapToCommand(mapValue.value, arguments.getContext(), arguments.getPosition()));
		}

		private Value argument(Context context, ISyntax syntaxPosition, String name, String stringArgType, Value suggestions) throws CodeError {
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

		private Value then(Arguments arguments) throws CodeError {
			CommandBuilderValue commandBuilderValue = arguments.getNext(CommandBuilderValue.class);
			CommandBuilderValue nextCommandBuilder = arguments.getNext(CommandBuilderValue.class);
			commandBuilderValue.value.then(nextCommandBuilder.value);
			return commandBuilderValue;
		}

		private Value executes(Arguments arguments) throws CodeError {
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
