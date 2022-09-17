package me.senseiwells.essentialclient.utils.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import me.senseiwells.arucas.classes.ClassInstance;
import me.senseiwells.arucas.classes.EnumDefinition;
import me.senseiwells.essentialclient.utils.render.Texts;
import net.minecraft.command.CommandSource;

import java.util.concurrent.CompletableFuture;

public class DefinitionArgumentType implements ArgumentType<ClassInstance> {
	static {
		INVALID_ENUM_EXCEPTION = new DynamicCommandExceptionType(Texts.ENUM_NOT_FOUND::generate);
	}

	private static final DynamicCommandExceptionType INVALID_ENUM_EXCEPTION;

	private final EnumDefinition definition;

	private DefinitionArgumentType(EnumDefinition definition) {
		this.definition = definition;
	}

	public static DefinitionArgumentType enumeration(EnumDefinition enumDefinition) {
		return new DefinitionArgumentType(enumDefinition);
	}

	@Override
	public ClassInstance parse(StringReader reader) throws CommandSyntaxException {
		String enumName = reader.readString();
		ClassInstance enumValue = this.definition.getEnum(enumName);
		if (enumValue == null) {
			throw INVALID_ENUM_EXCEPTION.create(enumName);
		}
		return enumValue;
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		return CommandSource.suggestMatching(this.definition.getNames(), builder);
	}
}
