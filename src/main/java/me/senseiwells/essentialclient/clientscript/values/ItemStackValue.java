package me.senseiwells.essentialclient.clientscript.values;

import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.api.docs.ClassDoc;
import me.senseiwells.arucas.api.docs.FunctionDoc;
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
import me.senseiwells.essentialclient.utils.clientscript.MaterialLike;
import me.senseiwells.essentialclient.utils.clientscript.NbtUtils;
import net.minecraft.block.BlockState;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static me.senseiwells.arucas.utils.ValueTypes.*;
import static me.senseiwells.essentialclient.clientscript.core.MinecraftAPI.*;

public class ItemStackValue extends GenericValue<ItemStack> implements MaterialLike {
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

	@Override
	public Item asItem() {
		return this.value.getItem();
	}

	@ClassDoc(
		name = ITEM_STACK,
		desc = "This class represents an item stack. It can be used to create new item stacks, or to modify existing ones.",
		importPath = "Minecraft"
	)
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

		@FunctionDoc(
			isStatic = true,
			name = "of",
			desc = "This creates an ItemStack from a material or a string",
			params = {MATERIAL_LIKE, "material", "the material, item stack, block, or string to create the ItemStack from"},
			returns = {ITEM_STACK, "the new ItemStack instance"},
			example = "ItemStack.of('dirt');"
		)
		private Value of(Arguments arguments) throws CodeError {
			Value value = arguments.getNext();
			if (value instanceof StringValue stringValue) {
				Identifier id = ArucasMinecraftExtension.getId(arguments, stringValue.value);
				return new ItemStackValue(Registry.ITEM.getOrEmpty(id).orElseThrow(
					() -> arguments.getError("'%s' is not a valid item stack", id)
				).getDefaultStack());
			}
			if (!(value instanceof MaterialLike materialLike)) {
				throw arguments.getError("Parameter must be of type String or Material");
			}
			return new ItemStackValue(materialLike.asItemStack());
		}

		@FunctionDoc(
			isStatic = true,
			name = "parse",
			desc = "This creates an ItemStack from a NBT string",
			params = {STRING, "nbtString", "the NBT string to create the ItemStack from"},
			returns = {ITEM_STACK, "the new ItemStack instance"},
			example = "ItemStack.parse('{id:\"minecraft:dirt\",Count:64}')"
		)
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

		@FunctionDoc(
			name = "getMaterial",
			desc = "This gets the material of the ItemStack",
			returns = {MATERIAL, "the material of the ItemStack"},
			example = "itemStack.getMaterial();"
		)
		private Value getMaterial(Arguments arguments) throws CodeError {
			return new MaterialValue(this.getItemStack(arguments).getItem());
		}

		@FunctionDoc(
			name = "getFullId",
			desc = "This gets the full id of the ItemStack, for example: 'minecraft:diamond_sword'",
			returns = {STRING, "the full id of the ItemStack"},
			example = "itemStack.getFullId();"
		)
		private Value getFullId(Arguments arguments) throws CodeError {
			return StringValue.of(Registry.ITEM.getId(this.getItemStack(arguments).getItem()).toString());
		}

		@FunctionDoc(
			name = "getId",
			desc = "This gets the id of the ItemStack, for example: 'diamond_sword'",
			returns = {STRING, "the id of the ItemStack"},
			example = "itemStack.getId();"
		)
		private Value getId(Arguments arguments) throws CodeError {
			return StringValue.of(Registry.ITEM.getId(this.getItemStack(arguments).getItem()).getPath());
		}

		@FunctionDoc(
			name = "getCount",
			desc = "This gets the count of the ItemStack, the amount of items in the stack",
			returns = {NUMBER, "the count of the ItemStack"},
			example = "itemStack.getCount();"
		)
		private Value getCount(Arguments arguments) throws CodeError {
			return NumberValue.of(this.getItemStack(arguments).getCount());
		}

		@FunctionDoc(
			name = "getDurability",
			desc = "This gets the durability of the item",
			returns = {NUMBER, "the durability of the item"},
			example = "itemStack.getDurability();"
		)
		private Value getDurability(Arguments arguments) throws CodeError {
			ItemStack itemStack = this.getItemStack(arguments);
			return NumberValue.of(itemStack.getMaxDamage() - itemStack.getDamage());
		}

		@FunctionDoc(
			name = "getMaxDurability",
			desc = "This gets the max durability of the item",
			returns = {NUMBER, "the max durability of the item"},
			example = "itemStack.getMaxDurability();"
		)
		private Value getMaxDurability(Arguments arguments) throws CodeError {
			return NumberValue.of(this.getItemStack(arguments).getMaxDamage());
		}

		@FunctionDoc(
			name = "getEnchantments",
			desc = {
				"This gets the enchantments of the item, in a map containing the",
				"id of the enchantment as the key and the level of the enchantment as the value"
			},
			returns = {MAP, "the enchantments of the item, map may be empty"},
			example = "itemStack.getEnchantments();"
		)
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

		@FunctionDoc(
			name = "isBlockItem",
			desc = "This checks if the ItemStack can be placed as a block",
			returns = {BOOLEAN, "true if the ItemStack can be placed as a block, false otherwise"},
			example = "itemStack.isBlockItem();"
		)
		private Value isBlockItem(Arguments arguments) throws CodeError {
			return BooleanValue.of(this.getItemStack(arguments).getItem() instanceof BlockItem);
		}

		@FunctionDoc(
			name = "isStackable",
			desc = "This checks if the ItemStack is stackable",
			returns = {BOOLEAN, "true if the ItemStack is stackable, false otherwise"},
			example = "itemStack.isStackable();"
		)
		private Value isStackable(Arguments arguments) throws CodeError {
			return BooleanValue.of(this.getItemStack(arguments).isStackable());
		}

		@FunctionDoc(
			name = "getMaxCount",
			desc = "This gets the max stack size of the ItemStack",
			returns = {NUMBER, "the max stack size of the ItemStack"},
			example = "itemStack.getMaxCount();"
		)
		private Value getMaxCount(Arguments arguments) throws CodeError {
			return NumberValue.of(this.getItemStack(arguments).getMaxCount());
		}

		@FunctionDoc(
			name = "asBlock",
			desc = "This gets the block of the ItemStack",
			returns = {BLOCK, "the block item of the ItemStack"},
			throwMsgs = "Item cannot be converted to a block",
			example = "itemStack.asBlock();"
		)
		private Value asBlock(Arguments arguments) throws CodeError {
			ItemStack itemStack = this.getItemStack(arguments);
			if (!(itemStack.getItem() instanceof BlockItem blockItem)) {
				throw arguments.getError("Item cannot be converted to block");
			}
			return new BlockValue(blockItem.getBlock().getDefaultState());
		}

		@FunctionDoc(
			name = "asEntity",
			desc = "This creates an item entity with the item",
			returns = {ITEM_ENTITY, "the entity of the ItemStack"},
			throwMsgs = "Item cannot be converted to an ItemEntity",
			example = "itemStack.asEntity();"
		)
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

		@FunctionDoc(
			name = "getCustomName",
			desc = "This gets the custom name of the ItemStack",
			returns = {STRING, "the custom name of the ItemStack"},
			example = "itemStack.getCustomName();"
		)
		private Value getCustomName(Arguments arguments) throws CodeError {
			return StringValue.of(this.getItemStack(arguments).getName().getString());
		}

		@FunctionDoc(
			name = "isNbtEqual",
			desc = "This checks if the ItemStack has the same NBT data as the other given ItemStack",
			params = {ITEM_STACK, "itemStack", "the other ItemStack to compare to"},
			returns = {BOOLEAN, "true if the ItemStack has the same NBT data as the other given ItemStack"},
			example = "itemStack.isNbtEqual(Material.GOLD_INGOT.asItemStack());"
		)
		private Value isNbtEqual(Arguments arguments) throws CodeError {
			ItemStack itemStack = this.getItemStack(arguments);
			ItemStack otherItemStack = arguments.getNextGeneric(ItemStackValue.class);
			return BooleanValue.of(ItemStack.areNbtEqual(itemStack, otherItemStack));
		}

		@FunctionDoc(
			name = "getNbt",
			desc = "This gets the NBT data of the ItemStack as a Map",
			returns = {MAP, "the NBT data of the ItemStack"},
			example = "itemStack.getNbt();"
		)
		private Value getNbt(Arguments arguments) throws CodeError {
			ItemStack itemStack = this.getItemStack(arguments);
			NbtCompound nbtCompound = itemStack.getNbt();
			ArucasMap nbtMap = NbtUtils.nbtToMap(arguments.getContext(), nbtCompound, 10);
			return new MapValue(nbtMap);
		}

		@FunctionDoc(
			name = "getTranslatedName",
			desc = {
				"This gets the translated name of the ItemStack, for example",
				"'diamond_sword' would return 'Diamond Sword' if your language is English"
			},
			returns = {STRING, "the translated name of the ItemStack"},
			example = "itemStack.getTranslatedName();"
		)
		private Value getTranslatedName(Arguments arguments) throws CodeError {
			ItemStack itemStack = this.getItemStack(arguments);
			return StringValue.of(I18n.translate(itemStack.getItem().getTranslationKey()));
		}

		@FunctionDoc(
			name = "getMiningSpeedMultiplier",
			desc = {
				"This gets the mining speed multiplier of the ItemStack for the given Block,",
				"for example a diamond pickaxe on stone would have a higher multiplier than air on stone"
			},
			params = {BLOCK, "block", "the Block to get the mining speed multiplier for"},
			returns = {NUMBER, "the mining speed multiplier of the ItemStack for the given Block"},
			example = """
			pickaxe = Material.DIAMOND_PICKAXE.asItemStack();
			goldBlock = Material.GOLD_BLOCK.asBlock();
			
			pickaxe.getMiningSpeedMultiplier(goldBlock);
			"""
		)
		private Value getMiningSpeedMultiplier(Arguments arguments) throws CodeError {
			ItemStack itemStack = this.getItemStack(arguments);
			BlockState blockState = arguments.getNextGeneric(BlockValue.class);
			return NumberValue.of(itemStack.getMiningSpeedMultiplier(blockState));
		}

		@FunctionDoc(
			name = "setCustomName",
			desc = "This sets the custom name of the ItemStack",
			params = {TEXT, "customName", "the custom name of the ItemStack, this can be text or string"},
			returns = {ITEM_STACK, "the ItemStack with the new custom name"},
			example = "itemStack.setCustomName('My Pickaxe');"
		)
		private Value setCustomName(Arguments arguments) throws CodeError {
			ItemStackValue itemStackValue = arguments.getNext(ItemStackValue.class);
			Value nameValue = arguments.getNext();
			Text name = nameValue instanceof TextValue textValue ? textValue.value : Text.of(nameValue.getAsString(arguments.getContext()));
			itemStackValue.value.setCustomName(name);
			return itemStackValue;
		}

		@FunctionDoc(
			name = "setStackSize",
			desc = "This sets the stack size of the ItemStack",
			params = {NUMBER, "stackSize", "the stack size of the ItemStack"},
			returns = {ITEM_STACK, "the ItemStack with the new stack size"},
			example = "itemStack.setStackSize(5);"
		)
		private Value setStackSize(Arguments arguments) throws CodeError {
			ItemStackValue itemStackValue = arguments.getNext(ItemStackValue.class);
			NumberValue numberValue = arguments.getNextNumber();
			itemStackValue.value.setCount(numberValue.value.intValue());
			return itemStackValue;
		}

		@FunctionDoc(
			name = "setItemLore",
			desc = "This sets the lore of the ItemStack",
			params = {LIST, "lore", "the lore of the ItemStack as a list of Text"},
			returns = {ITEM_STACK, "the ItemStack with the new lore"},
			example = """
			itemStack = Material.DIAMOND_PICKAXE.asItemStack();
			itemStack.setItemLore([
			    Text.of('This is a pickaxe'),
			    Text.of('It is made of diamond')
			]);
			"""
		)
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

		@FunctionDoc(
			name = "setNbt",
			desc = "This sets the NBT data of the ItemStack",
			params = {MAP, "nbtMap", "the NBT data of the ItemStack as a map"},
			returns = {ITEM_STACK, "the ItemStack with the new NBT data"},
			example = "itemStack.setNbt({'Enchantments': []});"
		)
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
