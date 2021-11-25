package essentialclient.config.clientrule;

public class StringClientRule extends ClientRule<String> {
	public StringClientRule(String name, String description, String defaultValue, Runnable runnable) {
		super(name, Type.STRING, description, defaultValue, runnable);
	}

	public StringClientRule(String name, String description, String defaultValue) {
		this(name, description, defaultValue, null);
	}

	@Override
	public void setValueFromString(String stringValue) {
		this.setValue(stringValue);
	}
}
