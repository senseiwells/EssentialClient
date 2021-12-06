package essentialclient.config.clientrule;

public class DoubleClientRule extends NumberClientRule<Double> {
	public DoubleClientRule(String name, String description, Double defaultValue) {
		super(name, Type.DOUBLE, description, defaultValue, null);
	}

	@Override
	public void setValueFromString(String stringValue) throws NumberFormatException {
		this.setValue(Double.parseDouble(stringValue));
	}
}
