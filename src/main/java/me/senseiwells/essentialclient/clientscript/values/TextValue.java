package me.senseiwells.essentialclient.clientscript.values;

import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.api.docs.ClassDoc;
import me.senseiwells.arucas.api.docs.FunctionDoc;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.Arguments;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.GenericValue;
import me.senseiwells.arucas.values.StringValue;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.functions.BuiltInFunction;
import me.senseiwells.arucas.values.functions.FunctionValue;
import me.senseiwells.arucas.values.functions.MemberFunction;
import me.senseiwells.essentialclient.utils.misc.FunctionClickEvent;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

import java.util.Locale;

import static me.senseiwells.arucas.utils.ValueTypes.ANY;
import static me.senseiwells.arucas.utils.ValueTypes.STRING;
import static me.senseiwells.essentialclient.clientscript.core.MinecraftAPI.TEXT;

public class TextValue extends GenericValue<MutableText> {
	public TextValue(MutableText value) {
		super(value);
	}

	@Override
	public GenericValue<MutableText> copy(Context context) {
		return new TextValue(this.value.shallowCopy());
	}

	@Override
	public String getAsString(Context context) {
		return "Text{text=%s}".formatted(this.value.getString());
	}

	@Override
	public int getHashCode(Context context) {
		return this.value.hashCode();
	}

	@Override
	public boolean isEquals(Context context, Value value) {
		return this.value == value.getValue();
	}

	@Override
	public String getTypeName() {
		return TEXT;
	}

	@ClassDoc(
		name = TEXT,
		desc = "This class is used to create formatted strings used inside Minecraft.",
		importPath = "Minecraft"
	)
	public static class ArucasTextClass extends ArucasClassExtension {
		public ArucasTextClass() {
			super(TEXT);
		}

		@Override
		public ArucasFunctionMap<BuiltInFunction> getDefinedStaticMethods() {
			return ArucasFunctionMap.of(
				BuiltInFunction.of("of", 1, this::of),
				BuiltInFunction.of("parse", 1, this::parse)
			);
		}

		@FunctionDoc(
			isStatic = true,
			name = "of",
			desc = "This converts a string into a text instance",
			params = {STRING, "string", "The string to convert into a text instance"},
			returns = {TEXT, "the text instance from the string"},
			example = "Text.of('Hello World!');"
		)
		private Value of(Arguments arguments) throws CodeError {
			StringValue stringValue = arguments.getNextString();
			return new TextValue(new LiteralText(stringValue.value));
		}

		@FunctionDoc(
			isStatic = true,
			name = "parse",
			desc = "This converts a text json into a text instance",
			params = {STRING, "textJson", "The string in json format, or a Json value itself"},
			returns = {TEXT, "the text instance from the json"},
			example = "Text.parse('{\"text\":\"Hello World!\",\"color\":\"white\",\"italic\":\"true\"}');"
		)
		private Value parse(Arguments arguments) throws CodeError {
			Value value = arguments.getNext();
			if (value instanceof JsonValue json) {
				return new TextValue(Text.Serializer.fromJson(json.value));
			}
			if (value instanceof StringValue string) {
				return new TextValue(Text.Serializer.fromJson(string.value));
			}
			throw arguments.getError("Must pass Json or String into parse()");
		}

		@Override
		public ArucasFunctionMap<MemberFunction> getDefinedMethods() {
			return ArucasFunctionMap.of(
				MemberFunction.of("withClickEvent", 2, this::withClickEvent),
				MemberFunction.of("withHoverEvent", 2, this::withHoverEvent),
				MemberFunction.of("format", 1, this::formatText),
				MemberFunction.of("append", 1, this::appendText)
			);
		}

		@FunctionDoc(
			name = "withClickEvent",
			desc = {
				"This allows you to add a click event to a text instance",
				"The possible events are: 'open_url', 'open_file', 'run_command', 'suggest_command', 'copy_to_clipboard', 'run_function'"
			},
			params = {
				STRING, "event", "the name of the event",
				STRING, "value", "the value associated with the event",
			},
			returns = {TEXT, "the text instance with the click event"},
			throwMsgs = "Invalid action: ...",
			example = """
			text = Text.of("Hello World!");
			
			// Examples of click events
			text.withClickEvent("open_url", "https://youtu.be/dQw4w9WgXcQ");
			text.withClickEvent("open_file", "C:/Users/user/Desktop/thing.txt");
			text.withClickEvent("run_command", "/gamemode creative");
			text.withClickEvent("suggest_command", "/gamemode survival");
			text.withClickEvent("copy_to_clipboard", "Ooops!");
			text.withClickEvent("run_function", fun() {
				print("Text was clicked!");
			});
			"""
		)
		private Value withClickEvent(Arguments arguments) throws CodeError {
			TextValue text = arguments.getNext(TextValue.class);
			String actionAsString = arguments.getNextGeneric(StringValue.class).toLowerCase(Locale.ROOT);
			ClickEvent.Action action = ClickEvent.Action.byName(actionAsString);
			ClickEvent clickEvent;
			if (action == null) {
				switch (actionAsString) {
					case "function", "run_function" -> {
						FunctionValue eventFunction = arguments.getNextFunction();
						clickEvent = new FunctionClickEvent(arguments.getContext(), eventFunction);
					}
					default -> throw arguments.getError("Invalid action: %s", actionAsString);
				}
			}
			else {
				StringValue stringValue = arguments.getNextString();
				clickEvent = new ClickEvent(action, stringValue.value);
			}
			text.value.styled(style -> style.withClickEvent(clickEvent));
			return text;
		}

		@FunctionDoc(
			name = "withHoverEvent",
			desc = {
				"This allows you to add a hover event to a text instance",
				"The possible events are: 'show_text', 'show_item', 'show_entity'"
			},
			params = {
				STRING, "event", "the name of the event",
				ANY, "value", "the value associated with the event",
			},
			returns = {TEXT, "the text instance with the hover event"},
			throwMsgs = "Invalid action: ...",
			example = """
			text = Text.of("Hello World!");
			
			// Examples of hover events
			text.withHoverEvent("show_text", Text.of("Hello world!"));
			text.withHoverEvent("show_item", Material.DIAMOND_SWORD.asItemStack());
			text.withHoverEvent("show_entity", Player.get());
			"""
		)
		private Value withHoverEvent(Arguments arguments) throws CodeError {
			TextValue text = arguments.getNext(TextValue.class);
			StringValue stringAction = arguments.getNextString();
			HoverEvent hoverEvent = switch (stringAction.value) {
				case "show_text" -> {
					MutableText mutableText = arguments.getNextGeneric(TextValue.class);
					yield new HoverEvent(HoverEvent.Action.SHOW_TEXT, mutableText);
				}
				case "show_item" -> {
					ItemStack stack = arguments.getNextGeneric(ItemStackValue.class);
					yield new HoverEvent(HoverEvent.Action.SHOW_ITEM, new HoverEvent.ItemStackContent(stack));
				}
				case "show_entity" -> {
					EntityValue<?> entityValue = arguments.getNext(EntityValue.class);
					Entity entity = entityValue.value;
					yield new HoverEvent(HoverEvent.Action.SHOW_ENTITY, new HoverEvent.EntityContent(entity.getType(), entity.getUuid(), entity.getDisplayName()));
				}
				default -> throw arguments.getError("Invalid action: %s", stringAction);
			};
			text.value.styled(style -> style.withHoverEvent(hoverEvent));
			return text;
		}

		@FunctionDoc(
			name = "format",
			desc = {
				"This allows you to add a formatting to a text instance",
				"A list of formatting names can be found [here](https://minecraft.fandom.com/wiki/Formatting_codes)"
			},
			params = {STRING, "formatting", "the name of the formatting"},
			returns = {TEXT, "the text instance with the formatting added"},
			throwMsgs = "Invalid formatting: ...",
			example = "text.format('DARK_RED').format('BOLD');"
		)
		private Value formatText(Arguments arguments) throws CodeError {
			TextValue text = arguments.getNext(TextValue.class);
			StringValue stringValue = arguments.getNextString();
			Formatting formatting = Formatting.byName(stringValue.value.toUpperCase(Locale.ROOT));
			if (formatting == null) {
				throw arguments.getError("Invalid formatting: %s", stringValue);
			}
			text.value.formatted(formatting);
			return text;
		}

		@FunctionDoc(
			name = "append",
			desc = "This allows you to append a text instance to another text instance",
			params = {TEXT, "otherText", "the text instance to append to"},
			returns = {TEXT, "the text instance with the appended text"},
			example = "Text.of('Hello').append(Text.of(' world!'));"
		)
		private Value appendText(Arguments arguments) throws CodeError {
			TextValue text = arguments.getNext(TextValue.class);
			TextValue textValue = arguments.getNext(TextValue.class);
			text.value.append(textValue.value);
			return text;
		}

		@Override
		public Class<TextValue> getValueClass() {
			return TextValue.class;
		}
	}
}
