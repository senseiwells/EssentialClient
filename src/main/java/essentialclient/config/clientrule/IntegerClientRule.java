package essentialclient.config.clientrule;

public class IntegerClientRule extends NumberClientRule<Integer> {
	public IntegerClientRule(String name, String description, int defaultValue, Runnable runnable, boolean putInMap) {
		super(name, Type.INTEGER, description, defaultValue, runnable, putInMap);
	}

	public IntegerClientRule(String name, String description, int defaultValue, Runnable runnable) {
		this(name, description, defaultValue, runnable, true);
	}

	public IntegerClientRule(String name, String description, int defaultValue, boolean putInMap) {
		this(name, description, defaultValue, null, putInMap);
	}

	public IntegerClientRule(String name, String description, int defaultValue) {
		this(name, description, defaultValue, null, true);
	}

	public IntegerClientRule(String name, String description) {
		this(name, description, 0);
	}

	@Override
	public void setValueFromString(String stringValue) throws NumberFormatException {
		this.setValue(Integer.parseInt(stringValue));
	}
}
