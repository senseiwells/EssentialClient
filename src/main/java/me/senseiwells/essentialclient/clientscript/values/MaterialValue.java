package me.senseiwells.essentialclient.clientscript.values;

import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.api.docs.ClassDoc;
import me.senseiwells.arucas.api.docs.FunctionDoc;
import me.senseiwells.arucas.api.docs.MemberDoc;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.Arguments;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.impl.ArucasList;
import me.senseiwells.arucas.values.GenericValue;
import me.senseiwells.arucas.values.ListValue;
import me.senseiwells.arucas.values.StringValue;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.functions.BuiltInFunction;
import me.senseiwells.arucas.values.functions.MemberFunction;
import me.senseiwells.essentialclient.clientscript.extensions.ArucasMinecraftExtension;
import net.minecraft.block.Block;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.*;

import static me.senseiwells.arucas.utils.ValueTypes.STRING;
import static me.senseiwells.essentialclient.clientscript.core.MinecraftAPI.*;

public class MaterialValue extends GenericValue<Item> {
	public MaterialValue(Item value) {
		super(value);
	}

	public String getTranslationKey() {
		return this.value.getTranslationKey();
	}

	public Identifier getId() {
		return Registry.ITEM.getId(this.value);
	}

	public ItemStack asItemStack(Arguments arguments) throws CodeError {
		return this.asItemStack(arguments.getContext(), arguments.getPosition());
	}

	public ItemStack asItemStack(Context context, ISyntax syntaxPosition) throws CodeError {
		return this.value.getDefaultStack();
	}

	public Block asBlock(Arguments arguments) throws CodeError {
		return this.asBlock(arguments.getContext(), arguments.getPosition());
	}

	public Block asBlock(Context context, ISyntax syntaxPosition) throws CodeError {
		if (this.value instanceof BlockItem blockItem) {
			return blockItem.getBlock();
		}
		throw new RuntimeError("Material cannot be converted into a block", syntaxPosition, context);
	}

	@Override
	public GenericValue<Item> copy(Context context) throws CodeError {
		return this;
	}

	@Override
	public String getAsString(Context context) throws CodeError {
		return this.value.toString();
	}

	@Override
	public int getHashCode(Context context) {
		return this.value.hashCode();
	}

	@Override
	public boolean isEquals(Context context, Value value) {
		return this.value == value.getValue();
	}

	@Override
	public String getTypeName() {
		return MATERIAL;
	}

	public static MaterialValue blockMaterial(Block block) {
		return new BlockMaterial(block);
	}

	private static class BlockMaterial extends MaterialValue {
		private final Block block;

		public BlockMaterial(Block value) {
			super(Items.AIR);
			this.block = value;
		}

		@Override
		public String getTranslationKey() {
			return this.block.getTranslationKey();
		}

		@Override
		public Identifier getId() {
			return Registry.BLOCK.getId(this.block);
		}

		@Override
		public ItemStack asItemStack(Context context, ISyntax syntaxPosition) throws CodeError {
			Item item = this.block.asItem();
			if (item != Items.AIR) {
				return item.getDefaultStack();
			}
			throw new RuntimeError("Material cannot be converted to an item stack", syntaxPosition, context);
		}

		@Override
		public Block asBlock(Context context, ISyntax syntaxPosition) {
			return this.block;
		}

		@Override
		public String getAsString(Context context) throws CodeError {
			return this.getId().getPath();
		}
	}

	@ClassDoc(
		name = MATERIAL,
		desc = {
			"This class represents all possible item and block types",
			"and allows you to convert them into instances of ItemStacks and Blocks"
		},
		importPath = "Minecraft"
	)
	public static class ArucasMaterialClass extends ArucasClassExtension {
		public ArucasMaterialClass() {
			super(MATERIAL);
		}

		@MemberDoc(
			isStatic = true,
			name = "ALL",
			desc = "This is a list of all materials in the game, including items and blocks, each material also has it's own member",
			type = MATERIAL,
			examples = "Material.ALL;"
		)
		@Override
		public Map<String, Value> getDefinedStaticVariables() {
			SortedMap<String, Value> materialMap = new TreeMap<>();
			ArucasList materialList = new ArucasList();
			for (Item item : Registry.ITEM) {
				MaterialValue materialValue = new MaterialValue(item);
				materialMap.put(item.toString().toUpperCase(Locale.ROOT), materialValue);
				materialList.add(materialValue);
			}
			for (Block block : Registry.BLOCK) {
				String blockName = Registry.BLOCK.getId(block).getPath().toUpperCase(Locale.ROOT);
				if (!materialMap.containsKey(blockName)) {
					MaterialValue materialValue = new BlockMaterial(block);
					materialMap.put(blockName, materialValue);
					materialList.add(materialValue);
				}
			}

			Map<String, Value> linkedMap = new LinkedHashMap<>();
			linkedMap.put("ALL", new ListValue(materialList));
			linkedMap.putAll(materialMap);
			return linkedMap;
		}

		@Override
		public ArucasFunctionMap<BuiltInFunction> getDefinedStaticMethods() {
			return ArucasFunctionMap.of(
				BuiltInFunction.of("of", 1, this::of)
			);
		}

		@FunctionDoc(
			isStatic = true,
			name = "of",
			desc = "This converts a block or item id into a Material",
			params = {STRING, "id", "the id of the block or item"},
			returns = {MATERIAL, "the material instance from the id"},
			throwMsgs = "... is not a valid Material",
			example = "Material.of('diamond');"
		)
		private Value of(Arguments arguments) throws CodeError {
			StringValue stringValue = arguments.getNextString();
			Optional<Item> item = Registry.ITEM.getOrEmpty(ArucasMinecraftExtension.getId(arguments, stringValue.value));
			return new MaterialValue(item.orElseThrow(
				() -> arguments.getError("'%s' is not a valid Material", stringValue.value)
			));
		}

		@Override
		public ArucasFunctionMap<MemberFunction> getDefinedMethods() {
			return ArucasFunctionMap.of(
				MemberFunction.of("getFullId", this::getFullId),
				MemberFunction.of("getId", this::getId),
				MemberFunction.of("asItemStack", this::asItemStack),
				MemberFunction.of("asBlock", this::asBlock),
				MemberFunction.of("getTranslatedName", this::getTranslatedName)
			);
		}

		@FunctionDoc(
			name = "getFullId",
			desc = "This returns the full id of the material, for example: 'minecraft:diamond'",
			returns = {STRING, "the full id representation of the material"},
			example = "material.getFullId();"
		)
		private Value getFullId(Arguments arguments) throws CodeError {
			MaterialValue materialValue = arguments.getNext(MaterialValue.class);
			return StringValue.of(materialValue.getId().toString());
		}

		@FunctionDoc(
			name = "getId",
			desc = "This returns the id of the material, for example: 'diamond'",
			returns = {STRING, "the id representation of the material"},
			example = "material.getId();"
		)
		private Value getId(Arguments arguments) throws CodeError {
			MaterialValue materialValue = arguments.getNext(MaterialValue.class);
			return StringValue.of(materialValue.getId().getPath());
		}

		@FunctionDoc(
			name = "asItemStack",
			desc = "This converts the material into an ItemStack",
			returns = {ITEM_STACK, "the ItemStack representation of the material"},
			throwMsgs = "Material cannot be converted to an item stack",
			example = "material.asItemStack();"
		)
		private Value asItemStack(Arguments arguments) throws CodeError {
			MaterialValue materialValue = arguments.getNext(MaterialValue.class);
			return new ItemStackValue(materialValue.asItemStack(arguments));
		}

		@FunctionDoc(
			name = "asBlock",
			desc = "This converts the material into a Block",
			returns = {BLOCK, "the Block representation of the material"},
			throwMsgs = "Material cannot be converted to a block",
			example = "material.asBlock();"
		)
		private Value asBlock(Arguments arguments) throws CodeError {
			MaterialValue materialValue = arguments.getNext(MaterialValue.class);
			return new BlockValue(materialValue.asBlock(arguments).getDefaultState());
		}

		@FunctionDoc(
			name = "getTranslatedName",
			desc = {
				"This gets the translated name of the ItemStack, for example: ",
				"Material.DIAMOND_SWORD would return 'Diamond Sword' if your language is English"
			},
			returns = {STRING, "the translated name of the Material"},
			example = "material.getTranslatedName();"
		)
		private Value getTranslatedName(Arguments arguments) throws CodeError {
			MaterialValue materialValue = arguments.getNext(MaterialValue.class);
			return StringValue.of(I18n.translate(materialValue.getTranslationKey()));
		}

		@Override
		public Class<MaterialValue> getValueClass() {
			return MaterialValue.class;
		}
	}
}
