package me.senseiwells.essentialclient.clientscript.values;

import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.api.docs.ClassDoc;
import me.senseiwells.arucas.api.docs.FunctionDoc;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.Arguments;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.impl.ArucasList;
import me.senseiwells.arucas.values.*;
import me.senseiwells.arucas.values.functions.BuiltInFunction;
import me.senseiwells.arucas.values.functions.MemberFunction;
import me.senseiwells.essentialclient.clientscript.extensions.ArucasMinecraftExtension;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import static me.senseiwells.arucas.utils.ValueTypes.LIST;
import static me.senseiwells.arucas.utils.ValueTypes.STRING;
import static me.senseiwells.essentialclient.clientscript.core.MinecraftAPI.ITEM_STACK;
import static me.senseiwells.essentialclient.clientscript.core.MinecraftAPI.RECIPE;

public class RecipeValue extends GenericValue<Recipe<?>> {
	public RecipeValue(Recipe<?> value) {
		super(value);
	}

	@Override
	public GenericValue<Recipe<?>> copy(Context context) throws CodeError {
		return this;
	}

	@Override
	public String getAsString(Context context) throws CodeError {
		return "Recipe{" + this.value.getId().getPath() + "}";
	}

	@Override
	public int getHashCode(Context context) throws CodeError {
		return this.value.hashCode();
	}

	@Override
	public boolean isEquals(Context context, Value value) throws CodeError {
		return this.value == value.getValue();
	}

	@Override
	public String getTypeName() {
		return RECIPE;
	}

	@ClassDoc(
		name = RECIPE,
		desc = "This class represents recipes in Minecraft.",
		importPath = "Minecraft"
	)
	public static class ArucasRecipeClass extends ArucasClassExtension {
		public ArucasRecipeClass() {
			super(RECIPE);
		}

		@Override
		public Map<String, Value> getDefinedStaticVariables() {
			ClientPlayNetworkHandler networkHandler = EssentialUtils.getNetworkHandler();
			if (networkHandler == null) {
				return super.getDefinedStaticVariables();
			}
			Map<String, Value> recipeMap = new HashMap<>();
			ArucasList recipeList = new ArucasList();
			for (Recipe<?> recipe : networkHandler.getRecipeManager().values()) {
				RecipeValue recipeValue = new RecipeValue(recipe);
				recipeMap.put(recipe.getId().getPath().toUpperCase(Locale.ROOT), recipeValue);
				recipeList.add(recipeValue);
			}
			recipeMap.put("ALL", new ListValue(recipeList));
			return recipeMap;
		}

		@Override
		public ArucasFunctionMap<BuiltInFunction> getDefinedStaticMethods() {
			return ArucasFunctionMap.of(
				BuiltInFunction.of("of", 1, this::newRecipe)
			);
		}

		@FunctionDoc(
			isStatic = true,
			name = "of",
			desc = "This converts a recipe id into a Recipe if it's valid",
			params = {STRING, "recipeId", "the id of the recipe to convert to a Recipe"},
			returns = {RECIPE, "the recipe instance from the id"},
			throwMsgs = "Recipe with id ... doesn't exist",
			example = "Recipe.of('redstone_block')"
		)
		private Value newRecipe(Arguments arguments) throws CodeError {
			String id = arguments.getNextGeneric(StringValue.class);
			ClientPlayNetworkHandler networkHandler = ArucasMinecraftExtension.getNetworkHandler();
			Identifier identifier = ArucasMinecraftExtension.getId(arguments, id);
			Optional<? extends Recipe<?>> recipe = networkHandler.getRecipeManager().get(identifier);
			if (recipe.isEmpty()) {
				throw arguments.getError("Recipe with id '%s' doesn't exist", id);
			}
			return new RecipeValue(recipe.get());
		}

		@Override
		public ArucasFunctionMap<MemberFunction> getDefinedMethods() {
			return ArucasFunctionMap.of(
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
			example = "recipe.getFullId()"
		)
		private Value getFullId(Arguments arguments) throws CodeError {
			RecipeValue thisValue = arguments.getNext(RecipeValue.class);
			return StringValue.of(thisValue.value.getId().toString());
		}

		@FunctionDoc(
			name = "getId",
			desc = "This returns the id of the recipe",
			returns = {STRING, "the id of the recipe"},
			example = "recipe.getId()"
		)
		private Value getId(Arguments arguments) throws CodeError {
			RecipeValue thisValue = arguments.getNext(RecipeValue.class);
			return StringValue.of(thisValue.value.getId().getPath());
		}

		@FunctionDoc(
			name = "getCraftingType",
			desc = "This returns the crafting type of the recipe",
			returns = {STRING, "the crafting type of the recipe, for example: 'crafting', 'smelting', 'blasting'"},
			example = "recipe.getCraftingType()"
		)
		private Value getCraftingType(Arguments arguments) throws CodeError {
			RecipeValue thisValue = arguments.getNext(RecipeValue.class);
			Identifier identifier = Registry.RECIPE_TYPE.getId(thisValue.value.getType());
			return identifier == null ? NullValue.NULL : StringValue.of(identifier.getPath());
		}

		@FunctionDoc(
			name = "getOutput",
			desc = "This returns the output of the recipe",
			returns = {ITEM_STACK, "the output of the recipe"},
			example = "recipe.getOutput()"
		)
		private Value getOutput(Arguments arguments) throws CodeError {
			RecipeValue thisValue = arguments.getNext(RecipeValue.class);
			return new ItemStackValue(thisValue.value.getOutput());
		}

		@FunctionDoc(
			name = "getIngredients",
			desc = "This returns all the possible ingredients of the recipe",
			returns = {LIST, "list of lists, each inner lists contains possible recipe items"},
			example = "recipe.getIngredients()"
		)
		private Value getIngredients(Arguments arguments) throws CodeError {
			RecipeValue thisValue = arguments.getNext(RecipeValue.class);
			ArucasList recipeIngredients = new ArucasList();
			for (Ingredient ingredient : thisValue.value.getIngredients()) {
				ArucasList slotIngredients = new ArucasList();
				for (ItemStack itemStack : ingredient.getMatchingStacks()) {
					slotIngredients.add(new ItemStackValue(itemStack));
				}
				recipeIngredients.add(new ListValue(slotIngredients));
			}
			return new ListValue(recipeIngredients);
		}

		@Override
		public Class<RecipeValue> getValueClass() {
			return RecipeValue.class;
		}
	}
}
