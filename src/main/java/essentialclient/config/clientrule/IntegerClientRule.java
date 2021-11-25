package essentialclient.config.clientrule;

public class IntegerClientRule extends NumberClientRule<Integer> {
	public IntegerClientRule(String name, String description) {
		super(name, Type.INTEGER, description, 0, null);
	}

	@Override
	public void setValueFromString(String stringValue) throws NumberFormatException {
		this.setValue(Integer.parseInt(stringValue));
	}
}
