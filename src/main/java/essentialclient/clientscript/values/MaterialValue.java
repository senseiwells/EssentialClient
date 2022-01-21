package essentialclient.clientscript.values;

import essentialclient.clientscript.extensions.ArucasMinecraftExtension;
import me.senseiwells.arucas.api.ArucasClassExtension;
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
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MaterialValue extends Value<Item> {
	public MaterialValue(Item value) {
		super(value);
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
			return new MaterialValue(Registry.ITEM.get(ArucasMinecraftExtension.getIdentifier(context, function.syntaxPosition, stringValue.value)));
		}

		@Override
		public ArucasFunctionMap<MemberFunction> getDefinedMethods() {
			return ArucasFunctionMap.of(
				new MemberFunction("getId", (context, function) -> StringValue.of(Registry.ITEM.getId(this.getMaterial(context, function)).getPath())),
				new MemberFunction("asItemStack", (context, function) -> new ItemStackValue(this.getMaterial(context, function).getDefaultStack())),
				new MemberFunction("asBlock", this::asBlock),
				new MemberFunction("getTranslatedName", this::getTranslatedName)
			);
		}

		private Value<?> asBlock(Context context, MemberFunction function) throws CodeError {
			Item material = this.getMaterial(context, function);
			if (!(material instanceof BlockItem blockItem)) {
				throw new RuntimeError("Item cannot be converted to block", function.syntaxPosition, context);
			}
			return new BlockValue(blockItem.getBlock().getDefaultState());
		}


		private Value<?> getTranslatedName(Context context, MemberFunction function) throws CodeError {
			Item material = this.getMaterial(context, function);
			return StringValue.of(I18n.translate(material.getTranslationKey()));
		}

		private Item getMaterial(Context context, MemberFunction function) throws CodeError {
			Item material = function.getParameterValueOfType(context, MaterialValue.class, 0).value;
			if (material == null) {
				throw new RuntimeError("Material was null", function.syntaxPosition, context);
			}
			return material;
		}

		@Override
		public Class<?> getValueClass() {
			return MaterialValue.class;
		}
	}
}
