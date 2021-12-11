package essentialclient.clientscript.values;

import me.senseiwells.arucas.values.Value;
import net.minecraft.item.ItemStack;

public class ItemStackValue extends Value<ItemStack> {
	public ItemStackValue(ItemStack itemStack) {
		super(itemStack);
	}

	@Override
	public Value<ItemStack> copy() {
		return new ItemStackValue(this.value.copy());
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof ItemStackValue otherValue)) {
			return false;
		}
		else {
			return this.value != null && otherValue.value != null ? this.value.isItemEqual(otherValue.value) : this.value == otherValue.value;
		}
	}

	@Override
	public String toString() {
		return "ItemStack{id=%s}".formatted(this.value.getItem().toString());
	}
}
