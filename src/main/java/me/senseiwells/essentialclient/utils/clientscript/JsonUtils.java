package me.senseiwells.essentialclient.utils.clientscript;

import com.google.gson.*;
import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.ValuePair;
import me.senseiwells.arucas.utils.impl.ArucasList;
import me.senseiwells.arucas.utils.impl.ArucasMap;
import me.senseiwells.arucas.values.*;

import java.util.Map;

public class JsonUtils {
	public static Value toValue(Context context, JsonElement element) throws CodeError {
		if (element.isJsonPrimitive()) {
			JsonPrimitive primitive = element.getAsJsonPrimitive();
			if (primitive.isBoolean()) {
				return BooleanValue.of(primitive.getAsBoolean());
			}
			if (primitive.isNumber()) {
				return NumberValue.of(primitive.getAsDouble());
			}
			return StringValue.of(primitive.getAsString());
		}
		if (element.isJsonArray()) {
			return toList(context, element.getAsJsonArray());
		}
		if (element.isJsonObject()) {
			return toMap(context, element.getAsJsonObject());
		}
		return NullValue.NULL;
	}

	private static ListValue toList(Context context, JsonArray array) throws CodeError {
		ArucasList list = new ArucasList();
		for (JsonElement element : array) {
			list.add(toValue(context, element));
		}
		return new ListValue(list);
	}

	private static MapValue toMap(Context context, JsonObject object) throws CodeError {
		ArucasMap map = new ArucasMap();
		for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
			map.put(context, StringValue.of(entry.getKey()), toValue(context, entry.getValue()));
		}
		return new MapValue(map);
	}

	public static JsonElement fromValue(Context context, Value value) throws CodeError {
		return fromValue(context, value, 0);
	}

	private static JsonElement fromValue(Context context, Value value, int depth) throws CodeError {
		if (depth > 100) {
			throw new RuntimeError("Json serialisation went too deep", ISyntax.empty(), context);
		}
		depth++;
		if (value == NullValue.NULL) {
			return JsonNull.INSTANCE;
		}
		if (value instanceof BooleanValue booleanValue) {
			return new JsonPrimitive(booleanValue.value);
		}
		if (value instanceof NumberValue numberValue) {
			return new JsonPrimitive(numberValue.value);
		}
		if (value instanceof MapValue mapValue) {
			return fromMap(context, mapValue, depth);
		}
		if (value instanceof ListValue listValue) {
			return fromList(context, listValue, depth);
		}
		return new JsonPrimitive(value.getAsString(context));
	}

	private static JsonArray fromList(Context context, ListValue list, int depth) throws CodeError {
		JsonArray array = new JsonArray();
		for (Value value : list.value) {
			array.add(fromValue(context, value, depth));
		}
		return array;
	}

	private static JsonObject fromMap(Context context, MapValue map, int depth) throws CodeError {
		JsonObject object = new JsonObject();
		for (ValuePair pair : map.value.pairSet()) {
			object.add(pair.getKey().getAsString(context), fromValue(context, pair.getValue(), depth));
		}
		return object;
	}
}
