package me.senseiwells.essentialclient.clientscript.values;

import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.impl.ArucasList;
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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public class MaterialValue extends Value<Item> {
	public MaterialValue(Item value) {
		super(value);
	}

	public String getTranslationKey() {
		return this.value.getTranslationKey();
	}

	public Identifier getId() {
		return Registry.ITEM.getId(this.value);
	}

	public ItemStack asItemStack(Context context, ISyntax syntaxPosition) throws CodeError {
		return this.value.getDefaultStack();
	}

	public Block asBlock(Context context, ISyntax syntaxPosition) throws CodeError {
		if (this.value instanceof BlockItem blockItem) {
			return blockItem.getBlock();
		}
		throw new RuntimeError("Material cannot be converted into a block", syntaxPosition, context);
	}

	@Override
	public Value<Item> copy(Context context) throws CodeError {
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
	public boolean isEquals(Context context, Value<?> value) {
		return this.value == value.value;
	}

	@Override
	public String getTypeName() {
		return "Material";
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

	public static class ArucasMaterialClass extends ArucasClassExtension {

		public ArucasMaterialClass() {
			super("Material");
		}

		@Override
		public Map<String, Value<?>> getDefinedStaticVariables() {
			Map<String, Value<?>> materialMap = new HashMap<>();
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
			materialMap.put("ALL", new ListValue(materialList));
			return materialMap;
		}

		@Override
		public ArucasFunctionMap<BuiltInFunction> getDefinedStaticMethods() {
			return ArucasFunctionMap.of(
				new BuiltInFunction("of", "string", this::of)
			);
		}

		private Value<?> of(Context context, BuiltInFunction function) throws CodeError {
			StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 0);
			Optional<Item> item = Registry.ITEM.getOrEmpty(ArucasMinecraftExtension.getId(context, function.syntaxPosition, stringValue.value));
			return new MaterialValue(item.orElseThrow(() -> {
				return new RuntimeError("'%s' is not a valid Material".formatted(stringValue.value), function.syntaxPosition, context);
			}));
		}

		@Override
		public ArucasFunctionMap<MemberFunction> getDefinedMethods() {
			return ArucasFunctionMap.of(
				new MemberFunction("getFullId", this::getFullId),
				new MemberFunction("getId", this::getId),
				new MemberFunction("asItemStack", this::asItemStack),
				new MemberFunction("asBlock", this::asBlock),
				new MemberFunction("getTranslatedName", this::getTranslatedName)
			);
		}

		private Value<?> getFullId(Context context, MemberFunction function) throws CodeError {
			MaterialValue materialValue = function.getThis(context, MaterialValue.class);
			return StringValue.of(materialValue.getId().toString());
		}

		private Value<?> getId(Context context, MemberFunction function) throws CodeError {
			MaterialValue materialValue = function.getThis(context, MaterialValue.class);
			return StringValue.of(materialValue.getId().getPath());
		}

		private Value<?> asItemStack(Context context, MemberFunction function) throws CodeError {
			MaterialValue materialValue = function.getThis(context, MaterialValue.class);
			return new ItemStackValue(materialValue.asItemStack(context, function.syntaxPosition));
		}

		private Value<?> asBlock(Context context, MemberFunction function) throws CodeError {
			MaterialValue materialValue = function.getThis(context, MaterialValue.class);
			return new BlockValue(materialValue.asBlock(context, function.syntaxPosition).getDefaultState());
		}

		private Value<?> getTranslatedName(Context context, MemberFunction function) throws CodeError {
			MaterialValue materialValue = function.getThis(context, MaterialValue.class);
			return StringValue.of(I18n.translate(materialValue.getTranslationKey()));
		}

		@Override
		public Class<MaterialValue> getValueClass() {
			return MaterialValue.class;
		}
	}
}
