package essentialclient.clientrule.entries;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import java.util.List;
import java.util.function.Consumer;

public class CycleClientRule extends ClientRule<String> {
	private final List<String> cycleValues;
	private final int maxIndex;
	private int index;

	public CycleClientRule(String name, String description, List<String> cycleValues, String defaultValue, Consumer<String> consumer) {
		super(name, Type.CYCLE, description, defaultValue, consumer);
		this.cycleValues = cycleValues;
		this.maxIndex = cycleValues.size() - 1;
		this.index = 0;
	}

	public CycleClientRule(String name, String description, List<String> cycleValues, Consumer<String> consumer) {
		this(name, description, cycleValues, cycleValues.get(0), consumer);
	}

	public CycleClientRule(String name, String description, List<String> cycleValues) {
		this(name, description, cycleValues, null);
	}

	public void cycleValues() {
		if (this.index >= this.maxIndex) {
			this.index = 0;
		}
		else {
			this.index++;
		}
		this.setValue(this.cycleValues.get(this.index));
	}

	public List<String> getCycleValues() {
		return this.cycleValues;
	}

	public int indexOfValue(String value) {
		return this.cycleValues.indexOf(value);
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
	public void setValue(String value) {
		int index = this.indexOfValue(value);
		if (index == -1) {
			this.cannotSetValue(value);
			return;
		}
		this.index = index;
		super.setValue(value);
	}

	@Override
	public void setValueFromString(String value) {
		this.setValue(value);
	}

	@Override
	public void resetToDefault() {
		super.resetToDefault();
		this.index = 0;
	}

	@Override
	public CycleClientRule copy() {
		return new CycleClientRule(this.getName(), this.getDescription(), this.getCycleValues(), this.getDefaultValue(), this.getConsumer());
	}
}
