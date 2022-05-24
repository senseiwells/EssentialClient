package me.senseiwells.essentialclient.clientscript.values;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.api.docs.ClassDoc;
import me.senseiwells.arucas.api.docs.FunctionDoc;
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

import static me.senseiwells.arucas.utils.ValueTypes.*;
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

	@ClassDoc(
		name = JSON,
		desc = "This class allows you to create and manipulate JSON objects.",
		importPath = "Minecraft"
	)
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

		@FunctionDoc(
			isStatic = true,
			name = "fromString",
			desc = "This converts a string into a Json provided it is formatted correctly",
			params = {STRING, "string", "the string that you want to parse into a Json"},
			returns = {JSON, "the Json parsed from the string"},
			throwMsgs = "Json could not be parsed",
			example = "Json.fromString('{\"key\":\"value\"}');"
		)
		private Value fromString(Arguments arguments) throws CodeError {
			StringValue stringValue = arguments.getNextString();
			try {
				return new JsonValue(GSON.fromJson(stringValue.value, JsonElement.class));
			}
			catch (JsonSyntaxException e) {
				throw arguments.getError("Json could not be parsed");
			}
		}

		@FunctionDoc(
			isStatic = true,
			name = "fromList",
			desc = {
				"This converts a list into a Json, an important thing to note is that",
				"any values that are not Numbers, Booleans, Lists, Maps, or Null will use their",
				"toString() member to convert them to a string"
			},
			params = {LIST, "list", "the list that you want to parse into a Json"},
			returns = {JSON, "the Json parsed from the list"},
			example = "Json.fromList(['value', 1, true]);"
		)
		private Value fromList(Arguments arguments) throws CodeError {
			ListValue listValue = arguments.getNextList();
			return new JsonValue(JsonUtils.fromValue(arguments.getContext(), listValue));
		}

		@FunctionDoc(
			isStatic = true,
			name = "fromMap",
			desc = {
				"This converts a map into a Json, an important thing to note is that",
				"any values that are not Numbers, Booleans, Lists, Maps, or Null will use their",
				"toString() member to convert them to a string"
			},
			params = {MAP, "map", "the map that you want to parse into a Json"},
			returns = {JSON, "the Json parsed from the map"},
			example = "Json.fromMap({'key': ['value1', 'value2']});"
		)
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

		@FunctionDoc(
			name = "getValue",
			desc = "This converts the Json back into a Value",
			returns = {ANY, "the Value parsed from the Json"},
			example = "json.getValue();"
		)
		private Value getValue(Arguments arguments) throws CodeError {
			JsonValue thisValue = arguments.getNext(JsonValue.class);
			return JsonUtils.toValue(arguments.getContext(), thisValue.value);
		}

		@FunctionDoc(
			name = "writeToFile",
			desc = "This writes the Json to a file",
			params = {FILE, "file", "the file that you want to write to"},
			throwMsgs = "There was an error writing the file: ...",
			example = "json.writeToFile(new File('D:/cool/realDirectory'));"
		)
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
