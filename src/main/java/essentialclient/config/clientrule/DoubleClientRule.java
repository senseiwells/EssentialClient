package essentialclient.config.clientrule;

public class DoubleClientRule extends NumberClientRule<Double> {
	public DoubleClientRule(String name, String description, Double defaultValue, boolean putInMap) {
		super(name, Type.DOUBLE, description, defaultValue, null, putInMap);
	}

	public DoubleClientRule(String name, String description, Double defaultValue) {
		this(name, description, defaultValue, true);
	}

	@Override
	public void setValueFromString(String stringValue) throws NumberFormatException {
		this.setValue(Double.parseDouble(stringValue));
	}
}
