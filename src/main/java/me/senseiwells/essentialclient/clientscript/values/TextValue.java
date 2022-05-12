package me.senseiwells.essentialclient.clientscript.values;

import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.Context;
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

import java.util.List;
import java.util.Locale;

public class TextValue extends Value<MutableText> {
	public TextValue(MutableText value) {
		super(value);
	}

	@Override
	public Value<MutableText> copy(Context context) {
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
	public boolean isEquals(Context context, Value<?> value) {
		return this.value == value.value;
	}

	@Override
	public String getTypeName() {
		return "Text";
	}

	/**
	 * Text class for Arucas. This allows for formatted strings used inside Minecraft. <br>
	 * Import the class with <code>import Text from Minecraft;</code> <br>
	 * Fully Documented.
	 * @author senseiwells
	 */
	public static class ArucasTextClass extends ArucasClassExtension {
		public ArucasTextClass() {
			super("Text");
		}

		@Override
		public ArucasFunctionMap<BuiltInFunction> getDefinedStaticMethods() {
			return ArucasFunctionMap.of(
				new BuiltInFunction("of", "text", this::of),
				new BuiltInFunction("parse", "text", this::parse)
			);
		}

		/**
		 * Name: <code>Text.of(string)</code> <br>
		 * Description: This converts a string into a text instance <br>
		 * Returns - Text: the text instance from the string <br>
		 * Example: <code>Text.of("Hello World!");</code>
		 */
		private Value<?> of(Context context, BuiltInFunction function) throws CodeError {
			StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 0);
			return new TextValue(new LiteralText(stringValue.value));
		}

		/**
		 * Name: <code>Text.parse(textJson)</code> <br>
		 * Description: This converts a text json into a text instance <br>
		 * Parameter - String/Json: the string in json format, or a Json value itself <br>
		 * Returns - Text: the text instance from the json <br>
		 * Example: <code>Text.parse("{\"text\":\"Hello World!\",\"color\":\"white\",\"italic\":\"true\"}");</code>
		 */
		private Value<?> parse(Context context, BuiltInFunction function) throws CodeError {
			Value<?> value = function.getParameterValue(context, 0);
			if (value instanceof JsonValue json) {
				return new TextValue(Text.Serializer.fromJson(json.value));
			}
			if (value instanceof StringValue string) {
				return new TextValue(Text.Serializer.fromJson(string.value));
			}
			throw new RuntimeError("Must pass Json or String into parse()", function.syntaxPosition, context);
		}

		@Override
		public ArucasFunctionMap<MemberFunction> getDefinedMethods() {
			return ArucasFunctionMap.of(
				new MemberFunction("withClickEvent", List.of("type", "value"), this::withClickEvent),
				new MemberFunction("withHoverEvent", List.of("type", "value"), this::withHoverEvent),
				new MemberFunction("format", "formatting", this::formatText),
				new MemberFunction("append", "otherText", this::appendText)
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
		private Value<?> withClickEvent(Context context, MemberFunction function) throws CodeError {
			TextValue text = function.getParameterValueOfType(context, TextValue.class, 0);
			String actionAsString = function.getParameterValueOfType(context, StringValue.class, 1).value.toLowerCase(Locale.ROOT);
			ClickEvent.Action action = ClickEvent.Action.byName(actionAsString);
			ClickEvent clickEvent;
			if (action == null) {
				switch (actionAsString) {
					case "function", "run_function" -> {
						FunctionValue eventFunction = function.getParameterValueOfType(context, FunctionValue.class, 2);
						clickEvent = new FunctionClickEvent(context, eventFunction);
					}
					default -> throw new RuntimeError("Invalid action: %s".formatted(actionAsString), function.syntaxPosition, context);
				}
			}
			else {
				StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 2);
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
		private Value<?> withHoverEvent(Context context, MemberFunction function) throws CodeError {
			TextValue text = function.getParameterValueOfType(context, TextValue.class, 0);
			StringValue stringAction = function.getParameterValueOfType(context, StringValue.class, 1);
			HoverEvent hoverEvent = switch (stringAction.value) {
				case "show_text" -> {
					MutableText mutableText = function.getParameterValueOfType(context, TextValue.class, 2).value;
					yield new HoverEvent(HoverEvent.Action.SHOW_TEXT, mutableText);
				}
				case "show_item" -> {
					ItemStack stack = function.getParameterValueOfType(context, ItemStackValue.class, 2).value;
					yield new HoverEvent(HoverEvent.Action.SHOW_ITEM, new HoverEvent.ItemStackContent(stack));
				}
				case "show_entity" -> {
					EntityValue<?> entityValue = function.getParameterValueOfType(context, EntityValue.class, 2);
					Entity entity = entityValue.value;
					yield new HoverEvent(HoverEvent.Action.SHOW_ENTITY, new HoverEvent.EntityContent(entity.getType(), entity.getUuid(), entity.getDisplayName()));
				}
				default -> throw new RuntimeError("Invalid action: %s".formatted(stringAction.value), function.syntaxPosition, context);
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
		private Value<?> formatText(Context context, MemberFunction function) throws CodeError {
			TextValue text = function.getParameterValueOfType(context, TextValue.class, 0);
			StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 1);
			Formatting formatting = Formatting.byName(stringValue.value.toUpperCase(Locale.ROOT));
			if (formatting == null) {
				throw new RuntimeError("Invalid formatting: %s".formatted(stringValue.value), function.syntaxPosition, context);
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
		private Value<?> appendText(Context context, MemberFunction function) throws CodeError {
			TextValue text = function.getParameterValueOfType(context, TextValue.class, 0);
			TextValue textValue = function.getParameterValueOfType(context, TextValue.class, 1);
			text.value.append(textValue.value);
			return text;
		}

		@Override
		public Class<TextValue> getValueClass() {
			return TextValue.class;
		}
	}
}
