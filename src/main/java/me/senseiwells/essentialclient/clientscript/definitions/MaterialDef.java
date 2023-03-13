package me.senseiwells.essentialclient.clientscript.definitions;

import me.senseiwells.arucas.api.docs.annotations.ClassDoc;
import me.senseiwells.arucas.api.docs.annotations.FunctionDoc;
import me.senseiwells.arucas.api.docs.annotations.ParameterDoc;
import me.senseiwells.arucas.api.docs.annotations.ReturnDoc;
import me.senseiwells.arucas.builtin.StringDef;
import me.senseiwells.arucas.classes.CreatableDefinition;
import me.senseiwells.arucas.classes.instance.ClassInstance;
import me.senseiwells.arucas.compiler.LocatableTrace;
import me.senseiwells.arucas.exceptions.RuntimeError;
import me.senseiwells.arucas.functions.builtin.Arguments;
import me.senseiwells.arucas.functions.builtin.BuiltInFunction;
import me.senseiwells.arucas.functions.builtin.MemberFunction;
import me.senseiwells.arucas.interpreter.Interpreter;
import me.senseiwells.arucas.utils.misc.Language;
import me.senseiwells.essentialclient.utils.clientscript.ClientScriptUtils;
import me.senseiwells.essentialclient.utils.clientscript.impl.ScriptMaterial;
import me.senseiwells.essentialclient.utils.mapping.RegistryHelper;
import net.minecraft.block.Block;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static me.senseiwells.essentialclient.clientscript.core.MinecraftAPI.MATERIAL;

@ClassDoc(
	name = MATERIAL,
	desc = {
		"This class represents all possible item and block types",
		"and allows you to convert them into instances of ItemStacks and Blocks"
	},
	language = Language.Java
)
public class MaterialDef extends CreatableDefinition<ScriptMaterial> {
	public MaterialDef(Interpreter interpreter) {
		super(MATERIAL, interpreter);
	}

	@Override
	public Object asJavaValue(ClassInstance instance) {
		return instance.asPrimitive(this).asDefault();
	}

	@Override
	public boolean equals(@NotNull ClassInstance instance, @NotNull Interpreter interpreter, @NotNull ClassInstance other, @NotNull LocatableTrace trace) {
		ScriptMaterial material = other.getPrimitive(this);
		return material != null && instance.asPrimitive(this).asDefault().equals(material.asDefault());
	}

	@Override
	public int hashCode(@NotNull ClassInstance instance, @NotNull Interpreter interpreter, @NotNull LocatableTrace trace) {
		return instance.asPrimitive(this).asDefault().hashCode();
	}

	@NotNull
	@Override
	public String toString(@NotNull ClassInstance instance, @NotNull Interpreter interpreter, @NotNull LocatableTrace trace) {
		return instance.asPrimitive(this).asString();
	}

	@Override
	public List<PrimitiveField> defineStaticFields() {
		SortedMap<String, ClassInstance> map = new TreeMap<>();

		for (Item item : RegistryHelper.getItemRegistry()) {
			map.put(item.toString().toUpperCase(), this.create(ScriptMaterial.materialOf(item)));
		}
		for (Block block : RegistryHelper.getBlockRegistry()) {
			Identifier identifier = RegistryHelper.getBlockRegistry().getId(block);
			if (identifier != null) {
				map.computeIfAbsent(identifier.getPath().toUpperCase(), s -> {
					return this.create(ScriptMaterial.materialOf(block));
				});
			}
		}

		List<PrimitiveField> fields = new ArrayList<>(map.size());
		map.forEach((key, value) -> fields.add(new PrimitiveField(key, value, false)));
		fields.add(new PrimitiveField("ALL", map.values().stream().toList(), false));
		return fields;
	}

	@Override
	public List<BuiltInFunction> defineStaticMethods() {
		return List.of(
			BuiltInFunction.of("of", 1, this::of)
		);
	}

	@FunctionDoc(
		isStatic = true,
		name = "of",
		desc = {
			"This converts a block or item id into a Material.",
			"This method will throw an error if the id is invalid"
		},
		params = {@ParameterDoc(type = StringDef.class, name = "id", desc = "the id of the block or item")},
		returns = @ReturnDoc(type = MaterialDef.class, desc = "the material instance from the id"),
		examples = "Material.of('diamond');"
	)
	private Object of(Arguments arguments) {
		String id = arguments.nextPrimitive(StringDef.class);
		Identifier identifier = ClientScriptUtils.stringToIdentifier(id);
		Optional<Item> item = RegistryHelper.getItemRegistry().getOrEmpty(identifier);
		if (item.isEmpty()) {
			Optional<Block> block = RegistryHelper.getBlockRegistry().getOrEmpty(identifier);
			if (block.isPresent()) {
				return block.get();
			}
		}
		return item.orElseThrow(() -> new RuntimeError("'%s' is not a valid Material".formatted(id)));
	}

	@Override
	public List<MemberFunction> defineMethods() {
		return List.of(
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
		returns = @ReturnDoc(type = StringDef.class, desc = "the full id representation of the material"),
		examples = "material.getFullId();"
	)
	private Object getFullId(Arguments arguments) {
		ScriptMaterial material = arguments.nextPrimitive(this);
		return material.getId().toString();
	}

	@FunctionDoc(
		name = "getId",
		desc = "This returns the id of the material, for example: 'diamond'",
		returns = @ReturnDoc(type = StringDef.class, desc = "the id representation of the material"),
		examples = "material.getId();"
	)
	private Object getId(Arguments arguments) {
		ScriptMaterial material = arguments.nextPrimitive(this);
		return material.getId().getPath();
	}

	@FunctionDoc(
		name = "asItemStack",
		desc = {
			"This converts the material into an ItemStack.",
			"If it cannot be converted an error will be thrown"
		},
		returns = @ReturnDoc(type = ItemStackDef.class, desc = "the ItemStack representation of the material"),
		examples = "material.asItemStack();"
	)
	private Object asItemStack(Arguments arguments) {
		ScriptMaterial material = arguments.nextPrimitive(this);
		return material.asItemStack();
	}

	@FunctionDoc(
		name = "asBlock",
		desc = {
			"This converts the material into a Block.",
			"If it cannot be converted an error will be thrown"
		},
		returns = @ReturnDoc(type = BlockDef.class, desc = "the Block representation of the material"),
		examples = "material.asBlock();"
	)
	private Object asBlock(Arguments arguments) {
		ScriptMaterial material = arguments.nextPrimitive(this);
		return material.asBlockState();
	}

	@FunctionDoc(
		name = "getTranslatedName",
		desc = {
			"This gets the translated name of the ItemStack, for example: ",
			"Material.DIAMOND_SWORD would return 'Diamond Sword' if your language is English"
		},
		returns = @ReturnDoc(type = StringDef.class, desc = "the translated name of the Material"),
		examples = "material.getTranslatedName();"
	)
	private Object getTranslatedName(Arguments arguments) {
		ScriptMaterial material = arguments.nextPrimitive(this);
		return I18n.translate(material.getTranslationKey());
	}
}
