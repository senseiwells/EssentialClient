package me.senseiwells.essentialclient.clientscript.definitions;

import kotlin.Triple;
import me.senseiwells.arucas.api.docs.ClassDoc;
import me.senseiwells.arucas.api.docs.FunctionDoc;
import me.senseiwells.arucas.builtin.ListDef;
import me.senseiwells.arucas.builtin.StringDef;
import me.senseiwells.arucas.classes.ClassInstance;
import me.senseiwells.arucas.classes.CreatableDefinition;
import me.senseiwells.arucas.core.Interpreter;
import me.senseiwells.arucas.exceptions.RuntimeError;
import me.senseiwells.arucas.utils.*;
import me.senseiwells.arucas.utils.impl.ArucasList;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import me.senseiwells.essentialclient.utils.clientscript.ClientScriptUtils;
import me.senseiwells.essentialclient.utils.clientscript.impl.ScriptItemStack;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.*;

import static me.senseiwells.arucas.utils.Util.Types.LIST;
import static me.senseiwells.arucas.utils.Util.Types.STRING;
import static me.senseiwells.essentialclient.clientscript.core.MinecraftAPI.ITEM_STACK;
import static me.senseiwells.essentialclient.clientscript.core.MinecraftAPI.RECIPE;

@ClassDoc(
	name = RECIPE,
	desc = "This class represents recipes in Minecraft.",
	importPath = "Minecraft",
	language = Util.Language.Java
)
public class RecipeDef extends CreatableDefinition<Recipe<?>> {
	public RecipeDef(Interpreter interpreter) {
		super(RECIPE, interpreter);
	}

	@Override
	public String toString$Arucas(ClassInstance instance, Interpreter interpreter, LocatableTrace trace) {
		return "Recipe{id=" + instance.asPrimitive(this).getId() + "}";
	}

	@Override
	public List<Triple<String, Object, Boolean>> defineStaticFields() {
		ClientPlayNetworkHandler networkHandler = EssentialUtils.getNetworkHandler();
		if (networkHandler == null) {
			return super.defineStaticFields();
		}

		SortedMap<String, ClassInstance> map = new TreeMap<>();
		for (Recipe<?> recipe : networkHandler.getRecipeManager().values()) {
			map.put(recipe.toString().toUpperCase(), this.create(recipe));
		}

		ArucasList list = new ArucasList();
		List<Triple<String, Object, Boolean>> fields = new ArrayList<>(map.size());
		map.forEach((key, value) -> {
			list.add(value);
			fields.add(new Triple<>(key, value, false));
		});
		fields.add(new Triple<>("ALL", list, false));
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
		params = {STRING, "recipeId", "the id of the recipe to convert to a Recipe"},
		returns = {RECIPE, "the recipe instance from the id"},
		examples = "Recipe.of('redstone_block')"
	)
	private Recipe<?> of(Arguments arguments) {
		String id = arguments.nextPrimitive(StringDef.class);
		ClientPlayNetworkHandler networkHandler = EssentialUtils.getNetworkHandler();
		Identifier identifier = ClientScriptUtils.stringToIdentifier(id);
		Optional<? extends Recipe<?>> recipe = networkHandler.getRecipeManager().get(identifier);
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
		returns = {STRING, "the full id of the recipe"},
		examples = "recipe.getFullId()"
	)
	private String getFullId(Arguments arguments) {
		Recipe<?> recipe = arguments.nextPrimitive(this);
		return recipe.getId().toString();
	}

	@FunctionDoc(
		name = "getId",
		desc = "This returns the id of the recipe",
		returns = {STRING, "the id of the recipe"},
		examples = "recipe.getId()"
	)
	private String getId(Arguments arguments) {
		Recipe<?> recipe = arguments.nextPrimitive(this);
		return recipe.getId().toString();
	}

	@FunctionDoc(
		name = "getCraftingType",
		desc = "This returns the crafting type of the recipe",
		returns = {STRING, "the crafting type of the recipe, for example: 'crafting', 'smelting', 'blasting'"},
		examples = "recipe.getCraftingType()"
	)
	private String getCraftingType(Arguments arguments) {
		Recipe<?> recipe = arguments.nextPrimitive(this);
		Identifier identifier = Registry.RECIPE_TYPE.getId(recipe.getType());
		return identifier == null ? null : identifier.getPath();
	}

	@FunctionDoc(
		name = "getOutput",
		desc = "This returns the output of the recipe",
		returns = {ITEM_STACK, "the output of the recipe"},
		examples = "recipe.getOutput()"
	)
	private ScriptItemStack getOutput(Arguments arguments) {
		Recipe<?> recipe = arguments.nextPrimitive(this);
		return new ScriptItemStack(recipe.getOutput());
	}

	@FunctionDoc(
		name = "getIngredients",
		desc = "This returns all the possible ingredients of the recipe",
		returns = {LIST, "list of lists, each inner lists contains possible recipe items"},
		examples = "recipe.getIngredients()"
	)
	private ArucasList getIngredients(Arguments arguments) {
		Recipe<?> recipe = arguments.nextPrimitive(this);
		ArucasList recipeIngredients = new ArucasList();
		Interpreter interpreter = arguments.getInterpreter();
		for (Ingredient ingredient : recipe.getIngredients()) {
			ArucasList slotIngredients = new ArucasList();
			for (ItemStack itemStack : ingredient.getMatchingStacks()) {
				slotIngredients.add(interpreter.create(ItemStackDef.class, new ScriptItemStack(itemStack)));
			}
			recipeIngredients.add(interpreter.create(ListDef.class, slotIngredients));
		}
		return recipeIngredients;
	}
}
