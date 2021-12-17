package essentialclient.clientscript.extensions;

import essentialclient.clientscript.values.TextValue;
import me.senseiwells.arucas.api.IArucasValueExtension;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.StringValue;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.functions.MemberFunction;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;

import java.util.List;
import java.util.Set;

public class ArucasTextMembers implements IArucasValueExtension {

	@Override
	public Set<MemberFunction> getDefinedFunctions() {
		return this.textFunctions;
	}

	@Override
	public Class<TextValue> getValueType() {
		return TextValue.class;
	}

	@Override
	public String getName() {
		return "TextMemberFunctions";
	}

	private final Set<MemberFunction> textFunctions = Set.of(
		new MemberFunction("withClickEvent", List.of("type", "value"), this::withClickEvent),
		new MemberFunction("format", "formatting", this::formatText),
		new MemberFunction("append", "otherText", this::appendText)
	);

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
}
