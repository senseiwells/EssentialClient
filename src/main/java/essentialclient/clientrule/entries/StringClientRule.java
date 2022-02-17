package essentialclient.clientrule.entries;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import java.util.function.Consumer;

public class StringClientRule extends ClientRule<String> {
	public StringClientRule(String name, String description, String defaultValue, Consumer<String> consumer) {
		super(name, Type.STRING, description, defaultValue, consumer);
	}

	public StringClientRule(String name, String description, String defaultValue) {
		this(name, description, defaultValue, null);
	}

	@Override
	public JsonElement toJson(String value) {
		return new JsonPrimitive(value);
	}

	@Override
	public String fromJson(JsonElement value) {
		return value.getAsString();
	}

	@Override
	public void setValueFromString(String value) {
		this.setValue(value);
	}

	@Override
	public ClientRule<String> copy() {
		return new StringClientRule(this.getName(), this.getDescription(), this.getDefaultValue(), this.getConsumer());
	}
}
