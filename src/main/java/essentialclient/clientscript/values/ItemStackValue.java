package essentialclient.clientscript.values;

import essentialclient.mixins.clientScript.NbtListMixin;
import essentialclient.utils.clientscript.NbtUtils;
import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.impl.ArucasMap;
import me.senseiwells.arucas.values.*;
import me.senseiwells.arucas.values.functions.BuiltInFunction;
import me.senseiwells.arucas.values.functions.MemberFunction;
import net.minecraft.block.BlockState;
import net.minecraft.client.resource.language.I18n;
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

public class ItemStackValue extends Value<ItemStack> {
	public ItemStackValue(ItemStack itemStack) {
		super(itemStack);
	}

	@Override
	public Value<ItemStack> copy(Context context) {
		return new ItemStackValue(this.value.copy());
	}

	@Override
	public boolean isEquals(Context context, Value<?> other) {
		if (!(other instanceof ItemStackValue otherValue)) {
			return false;
		}
		if (this.value == otherValue.value) {
			return true;
		}
		return this.value != null && otherValue.value != null && this.value.isItemEqual(otherValue.value);
	}

	@Override
	public String getAsString(Context context) {
		return "ItemStack{id=" + this.value.getItem().toString() + ", count=" + this.value.getCount() + "}";
	}

	@Override
	public int getHashCode(Context context) {
		return this.getAsString(context).hashCode();
	}

	public static class ArucasItemStackClass extends ArucasClassExtension {

		public ArucasItemStackClass() {
			super("ItemStack");
		}

		@Override
		public ArucasFunctionMap<BuiltInFunction> getDefinedStaticMethods() {
			return ArucasFunctionMap.of(
				new BuiltInFunction("of", "material", this::of)
			);
		}

		private Value<?> of(Context context, BuiltInFunction function) throws CodeError {
			MaterialValue materialValue = function.getParameterValueOfType(context, MaterialValue.class, 0);
			return new ItemStackValue(materialValue.value.getDefaultStack());
		}

		@Override
		public ArucasFunctionMap<MemberFunction> getDefinedMethods() {
			return ArucasFunctionMap.of(
				new MemberFunction("getMaterial", (context, function) -> new MaterialValue(this.getItemStack(context, function).getItem())),
				new MemberFunction("getId", (context, function) -> StringValue.of(Registry.ITEM.getId(this.getItemStack(context, function).getItem()).getPath())),
				new MemberFunction("getCount", (context, function) -> NumberValue.of(this.getItemStack(context, function).getCount())),
				new MemberFunction("getDurability", this::getDurability),
				new MemberFunction("getMaxDurability", (context, function) -> NumberValue.of(this.getItemStack(context, function).getMaxDamage())),
				new MemberFunction("getEnchantments", this::getEnchantments),
				new MemberFunction("isBlockItem", (context, function) -> BooleanValue.of(this.getItemStack(context, function).getItem() instanceof BlockItem)),
				new MemberFunction("isStackable", (context, function) -> BooleanValue.of(this.getItemStack(context, function).isStackable())),
				new MemberFunction("getMaxCount", (context, function) -> NumberValue.of(this.getItemStack(context, function).getMaxCount())),
				new MemberFunction("asBlock", this::asBlock),
				new MemberFunction("getCustomName", (context, function) -> StringValue.of(this.getItemStack(context, function).getName().asString())),
				new MemberFunction("isNbtEqual", "otherItem", this::isNbtEqual),
				new MemberFunction("getNbt", this::getNbt),
				new MemberFunction("getTranslatedName", this::getTranslatedName),
				new MemberFunction("getMiningSpeedMultiplier", "block", this::getMiningSpeedMultiplier),

				new MemberFunction("setCustomName", "name", this::setCustomName),
				new MemberFunction("setItemLore", "text", this::setLore)
			);
		}

		private Value<?> getDurability(Context context, MemberFunction function) throws CodeError {
			ItemStack itemStack = this.getItemStack(context, function);
			return NumberValue.of(itemStack.getMaxDamage() - itemStack.getDamage());
		}

		private Value<?> getEnchantments(Context context, MemberFunction function) throws CodeError {
			ItemStack itemStack = this.getItemStack(context, function);
			NbtList nbtList = itemStack.getItem() == Items.ENCHANTED_BOOK ? EnchantedBookItem.getEnchantmentNbt(itemStack) : itemStack.getEnchantments();
			ArucasMap enchantmentMap = new ArucasMap();
			for (Map.Entry<Enchantment, Integer> entry : EnchantmentHelper.fromNbt(nbtList).entrySet()) {
				Identifier enchantmentId = Registry.ENCHANTMENT.getId(entry.getKey());
				enchantmentMap.put(context, enchantmentId == null ? NullValue.NULL : StringValue.of(enchantmentId.getPath()), NumberValue.of(entry.getValue()));
			}
			return new MapValue(enchantmentMap);
		}

		private Value<?> asBlock(Context context, MemberFunction function) throws CodeError {
			ItemStack itemStack = this.getItemStack(context, function);
			if (!(itemStack.getItem() instanceof BlockItem blockItem)) {
				throw new RuntimeError("Item cannot be converted to block", function.syntaxPosition, context);
			}
			return new BlockValue(blockItem.getBlock().getDefaultState());
		}

		private Value<?> isNbtEqual(Context context, MemberFunction function) throws CodeError {
			ItemStack itemStack = this.getItemStack(context, function);
			ItemStack otherItemStack = function.getParameterValueOfType(context, ItemStackValue.class, 1).value;
			return BooleanValue.of(ItemStack.areNbtEqual(itemStack, otherItemStack));
		}

		private Value<?> getNbt(Context context, MemberFunction function) throws CodeError {
			ItemStack itemStack = this.getItemStack(context, function);
			NbtCompound nbtCompound = itemStack.getNbt();
			ArucasMap nbtMap = NbtUtils.mapNbt(context, nbtCompound, 0);
			return new MapValue(nbtMap);
		}

		private Value<?> getTranslatedName(Context context, MemberFunction function) throws CodeError {
			ItemStack itemStack = this.getItemStack(context, function);
			return StringValue.of(I18n.translate(itemStack.getItem().getTranslationKey()));
		}

		private Value<?> getMiningSpeedMultiplier(Context context, MemberFunction function) throws CodeError {
			ItemStack itemStack = this.getItemStack(context, function);
			BlockState blockState = function.getParameterValueOfType(context, BlockValue.class, 1).value;
			return NumberValue.of(itemStack.getMiningSpeedMultiplier(blockState));
		}

		private Value<?> setCustomName(Context context, MemberFunction function) throws CodeError {
			ItemStack itemStack = this.getItemStack(context, function);
			Value<?> nameValue = function.getParameterValue(context, 1);
			Text name = nameValue instanceof TextValue textValue ? textValue.value : new LiteralText(nameValue.getAsString(context));
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

		@Override
		public Class<?> getValueClass() {
			return ItemStackValue.class;
		}
	}
}
