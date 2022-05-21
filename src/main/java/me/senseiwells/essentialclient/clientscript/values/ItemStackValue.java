package me.senseiwells.essentialclient.clientscript.values;

import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.Arguments;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.impl.ArucasMap;
import me.senseiwells.arucas.values.*;
import me.senseiwells.arucas.values.functions.BuiltInFunction;
import me.senseiwells.arucas.values.functions.MemberFunction;
import me.senseiwells.essentialclient.clientscript.extensions.ArucasMinecraftExtension;
import me.senseiwells.essentialclient.mixins.clientScript.NbtListMixin;
import me.senseiwells.essentialclient.utils.clientscript.NbtUtils;
import net.minecraft.block.BlockState;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
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

import static me.senseiwells.essentialclient.clientscript.core.MinecraftAPI.ITEM_STACK;

public class ItemStackValue extends GenericValue<ItemStack> {
	public ItemStackValue(ItemStack itemStack) {
		super(itemStack);
	}

	@Override
	public GenericValue<ItemStack> copy(Context context) {
		return new ItemStackValue(this.value.copy());
	}

	@Override
	public boolean isEquals(Context context, Value other) {
		if (!(other instanceof ItemStackValue otherValue)) {
			return false;
		}
		if (this.value == otherValue.value) {
			return true;
		}
		return this.value != null && otherValue.value != null && this.value.isItemEqual(otherValue.value);
	}

	@Override
	public String getTypeName() {
		return ITEM_STACK;
	}

	@Override
	public String getAsString(Context context) {
		return "ItemStack{id=" + this.value.getItem().toString() + ", count=" + this.value.getCount() + "}";
	}

	@Override
	public int getHashCode(Context context) {
		return this.getAsString(context).hashCode();
	}

	/**
	 * ItemStack class for Arucas. <br>
	 * Import the class with <code>import ItemStack from Minecraft;</code> <br>
	 * Fully Documented.
	 *
	 * @author senseiwells
	 */
	public static class ArucasItemStackClass extends ArucasClassExtension {
		public ArucasItemStackClass() {
			super(ITEM_STACK);
		}

		@Override
		public ArucasFunctionMap<BuiltInFunction> getDefinedStaticMethods() {
			return ArucasFunctionMap.of(
				BuiltInFunction.of("of", 1, this::of),
				BuiltInFunction.of("parse", 1, this::parse)
			);
		}

		/**
		 * Name: <code>ItemStack.of(material)</code> <br>
		 * Description: This creates an ItemStack from a material or a string <br>
		 * Parameter - String: The material or string to create the ItemStack from <br>
		 * Returns - ItemStack: the new ItemStack instance <br>
		 * Example: <code>ItemStack.of("dirt");</code>
		 */
		private Value of(Arguments arguments) throws CodeError {
			Value value = arguments.getNext();
			if (value instanceof StringValue stringValue) {
				Identifier id = ArucasMinecraftExtension.getId(arguments, stringValue.value);
				return new ItemStackValue(Registry.ITEM.getOrEmpty(id).orElseThrow(
					() -> arguments.getError("'%s' is not a valid item stack", id)
				).getDefaultStack());
			}
			if (!(value instanceof MaterialValue materialValue)) {
				throw arguments.getError("Parameter must be of type String or Material");
			}
			return new ItemStackValue(materialValue.asItemStack(arguments));
		}

		/**
		 * Name: <code>ItemStack.parse(nbtString)</code> <br>
		 * Description: This creates an ItemStack from a NBT string <br>
		 * Parameter - String: The NBT string to create the ItemStack from <br>
		 * Returns - ItemStack: the new ItemStack instance <br>
		 * Example: <code>ItemStack.parse("{id:\"minecraft:dirt\",Count:64}");</code>
		 */
		private Value parse(Arguments arguments) throws CodeError {
			MapValue mapValue = arguments.getNextMap();
			return new ItemStackValue(ItemStack.fromNbt(NbtUtils.mapToNbt(arguments.getContext(), mapValue.value, 10)));
		}

		@Override
		public ArucasFunctionMap<MemberFunction> getDefinedMethods() {
			return ArucasFunctionMap.of(
				MemberFunction.of("getMaterial", this::getMaterial),
				MemberFunction.of("getFullId", this::getFullId),
				MemberFunction.of("getId", this::getId),
				MemberFunction.of("getCount", this::getCount),
				MemberFunction.of("getDurability", this::getDurability),
				MemberFunction.of("getMaxDurability", this::getMaxDurability),
				MemberFunction.of("getEnchantments", this::getEnchantments),
				MemberFunction.of("isBlockItem", this::isBlockItem),
				MemberFunction.of("isStackable", this::isStackable),
				MemberFunction.of("getMaxCount", this::getMaxCount),
				MemberFunction.of("asBlock", this::asBlock),
				MemberFunction.of("asEntity", this::asEntity),
				MemberFunction.of("getCustomName", this::getCustomName),
				MemberFunction.of("isNbtEqual", 1, this::isNbtEqual),
				MemberFunction.of("getNbt", this::getNbt),
				MemberFunction.of("getTranslatedName", this::getTranslatedName),
				MemberFunction.of("getMiningSpeedMultiplier", 1, this::getMiningSpeedMultiplier),

				MemberFunction.of("setCustomName", 1, this::setCustomName),
				MemberFunction.of("setStackSize", 1, this::setStackSize),
				MemberFunction.of("setItemLore", 1, this::setLore),
				MemberFunction.of("setNbt", 1, this::setNbt)
			);
		}

		/**
		 * Name: <code>&lt;ItemStack>.getMaterial()</code> <br>
		 * Description: This gets the material of the ItemStack <br>
		 * Returns - Material: the material of the ItemStack <br>
		 * Example: <code>itemStack.getMaterial();</code>
		 */
		private Value getMaterial(Arguments arguments) throws CodeError {
			return new MaterialValue(this.getItemStack(arguments).getItem());
		}

		/**
		 * Name: <code>&lt;ItemStack>.getFullId()</code> <br>
		 * Description: This gets the full id of the ItemStack, for example:
		 * <code>'diamond_sword'</code> <br>
		 * Returns - String: the full id of the ItemStack <br>
		 * Example: <code>itemStack.getFullId();</code>
		 */
		private Value getFullId(Arguments arguments) throws CodeError {
			return StringValue.of(Registry.ITEM.getId(this.getItemStack(arguments).getItem()).toString());
		}

		/**
		 * Name: <code>&lt;ItemStack>.getId()</code> <br>
		 * Description: This gets the full id of the ItemStack, for example:
		 * <code>'minecraft:diamond_sword'</code> <br>
		 * Returns - String: the id of the ItemStack <br>
		 * Example: <code>itemStack.getId();</code>
		 */
		private Value getId(Arguments arguments) throws CodeError {
			return StringValue.of(Registry.ITEM.getId(this.getItemStack(arguments).getItem()).getPath());
		}

		/**
		 * Name: <code>&lt;ItemStack>.getCount()</code> <br>
		 * Description: This gets the count of the ItemStack, the amount of items in the stack <br>
		 * Returns - Number: the count of the ItemStack <br>
		 * Example: <code>itemStack.getCount();</code>
		 */
		private Value getCount(Arguments arguments) throws CodeError {
			return NumberValue.of(this.getItemStack(arguments).getCount());
		}

		/**
		 * Name: <code>&lt;ItemStack>.getDurability()</code> <br>
		 * Description: This gets the durability of the item <br>
		 * Returns - Number: the durability of the item <br>
		 * Example: <code>itemStack.getDurability();</code>
		 */
		private Value getDurability(Arguments arguments) throws CodeError {
			ItemStack itemStack = this.getItemStack(arguments);
			return NumberValue.of(itemStack.getMaxDamage() - itemStack.getDamage());
		}

		/**
		 * Name: <code>&lt;ItemStack>.getMaxDurability()</code> <br>
		 * Description: This gets the max durability of the item <br>
		 * Returns - Number: the max durability of the item <br>
		 * Example: <code>itemStack.getMaxDurability();</code>
		 */
		private Value getMaxDurability(Arguments arguments) throws CodeError {
			return NumberValue.of(this.getItemStack(arguments).getMaxDamage());
		}

		/**
		 * Name: <code>&lt;ItemStack>.getEnchantments()</code> <br>
		 * Description: This gets the enchantments of the item, in a map containing the
		 * id of the enchantment as the key and the level of the enchantment as the value <br>
		 * Returns - Map: the enchantments of the item, map may be empty <br>
		 * Example: <code>itemStack.getEnchantments();</code>
		 */
		private Value getEnchantments(Arguments arguments) throws CodeError {
			ItemStack itemStack = this.getItemStack(arguments);
			NbtList nbtList = itemStack.getItem() == Items.ENCHANTED_BOOK ? EnchantedBookItem.getEnchantmentNbt(itemStack) : itemStack.getEnchantments();
			ArucasMap enchantmentMap = new ArucasMap();
			for (Map.Entry<Enchantment, Integer> entry : EnchantmentHelper.fromNbt(nbtList).entrySet()) {
				Identifier enchantmentId = Registry.ENCHANTMENT.getId(entry.getKey());
				enchantmentMap.put(arguments.getContext(), enchantmentId == null ? NullValue.NULL : StringValue.of(enchantmentId.getPath()), NumberValue.of(entry.getValue()));
			}
			return new MapValue(enchantmentMap);
		}

		/**
		 * Name: <code>&lt;ItemStack>.isBlockItem()</code> <br>
		 * Description: This checks if the ItemStack can be placed as a block <br>
		 * Returns - Boolean: true if the ItemStack can be placed as a block, false otherwise <br>
		 * Example: <code>itemStack.isBlockItem();</code>
		 */
		private Value isBlockItem(Arguments arguments) throws CodeError {
			return BooleanValue.of(this.getItemStack(arguments).getItem() instanceof BlockItem);
		}

		/**
		 * Name: <code>&lt;ItemStack>.isStackable()</code> <br>
		 * Description: This checks if the ItemStack is stackable <br>
		 * Returns - Boolean: true if the ItemStack is stackable, false otherwise <br>
		 * Example: <code>itemStack.isStackable();</code>
		 */
		private Value isStackable(Arguments arguments) throws CodeError {
			return BooleanValue.of(this.getItemStack(arguments).isStackable());
		}

		/**
		 * Name: <code>&lt;ItemStack>.getMaxCount()</code> <br>
		 * Description: This gets the max stack size of the ItemStack <br>
		 * Returns - Number: the max stack size of the ItemStack <br>
		 * Example: <code>itemStack.getMaxCount();</code>
		 */
		private Value getMaxCount(Arguments arguments) throws CodeError {
			return NumberValue.of(this.getItemStack(arguments).getMaxCount());
		}

		/**
		 * Name: <code>&lt;ItemStack>.asBlock()</code> <br>
		 * Description: This gets the block of the ItemStack <br>
		 * Returns - Block: the block item of the ItemStack <br>
		 * Throws - Error: <code>"Item cannot be converted to a block"</code> if the ItemStack cannot be placed as a block <br>
		 * Example: <code>itemStack.asBlock();</code>
		 */
		private Value asBlock(Arguments arguments) throws CodeError {
			ItemStack itemStack = this.getItemStack(arguments);
			if (!(itemStack.getItem() instanceof BlockItem blockItem)) {
				throw arguments.getError("Item cannot be converted to block");
			}
			return new BlockValue(blockItem.getBlock().getDefaultState());
		}

		/**
		 * Name: <code>&lt;ItemStack>.asEntity()</code> <br>
		 * Description: This creates an item entity with the item <br>
		 * Returns - ItemEntity: the entity of the ItemStack <br>
		 * Throws - Error: <code>"Item cannot be converted to an ItemEntity"</code> if the ItemStack cannot be converted to an entity <br>
		 * Example: <code>itemStack.asEntity();</code>
		 */
		private Value asEntity(Arguments arguments) throws CodeError {
			ItemStack itemStack = this.getItemStack(arguments);
			ClientWorld world = ArucasMinecraftExtension.getWorld();
			ItemEntity itemEntity = EntityType.ITEM.create(world);
			if (itemEntity == null) {
				throw arguments.getError("'%s' cannot be converted into ItemEntity", itemStack);
			}
			itemEntity.setStack(itemStack);
			return new ItemEntityValue(itemEntity);
		}

		/**
		 * Name: <code>&lt;ItemStack>.getCustomName()</code> <br>
		 * Description: This gets the custom name of the ItemStack <br>
		 * Returns - String: the custom name of the ItemStack <br>
		 * Example: <code>itemStack.getCustomName();</code>
		 */
		private Value getCustomName(Arguments arguments) throws CodeError {
			return StringValue.of(this.getItemStack(arguments).getName().asString());
		}

		/**
		 * Name: <code>&lt;ItemStack>.isNbtEqual(itemStack)</code> <br>
		 * Description: This checks if the ItemStack has the same NBT data as the other given ItemStack <br>
		 * Parameter - ItemStack: the other ItemStack to compare to <br>
		 * Returns - Boolean: true if the ItemStack has the same NBT data as the other given ItemStack <br>
		 * Example: <code>itemStack.isNbtEqual(Material.GOLD_INGOT.asItemStack());</code>
		 */
		private Value isNbtEqual(Arguments arguments) throws CodeError {
			ItemStack itemStack = this.getItemStack(arguments);
			ItemStack otherItemStack = arguments.getNextGeneric(ItemStackValue.class);
			return BooleanValue.of(ItemStack.areNbtEqual(itemStack, otherItemStack));
		}

		/**
		 * Name: <code>&lt;ItemStack>.getNbt()</code> <br>
		 * Description: This gets the NBT data of the ItemStack as a Map <br>
		 * Returns - Map: the NBT data of the ItemStack <br>
		 * Example: <code>itemStack.getNbt();</code>
		 */
		private Value getNbt(Arguments arguments) throws CodeError {
			ItemStack itemStack = this.getItemStack(arguments);
			NbtCompound nbtCompound = itemStack.getNbt();
			ArucasMap nbtMap = NbtUtils.nbtToMap(arguments.getContext(), nbtCompound, 10);
			return new MapValue(nbtMap);
		}

		/**
		 * Name: <code>&lt;ItemStack>.getTranslatedName()</code> <br>
		 * Description: This gets the translated name of the ItemStack, for example
		 * <code>'diamond_sword'</code> would return <code>'Diamond Sword'</code> if your language is English <br>
		 * Returns - String: the translated name of the ItemStack <br>
		 * Example: <code>itemStack.getTranslatedName();</code>
		 */
		private Value getTranslatedName(Arguments arguments) throws CodeError {
			ItemStack itemStack = this.getItemStack(arguments);
			return StringValue.of(I18n.translate(itemStack.getItem().getTranslationKey()));
		}

		/**
		 * Name: <code>&lt;ItemStack>.getMiningSpeedMultiplier(block)</code> <br>
		 * Description: This gets the mining speed multiplier of the ItemStack for the given Block,
		 * for example a diamond pickaxe on stone would have a higher multiplier than air on stone <br>
		 * Parameter - Block: the Block to get the mining speed multiplier for <br>
		 * Returns - Number: the mining speed multiplier of the ItemStack for the given Block <br>
		 * Example: <code>Material.DIAMOND_PICKAXE.asItemStack().getMiningSpeedMultiplier(Material.GOLD_BLOCK.asBlock());</code>
		 */
		private Value getMiningSpeedMultiplier(Arguments arguments) throws CodeError {
			ItemStack itemStack = this.getItemStack(arguments);
			BlockState blockState = arguments.getNextGeneric(BlockValue.class);
			return NumberValue.of(itemStack.getMiningSpeedMultiplier(blockState));
		}

		/**
		 * Name: <code>&lt;ItemStack>.setCustomName(customName)</code> <br>
		 * Description: This sets the custom name of the ItemStack <br>
		 * Parameter - String/Text: the custom name of the ItemStack <br>
		 * Returns - ItemStack: the ItemStack with the new custom name <br>
		 * Example: <code>Material.DIAMOND_PICKAXE.asItemStack().setCustomName('My Pickaxe');</code>
		 */
		private Value setCustomName(Arguments arguments) throws CodeError {
			ItemStackValue itemStackValue = arguments.getNext(ItemStackValue.class);
			Value nameValue = arguments.getNext();
			Text name = nameValue instanceof TextValue textValue ? textValue.value : new LiteralText(nameValue.getAsString(arguments.getContext()));
			itemStackValue.value.setCustomName(name);
			return itemStackValue;
		}

		/**
		 * Name: <code>&lt;ItemStack>.setStackSize(customName)</code> <br>
		 * Description: This sets the stack size of the ItemStack <br>
		 * Parameter - Number: the stack size of the ItemStack <br>
		 * Returns - ItemStack: the ItemStack with the new stack size <br>
		 * Example: <code>Material.DIAMOND_PICKAXE.asItemStack().setStackSize(5);</code>
		 */
		private Value setStackSize(Arguments arguments) throws CodeError {
			ItemStackValue itemStackValue = arguments.getNext(ItemStackValue.class);
			NumberValue numberValue = arguments.getNextNumber();
			itemStackValue.value.setCount(numberValue.value.intValue());
			return itemStackValue;
		}

		private Value setLore(Arguments arguments) throws CodeError {
			ItemStackValue itemStackValue = arguments.getNext(ItemStackValue.class);
			ListValue listValue = arguments.getNextList();
			List<NbtElement> textList = new ArrayList<>();
			for (Value value : listValue.value) {
				if (!(value instanceof TextValue textValue)) {
					throw arguments.getError("List must contain only Text");
				}
				textList.add(NbtString.of(Text.Serializer.toJson(textValue.value)));
			}
			itemStackValue.value.getOrCreateSubNbt("display").put("Lore", NbtListMixin.createNbtList(textList, (byte) 8));

			return itemStackValue;
		}

		/**
		 * Name: <code>&lt;ItemStack>.setNbt(nbtMap)</code> <br>
		 * Description: This sets the NBT data of the ItemStack <br>
		 * Returns - ItemStack: the ItemStack with the new NBT data <br>
		 * Example: <code>itemStack.setNbt({"Enchantments": []});</code>
		 */
		private Value setNbt(Arguments arguments) throws CodeError {
			ItemStack itemStack = this.getItemStack(arguments);
			MapValue mapValue = arguments.getNextMap();
			itemStack.setNbt(NbtUtils.mapToNbt(arguments.getContext(), mapValue.value, 10));
			return NullValue.NULL;
		}

		private ItemStack getItemStack(Arguments arguments) throws CodeError {
			ItemStack itemStack = arguments.getNextGeneric(ItemStackValue.class);
			if (itemStack == null) {
				throw arguments.getError("ItemStack was null");
			}
			return itemStack;
		}

		@Override
		public Class<ItemStackValue> getValueClass() {
			return ItemStackValue.class;
		}
	}
}
