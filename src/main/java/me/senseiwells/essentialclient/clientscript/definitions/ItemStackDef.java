package me.senseiwells.essentialclient.clientscript.definitions;

import me.senseiwells.arucas.api.docs.ClassDoc;
import me.senseiwells.arucas.api.docs.FunctionDoc;
import me.senseiwells.arucas.builtin.ListDef;
import me.senseiwells.arucas.builtin.MapDef;
import me.senseiwells.arucas.builtin.NumberDef;
import me.senseiwells.arucas.builtin.StringDef;
import me.senseiwells.arucas.classes.ClassInstance;
import me.senseiwells.arucas.classes.CreatableDefinition;
import me.senseiwells.arucas.classes.PrimitiveDefinition;
import me.senseiwells.arucas.core.Interpreter;
import me.senseiwells.arucas.exceptions.RuntimeError;
import me.senseiwells.arucas.utils.*;
import me.senseiwells.arucas.utils.impl.ArucasList;
import me.senseiwells.arucas.utils.impl.ArucasMap;
import me.senseiwells.essentialclient.mixins.clientScript.NbtListMixin;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import me.senseiwells.essentialclient.utils.clientscript.ClientScriptUtils;
import me.senseiwells.essentialclient.utils.clientscript.impl.ScriptBlockState;
import me.senseiwells.essentialclient.utils.clientscript.impl.ScriptItemStack;
import me.senseiwells.essentialclient.utils.clientscript.impl.ScriptMaterial;
import me.senseiwells.essentialclient.utils.render.Texts;
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

import static me.senseiwells.arucas.utils.Util.Types.*;
import static me.senseiwells.essentialclient.clientscript.core.MinecraftAPI.*;

@ClassDoc(
	name = ITEM_STACK,
	desc = "This class represents an item stack. It can be used to create new item stacks, or to modify existing ones.",
	importPath = "Minecraft",
	superclass = MaterialDef.class,
	language = Util.Language.Java
)
public class ItemStackDef extends CreatableDefinition<ScriptItemStack> {
	public ItemStackDef(Interpreter interpreter) {
		super(ITEM_STACK, interpreter);
	}

	@Override
	public PrimitiveDefinition<? super ScriptItemStack> superclass() {
		return this.getPrimitiveDef(MaterialDef.class);
	}

	@Override
	public Object asJavaValue(ClassInstance instance) {
		return instance.asPrimitive(this).asDefault();
	}

	@Override
	public boolean equals$Arucas(ClassInstance instance, Interpreter interpreter, ClassInstance other, LocatableTrace trace) {
		ScriptItemStack stack = other.getPrimitive(this);
		return stack != null && instance.asPrimitive(this).stack.isItemEqual(stack.stack);
	}

	@Override
	public int hashCode$Arucas(ClassInstance instance, Interpreter interpreter, LocatableTrace trace) {
		return this.toString$Arucas(instance, interpreter, trace).hashCode();
	}

	@Override
	public String toString$Arucas(ClassInstance instance, Interpreter interpreter, LocatableTrace trace) {
		ScriptItemStack stack = instance.asPrimitive(this);
		return "ItemStack{id=" + stack.getId() + ", count=" + stack.stack.getCount() + "}";
	}

	@Override
	public List<BuiltInFunction> defineStaticMethods() {
		return List.of(
			BuiltInFunction.of("of", 1, this::of),
			BuiltInFunction.of("parse", 1, this::parse)
		);
	}

	@FunctionDoc(
		isStatic = true,
		name = "of",
		desc = "This creates an ItemStack from a material or a string",
		params = {MATERIAL, "material", "the material, item stack, block, or string to create the ItemStack from"},
		returns = {ITEM_STACK, "the new ItemStack instance"},
		examples = "ItemStack.of('dirt');"
	)
	private ItemStack of(Arguments arguments) {
		if (arguments.isNext(StringDef.class)) {
			String id = arguments.nextPrimitive(StringDef.class);
			Identifier identifier = ClientScriptUtils.stringToIdentifier(id);
			return Registry.ITEM.getOrEmpty(identifier).orElseThrow(
				() -> new RuntimeError("'%s' is not a valid item stack".formatted(id))
			).getDefaultStack();
		}
		if (arguments.isNext(MaterialDef.class)) {
			ScriptMaterial material = arguments.nextPrimitive(MaterialDef.class);
			return material.asItemStack();
		}
		throw new RuntimeError("Parameter must be of type String or Material");
	}

	@FunctionDoc(
		isStatic = true,
		name = "parse",
		desc = {
			"This creates an ItemStack from a NBT string, this can be in the form of a map",
			"or an ItemStack NBT, or like the item stack command format"
		},
		params = {STRING, "nbtString", "the NBT string to create the ItemStack from"},
		returns = {ITEM_STACK, "the new ItemStack instance"},
		examples = "ItemStack.parse('{id:\"minecraft:dirt\",Count:64}')"
	)
	private ItemStack parse(Arguments arguments) {
		if (arguments.isNext(StringDef.class)) {
			String nbt = arguments.nextPrimitive(StringDef.class);
			return ClientScriptUtils.stringToItemStack(nbt);
		}
		if (arguments.isNext(MapDef.class)) {
			ArucasMap map = arguments.nextPrimitive(MapDef.class);
			return ItemStack.fromNbt(ClientScriptUtils.mapToNbt(arguments.getInterpreter(), map, 100));
		}
		throw new RuntimeError("Argument must be able to convert to NBT");
	}

	@Override
	public List<MemberFunction> defineMethods() {
		return List.of(
			MemberFunction.of("getMaterial", this::getMaterial),
			MemberFunction.of("getCount", this::getCount),
			MemberFunction.of("getDurability", this::getDurability),
			MemberFunction.of("getMaxDurability", this::getMaxDurability),
			MemberFunction.of("getEnchantments", this::getEnchantments),
			MemberFunction.of("isBlockItem", this::isBlockItem),
			MemberFunction.of("isStackable", this::isStackable),
			MemberFunction.of("getMaxCount", this::getMaxCount),
			MemberFunction.of("asEntity", this::asEntity),
			MemberFunction.of("getCustomName", this::getCustomName),
			MemberFunction.of("isNbtEqual", 1, this::isNbtEqual),
			MemberFunction.of("getNbt", this::getNbt),
			MemberFunction.of("getNbtAsString", this::getNbtAsString),
			MemberFunction.of("getTranslatedName", this::getTranslatedName),
			MemberFunction.of("getMiningSpeedMultiplier", 1, this::getMiningSpeedMultiplier),

			MemberFunction.of("setCustomName", 1, this::setCustomName),
			MemberFunction.of("setStackSize", 1, this::setStackSize),
			MemberFunction.of("setItemLore", 1, this::setLore),
			MemberFunction.of("setNbtFromString", 1, this::setNbtFromString),
			MemberFunction.of("setNbt", 1, this::setNbt)
		);
	}

	@FunctionDoc(
		name = "getMaterial",
		desc = "This gets the material of the ItemStack",
		returns = {MATERIAL, "the material of the ItemStack"},
		examples = "itemStack.getMaterial();"
	)
	private Item getMaterial(Arguments arguments) {
		return arguments.nextPrimitive(this).asItem();
	}

	@FunctionDoc(
		name = "getCount",
		desc = "This gets the count of the ItemStack, the amount of items in the stack",
		returns = {NUMBER, "the count of the ItemStack"},
		examples = "itemStack.getCount();"
	)
	private double getCount(Arguments arguments) {
		return arguments.nextPrimitive(this).stack.getCount();
	}

	@FunctionDoc(
		name = "getDurability",
		desc = "This gets the durability of the item",
		returns = {NUMBER, "the durability of the item"},
		examples = "itemStack.getDurability();"
	)
	private double getDurability(Arguments arguments) {
		ItemStack stack = arguments.nextPrimitive(this).stack;
		return stack.getMaxDamage() - stack.getDamage();
	}

	@FunctionDoc(
		name = "getMaxDurability",
		desc = "This gets the max durability of the item",
		returns = {NUMBER, "the max durability of the item"},
		examples = "itemStack.getMaxDurability();"
	)
	private double getMaxDurability(Arguments arguments) {
		return arguments.nextPrimitive(this).stack.getMaxDamage();
	}

	@FunctionDoc(
		name = "getEnchantments",
		desc = {
			"This gets the enchantments of the item, in a map containing the",
			"id of the enchantment as the key and the level of the enchantment as the value"
		},
		returns = {MAP, "the enchantments of the item, map may be empty"},
		examples = "itemStack.getEnchantments();"
	)
	private ArucasMap getEnchantments(Arguments arguments) {
		ItemStack itemStack = arguments.nextPrimitive(this).stack;
		NbtList nbtList = itemStack.getItem() == Items.ENCHANTED_BOOK ? EnchantedBookItem.getEnchantmentNbt(itemStack) : itemStack.getEnchantments();
		Interpreter interpreter = arguments.getInterpreter();
		ArucasMap enchantmentMap = new ArucasMap();
		for (Map.Entry<Enchantment, Integer> entry : EnchantmentHelper.fromNbt(nbtList).entrySet()) {
			Identifier enchantmentId = Registry.ENCHANTMENT.getId(entry.getKey());
			enchantmentMap.put(
				interpreter,
				enchantmentId == null ? interpreter.getNull() : interpreter.create(StringDef.class, enchantmentId.getPath()),
				interpreter.create(NumberDef.class, entry.getValue().doubleValue())
			);
		}
		return enchantmentMap;
	}

	@FunctionDoc(
		name = "isBlockItem",
		desc = "This checks if the ItemStack can be placed as a block",
		returns = {BOOLEAN, "true if the ItemStack can be placed as a block, false otherwise"},
		examples = "itemStack.isBlockItem();"
	)
	private boolean isBlockItem(Arguments arguments) {
		return arguments.nextPrimitive(this).asItem() instanceof BlockItem;
	}

	@FunctionDoc(
		name = "isStackable",
		desc = "This checks if the ItemStack is stackable",
		returns = {BOOLEAN, "true if the ItemStack is stackable, false otherwise"},
		examples = "itemStack.isStackable();"
	)
	private boolean isStackable(Arguments arguments) {
		return arguments.nextPrimitive(this).stack.isStackable();
	}

	@FunctionDoc(
		name = "getMaxCount",
		desc = "This gets the max stack size of the ItemStack",
		returns = {NUMBER, "the max stack size of the ItemStack"},
		examples = "itemStack.getMaxCount();"
	)
	private double getMaxCount(Arguments arguments) {
		return arguments.nextPrimitive(this).stack.getMaxCount();
	}

	@FunctionDoc(
		name = "asEntity",
		desc = "This creates an item entity with the item",
		returns = {ITEM_ENTITY, "the entity of the ItemStack"},
		examples = "itemStack.asEntity();"
	)
	private ItemEntity asEntity(Arguments arguments) {
		ItemStack itemStack = arguments.nextPrimitive(this).stack;
		ClientWorld world = EssentialUtils.getWorld();
		ItemEntity itemEntity = EntityType.ITEM.create(world);
		if (itemEntity == null) {
			throw new RuntimeError("'%s' cannot be converted into ItemEntity".formatted(itemStack));
		}
		itemEntity.setStack(itemStack);
		return itemEntity;
	}

	@FunctionDoc(
		name = "getCustomName",
		desc = "This gets the custom name of the ItemStack",
		returns = {STRING, "the custom name of the ItemStack"},
		examples = "itemStack.getCustomName();"
	)
	private String getCustomName(Arguments arguments) {
		return arguments.nextPrimitive(this).stack.getName().getString();
	}

	@FunctionDoc(
		name = "isNbtEqual",
		desc = "This checks if the ItemStack has the same NBT data as the other given ItemStack",
		params = {ITEM_STACK, "itemStack", "the other ItemStack to compare to"},
		returns = {BOOLEAN, "true if the ItemStack has the same NBT data as the other given ItemStack"},
		examples = "itemStack.isNbtEqual(Material.GOLD_INGOT.asItemStack());"
	)
	private boolean isNbtEqual(Arguments arguments) {
		ItemStack itemStack = arguments.nextPrimitive(this).stack;
		ItemStack otherItemStack = arguments.nextPrimitive(this).stack;
		return ItemStack.areNbtEqual(itemStack, otherItemStack);
	}

	@FunctionDoc(
		name = "getNbt",
		desc = "This gets the NBT data of the ItemStack as a Map",
		returns = {MAP, "the NBT data of the ItemStack"},
		examples = "itemStack.getNbt();"
	)
	private ArucasMap getNbt(Arguments arguments) {
		ItemStack itemStack = arguments.nextPrimitive(this).stack;
		NbtCompound nbtCompound = itemStack.getNbt();
		return ClientScriptUtils.nbtToMap(arguments.getInterpreter(), nbtCompound, 10);
	}

	@FunctionDoc(
		name = "getNbtAsString",
		desc = "This gets the NBT data of the ItemStack as a String",
		returns = {STRING, "the NBT data of the ItemStack"},
		examples = "itemStack.getNbtAsString();"
	)
	private String getNbtAsString(Arguments arguments) {
		ItemStack itemStack = arguments.nextPrimitive(this).stack;
		NbtCompound nbtCompound = itemStack.getNbt();
		return nbtCompound == null ? "" : itemStack.getNbt().toString();
	}

	@FunctionDoc(
		name = "getTranslatedName",
		desc = {
			"This gets the translated name of the ItemStack, for example",
			"'diamond_sword' would return 'Diamond Sword' if your language is English"
		},
		returns = {STRING, "the translated name of the ItemStack"},
		examples = "itemStack.getTranslatedName();"
	)
	private String getTranslatedName(Arguments arguments) {
		ItemStack itemStack = arguments.nextPrimitive(this).stack;
		return I18n.translate(itemStack.getItem().getTranslationKey());
	}

	@FunctionDoc(
		name = "getMiningSpeedMultiplier",
		desc = {
			"This gets the mining speed multiplier of the ItemStack for the given Block,",
			"for example a diamond pickaxe on stone would have a higher multiplier than air on stone"
		},
		params = {BLOCK, "block", "the Block to get the mining speed multiplier for"},
		returns = {NUMBER, "the mining speed multiplier of the ItemStack for the given Block"},
		examples = """
			pickaxe = Material.DIAMOND_PICKAXE.asItemStack();
			goldBlock = Material.GOLD_BLOCK.asBlock();

			pickaxe.getMiningSpeedMultiplier(goldBlock);
			"""
	)
	private double getMiningSpeedMultiplier(Arguments arguments) {
		ItemStack itemStack = arguments.nextPrimitive(this).stack;
		ScriptBlockState blockState = arguments.nextPrimitive(BlockDef.class);
		return itemStack.getMiningSpeedMultiplier(blockState.state);
	}

	@FunctionDoc(
		name = "setCustomName",
		desc = "This sets the custom name of the ItemStack",
		params = {TEXT, "customName", "the custom name of the ItemStack, this can be text or string"},
		returns = {ITEM_STACK, "the ItemStack with the new custom name"},
		examples = "itemStack.setCustomName('My Pickaxe');"
	)
	private ClassInstance setCustomName(Arguments arguments) {
		ClassInstance instance = arguments.next(this);
		ItemStack itemStack = instance.asPrimitive(this).stack;
		Text text;
		if (arguments.isNext(TextDef.class)) {
			text = arguments.nextPrimitive(TextDef.class);
		} else {
			text = Texts.literal(arguments.next().toString(arguments.getInterpreter()));
		}
		itemStack.setCustomName(text);
		return instance;
	}

	@FunctionDoc(
		name = "setStackSize",
		desc = "This sets the stack size of the ItemStack",
		params = {NUMBER, "stackSize", "the stack size of the ItemStack"},
		returns = {ITEM_STACK, "the ItemStack with the new stack size"},
		examples = "itemStack.setStackSize(5);"
	)
	private ClassInstance setStackSize(Arguments arguments) {
		ClassInstance instance = arguments.next(this);
		ItemStack itemStack = instance.asPrimitive(this).stack;
		double count = arguments.nextPrimitive(NumberDef.class);
		itemStack.setCount((int) count);
		return instance;
	}

	@FunctionDoc(
		name = "setItemLore",
		desc = "This sets the lore of the ItemStack",
		params = {LIST, "lore", "the lore of the ItemStack as a list of Text"},
		returns = {ITEM_STACK, "the ItemStack with the new lore"},
		examples = """
			itemStack = Material.DIAMOND_PICKAXE.asItemStack();
			itemStack.setItemLore([
				Text.of('This is a pickaxe'),
				Text.of('It is made of diamond')
			]);
			"""
	)
	private ClassInstance setLore(Arguments arguments) {
		ClassInstance instance = arguments.next(this);
		ArucasList list = arguments.nextPrimitive(ListDef.class);
		List<NbtElement> textList = new ArrayList<>();
		for (ClassInstance value : list) {
			Text text = value.getPrimitive(TextDef.class);
			if (text == null) {
				throw new RuntimeError("List must contain only Text");
			}
			textList.add(NbtString.of(Text.Serializer.toJson(text)));
		}
		ItemStack itemStack = instance.asPrimitive(this).stack;
		itemStack.getOrCreateSubNbt("display").put("Lore", NbtListMixin.createNbtList(textList, (byte) 8));
		return instance;
	}

	@FunctionDoc(
		name = "setNbtFromString",
		desc = "This sets the NBT data of the ItemStack from an NBT string",
		params = {STRING, "nbtString", "the NBT data of the ItemStack as a string"},
		returns = {ITEM_STACK, "the ItemStack with the new NBT data"},
		examples = "itemStack.setNbtFromString(\"{\\\"Lore\\\": []}\");"
	)
	private Void setNbtFromString(Arguments arguments) {
		ItemStack itemStack = arguments.nextPrimitive(this).stack;
		String nbtString = arguments.nextPrimitive(StringDef.class);
		NbtElement nbt = ClientScriptUtils.stringToNbt(nbtString);
		if (!(nbt instanceof NbtCompound compound)) {
			throw new RuntimeError("'%s' cannot be converted into an nbt compound".formatted(nbtString));
		}
		itemStack.setNbt(compound);
		return null;
	}

	@FunctionDoc(
		name = "setNbt",
		desc = "This sets the NBT data of the ItemStack",
		params = {MAP, "nbtMap", "the NBT data of the ItemStack as a map"},
		returns = {ITEM_STACK, "the ItemStack with the new NBT data"},
		examples = "itemStack.setNbt({'Lore': []});"
	)
	private Void setNbt(Arguments arguments) {
		ItemStack itemStack = arguments.nextPrimitive(this).stack;
		ArucasMap map = arguments.nextPrimitive(MapDef.class);
		itemStack.setNbt(ClientScriptUtils.mapToNbt(arguments.getInterpreter(), map, 10));
		return null;
	}
}
