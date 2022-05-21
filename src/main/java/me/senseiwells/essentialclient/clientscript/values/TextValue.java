package me.senseiwells.essentialclient.clientscript.values;

import me.senseiwells.arucas.api.ArucasClassExtension;
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

	/**
	 * Text class for Arucas. This allows for formatted strings used inside Minecraft. <br>
	 * Import the class with <code>import Text from Minecraft;</code> <br>
	 * Fully Documented.
	 *
	 * @author senseiwells
	 */
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

		/**
		 * Name: <code>Text.of(string)</code> <br>
		 * Description: This converts a string into a text instance <br>
		 * Returns - Text: the text instance from the string <br>
		 * Example: <code>Text.of("Hello World!");</code>
		 */
		private Value of(Arguments arguments) throws CodeError {
			StringValue stringValue = arguments.getNextString();
			return new TextValue(new LiteralText(stringValue.value));
		}

		/**
		 * Name: <code>Text.parse(textJson)</code> <br>
		 * Description: This converts a text json into a text instance <br>
		 * Parameter - String/Json: the string in json format, or a Json value itself <br>
		 * Returns - Text: the text instance from the json <br>
		 * Example: <code>Text.parse("{\"text\":\"Hello World!\",\"color\":\"white\",\"italic\":\"true\"}");</code>
		 */
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

		/**
		 * Name: <code>&lt;Text>.withClickEvent(event, value)</code> <br>
		 * Description: This allows you to add a click event to a text instance <br>
		 * Parameter - String, String/Function: the name of the event, the value associated with the event,
		 * <code>"open_url", "https://google.com"</code>, <code>"open_file", C:/Users/user/Desktop/thing.txt"</code>
		 * <code>"run_command", "/gamemode creative"</code> <code>"suggest_command", "/gamemode survival"</code>
		 * <code>"copy_to_clipboard", "Ooops!"</code> <code>"run_function", fun() { }</code> <br>
		 * Throws - Error: <code>"Invalid action: ..."</code> if the given action was invalid <br>
		 * Example: <code>text.withClickEvent("open_url", "https://youtu.be/dQw4w9WgXcQ");</code>
		 */
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

		/**
		 * Name: <code>&lt;Text>.withHoverEvent(event, value)</code> <br>
		 * Description: This allows you to add a hover event to a text instance <br>
		 * Parameter - String, Text/Item/Entity: the name of the event, the value associated with the event,
		 * <code>"show_text", Text.of("Hello world!")</code>, <code>"show_item", Material.DIAMOND_SWORD.asItemStack()</code>,
		 * <code>"show_entity", Player.get()</code> <br>
		 * Throws - Error: <code>"Invalid action: ..."</code> if the given action was invalid <br>
		 * Example: <code>text.withHoverEvent("show_text", Text.of("Hello world!"));</code>
		 */
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

		/**
		 * Name: <code>&lt;Text>.format(formatting)</code> <br>
		 * Description: This allows you to add a formatting to a text instance, a list of formatting
		 * names can be found [here](https://minecraft.fandom.com/wiki/Formatting_codes) <br>
		 * Parameter - String: the name of the formatting <br>
		 * Throws - Error: <code>"Invalid formatting: ..."</code> if the given formatting was invalid <br>
		 * Example: <code>text.format("DARK_RED").format("BOLD");</code>
		 */
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

		/**
		 * Name: <code>&lt;Text>.append(otherText)</code> <br>
		 * Description: This allows you to append a text instance to another text instance <br>
		 * Parameter - Text: the text instance to append to <br>
		 * Example: <code>Text.of("Hello").append(Text.of(" world!"));</code>
		 */
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
