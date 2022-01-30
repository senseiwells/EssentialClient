package essentialclient.clientscript.values;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.ParsedArgument;
import essentialclient.utils.clientscript.ClientScriptUtils;
import essentialclient.utils.command.CommandHelper;
import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.impl.ArucasList;
import me.senseiwells.arucas.values.*;
import me.senseiwells.arucas.values.functions.BuiltInFunction;
import me.senseiwells.arucas.values.functions.FunctionValue;
import me.senseiwells.arucas.values.functions.MemberFunction;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.BlockStateArgumentType;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CommandBuilderValue extends Value<ArgumentBuilder<ServerCommandSource, ?>> {
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
	public boolean isEquals(Context context, Value<?> other) throws CodeError {
		return this == other;
	}

	public static class CommandBuilderClass extends ArucasClassExtension {
		public CommandBuilderClass() {
			super("CommandBuilder");
		}

		@Override
		public ArucasFunctionMap<BuiltInFunction> getDefinedStaticMethods() {
			return ArucasFunctionMap.of(
				new BuiltInFunction("literal", List.of("name"), this::literal),
				new BuiltInFunction("argument", List.of("name", "type"), this::argument2),
				new BuiltInFunction("argument", List.of("name", "type", "suggests"), this::argument3),
				new BuiltInFunction("fromMap", "map", this::fromMap)
			);
		}

		private Value<?> literal(Context context, BuiltInFunction function) throws CodeError {
			StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 0);
			LiteralArgumentBuilder<ServerCommandSource> literalBuilder = CommandManager.literal(stringValue.value);
			return new CommandBuilderValue(literalBuilder);
		}

		private Value<?> argument2(Context context, BuiltInFunction function) throws CodeError {
			StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 0);
			StringValue argumentTypeString = function.getParameterValueOfType(context, StringValue.class, 1);
			return argument(context, function.syntaxPosition, stringValue.value, argumentTypeString.value, NullValue.NULL);
		}

		private Value<?> argument3(Context context, BuiltInFunction function) throws CodeError {
			StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 0);
			StringValue argumentTypeString = function.getParameterValueOfType(context, StringValue.class, 1);
			Value<?> suggestions = function.getParameterValue(context, 2);
			return argument(context, function.syntaxPosition, stringValue.value, argumentTypeString.value, suggestions);
		}

		private Value<?> fromMap(Context context, BuiltInFunction function) throws CodeError {
			MapValue mapValue = function.getParameterValueOfType(context, MapValue.class, 0);
			return new CommandBuilderValue(ClientScriptUtils.mapToCommand(mapValue.value, context, function.syntaxPosition));
		}

		private Value<?> argument(Context context, ISyntax syntaxPosition, String name, String stringArgType, Value<?> suggestions) throws CodeError {
			boolean shouldSuggestPlayers = false;
			ArgumentType<?> argumentType = switch (stringArgType) {
				case "PlayerName" -> {
					shouldSuggestPlayers = true;
					yield StringArgumentType.word();
				}
				case "ItemStack" -> ItemStackArgumentType.itemStack();
				case "Block" -> BlockStateArgumentType.blockState();
				case "Word" -> StringArgumentType.word();
				case "GreedyString" -> StringArgumentType.greedyString();
				case "Integer" -> IntegerArgumentType.integer();
				case "Double" -> DoubleArgumentType.doubleArg();
				default -> throw new RuntimeError("Invalid argument type", syntaxPosition, context);
			};
			RequiredArgumentBuilder<ServerCommandSource, ?> argumentBuilder = CommandManager.argument(name, argumentType);
			if (shouldSuggestPlayers) {
				argumentBuilder.suggests((c, b) -> CommandHelper.suggestOnlinePlayers(b));
			}
			if (suggestions != NullValue.NULL) {
				if (suggestions instanceof ListValue listValue) {
					List<String> stringSuggestions = new ArrayList<>();
					for (Value<?> value : listValue.value.toArray()) {
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
				new MemberFunction("then", "commandNode", this::then),
				new MemberFunction("executes", "function", this::executes)
			);
		}

		private Value<?> then(Context context, MemberFunction function) throws CodeError {
			CommandBuilderValue commandBuilderValue = function.getThis(context, CommandBuilderValue.class);
			CommandBuilderValue nextCommandBuilder = function.getParameterValueOfType(context, CommandBuilderValue.class, 1);
			commandBuilderValue.value.then(nextCommandBuilder.value);
			return commandBuilderValue;
		}

		private Value<?> executes(Context context, MemberFunction function) throws CodeError {
			CommandBuilderValue commandBuilderValue = function.getThis(context, CommandBuilderValue.class);
			FunctionValue functionValue = function.getParameterValueOfType(context, FunctionValue.class, 1);
			commandBuilderValue.value.executes(c -> {
				context.getThreadHandler().runAsyncFunctionInContext(context, ctx -> {
					Collection<ParsedArgument<?, ?>> arguments = CommandHelper.getArguments(c);
					if (arguments == null) {
						throw new RuntimeError("Couldn't get arguments", function.syntaxPosition, ctx);
					}
					ArucasList arucasList = new ArucasList();
					for (ParsedArgument<?, ?> argument : arguments) {
						arucasList.add(ClientScriptUtils.commandArgumentToValue(argument.getResult()));
					}
					functionValue.call(ctx, arucasList);
				});
				return 1;
			});
			return commandBuilderValue;
		}

		@Override
		public Class<?> getValueClass() {
			return CommandBuilderValue.class;
		}
	}
}
