package me.senseiwells.essentialclient.clientscript.definitions;

import me.senseiwells.arucas.api.docs.annotations.ClassDoc;
import me.senseiwells.arucas.api.docs.annotations.FunctionDoc;
import me.senseiwells.arucas.api.docs.annotations.ParameterDoc;
import me.senseiwells.arucas.api.docs.annotations.ReturnDoc;
import me.senseiwells.arucas.builtin.*;
import me.senseiwells.arucas.classes.CreatableDefinition;
import me.senseiwells.arucas.classes.PrimitiveDefinition;
import me.senseiwells.arucas.classes.instance.ClassInstance;
import me.senseiwells.arucas.compiler.LocatableTrace;
import me.senseiwells.arucas.exceptions.RuntimeError;
import me.senseiwells.arucas.functions.builtin.Arguments;
import me.senseiwells.arucas.functions.builtin.BuiltInFunction;
import me.senseiwells.arucas.functions.builtin.MemberFunction;
import me.senseiwells.arucas.interpreter.Interpreter;
import me.senseiwells.arucas.utils.impl.ArucasList;
import me.senseiwells.arucas.utils.impl.ArucasMap;
import me.senseiwells.arucas.utils.misc.Language;
import me.senseiwells.essentialclient.mixins.clientScript.NbtListMixin;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import me.senseiwells.essentialclient.utils.clientscript.ClientScriptUtils;
import me.senseiwells.essentialclient.utils.clientscript.impl.ScriptBlockState;
import me.senseiwells.essentialclient.utils.clientscript.impl.ScriptItemStack;
import me.senseiwells.essentialclient.utils.clientscript.impl.ScriptMaterial;
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
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static me.senseiwells.essentialclient.clientscript.core.MinecraftAPI.ITEM_STACK;

@ClassDoc(
	name = ITEM_STACK,
	desc = "This class represents an item stack. It can be used to create new item stacks, or to modify existing ones.",
	superclass = MaterialDef.class,
	language = Language.Java
)
public class ItemStackDef extends CreatableDefinition<ScriptItemStack> {
	public ItemStackDef(Interpreter interpreter) {
		super(ITEM_STACK, interpreter);
	}

	@NotNull
	@Override
	public PrimitiveDefinition<? super ScriptItemStack> superclass() {
		return this.getPrimitiveDef(MaterialDef.class);
	}

	@Override
	public Object asJavaValue(ClassInstance instance) {
		return instance.asPrimitive(this).asDefault();
	}

	@Override
	public boolean equals(@NotNull ClassInstance instance, @NotNull Interpreter interpreter, @NotNull ClassInstance other, @NotNull LocatableTrace trace) {
		ScriptItemStack stack = other.getPrimitive(this);
		return stack != null && instance.asPrimitive(this).stack.getItem() == stack.stack.getItem();
	}

	@Override
	public int hashCode(@NotNull ClassInstance instance, @NotNull Interpreter interpreter, @NotNull LocatableTrace trace) {
		return this.toString(instance, interpreter, trace).hashCode();
	}

	@NotNull
	@Override
	public String toString(@NotNull ClassInstance instance, @NotNull Interpreter interpreter, @NotNull LocatableTrace trace) {
		ScriptItemStack stack = instance.asPrimitive(this);
		return "ItemStack{id=" + stack.getId() + ", count=" + stack.stack.getCount() + "}";
	}

	@Override
	public @NotNull ClassInstance copy(@NotNull ClassInstance instance, @NotNull Interpreter interpreter, @NotNull LocatableTrace trace) {
		return this.create(new ScriptItemStack(instance.asPrimitive(this).stack.copy()));
	}

	@Override
	public List<BuiltInFunction> defineStaticMethods() {
		return List.of(
			BuiltInFunction.of("of", 1, this::of)
			// BuiltInFunction.of("parse", 1, this::parse)
		);
	}

	@FunctionDoc(
		isStatic = true,
		name = "of",
		desc = "This creates an ItemStack from a material or a string",
		params = {@ParameterDoc(type = MaterialDef.class, name = "material", desc = "the material, item stack, block, or string to create the ItemStack from")},
		returns = @ReturnDoc(type = ItemStackDef.class, desc = "the new ItemStack instance"),
		examples = "ItemStack.of('dirt');"
	)
	private ItemStack of(Arguments arguments) {
		if (arguments.isNext(StringDef.class)) {
			String id = arguments.nextPrimitive(StringDef.class);
			Identifier identifier = ClientScriptUtils.stringToIdentifier(id);
			return Registries.ITEM.getOrEmpty(identifier).orElseThrow(
				() -> new RuntimeError("'%s' is not a valid item stack".formatted(id))
			).getDefaultStack();
		}
		if (arguments.isNext(MaterialDef.class)) {
			ScriptMaterial material = arguments.nextPrimitive(MaterialDef.class);
			return material.asItemStack();
		}
		throw new RuntimeError("Parameter must be of type String or Material");
	}

	// @FunctionDoc(
	// 	isStatic = true,
	// 	name = "parse",
	// 	desc = {
	// 		"This creates an ItemStack from a NBT string, this can be in the form of a map",
	// 		"or an ItemStack NBT, or like the item stack command format"
	// 	},
	// 	params = {@ParameterDoc(type = StringDef.class, name = "nbtString", desc = "the NBT string to create the ItemStack from")},
	// 	returns = @ReturnDoc(type = ItemStackDef.class, desc = "the new ItemStack instance"),
	// 	examples = "ItemStack.parse('{id:\"minecraft:dirt\",Count:64}')"
	// )
	// private ItemStack parse(Arguments arguments) {
	// 	if (arguments.isNext(StringDef.class)) {
	// 		String nbt = arguments.nextPrimitive(StringDef.class);
	// 		return ClientScriptUtils.stringToItemStack(nbt);
	// 	}
	// 	if (arguments.isNext(MapDef.class)) {
	// 		ArucasMap map = arguments.nextPrimitive(MapDef.class);
	// 		return ItemStack.fromNbt(ClientScriptUtils.mapToNbt(arguments.getInterpreter(), map, 100));
	// 	}
	// 	throw new RuntimeError("Argument must be able to convert to NBT");
	// }

	@Override
	public List<MemberFunction> defineMethods() {
		return List.of(
			MemberFunction.of("getMaterial", this::getMaterial),
			MemberFunction.of("getCount", this::getCount),
			MemberFunction.of("getDurability", this::getDurability),
			MemberFunction.of("getMaxDurability", this::getMaxDurability),
			// MemberFunction.of("getEnchantments", this::getEnchantments),
			MemberFunction.of("isBlockItem", this::isBlockItem),
			MemberFunction.of("isStackable", this::isStackable),
			MemberFunction.of("getMaxCount", this::getMaxCount),
			MemberFunction.of("asEntity", this::asEntity),
			MemberFunction.of("getCustomName", this::getCustomName),
			// MemberFunction.of("isNbtEqual", 1, this::isNbtEqual),
			// MemberFunction.of("getNbt", this::getNbt),
			// MemberFunction.of("getNbtAsString", this::getNbtAsString),
			MemberFunction.of("getTranslatedName", this::getTranslatedName),
			MemberFunction.of("getMiningSpeedMultiplier", 1, this::getMiningSpeedMultiplier),
			// MemberFunction.of("setCustomName", 1, this::setCustomName),
			MemberFunction.of("setStackSize", 1, this::setStackSize),
			MemberFunction.of("increment", 1, this::increment),
			MemberFunction.of("decrement", 1, this::decrement)
			// MemberFunction.of("setItemLore", 1, this::setLore),
			// MemberFunction.of("setNbtFromString", 1, this::setNbtFromString),
			// MemberFunction.of("setNbt", 1, this::setNbt)
		);
	}

	@FunctionDoc(
		name = "getMaterial",
		desc = "This gets the material of the ItemStack",
		returns = @ReturnDoc(type = MaterialDef.class, desc = "the material of the ItemStack"),
		examples = "itemStack.getMaterial();"
	)
	private Item getMaterial(Arguments arguments) {
		return arguments.nextPrimitive(this).asItem();
	}

	@FunctionDoc(
		name = "getCount",
		desc = "This gets the count of the ItemStack, the amount of items in the stack",
		returns = @ReturnDoc(type = NumberDef.class, desc = "the count of the ItemStack"),
		examples = "itemStack.getCount();"
	)
	private double getCount(Arguments arguments) {
		return arguments.nextPrimitive(this).stack.getCount();
	}

	@FunctionDoc(
		name = "getDurability",
		desc = "This gets the durability of the item",
		returns = @ReturnDoc(type = NumberDef.class, desc = "the durability of the item"),
		examples = "itemStack.getDurability();"
	)
	private double getDurability(Arguments arguments) {
		ItemStack stack = arguments.nextPrimitive(this).stack;
		return stack.getMaxDamage() - stack.getDamage();
	}

	@FunctionDoc(
		name = "getMaxDurability",
		desc = "This gets the max durability of the item",
		returns = @ReturnDoc(type = NumberDef.class, desc = "the max durability of the item"),
		examples = "itemStack.getMaxDurability();"
	)
	private double getMaxDurability(Arguments arguments) {
		return arguments.nextPrimitive(this).stack.getMaxDamage();
	}

	// @FunctionDoc(
	// 	name = "getEnchantments",
	// 	desc = {
	// 		"This gets the enchantments of the item, in a map containing the",
	// 		"id of the enchantment as the key and the level of the enchantment as the value"
	// 	},
	// 	returns = @ReturnDoc(type = MapDef.class, desc = "the enchantments of the item, map may be empty"),
	// 	examples = "itemStack.getEnchantments();"
	// )
	// private ArucasMap getEnchantments(Arguments arguments) {
	// 	ItemStack itemStack = arguments.nextPrimitive(this).stack;
	// 	NbtList nbtList = itemStack.getItem() == Items.ENCHANTED_BOOK ? EnchantedBookItem.getEnchantmentNbt(itemStack) : itemStack.getEnchantments();
	// 	Interpreter interpreter = arguments.getInterpreter();
	// 	ArucasMap enchantmentMap = new ArucasMap();
	// 	for (Map.Entry<Enchantment, Integer> entry : EnchantmentHelper.fromNbt(nbtList).entrySet()) {
	// 		Identifier enchantmentId = Registries.ENCHANTMENT.getId(entry.getKey());
	// 		enchantmentMap.put(
	// 			interpreter,
	// 			enchantmentId == null ? interpreter.getNull() : interpreter.create(StringDef.class, enchantmentId.getPath()),
	// 			interpreter.create(NumberDef.class, entry.getValue().doubleValue())
	// 		);
	// 	}
	// 	return enchantmentMap;
	// }

	@FunctionDoc(
		name = "isBlockItem",
		desc = "This checks if the ItemStack can be placed as a block",
		returns = @ReturnDoc(type = BooleanDef.class, desc = "true if the ItemStack can be placed as a block, false otherwise"),
		examples = "itemStack.isBlockItem();"
	)
	private boolean isBlockItem(Arguments arguments) {
		return arguments.nextPrimitive(this).asItem() instanceof BlockItem;
	}

	@FunctionDoc(
		name = "isStackable",
		desc = "This checks if the ItemStack is stackable",
		returns = @ReturnDoc(type = BooleanDef.class, desc = "true if the ItemStack is stackable, false otherwise"),
		examples = "itemStack.isStackable();"
	)
	private boolean isStackable(Arguments arguments) {
		return arguments.nextPrimitive(this).stack.isStackable();
	}

	@FunctionDoc(
		name = "getMaxCount",
		desc = "This gets the max stack size of the ItemStack",
		returns = @ReturnDoc(type = NumberDef.class, desc = "the max stack size of the ItemStack"),
		examples = "itemStack.getMaxCount();"
	)
	private double getMaxCount(Arguments arguments) {
		return arguments.nextPrimitive(this).stack.getMaxCount();
	}

	@FunctionDoc(
		name = "asEntity",
		desc = "This creates an item entity with the item",
		returns = @ReturnDoc(type = ItemEntityDef.class, desc = "the entity of the ItemStack"),
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
		returns = @ReturnDoc(type = StringDef.class, desc = "the custom name of the ItemStack"),
		examples = "itemStack.getCustomName();"
	)
	private String getCustomName(Arguments arguments) {
		return arguments.nextPrimitive(this).stack.getName().getString();
	}

	// @FunctionDoc(
	// 	name = "isNbtEqual",
	// 	desc = "This checks if the ItemStack has the same NBT data as the other given ItemStack",
	// 	params = {@ParameterDoc(type = ItemStackDef.class, name = "itemStack", desc = "the other ItemStack to compare to")},
	// 	returns = @ReturnDoc(type = BooleanDef.class, desc = "true if the ItemStack has the same NBT data as the other given ItemStack"),
	// 	examples = "itemStack.isNbtEqual(Material.GOLD_INGOT.asItemStack());"
	// )
	// private boolean isNbtEqual(Arguments arguments) {
	// 	ItemStack itemStack = arguments.nextPrimitive(this).stack;
	// 	ItemStack otherItemStack = arguments.nextPrimitive(this).stack;
	// 	return ItemStack.canCombine(itemStack, otherItemStack);
	// }
	//
	// @FunctionDoc(
	// 	name = "getNbt",
	// 	desc = "This gets the NBT data of the ItemStack as a Map",
	// 	returns = @ReturnDoc(type = MapDef.class, desc = "the NBT data of the ItemStack"),
	// 	examples = "itemStack.getNbt();"
	// )
	// private ArucasMap getNbt(Arguments arguments) {
	// 	ItemStack itemStack = arguments.nextPrimitive(this).stack;
	// 	NbtCompound nbtCompound = itemStack.getNbt();
	// 	return ClientScriptUtils.nbtToMap(arguments.getInterpreter(), nbtCompound, 10);
	// }

	// @FunctionDoc(
	// 	name = "getNbtAsString",
	// 	desc = "This gets the NBT data of the ItemStack as a String",
	// 	returns = @ReturnDoc(type = StringDef.class, desc = "the NBT data of the ItemStack"),
	// 	examples = "itemStack.getNbtAsString();"
	// )
	// private String getNbtAsString(Arguments arguments) {
	// 	ItemStack itemStack = arguments.nextPrimitive(this).stack;
	// 	NbtCompound nbtCompound = itemStack.getNbt();
	// 	return nbtCompound == null ? "" : itemStack.getNbt().toString();
	// }

	@FunctionDoc(
		name = "getTranslatedName",
		desc = {
			"This gets the translated name of the ItemStack, for example",
			"'diamond_sword' would return 'Diamond Sword' if your language is English"
		},
		returns = @ReturnDoc(type = StringDef.class, desc = "the translated name of the ItemStack"),
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
		params = {@ParameterDoc(type = BlockDef.class, name = "block", desc = "the Block to get the mining speed multiplier for")},
		returns = @ReturnDoc(type = NumberDef.class, desc = "the mining speed multiplier of the ItemStack for the given Block"),
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

	// @FunctionDoc(
	// 	name = "setCustomName",
	// 	desc = "This sets the custom name of the ItemStack",
	// 	params = {@ParameterDoc(type = TextDef.class, name = "customName", desc = "the custom name of the ItemStack, this can be text or string")},
	// 	returns = @ReturnDoc(type = ItemStackDef.class, desc = "the ItemStack with the new custom name"),
	// 	examples = "itemStack.setCustomName('My Pickaxe');"
	// )
	// private ClassInstance setCustomName(Arguments arguments) {
	// 	ClassInstance instance = arguments.next(this);
	// 	ItemStack itemStack = instance.asPrimitive(this).stack;
	// 	Text text;
	// 	if (arguments.isNext(TextDef.class)) {
	// 		text = arguments.nextPrimitive(TextDef.class);
	// 	} else {
	// 		text = Text.literal(arguments.next().toString(arguments.getInterpreter()));
	// 	}
	// 	itemStack.setCustomName(text);
	// 	return instance;
	// }

	@FunctionDoc(
		name = "setStackSize",
		desc = "This sets the stack size of the ItemStack",
		params = {@ParameterDoc(type = NumberDef.class, name = "stackSize", desc = "the stack size of the ItemStack")},
		returns = @ReturnDoc(type = ItemStackDef.class, desc = "the ItemStack with the new stack size"),
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
		name = "increment",
		desc = "This increments the stack size of the ItemStack by number",
		params = {@ParameterDoc(type = NumberDef.class, name = "count", desc = "number to increase")},
		returns = @ReturnDoc(type = ItemStackDef.class, desc = "the ItemStack with the new stack size"),
		examples = "itemStack.increment(5);"
	)
	private ClassInstance increment(Arguments arguments) {
		ClassInstance instance = arguments.next(this);
		ItemStack itemStack = instance.asPrimitive(this).stack;
		double count = arguments.nextPrimitive(NumberDef.class);
		itemStack.increment((int) count);
		return instance;
	}

	@FunctionDoc(
		name = "decrement",
		desc = "This decrements the stack size of the ItemStack by number",
		params = {@ParameterDoc(type = NumberDef.class, name = "count", desc = "number to decrease")},
		returns = @ReturnDoc(type = ItemStackDef.class, desc = "the ItemStack with the new stack size"),
		examples = "itemStack.decrement(5);"
	)
	private ClassInstance decrement(Arguments arguments) {
		ClassInstance instance = arguments.next(this);
		ItemStack itemStack = instance.asPrimitive(this).stack;
		double count = arguments.nextPrimitive(NumberDef.class);
		itemStack.decrement((int) count);
		return instance;
	}

	// @FunctionDoc(
	// 	name = "setItemLore",
	// 	desc = "This sets the lore of the ItemStack",
	// 	params = {@ParameterDoc(type = ListDef.class, name = "lore", desc = "the lore of the ItemStack as a list of Text")},
	// 	returns = @ReturnDoc(type = ItemStackDef.class, desc = "the ItemStack with the new lore"),
	// 	examples = """
	// 		itemStack = Material.DIAMOND_PICKAXE.asItemStack();
	// 		itemStack.setItemLore([
	// 			Text.of('This is a pickaxe'),
	// 			Text.of('It is made of diamond')
	// 		]);
	// 		"""
	// )
	// private ClassInstance setLore(Arguments arguments) {
	// 	ClassInstance instance = arguments.next(this);
	// 	ArucasList list = arguments.nextPrimitive(ListDef.class);
	// 	List<NbtElement> textList = new ArrayList<>();
	// 	for (ClassInstance value : list) {
	// 		Text text = value.getPrimitive(TextDef.class);
	// 		if (text == null) {
	// 			throw new RuntimeError("List must contain only Text");
	// 		}
	// 		textList.add(NbtString.of(Text.Serialization.toJsonString(text)));
	// 	}
	// 	ItemStack itemStack = instance.asPrimitive(this).stack;
	// 	itemStack.getOrCreateSubNbt("display").put("Lore", NbtListMixin.createNbtList(textList, (byte) 8));
	// 	return instance;
	// }

	// @FunctionDoc(
	// 	name = "setNbtFromString",
	// 	desc = "This sets the NBT data of the ItemStack from an NBT string",
	// 	params = {@ParameterDoc(type = StringDef.class, name = "nbtString", desc = "the NBT data of the ItemStack as a string")},
	// 	returns = @ReturnDoc(type = ItemStackDef.class, desc = "the ItemStack with the new NBT data"),
	// 	examples = "itemStack.setNbtFromString(\"{\\\"Lore\\\": []}\");"
	// )
	// private ClassInstance setNbtFromString(Arguments arguments) {
	// 	ClassInstance instance = arguments.next(this);
	// 	ItemStack itemStack = instance.asPrimitive(this).stack;
	// 	String nbtString = arguments.nextPrimitive(StringDef.class);
	// 	NbtElement nbt = ClientScriptUtils.stringToNbt(nbtString);
	// 	if (!(nbt instanceof NbtCompound compound)) {
	// 		throw new RuntimeError("'%s' cannot be converted into an nbt compound".formatted(nbtString));
	// 	}
	// 	itemStack.setNbt(compound);
	// 	return instance;
	// }
	//
	// @FunctionDoc(
	// 	name = "setNbt",
	// 	desc = "This sets the NBT data of the ItemStack",
	// 	params = {@ParameterDoc(type = MapDef.class, name = "nbtMap", desc = "the NBT data of the ItemStack as a map")},
	// 	returns = @ReturnDoc(type = ItemStackDef.class, desc = "the ItemStack with the new NBT data"),
	// 	examples = "itemStack.setNbt({'Lore': []});"
	// )
	// private ClassInstance setNbt(Arguments arguments) {
	// 	ClassInstance instance = arguments.next(this);
	// 	ItemStack itemStack = instance.asPrimitive(this).stack;
	// 	ArucasMap map = arguments.nextPrimitive(MapDef.class);
	// 	itemStack.setNbt(ClientScriptUtils.mapToNbt(arguments.getInterpreter(), map, 10));
	// 	return instance;
	// }
}
