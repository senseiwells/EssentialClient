package essentialclient.config.clientrule;

public abstract class ClientRule<T> {

	private final String name;
	private final Type type;
	private final String description;
	private final T defaultValue;
	private final Runnable runnable;
	private T value;

	public ClientRule(String name, Type type, String description, T defaultValue, Runnable runnable) {
		this.name = name;
		this.type = type;
		this.description = description;
		this.defaultValue = defaultValue;
		this.runnable = runnable;
		this.value = defaultValue;
		ClientRules.addRule(name, this);
	}

	public String getName() {
		return this.name;
	}

	public Type getType() {
		return this.type;
	}

	public String getDescription() {
		return this.description;
	}

	public T getDefaultValue() {
		return this.defaultValue;
	}

	public T getValue() {
		return this.value;
	}

	public void setValue(T value) {
		this.value = value;
	}

	public abstract void setValueFromString(String stringValue) throws Exception;

	public void resetToDefault() {
		this.value = this.defaultValue;
	}

	public boolean isNotDefault() {
		return !this.value.equals(this.defaultValue);
	}

	public void run() {
		if (this.runnable != null) {
			this.runnable.run();
		}
	}

	public enum Type {
		BOOLEAN("Boolean"),
		INTEGER("Integer"),
		DOUBLE("Double"),
		STRING("String"),
		CYCLE("Cycle"),
		SLIDER("Slider"),
		;

		private final String name;

		Type(String string) {
			this.name = string;
		}

		@Override
		public String toString() {
			return this.name;
		}

	}
}
