package essentialclient.utils.clientscript;

import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import essentialclient.clientscript.values.BlockValue;
import essentialclient.clientscript.values.EntityValue;
import essentialclient.clientscript.values.ItemStackValue;
import essentialclient.clientscript.values.PosValue;
import essentialclient.utils.command.ClientEntityArgumentType;
import essentialclient.utils.command.ClientEntitySelector;
import essentialclient.utils.command.CommandHelper;
import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.impl.ArucasList;
import me.senseiwells.arucas.utils.impl.ArucasMap;
import me.senseiwells.arucas.values.*;
import me.senseiwells.arucas.values.functions.FunctionValue;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.*;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.Collection;

public class ClientScriptUtils {
	public static final StringValue
		NAME = StringValue.of("name"),
		SUBCOMMANDS = StringValue.of("subcommands"),
		ARGUMENTS = StringValue.of("arguments"),
		TYPE = StringValue.of("type"),
		MIN = StringValue.of("min"),
		MAX = StringValue.of("max"),
		SUGGESTS = StringValue.of("suggests");

	public static ArgumentBuilder<ServerCommandSource, ?> mapToCommand(ArucasMap arucasMap, Context context, ISyntax syntaxPosition) throws CodeError {
		Value<?> nameValue = arucasMap.get(context, NAME);
		Value<?> subCommandValues = arucasMap.get(context, SUBCOMMANDS);
		Value<?> argumentValues = arucasMap.get(context, ARGUMENTS);
		if (nameValue instanceof StringValue && subCommandValues instanceof MapValue && argumentValues instanceof MapValue) {
			LiteralArgumentBuilder<ServerCommandSource> baseCommand = CommandManager.literal((String) nameValue.value);
			ArucasMap commandMap = (ArucasMap) subCommandValues.value;
			ArucasMap argumentMap = (ArucasMap) argumentValues.value;
			return mapToCommand(baseCommand, commandMap, argumentMap, context, syntaxPosition);
		}
		throw new RuntimeError("Command must include 'name' : <String>, 'subcommands' : <Map>, and 'arguments' : <Map>", syntaxPosition, context);
	}

	private static ArgumentBuilder<ServerCommandSource, ?> mapToCommand(ArgumentBuilder<ServerCommandSource, ?> parent, ArucasMap arucasMap, ArucasMap argumentMap, Context context, ISyntax syntaxPosition) throws CodeError {
		for (ArucasMap.Node node : arucasMap.entrySet(context)) {
			if (!(node.getKey() instanceof StringValue stringValue)) {
				throw new RuntimeError("Expected string value in subcommand map", syntaxPosition, context);
			}
			if (stringValue.value.isBlank()) {
				if (!(node.getValue() instanceof FunctionValue functionValue)) {
					throw new RuntimeError("Expected function value", syntaxPosition, context);
				}
				parent.executes(c -> {
					context.getThreadHandler().runAsyncFunctionInContext(context.createBranch(), ctx -> {
						Collection<ParsedArgument<?, ?>> arguments = CommandHelper.getArguments(c);
						if (arguments == null) {
							throw new RuntimeError("Couldn't get arguments for '%s'".formatted(stringValue.value), syntaxPosition, ctx);
						}
						ArucasList arucasList = new ArucasList();
						for (ParsedArgument<?, ?> argument : arguments) {
							arucasList.add(ClientScriptUtils.commandArgumentToValue(argument.getResult(), c));
						}
						functionValue.call(ctx, arucasList);
					});
					return 1;
				});
				continue;
			}
			if (!(node.getValue() instanceof MapValue)) {
				throw new RuntimeError("Expected map value for '%s'".formatted(stringValue.value), syntaxPosition, context);
			}
			ArucasMap subMap = (ArucasMap) node.getValue().value;
			String[] arguments = stringValue.value.split(" ");
			if (arguments.length > 1) {
				ArgumentBuilder<ServerCommandSource, ?> current = getSubCommand(arguments[arguments.length - 1], subMap, argumentMap, context, syntaxPosition);
				for (int i = arguments.length - 2; i >= 0; i--) {
					current = getConnectedSubCommand(arguments[i], argumentMap, context, syntaxPosition).then(current);
				}
				parent.then(current);
				continue;
			}
			parent.then(getSubCommand(stringValue.value, subMap, argumentMap, context, syntaxPosition));
		}
		return parent;
	}

	private static ArgumentBuilder<ServerCommandSource, ?> getSubCommand(String string, ArucasMap subMap, ArucasMap argumentMap, Context context, ISyntax syntaxPosition) throws CodeError {
		if (string.charAt(0) != '<' || string.charAt(string.length() - 1) != '>') {
			return mapToCommand(CommandManager.literal(string), subMap, argumentMap, context, syntaxPosition);
		}
		string = string.substring(1, string.length() - 1);
		Value<?> value = argumentMap.get(context, StringValue.of(string));
		if (value instanceof MapValue) {
			return mapToCommand(getArgument(string, (ArucasMap) value.value, context, syntaxPosition), subMap, argumentMap, context, syntaxPosition);
		}
		throw new RuntimeError("Expected map value for argument '%s'".formatted(string), syntaxPosition, context);
	}

	private static ArgumentBuilder<ServerCommandSource, ?> getConnectedSubCommand(String string, ArucasMap argumentMap, Context context, ISyntax syntaxPosition) throws CodeError {
		if (string.charAt(0) != '<' || string.charAt(string.length() - 1) != '>') {
			return CommandManager.literal(string);
		}
		string = string.substring(1, string.length() - 1);
		Value<?> value = argumentMap.get(context, StringValue.of(string));
		if (value instanceof MapValue) {
			return getArgument(string, (ArucasMap) value.value, context, syntaxPosition);
		}
		throw new RuntimeError("Expected map value for argument '%s'".formatted(string), syntaxPosition, context);
	}

	private static ArgumentBuilder<ServerCommandSource, ?> getArgument(String name, ArucasMap arguments, Context context, ISyntax syntaxPosition) throws CodeError {
		Value<?> argumentValue = arguments.get(context, TYPE);
		if (!(argumentValue instanceof StringValue)) {
			throw new RuntimeError("Expected string for 'type' for argument '%s'".formatted(name), syntaxPosition, context);
		}
		SuggestionProvider<ServerCommandSource> extraSuggestion = null;
		String argumentTypeName = (String) argumentValue.value;
		ArgumentType<?> argumentType = switch (argumentTypeName) {
			case "PlayerName" -> {
				extraSuggestion = (c, b) -> CommandHelper.suggestOnlinePlayers(b);
				yield StringArgumentType.word();
			}
			case "Double" -> {
				if (arguments.get(context, MIN) instanceof NumberValue minValue) {
					if (arguments.get(context, MAX) instanceof NumberValue maxValue) {
						yield DoubleArgumentType.doubleArg(minValue.value, maxValue.value);
					}
					yield DoubleArgumentType.doubleArg(minValue.value);
				}
				yield DoubleArgumentType.doubleArg();
			}
			case "Integer" -> {
				if (arguments.get(context, MIN) instanceof NumberValue minValue) {
					if (arguments.get(context, MAX) instanceof NumberValue maxValue) {
						yield IntegerArgumentType.integer(minValue.value.intValue(), maxValue.value.intValue());
					}
					yield IntegerArgumentType.integer(minValue.value.intValue());
				}
				yield IntegerArgumentType.integer();
			}
			case "RecipeId" -> {
				extraSuggestion = SuggestionProviders.ALL_RECIPES;
				yield IdentifierArgumentType.identifier();
			}
			case "EntityId" -> {
				extraSuggestion = SuggestionProviders.SUMMONABLE_ENTITIES;
				yield EntitySummonArgumentType.entitySummon();
			}
			default -> parseArgumentType(argumentTypeName, context, syntaxPosition);
		};
		RequiredArgumentBuilder<ServerCommandSource, ?> argumentBuilder = CommandManager.argument(name, argumentType);
		if (extraSuggestion != null) {
			argumentBuilder.suggests(extraSuggestion);
		}
		Value<?> suggestion = arguments.get(context, SUGGESTS);
		if (suggestion instanceof ListValue listValue) {
			int size = listValue.value.size();
			String[] suggestions = new String[size];
			Value<?>[] values = listValue.value.toArray();
			for (int i = 0; i < size; i++) {
				suggestions[i] = values[i].getAsString(context);
			}
			argumentBuilder.suggests((c, b) -> CommandSource.suggestMatching(suggestions, b));
		}
		else if (suggestion != null) {
			throw new RuntimeError("Suggestion should be a list", syntaxPosition, context);
		}
		return argumentBuilder;
	}

	public static ArgumentType<?> parseArgumentType(String string, Context context, ISyntax syntaxPosition) throws RuntimeError {
		return switch (string) {
			case "PlayerName", "Word" -> StringArgumentType.word();
			case "Double" -> DoubleArgumentType.doubleArg();
			case "Integer" -> IntegerArgumentType.integer();
			case "Boolean" -> BoolArgumentType.bool();
			case "ItemStack" -> ItemStackArgumentType.itemStack();
			case "Block" -> BlockStateArgumentType.blockState();
			case "GreedyString" -> StringArgumentType.greedyString();
			case "Entity" -> ClientEntityArgumentType.entity();
			case "BlockPos" -> BlockPosArgumentType.blockPos();
			case "Pos" -> Vec3ArgumentType.vec3();
			case "Effect" -> StatusEffectArgumentType.statusEffect();
			case "Particle" -> ParticleEffectArgumentType.particleEffect();
			case "RecipeId" -> IdentifierArgumentType.identifier();
			case "EntityId" -> EntityArgumentType.entity();
			case "EnchantmentId" -> EnchantmentArgumentType.enchantment();
			default -> throw new RuntimeError("Invalid argument type", syntaxPosition, context);
		};
	}

	public static Value<?> commandArgumentToValue(Object object, CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		if (object == null) {
			return NullValue.NULL;
		}
		if (object instanceof Number number) {
			return NumberValue.of(number.doubleValue());
		}
		if (object instanceof Boolean bool) {
			return BooleanValue.of(bool);
		}
		if (object instanceof ItemStackArgument itemStack) {
			return new ItemStackValue(itemStack.createStack(1, false));
		}
		if (object instanceof BlockStateArgument blockState) {
			return new BlockValue(blockState.getBlockState());
		}
		if (object instanceof ClientEntitySelector selector) {
			return EntityValue.of(selector.getEntity(context.getSource()));
		}
		if (object instanceof PosArgument posArgument) {
			return new PosValue(posArgument.toAbsolutePos(context.getSource()));
		}
		if (object instanceof Enchantment enchantment) {
			Identifier identifier = Registry.ENCHANTMENT.getId(enchantment);
			return identifier == null ? NullValue.NULL : StringValue.of(identifier.toString());
		}
		return StringValue.of(object.toString());
	}
}
