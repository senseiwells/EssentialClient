package essentialclient.utils.clientscript;

import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.impl.ArucasList;
import me.senseiwells.arucas.utils.impl.ArucasMap;
import me.senseiwells.arucas.values.*;
import net.minecraft.nbt.AbstractNbtNumber;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtByteArray;
import net.minecraft.nbt.NbtByte;

import java.util.stream.Collectors;

public class NbtUtils {
	public static ArucasMap mapNbt(Context context, NbtCompound compound, int depth) throws CodeError {
		ArucasMap nbtMap = new ArucasMap();
		depth++;
		if (compound == null || depth > 10) {
			return nbtMap;
		}
		for (String tagName : compound.getKeys()) {
			NbtElement element =  compound.get(tagName);
			if (element == null) {
				continue;
			}
			nbtMap.put(context, StringValue.of(tagName), getElement(context, element, depth));
		}
		return nbtMap;
	}

	private static ArucasList listNbt(Context context, NbtList list, int depth) throws CodeError {
		ArucasList nbtList = new ArucasList();
		depth++;
		if (list == null || depth > 10) {
			return nbtList;
		}
		for (NbtElement element : list) {
			nbtList.add(getElement(context, element, depth));
		}
		return nbtList;
	}

	private static Value<?> getElement(Context context, NbtElement element, int depth) throws CodeError {
		if (element instanceof NbtCompound inCompound) {
			return new MapValue(mapNbt(context, inCompound, depth));
		}
		if (element instanceof NbtList nbtList) {
			return new ListValue(listNbt(context, nbtList, depth));
		}
		if (element instanceof AbstractNbtNumber nbtNumber) {
			return NumberValue.of(nbtNumber.doubleValue());
		}
		if (element instanceof NbtByteArray byteArray){
			return new ListValue(byteArray.stream().map(a->NumberValue.of(a.byteValue())).collect(Collectors.toCollection(ArucasList::new)));
		}
		return StringValue.of(element.asString());
	}
}
