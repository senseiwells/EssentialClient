package essentialclient.clientscript.values;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import essentialclient.utils.clientscript.JsonUtils;
import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.ExceptionUtils;
import me.senseiwells.arucas.values.*;
import me.senseiwells.arucas.values.functions.BuiltInFunction;
import me.senseiwells.arucas.values.functions.MemberFunction;

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

		private Value<?> fromString(Context context, BuiltInFunction function) throws CodeError {
			StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 0);
			try {
				return new JsonValue(GSON.fromJson(stringValue.value, JsonElement.class));
			}
			catch (JsonSyntaxException e) {
				throw new RuntimeError("Json could not be parsed", function.syntaxPosition, context);
			}
		}

		private Value<?> fromList(Context context, BuiltInFunction function) throws CodeError {
			ListValue listValue = function.getParameterValueOfType(context, ListValue.class, 0);
			return new JsonValue(JsonUtils.fromValue(context, listValue));
		}

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

		private Value<?> getValue(Context context, MemberFunction function) throws CodeError {
			JsonValue thisValue = function.getThis(context, JsonValue.class);
			return JsonUtils.toValue(context, thisValue.value);
		}

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
