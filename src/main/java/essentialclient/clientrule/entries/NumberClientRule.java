package essentialclient.clientrule.entries;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import java.util.function.Consumer;

public abstract class NumberClientRule<T extends Number> extends ClientRule<T> {
	public NumberClientRule(String name, Type type, String description, T defaultValue, Consumer<T> consumer) {
		super(name, type, description, defaultValue, consumer);
	}

	@Override
	public JsonElement toJson(Number value) {
		return new JsonPrimitive(value);
	}
}
