package essentialclient.config.clientrule;

import java.util.List;

public class CycleClientRule extends ClientRule<String> {
	private final List<String> cycleValues;
	private final int maxIndex;
	private int index;

	public CycleClientRule(String name, String description, List<String> cycleValues, Runnable runnable) {
		super(name, Type.CYCLE, description, cycleValues.get(0), runnable);
		this.cycleValues = cycleValues;
		this.maxIndex = cycleValues.size() - 1;
		this.index = 0;
	}

	@Override
	public void setValueFromString(String stringValue) {
		if (this.isValueValid(stringValue)) {
			this.setValue(stringValue);
			return;
		}
		throw new RuntimeException("Invalid Cycle Entry");
	}

	public boolean isValueValid(String value) {
		return this.cycleValues.contains(value);
	}

	@Override
	public void resetToDefault() {
		super.resetToDefault();
		this.index = 0;
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
}
