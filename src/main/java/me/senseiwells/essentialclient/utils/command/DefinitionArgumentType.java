package me.senseiwells.essentialclient.utils.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import me.senseiwells.arucas.values.EnumValue;
import me.senseiwells.arucas.values.classes.ArucasEnumDefinition;
import me.senseiwells.essentialclient.utils.render.Texts;
import net.minecraft.command.CommandSource;

import java.util.concurrent.CompletableFuture;

public class DefinitionArgumentType implements ArgumentType<EnumValue> {
	static {
		INVALID_ENUM_EXCEPTION = new DynamicCommandExceptionType(o -> Texts.literal("Enum element not found: " + o.toString()));
	}

	private static final DynamicCommandExceptionType INVALID_ENUM_EXCEPTION;

	private final ArucasEnumDefinition definition;

	private DefinitionArgumentType(ArucasEnumDefinition definition) {
		this.definition = definition;
	}

	public static DefinitionArgumentType enumeration(ArucasEnumDefinition enumDefinition) {
		return new DefinitionArgumentType(enumDefinition);
	}

	@Override
	public EnumValue parse(StringReader reader) throws CommandSyntaxException {
		String enumName = reader.readString();
		EnumValue enumValue = this.definition.getEnumValue(enumName);
		if (enumValue == null) {
			throw INVALID_ENUM_EXCEPTION.create(enumName);
		}
		return enumValue;
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		return CommandSource.suggestMatching(this.definition.names(), builder);
	}
}
