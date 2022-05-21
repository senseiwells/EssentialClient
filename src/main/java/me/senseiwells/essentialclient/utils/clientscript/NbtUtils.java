package me.senseiwells.essentialclient.utils.clientscript;

import com.mojang.brigadier.StringReader;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.ValuePair;
import me.senseiwells.arucas.utils.impl.ArucasList;
import me.senseiwells.arucas.utils.impl.ArucasMap;
import me.senseiwells.arucas.utils.impl.IArucasCollection;
import me.senseiwells.arucas.values.*;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import net.minecraft.nbt.*;

import java.util.Collection;

public class NbtUtils {
	public static ArucasMap nbtToMap(Context context, NbtCompound compound, int depth) throws CodeError {
		ArucasMap nbtMap = new ArucasMap();
		depth--;
		if (compound == null || depth < 0) {
			return nbtMap;
		}
		for (String tagName : compound.getKeys()) {
			NbtElement element =  compound.get(tagName);
			if (element == null) {
				continue;
			}
			nbtMap.put(context, StringValue.of(tagName), nbtToValue(context, element, depth));
		}
		return nbtMap;
	}

	public static ArucasList nbtToList(Context context, AbstractNbtList<?> list, int depth) throws CodeError {
		ArucasList nbtList = new ArucasList();
		depth--;
		if (list == null || depth < 0) {
			return nbtList;
		}
		for (NbtElement element : list) {
			nbtList.add(nbtToValue(context, element, depth));
		}
		return nbtList;
	}

	public static Value nbtToValue(Context context, NbtElement element, int depth) throws CodeError {
		if (element instanceof NbtCompound inCompound) {
			return new MapValue(nbtToMap(context, inCompound, depth));
		}
		if (element instanceof AbstractNbtList<?> nbtList) {
			return new ListValue(nbtToList(context, nbtList, depth));
		}
		if (element instanceof AbstractNbtNumber nbtNumber) {
			return NumberValue.of(nbtNumber.doubleValue());
		}
		if (element == NbtNull.INSTANCE) {
			return NullValue.NULL;
		}
		return StringValue.of(element.asString());
	}

	public static NbtCompound mapToNbt(Context context, ArucasMap map, int depth) throws CodeError {
		NbtCompound compound = new NbtCompound();
		if (map == null || depth < 0) {
			return compound;
		}
		for (ValuePair values : map.pairSet()) {
			compound.put(values.getKey().getAsString(context), valueToNbt(context, values.getValue(), depth));
		}
		return compound;
	}

	public static NbtList collectionToNbt(Context context, Collection<? extends Value> collection, int depth) throws CodeError {
		NbtList list = new NbtList();
		if (collection == null || depth < 0) {
			return list;
		}
		for (Value value : collection) {
			NbtElement element = valueToNbt(context, value, depth);
			// Doing it like this avoids the throwing of an error
			list.addElement(list.size(), element);
		}
		return list;
	}

	public static NbtElement valueToNbt(Context context, Value value, int depth) throws CodeError {
		if (value.getValue() instanceof ArucasMap map) {
			return mapToNbt(context, map, depth);
		}
		if (value.getValue() instanceof IArucasCollection collection) {
			return collectionToNbt(context, collection.asCollection(), depth);
		}
		if (value instanceof NumberValue numberValue) {
			return NbtDouble.of(numberValue.value);
		}
		if (value == NullValue.NULL) {
			return NbtNull.INSTANCE;
		}
		return NbtString.of(value.getAsString(context));
	}

	public static NbtElement rawStringToNbt(String string) {
		return EssentialUtils.throwAsRuntime(() -> new StringNbtReader(new StringReader(string)).parseElement());
	}
}
