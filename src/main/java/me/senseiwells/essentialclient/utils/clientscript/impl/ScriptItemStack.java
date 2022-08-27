package me.senseiwells.essentialclient.utils.clientscript.impl;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ScriptItemStack implements ScriptMaterial {
	public final ItemStack stack;

	public ScriptItemStack(ItemStack itemStack) {
		this.stack = itemStack;
	}

	@Override
	public Item asItem() {
		return this.stack.getItem();
	}

	@Override
	public ItemStack asItemStack() {
		return this.stack;
	}

	@Override
	public Object asDefault() {
		return this.stack;
	}

	@Override
	public String asString() {
		return this.stack.toString();
	}
}
