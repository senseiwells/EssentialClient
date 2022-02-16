package essentialclient.clientrule.entries;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import java.util.function.Consumer;

public class BooleanClientRule extends ClientRule<Boolean> {
	public BooleanClientRule(String name, String description, boolean defaultValue, Consumer<Boolean> consumer) {
		super(name, Type.BOOLEAN, description, defaultValue, consumer);
	}

	public BooleanClientRule(String name, String description, boolean defaultValue) {
		this(name, description, defaultValue, null);
	}

	public BooleanClientRule(String name, String description, Consumer<Boolean> consumer) {
		this(name, description, false, consumer);
	}

	public BooleanClientRule(String name, String description) {
		this(name, description, null);
	}

	public void invertBoolean() {
		this.setValue(!this.getValue());
	}

	@Override
	public JsonElement toJson(Boolean value) {
		return new JsonPrimitive(value);
	}

	@Override
	public Boolean fromJson(JsonElement value) {
		return value.getAsBoolean();
	}

	@Override
	public void setValueFromString(String value) {
		this.setValue(value.equals("true"));
	}

	@Override
	public BooleanClientRule copy() {
		return new BooleanClientRule(this.getName(), this.getDescription(), this.getDefaultValue(), this.getConsumer());
	}
}
