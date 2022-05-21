package me.senseiwells.essentialclient.clientscript.values;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.Arguments;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.ExceptionUtils;
import me.senseiwells.arucas.values.*;
import me.senseiwells.arucas.values.functions.BuiltInFunction;
import me.senseiwells.arucas.values.functions.MemberFunction;
import me.senseiwells.essentialclient.utils.clientscript.JsonUtils;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

import static me.senseiwells.essentialclient.clientscript.core.MinecraftAPI.JSON;

public class JsonValue extends GenericValue<JsonElement> {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	public JsonValue(JsonElement value) {
		super(value);
	}

	@Override
	public GenericValue<JsonElement> copy(Context context) {
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
	public boolean isEquals(Context context, Value other) throws CodeError {
		return false;
	}

	@Override
	public String getTypeName() {
		return JSON;
	}

	/**
	 * Json class for Arucas. <br>
	 * Import the class with <code>import Json from Minecraft;</code> <br>
	 * Fully Documented.
	 *
	 * @author senseiwells
	 */
	public static class ArucasJsonClass extends ArucasClassExtension {
		public ArucasJsonClass() {
			super(JSON);
		}

		@Override
		public ArucasFunctionMap<BuiltInFunction> getDefinedStaticMethods() {
			return ArucasFunctionMap.of(
				BuiltInFunction.of("fromString", 1, this::fromString),
				BuiltInFunction.of("fromList", 1, this::fromList),
				BuiltInFunction.of("fromMap", 1, this::fromMap)
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
		private Value fromString(Arguments arguments) throws CodeError {
			StringValue stringValue = arguments.getNextString();
			try {
				return new JsonValue(GSON.fromJson(stringValue.value, JsonElement.class));
			}
			catch (JsonSyntaxException e) {
				throw arguments.getError("Json could not be parsed");
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
		private Value fromList(Arguments arguments) throws CodeError {
			ListValue listValue = arguments.getNextList();
			return new JsonValue(JsonUtils.fromValue(arguments.getContext(), listValue));
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
		private Value fromMap(Arguments arguments) throws CodeError {
			MapValue mapValue = arguments.getNextMap();
			return new JsonValue(JsonUtils.fromValue(arguments.getContext(), mapValue));
		}

		@Override
		public ArucasFunctionMap<MemberFunction> getDefinedMethods() {
			return ArucasFunctionMap.of(
				MemberFunction.of("getValue", this::getValue),
				MemberFunction.of("writeToFile", 1, this::writeToFile)
			);
		}

		/**
		 * Name: <code>&lt;Json>.getValue()</code> <br>
		 * Description: This converts the Json back into a Value <br>
		 * Returns - Value: the Value parsed from the Json <br>
		 * Example: <code>json.getValue()</code>
		 */
		private Value getValue(Arguments arguments) throws CodeError {
			JsonValue thisValue = arguments.getNext(JsonValue.class);
			return JsonUtils.toValue(arguments.getContext(), thisValue.value);
		}

		/**
		 * Name: <code>&lt;Json>.writeToFile(file)</code> <br>
		 * Description: This writes the Json to a file <br>
		 * Parameter - File: the file that you want to write to <br>
		 * - Throws: <code>"There was an error writing the file: ..."</code> if there is an error writing to the file <br>
		 * Example: <code>json.writeToFile(new File("D:/cool/realDirectory"))</code>
		 */
		private Value writeToFile(Arguments arguments) throws CodeError {
			JsonValue thisValue = arguments.getNext(JsonValue.class);
			FileValue fileValue = arguments.getNext(FileValue.class);
			Context context = arguments.getContext();
			try (PrintWriter printWriter = new PrintWriter(fileValue.value)) {
				printWriter.println(thisValue.getAsString(context));
				return NullValue.NULL;
			}
			catch (FileNotFoundException | SecurityException e) {
				throw arguments.getError(
					"There was an error writing the file: '%s'\n%s",
					fileValue.getAsString(context),
					ExceptionUtils.getStackTrace(e)
				);
			}
		}

		@Override
		public Class<JsonValue> getValueClass() {
			return JsonValue.class;
		}
	}
}
