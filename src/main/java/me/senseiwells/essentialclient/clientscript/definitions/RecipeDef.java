package me.senseiwells.essentialclient.clientscript.definitions;

import me.senseiwells.arucas.api.docs.annotations.ClassDoc;
import me.senseiwells.arucas.api.docs.annotations.FunctionDoc;
import me.senseiwells.arucas.api.docs.annotations.ParameterDoc;
import me.senseiwells.arucas.api.docs.annotations.ReturnDoc;
import me.senseiwells.arucas.builtin.ListDef;
import me.senseiwells.arucas.builtin.StringDef;
import me.senseiwells.arucas.classes.CreatableDefinition;
import me.senseiwells.arucas.classes.instance.ClassInstance;
import me.senseiwells.arucas.compiler.LocatableTrace;
import me.senseiwells.arucas.exceptions.RuntimeError;
import me.senseiwells.arucas.functions.builtin.Arguments;
import me.senseiwells.arucas.functions.builtin.BuiltInFunction;
import me.senseiwells.arucas.functions.builtin.MemberFunction;
import me.senseiwells.arucas.interpreter.Interpreter;
import me.senseiwells.arucas.utils.impl.ArucasList;
import me.senseiwells.arucas.utils.misc.Language;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import me.senseiwells.essentialclient.utils.clientscript.ClientScriptUtils;
import me.senseiwells.essentialclient.utils.clientscript.impl.ScriptItemStack;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static me.senseiwells.essentialclient.clientscript.core.MinecraftAPI.RECIPE;

@ClassDoc(
	name = RECIPE,
	desc = "This class represents recipes in Minecraft.",
	language = Language.Java
)
public class RecipeDef extends CreatableDefinition<RecipeEntry<Recipe<?>>> {
	public RecipeDef(Interpreter interpreter) {
		super(RECIPE, interpreter);
	}

	@NotNull
	@Override
	public String toString(@NotNull ClassInstance instance, @NotNull Interpreter interpreter, @NotNull LocatableTrace trace) {
		return "Recipe{id=" + instance.asPrimitive(this).id() + "}";
	}

	@Override
	public List<PrimitiveField> defineStaticFields() {
		ClientPlayNetworkHandler networkHandler = EssentialUtils.getNetworkHandler();
		if (networkHandler == null) {
			return super.defineStaticFields();
		}

		SortedMap<String, ClassInstance> map = new TreeMap<>();
		for (RecipeEntry<?> recipe : networkHandler.getRecipeManager().values()) {
			//noinspection unchecked
			map.put(recipe.toString().toUpperCase(), this.create((RecipeEntry<Recipe<?>>) recipe));
		}

		ArucasList list = new ArucasList();
		List<PrimitiveField> fields = new ArrayList<>(map.size());
		map.forEach((key, value) -> {
			list.add(value);
			fields.add(new PrimitiveField(key, value, false));
		});
		fields.add(new PrimitiveField("ALL", list, false));
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
			"This converts a recipe id into a Recipe if it's valid,",
			"otherwise an error will be thrown"
		},
		params = {@ParameterDoc(type = StringDef.class, name = "recipeId", desc = "the id of the recipe to convert to a Recipe")},
		returns = @ReturnDoc(type = RecipeDef.class, desc = "the recipe instance from the id"),
		examples = "Recipe.of('redstone_block')"
	)
	private RecipeEntry<?> of(Arguments arguments) {
		String id = arguments.nextPrimitive(StringDef.class);
		ClientPlayNetworkHandler networkHandler = EssentialUtils.getNetworkHandler();
		Identifier identifier = ClientScriptUtils.stringToIdentifier(id);
		Optional<RecipeEntry<?>> recipe = networkHandler.getRecipeManager().get(identifier);
		if (recipe.isEmpty()) {
			throw new RuntimeError("Recipe with id '%s' doesn't exist".formatted(id));
		}
		return recipe.get();
	}

	@Override
	public List<MemberFunction> defineMethods() {
		return List.of(
			MemberFunction.of("getFullId", this::getFullId),
			MemberFunction.of("getId", this::getId),
			MemberFunction.of("getCraftingType", this::getCraftingType),
			MemberFunction.of("getOutput", this::getOutput),
			MemberFunction.of("getIngredients", this::getIngredients)
		);
	}

	@FunctionDoc(
		name = "getFullId",
		desc = "This returns the full id of the recipe",
		returns = @ReturnDoc(type = StringDef.class, desc = "the full id of the recipe"),
		examples = "recipe.getFullId()"
	)
	private String getFullId(Arguments arguments) {
		RecipeEntry<?> recipe = arguments.nextPrimitive(this);
		return recipe.id().toString();
	}

	@FunctionDoc(
		name = "getId",
		desc = "This returns the id of the recipe",
		returns = @ReturnDoc(type = StringDef.class, desc = "the id of the recipe"),
		examples = "recipe.getId()"
	)
	private String getId(Arguments arguments) {
		RecipeEntry<?> recipe = arguments.nextPrimitive(this);
		return recipe.id().toString();
	}

	@FunctionDoc(
		name = "getCraftingType",
		desc = "This returns the crafting type of the recipe",
		returns = @ReturnDoc(type = StringDef.class, desc = "the crafting type of the recipe, for example: 'crafting', 'smelting', 'blasting'"),
		examples = "recipe.getCraftingType()"
	)
	private String getCraftingType(Arguments arguments) {
		RecipeEntry<?> recipe = arguments.nextPrimitive(this);
		Identifier identifier = Registries.RECIPE_TYPE.getId(recipe.value().getType());
		return identifier == null ? null : identifier.getPath();
	}

	@FunctionDoc(
		name = "getOutput",
		desc = "This returns the output of the recipe",
		returns = @ReturnDoc(type = ItemStackDef.class, desc = "the output of the recipe"),
		examples = "recipe.getOutput()"
	)
	private ScriptItemStack getOutput(Arguments arguments) {
		RecipeEntry<?> recipe = arguments.nextPrimitive(this);
		return new ScriptItemStack(recipe.value().getResult(EssentialUtils.getRegistryManager()));
	}

	@FunctionDoc(
		name = "getIngredients",
		desc = "This returns all the possible ingredients of the recipe",
		returns = @ReturnDoc(type = ListDef.class, desc = "list of lists, each inner lists contains possible recipe items"),
		examples = "recipe.getIngredients()"
	)
	private ArucasList getIngredients(Arguments arguments) {
		RecipeEntry<?> recipe = arguments.nextPrimitive(this);
		ArucasList recipeIngredients = new ArucasList();
		Interpreter interpreter = arguments.getInterpreter();
		for (Ingredient ingredient : recipe.value().getIngredients()) {
			ArucasList slotIngredients = new ArucasList();
			for (ItemStack itemStack : ingredient.getMatchingStacks()) {
				slotIngredients.add(interpreter.create(ItemStackDef.class, new ScriptItemStack(itemStack)));
			}
			recipeIngredients.add(interpreter.create(ListDef.class, slotIngredients));
		}
		return recipeIngredients;
	}
}
