package me.senseiwells.essentialclient.clientscript.values;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.ExceptionUtils;
import me.senseiwells.arucas.values.*;
import me.senseiwells.arucas.values.functions.BuiltInFunction;
import me.senseiwells.arucas.values.functions.MemberFunction;
import me.senseiwells.essentialclient.utils.clientscript.JsonUtils;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class JsonValue extends Value<JsonElement> {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	public JsonValue(JsonElement value) {
		super(value);
	}

	@Override
	public Value<JsonElement> copy(Context context) {
		return this;
	}

	@Override
	public String getAsString(Context context) throws CodeError {
		return GSON.toJson(this.value);
	}

	@Override
	public int getHashCode(Context context) throws CodeError {
		return this.value.hashCode();
	}

	@Override
	public boolean isEquals(Context context, Value<?> other) throws CodeError {
		return false;
	}

	@Override
	public String getTypeName() {
		return "Json";
	}

	/**
	 * Json class for Arucas. <br>
	 * Import the class with <code>import Json from Minecraft;</code> <br>
	 * Fully Documented.
	 * @author senseiwells
	 */
	public static class ArucasJsonClass extends ArucasClassExtension {
		public ArucasJsonClass() {
			super("Json");
		}

		@Override
		public ArucasFunctionMap<BuiltInFunction> getDefinedStaticMethods() {
			return ArucasFunctionMap.of(
				new BuiltInFunction("fromString", "string", this::fromString),
				new BuiltInFunction("fromList", "list", this::fromList),
				new BuiltInFunction("fromMap", "map", this::fromMap)
			);
		}

		/**
		 * Name: <code>Json.fromString(string)</code> <br>
		 * Description: This converts a string into a Json provided it is formatted correctly <br>
		 * Parameter - String: the string that you want to parse into a Json <br>
		 * Returns - Json: the Json parsed from the string <br>
		 * Throws - Error: <code>"Json could not be parsed"</code> if the string is not formatted correctly <br>
		 * Example: <code>Json.fromString("{\"key\":\"value\"}")</code>
		 */
		private Value<?> fromString(Context context, BuiltInFunction function) throws CodeError {
			StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 0);
			try {
				return new JsonValue(GSON.fromJson(stringValue.value, JsonElement.class));
			}
			catch (JsonSyntaxException e) {
				throw new RuntimeError("Json could not be parsed", function.syntaxPosition, context);
			}
		}

		/**
		 * Name: <code>Json.fromList(list)</code> <br>
		 * Description: This converts a list into a Json, an important thing to note is that
		 * any values that are not Numbers, Booleans, Lists, Maps, or Null will use their
		 * <code>toString()</code> member to convert them to a string <br>
		 * Parameter - List: the list that you want to parse into a Json <br>
		 * Returns - Json: the Json parsed from the list <br>
		 * Example: <code>Json.fromList(["value", 1, true])</code>
		 */
		private Value<?> fromList(Context context, BuiltInFunction function) throws CodeError {
			ListValue listValue = function.getParameterValueOfType(context, ListValue.class, 0);
			return new JsonValue(JsonUtils.fromValue(context, listValue));
		}

		/**
		 * Name: <code>Json.fromMap(map)</code> <br>
		 * Description: This converts a map into a Json, an important thing to note is that
		 * any values that are not Numbers, Booleans, Lists, Maps, or Null will use their
		 * <code>toString()</code> member to convert them to a string <br>
		 * Parameter - Map: the map that you want to parse into a Json <br>
		 * Returns - Json: the Json parsed from the map <br>
		 * Example: <code>Json.fromMap({"key": ["value1", "value2"]})</code>
		 */
		private Value<?> fromMap(Context context, BuiltInFunction function) throws CodeError {
			MapValue mapValue = function.getParameterValueOfType(context, MapValue.class, 0);
			return new JsonValue(JsonUtils.fromValue(context, mapValue));
		}

		@Override
		public ArucasFunctionMap<MemberFunction> getDefinedMethods() {
			return ArucasFunctionMap.of(
				new MemberFunction("getValue", this::getValue),
				new MemberFunction("writeToFile", "file", this::writeToFile)
			);
		}

		/**
		 * Name: <code>&lt;Json>.getValue()</code> <br>
		 * Description: This converts the Json back into a Value <br>
		 * Returns - Value: the Value parsed from the Json <br>
		 * Example: <code>json.getValue()</code>
		 */
		private Value<?> getValue(Context context, MemberFunction function) throws CodeError {
			JsonValue thisValue = function.getThis(context, JsonValue.class);
			return JsonUtils.toValue(context, thisValue.value);
		}

		/**
		 * Name: <code>&lt;Json>.writeToFile(file)</code> <br>
		 * Description: This writes the Json to a file <br>
		 * Parameter - File: the file that you want to write to <br>
		 - Throws: <code>"There was an error writing the file: ..."</code> if there is an error writing to the file <br>
		 * Example: <code>json.writeToFile(new File("D:/cool/realDirectory"))</code>
		 */
		private Value<?> writeToFile(Context context, MemberFunction function) throws CodeError {
			JsonValue thisValue = function.getThis(context, JsonValue.class);
			FileValue fileValue = function.getParameterValueOfType(context, FileValue.class, 1);
			try (PrintWriter printWriter = new PrintWriter(fileValue.value)) {
				printWriter.println(thisValue.getAsString(context));
				return NullValue.NULL;
			}
			catch (FileNotFoundException | SecurityException e) {
				throw new RuntimeError(
					"There was an error writing the file: \"%s\"\n%s".formatted(fileValue.getAsString(context), ExceptionUtils.getStackTrace(e)),
					function.syntaxPosition,
					context
				);
			}
		}

		@Override
		public Class<JsonValue> getValueClass() {
			return JsonValue.class;
		}
	}
}
