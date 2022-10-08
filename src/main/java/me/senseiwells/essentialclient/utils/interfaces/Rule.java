package me.senseiwells.essentialclient.utils.interfaces;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import me.senseiwells.essentialclient.EssentialClient;

import java.util.*;
import java.util.function.Consumer;

public interface Rule<T> {
	String getName();

	Type getType();

	String getDescription();

	String getOptionalInfo();

	T getDefaultValue();

	T getValue();

	T fromJson(JsonElement element);

	JsonElement toJson(T value);

	List<RuleListener<T>> getListeners();

	Rule<T> shallowCopy();

	void setValueQuietly(T value);

	void setValueFromString(String value);

	void setOptionalInfo(String optionalInfo);

	default String getCategory() {
		return null;
	}

	default boolean changeable() {
		return true;
	}

	default boolean isAvailable() {
		return true;
	}

	default JsonElement getValueAsJson() {
		return this.toJson(this.getValue());
	}

	default JsonElement getDefaultValueAsJson() {
		return this.toJson(this.getDefaultValue());
	}

	default void addListener(RuleListener<T> ruleListener) {
		if (ruleListener != null) {
			this.getListeners().add(ruleListener);
		}
	}

	default void setValue(T value) {
		if (!Objects.equals(this.getValue(), value)) {
			this.setValueQuietly(value);
			this.onValueChange();
		}
	}

	default void onValueChange() {
		if (this.getListeners() != null) {
			for (RuleListener<T> ruleListener : this.getListeners()) {
				ruleListener.accept(this);
			}
		}
	}

	default void setValueFromJson(JsonElement element) {
		this.setValue(this.fromJson(element));
	}

	default void resetToDefault() {
		this.setValue(this.getDefaultValue());
	}

	default boolean isNotDefault() {
		return !this.getValue().equals(this.getDefaultValue());
	}

	default void logCannotSet(Object value) {
		EssentialClient.LOGGER.error("Cannot set the value '{}' for rule {}", value, this.getName());
	}

	interface Bool extends Rule<Boolean> {
		default void invert() {
			this.setValue(!this.getValue());
		}

		@Override
		default Type getType() {
			return Type.BOOLEAN;
		}

		@Override
		default Boolean fromJson(JsonElement element) {
			return element.getAsBoolean();
		}

		@Override
		default JsonElement toJson(Boolean value) {
			return new JsonPrimitive(value);
		}

		@Override
		default void setValueFromString(String value) {
			this.setValue("true".equals(value));
		}
	}

	interface Num<T extends Number> extends Rule<T> {
		@Override
		Type getType();

		@Override
		default JsonElement toJson(T value) {
			return new JsonPrimitive(value);
		}
	}

	interface Slider<T extends Number> extends Num<T> {
		String getFormatted();

		T getMin();

		T getMax();

		T getNewValue(double percent);

		double getPercentage();

		default void setFromPercentage(double percentage) {
			this.setValue(this.getNewValue(percentage));
		}

		@Override
		default Type getType() {
			return Type.SLIDER;
		}
	}

	interface Str extends Rule<String> {
		@Override
		default Type getType() {
			return Type.STRING;
		}

		@Override
		default String fromJson(JsonElement element) {
			return element.getAsString();
		}

		@Override
		default JsonElement toJson(String value) {
			return new JsonPrimitive(value);
		}

		@Override
		default void setValueFromString(String value) {
			this.setValue(value);
		}

		int getMaxLength();

		void setMaxLength(int maxLength);
	}

	interface Cycle extends Rule<String> {
		List<String> getCycleValues();

		int getCurrentIndex();

		void setCurrentIndex(int index);

		default int getMaxIndex() {
			return this.getCycleValues().size() - 1;
		}

		default void cycleValues() {
			int currentIndex = this.getCurrentIndex();
			this.setCurrentIndex(currentIndex >= this.getMaxIndex() ? 0 : ++currentIndex);
			this.setValue(this.getCycleValues().get(this.getCurrentIndex()));
		}

		@Override
		default Type getType() {
			return Type.CYCLE;
		}

		@Override
		default JsonElement toJson(String value) {
			return new JsonPrimitive(value);
		}

		@Override
		default String fromJson(JsonElement value) {
			return value.getAsString();
		}

		@Override
		default void setValueFromString(String value) {
			this.setValue(value);
		}

		@Override
		default void setValue(String value) {
			int index = this.getCycleValues().indexOf(value);
			if (index == -1) {
				this.logCannotSet(value);
				return;
			}
			this.setCurrentIndex(index);
			Rule.super.setValue(value);
		}

		@Override
		default void resetToDefault() {
			Rule.super.resetToDefault();
			this.setCurrentIndex(0);
		}
	}

	interface ListRule extends Rule<List<String>> {
		@Override
		default List<String> fromJson(JsonElement value) {
			JsonArray array = value.getAsJsonArray();

			List<String> configData = new ArrayList<>();
			for (JsonElement element : array) {
				configData.add(element.getAsString());
			}
			return configData;
		}

		@Override
		default JsonElement toJson(List<String> value) {
			JsonArray array = new JsonArray();
			for (String string : value) {
				array.add(string);
			}
			return array;
		}

		@Override
		default Type getType() {
			return Type.LIST;
		}

		int getMaxLength();

		void setMaxLength(int maxLength);
	}

	@FunctionalInterface
	interface RuleListener<T> extends Consumer<Rule<T>> { }

	enum Type {
		BOOLEAN("Boolean", "boolean", Set.of(boolean.class, Boolean.class)),
		INTEGER("Integer", "int", Set.of(int.class, Integer.class)),
		DOUBLE("Double", "double", Set.of(double.class, Double.class)),
		STRING("String", "string", Set.of(String.class)),
		CYCLE("Cycle"),
		SLIDER("Slider"),
		LIST("List"),
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
				if (type.name.equalsIgnoreCase(name) || type.alias.equals(name)) {
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
