package essentialclient.clientrule.entries;

import com.google.gson.JsonElement;

import java.util.Set;
import java.util.function.Consumer;

public abstract class ClientRule<T> {
	private final String name;
	private final Type type;
	private final String description;
	private final T defaultValue;
	private final Consumer<T> consumer;
	private T value;

	public ClientRule(String name, Type type, String description, T defaultValue, Consumer<T> consumer) {
		this.name = name;
		this.type = type;
		this.description = description;
		this.defaultValue = defaultValue;
		this.consumer = consumer;
		this.value = defaultValue;
	}

	public final String getName() {
		return this.name;
	}

	public final Type getType() {
		return this.type;
	}

	public final String getDescription() {
		return this.description;
	}

	public final T getDefaultValue() {
		return this.defaultValue;
	}

	public final T getValue() {
		return this.value;
	}

	public final Consumer<T> getConsumer() {
		return this.consumer;
	}

	public abstract JsonElement toJson(T value);

	public final JsonElement getValueAsJson() {
		return this.toJson(this.value);
	}

	public final JsonElement getDefaultAsJson() {
		return this.toJson(this.defaultValue);
	}

	public void setValue(T value) {
		this.value = value;
		this.onValueChange();
	}

	public abstract T fromJson(JsonElement value);

	public final void setValueFromJson(JsonElement value) {
		this.setValue(this.fromJson(value));
	}

	public abstract void setValueFromString(String value);

	public void resetToDefault() {
		this.setValue(this.defaultValue);
	}

	public boolean isNotDefault() {
		return !this.value.equals(this.defaultValue);
	}

	protected void onValueChange() {
		this.run();
	}

	public final void run() {
		if (this.consumer != null) {
			this.consumer.accept(this.value);
		}
	}

	public abstract ClientRule<T> copy();

	public enum Type {
		BOOLEAN("Boolean", "boolean", Set.of(boolean.class, Boolean.class)),
		INTEGER("Integer", "int", Set.of(int.class, Integer.class)),
		DOUBLE("Double", "double", Set.of(double.class, Double.class)),
		STRING("String", "string", Set.of(String.class)),
		CYCLE("Cycle"),
		SLIDER("Slider"),
		NONE("None");

		private final String name;
		private final String alias;
		private final Set<Class<?>> classAliases;

		Type(String string, String alias, Set<Class<?>> classAliases) {
			this.name = string;
			this.alias = alias;
			this.classAliases = classAliases;
		}

		Type(String string) {
			this(string, string.toLowerCase(), Set.of());
		}

		public static Type fromClass(Class<?> clazz) {
			for (Type type : Type.values()) {
				if (type.classAliases.contains(clazz)) {
					return type;
				}
			}
			return STRING;
		}

		public static Type fromString(String name) {
			for (Type type : Type.values()) {
				if (type.name.equals(name) || type.alias.equals(name)) {
					return type;
				}
			}
			return STRING;
		}

		@Override
		public String toString() {
			return this.name;
		}
	}
}
