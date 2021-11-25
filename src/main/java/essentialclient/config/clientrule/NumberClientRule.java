package essentialclient.config.clientrule;

public abstract class NumberClientRule<T extends Number> extends ClientRule<T> {
	public NumberClientRule(String name, Type type, String description, T defaultValue, Runnable runnable) {
		super(name, type, description, defaultValue, runnable);
	}
}
