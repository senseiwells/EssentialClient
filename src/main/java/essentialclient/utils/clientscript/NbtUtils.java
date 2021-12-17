package essentialclient.utils.clientscript;

import me.senseiwells.arucas.utils.ArucasValueList;
import me.senseiwells.arucas.utils.ArucasValueMap;
import me.senseiwells.arucas.values.*;
import net.minecraft.nbt.AbstractNbtNumber;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

public class NbtUtils {
	public static ArucasValueMap mapNbt(NbtCompound compound, int depth) {
		ArucasValueMap nbtMap = new ArucasValueMap();
		depth++;
		if (compound == null || depth > 10) {
			return nbtMap;
		}
		for (String tagName : compound.getKeys()) {
			NbtElement element =  compound.get(tagName);
			if (element == null) {
				continue;
			}
			nbtMap.put(new StringValue(tagName), getElement(element, depth));
		}
		return nbtMap;
	}

	private static ArucasValueList listNbt(NbtList list, int depth) {
		ArucasValueList nbtList = new ArucasValueList();
		depth++;
		if (list == null || depth > 10) {
			return nbtList;
		}
		for (NbtElement element : list) {
			nbtList.add(getElement(element, depth));
		}
		return nbtList;
	}

	private static Value<?> getElement(NbtElement element, int depth) {
		if (element instanceof NbtCompound inCompound) {
			return new MapValue(mapNbt(inCompound, depth));
		}
		if (element instanceof NbtList nbtList) {
			return new ListValue(listNbt(nbtList, depth));
		}
		if (element instanceof AbstractNbtNumber nbtNumber) {
			return new NumberValue(nbtNumber.doubleValue());
		}
		return new StringValue(element.asString());
	}
}
