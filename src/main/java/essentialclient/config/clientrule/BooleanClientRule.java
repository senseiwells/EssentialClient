package essentialclient.config.clientrule;

public class BooleanClientRule extends ClientRule<Boolean> {
	public BooleanClientRule(String name, String description, Runnable runnable) {
		super(name, Type.BOOLEAN, description, false, runnable);
	}

	public BooleanClientRule(String name, String description) {
		this(name, description, null);
	}

	public void invertBoolean() {
		this.setValue(!this.getValue());
	}

	@Override
	public void setValueFromString(String stringValue) {
		this.setValue(stringValue.equals("true"));
	}
}
