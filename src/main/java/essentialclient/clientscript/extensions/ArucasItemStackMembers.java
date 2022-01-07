package essentialclient.clientscript.extensions;

import essentialclient.clientscript.values.BlockStateValue;
import essentialclient.clientscript.values.ItemStackValue;
import essentialclient.clientscript.values.TextValue;
import essentialclient.mixins.functions.NbtListMixin;
import essentialclient.utils.clientscript.NbtUtils;
import me.senseiwells.arucas.api.IArucasValueExtension;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.ArucasValueMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.*;
import me.senseiwells.arucas.values.functions.MemberFunction;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.BlockItem;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ArucasItemStackMembers implements IArucasValueExtension {

	@Override
	public Set<MemberFunction> getDefinedFunctions() {
		return this.MemberFunctions;
	}

	@Override
	public Class<ItemStackValue> getValueType() {
		return ItemStackValue.class;
	}

	@Override
	public String getName() {
		return "ItemStackMemberFunctions";
	}

	private final Set<MemberFunction> MemberFunctions = Set.of(
		new MemberFunction("getItemId", List.of(), (context, function) -> new StringValue(Registry.ITEM.getId(this.getItemStack(context, function).getItem()).getPath()), true),
		new MemberFunction("getId", (context, function) -> new StringValue(Registry.ITEM.getId(this.getItemStack(context, function).getItem()).getPath())),
		new MemberFunction("getCount", (context, function) -> new NumberValue(this.getItemStack(context, function).getCount())),
		new MemberFunction("getDurability", this::getDurability),
		new MemberFunction("getMaxDurability", (context, function) -> new NumberValue(this.getItemStack(context, function).getMaxDamage())),
		new MemberFunction("getEnchantments", this::getEnchantments),
		new MemberFunction("isBlockItem", (context, function) -> BooleanValue.of(this.getItemStack(context, function).getItem() instanceof BlockItem)),
		new MemberFunction("isStackable", (context, function) -> BooleanValue.of(this.getItemStack(context, function).isStackable())),
		new MemberFunction("getMaxCount", (context, function) -> new NumberValue(this.getItemStack(context, function).getMaxCount())),
		new MemberFunction("asBlock", this::asBlock),
		new MemberFunction("getItemName", List.of(), (context, function) -> new StringValue(this.getItemStack(context, function).getName().asString()), true),
		new MemberFunction("getCustomName", (context, function) -> new StringValue(this.getItemStack(context, function).getName().asString())),
		new MemberFunction("isNbtEqual", "otherItem", this::isNbtEqual),
		new MemberFunction("getNbt", this::getNbt),
		new MemberFunction("getMiningSpeedMultiplier", "block", this::getMiningSpeedMultiplier),
		new MemberFunction("setCustomName", "name", this::setCustomName),
		new MemberFunction("setItemLore", "text", this::setLore)
	);

	private Value<?> getDurability(Context context, MemberFunction function) throws CodeError {
		ItemStack itemStack = this.getItemStack(context, function);
		return new NumberValue(itemStack.getMaxDamage() - itemStack.getDamage());
	}
	private Value<?> getMiningSpeedMultiplier(Context context, MemberFunction function) throws CodeError {
		ItemStack itemStack = this.getItemStack(context, function);
		BlockState blockState = function.getParameterValueOfType(context, BlockStateValue.class, 1).value;
		return new NumberValue(itemStack.getMiningSpeedMultiplier(blockState));
	}
	private Value<?> getEnchantments(Context context, MemberFunction function) throws CodeError {
		ItemStack itemStack = this.getItemStack(context, function);
		NbtList nbtList = itemStack.getItem() == Items.ENCHANTED_BOOK ? EnchantedBookItem.getEnchantmentNbt(itemStack) : itemStack.getEnchantments();
		ArucasValueMap enchantmentMap = new ArucasValueMap();
		for (Map.Entry<Enchantment, Integer> entry : EnchantmentHelper.fromNbt(nbtList).entrySet()) {
			Identifier enchantmentId = Registry.ENCHANTMENT.getId(entry.getKey());
			enchantmentMap.put(enchantmentId == null ? NullValue.NULL : new StringValue(enchantmentId.getPath()), new NumberValue(entry.getValue()));
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
		return BooleanValue.of(ItemStack.areNbtEqual(itemStack, otherItemStack));
	}

	private Value<?> getNbt(Context context, MemberFunction function) throws CodeError {
		ItemStack itemStack = this.getItemStack(context, function);
		NbtCompound nbtCompound = itemStack.getNbt();
		ArucasValueMap nbtMap = NbtUtils.mapNbt(nbtCompound, 0);
		return new MapValue(nbtMap);
	}

	private Value<?> setCustomName(Context context, MemberFunction function) throws CodeError {
		ItemStack itemStack = this.getItemStack(context, function);
		Value<?> nameValue = function.getParameterValue(context, 1);
		Text name = nameValue instanceof TextValue textValue ? textValue.value : new LiteralText(nameValue.toString());
		return new ItemStackValue(itemStack.setCustomName(name));
	}

	private Value<?> setLore(Context context, MemberFunction function) throws CodeError {
		ItemStack itemStack = this.getItemStack(context, function);
		ListValue listValue = function.getParameterValueOfType(context, ListValue.class, 1);
		List<NbtElement> textList = new ArrayList<>();
		for (Value<?> value : listValue.value) {
			if (!(value instanceof TextValue textValue)) {
				throw new RuntimeError("List must contain only Text", function.syntaxPosition, context);
			}
			textList.add(NbtString.of(Text.Serializer.toJson(textValue.value)));
		}
		itemStack.getOrCreateSubNbt("display").put("Lore", NbtListMixin.createNbtList(textList, (byte) 8));

		return new ItemStackValue(itemStack);
	}

	private ItemStack getItemStack(Context context, MemberFunction function) throws CodeError {
		ItemStack itemStack = function.getParameterValueOfType(context, ItemStackValue.class, 0).value;
		if (itemStack == null) {
			throw new RuntimeError("ItemStack was null", function.syntaxPosition, context);
		}
		return itemStack;
	}
}
