package essentialclient.clientscript.extensions;

import essentialclient.clientscript.values.BlockStateValue;
import essentialclient.clientscript.values.ItemStackValue;
import me.senseiwells.arucas.api.IArucasExtension;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.ArucasValueMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.*;
import me.senseiwells.arucas.values.functions.AbstractBuiltInFunction;
import me.senseiwells.arucas.values.functions.MemberFunction;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.BlockItem;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.Map;
import java.util.Set;

public class ArucasItemStackMembers implements IArucasExtension {

	@Override
	public Set<? extends AbstractBuiltInFunction<?>> getDefinedFunctions() {
		return this.MemberFunctions;
	}

	@Override
	public String getName() {
		return "ItemStackMemberFunctions";
	}

	private final Set<? extends AbstractBuiltInFunction<?>> MemberFunctions = Set.of(
		new MemberFunction("getItemId", (context, function) -> new StringValue(Registry.ITEM.getId(this.getItemStack(context, function).getItem()).getPath())),
		new MemberFunction("getCount", (context, function) -> new NumberValue(this.getItemStack(context, function).getCount())),
		new MemberFunction("getDurability", this::getDurability),
		new MemberFunction("getEnchantments", this::getEnchantments),
		new MemberFunction("isBlockItem", (context, function) -> new BooleanValue(this.getItemStack(context, function).getItem() instanceof BlockItem)),
		new MemberFunction("isStackable", (context, function) -> new BooleanValue(this.getItemStack(context, function).isStackable())),
		new MemberFunction("asBlock", this::asBlock),
		new MemberFunction("getItemName", (context, function) -> new StringValue(this.getItemStack(context, function).getName().asString())),
		new MemberFunction("isNbtEqual", "otherItem", this::isNbtEqual)
	);

	private Value<?> getDurability(Context context, MemberFunction function) throws CodeError {
		ItemStack itemStack = this.getItemStack(context, function);
		return new NumberValue(itemStack.getMaxDamage() - itemStack.getDamage());
	}

	private Value<?> getEnchantments(Context context, MemberFunction function) throws CodeError {
		ItemStack itemStack = this.getItemStack(context, function);
		NbtList nbtList = itemStack.getItem() == Items.ENCHANTED_BOOK ? EnchantedBookItem.getEnchantmentNbt(itemStack) : itemStack.getEnchantments();
		ArucasValueMap enchantmentMap = new ArucasValueMap();
		for (Map.Entry<Enchantment, Integer> entry : EnchantmentHelper.fromNbt(nbtList).entrySet()) {
			Identifier enchantmentId = Registry.ENCHANTMENT.getId(entry.getKey());
			enchantmentMap.put(enchantmentId == null ? new NullValue() : new StringValue(enchantmentId.getPath()), new NumberValue(entry.getValue()));
		}
		return new MapValue(enchantmentMap);
	}

	private Value<?> asBlock(Context context, MemberFunction function) throws CodeError {
		ItemStack itemStack = this.getItemStack(context, function);
		if (!(itemStack.getItem() instanceof BlockItem blockItem)) {
			throw new RuntimeError("Item cannot be converted to block", function.syntaxPosition, context);
		}
		return new BlockStateValue(blockItem.getBlock().getDefaultState());
	}

	private Value<?> isNbtEqual(Context context, MemberFunction function) throws CodeError {
		ItemStack itemStack = this.getItemStack(context, function);
		ItemStack otherItemStack = function.getParameterValueOfType(context, ItemStackValue.class, 1).value;
		return new BooleanValue(ItemStack.areTagsEqual(itemStack, otherItemStack));
	}

	private ItemStack getItemStack(Context context, MemberFunction function) throws CodeError {
		ItemStack itemStack = function.getParameterValueOfType(context, ItemStackValue.class, 0).value;
		if (itemStack == null) {
			throw new RuntimeError("ItemStack was null", function.syntaxPosition, context);
		}
		return itemStack;
	}
}
