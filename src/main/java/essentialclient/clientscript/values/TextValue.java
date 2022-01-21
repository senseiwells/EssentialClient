package essentialclient.clientscript.values;

import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.StringValue;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.functions.BuiltInFunction;
import me.senseiwells.arucas.values.functions.MemberFunction;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

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

		private Value<?> of(Context context, BuiltInFunction function) throws CodeError {
			StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 0);
			return new TextValue(new LiteralText(stringValue.value));
		}

		private Value<?> parse(Context context, BuiltInFunction function) throws CodeError {
			StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 0);
			return new TextValue(Text.Serializer.fromJson(stringValue.value));
		}

		@Override
		public ArucasFunctionMap<MemberFunction> getDefinedMethods() {
			return ArucasFunctionMap.of(
				new MemberFunction("withClickEvent", List.of("type", "value"), this::withClickEvent),
				new MemberFunction("format", "formatting", this::formatText),
				new MemberFunction("append", "otherText", this::appendText)
			);
		}

		private Value<?> withClickEvent(Context context, MemberFunction function) throws CodeError {
			MutableText text = this.getText(context, function);
			StringValue stringAction = function.getParameterValueOfType(context, StringValue.class, 1);
			ClickEvent.Action action = ClickEvent.Action.byName(stringAction.value);
			if (action == null) {
				throw new RuntimeError("Invalid action: %s".formatted(stringAction.value), function.syntaxPosition, context);
			}
			StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 2);
			text.styled(style -> style.withClickEvent(new ClickEvent(action, stringValue.value)));
			return new TextValue(text);
		}

		private Value<?> formatText(Context context, MemberFunction function) throws CodeError {
			MutableText text = this.getText(context, function);
			StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 1);
			Formatting formatting = Formatting.byName(stringValue.value);
			if (formatting == null) {
				throw new RuntimeError("Invalid formatting: %s".formatted(stringValue.value), function.syntaxPosition, context);
			}
			text.formatted(formatting);
			return new TextValue(text);
		}

		private Value<?> appendText(Context context, MemberFunction function) throws CodeError {
			MutableText text = this.getText(context, function);
			TextValue textValue = function.getParameterValueOfType(context, TextValue.class, 1);
			text.append(textValue.value);
			return new TextValue(text);
		}

		private MutableText getText(Context context, MemberFunction function) throws CodeError {
			MutableText text = function.getParameterValueOfType(context, TextValue.class, 0).value;
			if (text == null) {
				throw new RuntimeError("Text was null", function.syntaxPosition, context);
			}
			return text;
		}

		@Override
		public Class<?> getValueClass() {
			return TextValue.class;
		}
	}
}
