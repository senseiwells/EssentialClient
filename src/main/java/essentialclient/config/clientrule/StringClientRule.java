package essentialclient.config.clientrule;

public class StringClientRule extends ClientRule<String> {
	public StringClientRule(String name, String description, String defaultValue, Runnable runnable, boolean putInMap) {
		super(name, Type.STRING, description, defaultValue, runnable, putInMap);
	}

	public StringClientRule(String name, String description, String defaultValue, Runnable runnable) {
		this(name, description, defaultValue, runnable, true);
	}

	public StringClientRule(String name, String description, String defaultValue) {
		this(name, description, defaultValue, null);
	}

	@Override
	public void setValueFromString(String stringValue) {
		this.setValue(stringValue);
	}
}
