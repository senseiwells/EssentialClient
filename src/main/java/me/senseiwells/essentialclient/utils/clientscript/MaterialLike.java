package me.senseiwells.essentialclient.utils.clientscript;

import me.senseiwells.arucas.throwables.BuiltInException;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public interface MaterialLike {
	Item asItem();

	default ItemStack asItemStack() {
		return this.asItem().getDefaultStack();
	}

	default Block asBlock() {
		Item item = this.asItem();
		if (item instanceof BlockItem blockItem) {
			return blockItem.getBlock();
		}
		throw new BuiltInException("Material cannot be converted into a block");
	}
}
