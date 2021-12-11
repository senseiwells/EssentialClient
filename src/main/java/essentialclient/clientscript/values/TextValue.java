package essentialclient.clientscript.values;

import me.senseiwells.arucas.values.Value;
import net.minecraft.text.MutableText;

public class TextValue extends Value<MutableText> {
	public TextValue(MutableText value) {
		super(value);
	}

	@Override
	public Value<MutableText> copy() {
		return new TextValue(this.value.shallowCopy());
	}

	@Override
	public String toString() {
		return "Text{text=%s}".formatted(this.value.getString());
	}
}
