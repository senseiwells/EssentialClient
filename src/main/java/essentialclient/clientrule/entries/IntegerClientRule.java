package essentialclient.clientrule.entries;

import com.google.gson.JsonElement;
import essentialclient.utils.EssentialUtils;

import java.util.function.Consumer;

public class IntegerClientRule extends NumberClientRule<Integer> {
	public IntegerClientRule(String name, String description, int defaultValue, Consumer<Integer> consumer) {
		super(name, Type.INTEGER, description, defaultValue, consumer);
	}

	public IntegerClientRule(String name, String description, int defaultValue) {
		this(name, description, defaultValue, null);
	}

	public IntegerClientRule(String name, String description) {
		this(name, description, 0);
	}

	@Override
	public Integer fromJson(JsonElement value) {
		return value.getAsInt();
	}

	@Override
	public void setValueFromString(String value) {
		EssentialUtils.throwAsRuntime(() -> this.setValue(Integer.parseInt(value)));
	}

	@Override
	public ClientRule<Integer> copy() {
		return new IntegerClientRule(this.getName(), this.getDescription(), this.getDefaultValue(), this.getConsumer());
	}
}
