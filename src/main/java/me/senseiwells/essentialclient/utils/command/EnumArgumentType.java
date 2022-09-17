package me.senseiwells.essentialclient.utils.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import me.senseiwells.essentialclient.utils.render.Texts;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class EnumArgumentType<T extends Enum<T>> implements ArgumentType<T> {
	static {
		INVALID_ELEMENT_EXCEPTION = new DynamicCommandExceptionType(Texts.ENUM_NOT_FOUND::generate);
	}

	private static final DynamicCommandExceptionType INVALID_ELEMENT_EXCEPTION;
	private final HashMap<String, T> values;
	private final Class<T> clazz;

	private EnumArgumentType(Class<T> clazz) {
		this.clazz = clazz;
		Enum<?>[] arrayOfEnum = clazz.getEnumConstants();
		this.values = new HashMap<>(arrayOfEnum.length);
		for (Enum<?> enumeration : arrayOfEnum) {
			this.values.put(enumeration.name().toLowerCase(), clazz.cast(enumeration));
		}
	}

	public static <T extends Enum<T>> EnumArgumentType<T> enumeration(Class<T> clazz) {
		return new EnumArgumentType<>(clazz);
	}

	public static <T extends Enum<T>> T getEnumeration(CommandContext<ServerCommandSource> commandContext, String string, Class<T> clazz) {
		return commandContext.getArgument(string, clazz);
	}

	@Override
	public T parse(StringReader reader) throws CommandSyntaxException {
		String name = reader.readString();
		Enum<?> enumeration = this.values.get(name);
		if (enumeration != null) {
			return this.clazz.cast(enumeration);
		}
		throw INVALID_ELEMENT_EXCEPTION.create(name);
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		return CommandSource.suggestMatching(this.values.keySet(), builder);
	}
}
