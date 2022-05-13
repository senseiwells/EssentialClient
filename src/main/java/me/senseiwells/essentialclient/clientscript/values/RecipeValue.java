package me.senseiwells.essentialclient.clientscript.values;

import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.impl.ArucasList;
import me.senseiwells.arucas.values.ListValue;
import me.senseiwells.arucas.values.NullValue;
import me.senseiwells.arucas.values.StringValue;
import me.senseiwells.arucas.values.Value;
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

public class RecipeValue extends Value<Recipe<?>> {
	public RecipeValue(Recipe<?> value) {
		super(value);
	}

	@Override
	public Value<Recipe<?>> copy(Context context) throws CodeError {
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
	public boolean isEquals(Context context, Value<?> value) throws CodeError {
		return this.value == value.value;
	}

	@Override
	public String getTypeName() {
		return "Recipe";
	}

	/**
	 * Recipe class for Arucas. This class represents recipes in Minecraft. <br>
	 * Import the class with <code>import Recipe from Minecraft;</code> <br>
	 * Fully Documented.
	 * @author senseiwells
	 */
	public static class ArucasRecipeClass extends ArucasClassExtension {
		public ArucasRecipeClass() {
			super("Recipe");
		}

		@Override
		public Map<String, Value<?>> getDefinedStaticVariables() {
			ClientPlayNetworkHandler networkHandler = EssentialUtils.getNetworkHandler();
			if (networkHandler == null) {
				return super.getDefinedStaticVariables();
			}
			Map<String, Value<?>> recipeMap = new HashMap<>();
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
				new BuiltInFunction("of", "recipeId", this::newRecipe)
			);
		}

		/**
		 * Name: <code>Recipe.of(recipeId)</code> <br>
		 * Description: This converts a recipe id into a Recipe if it's valid <br>
		 * Returns - Recipe: the entity instance from the id <br>
		 * Throws - Error: <code>Recipe with id ... doesn't exist</code> if the id is not a valid recipe id <br>
		 * Example: <code>Recipe.of("redstone_block");</code>
		 */
		private Value<?> newRecipe(Context context, BuiltInFunction function) throws CodeError {
			String id = function.getParameterValueOfType(context, StringValue.class, 0).value;
			ClientPlayNetworkHandler networkHandler = ArucasMinecraftExtension.getNetworkHandler();
			Identifier identifier = ArucasMinecraftExtension.getId(context, function.syntaxPosition, id);
			Optional<? extends Recipe<?>> recipe = networkHandler.getRecipeManager().get(identifier);
			if (recipe.isEmpty()) {
				throw new RuntimeError("Recipe with id '%s' doesn't exist".formatted(id), function.syntaxPosition, context);
			}
			return new RecipeValue(recipe.get());
		}

		@Override
		public ArucasFunctionMap<MemberFunction> getDefinedMethods() {
			return ArucasFunctionMap.of(
				new MemberFunction("getFullId", this::getFullId),
				new MemberFunction("getId", this::getId),
				new MemberFunction("getCraftingType", this::getCraftingType),
				new MemberFunction("getOutput", this::getOutput),
				new MemberFunction("getIngredients", this::getIngredients)
			);
		}

		/**
		 * Name: <code>&lt;Recipe>.getFullId()</code> <br>
		 * Description: This returns the full id of the recipe <br>
		 * Returns - String: the full id of the recipe <br>
		 * Example: <code>recipe.getFullId();</code>
		 */
		private Value<?> getFullId(Context context, MemberFunction function) throws CodeError {
			RecipeValue thisValue = function.getThis(context, RecipeValue.class);
			return StringValue.of(thisValue.value.getId().toString());
		}

		/**
		 * Name: <code>&lt;Recipe>.getId()</code> <br>
		 * Description: This returns the id of the recipe <br>
		 * Returns - String: the id of the recipe <br>
		 * Example: <code>recipe.getId();</code>
		 */
		private Value<?> getId(Context context, MemberFunction function) throws CodeError {
			RecipeValue thisValue = function.getThis(context, RecipeValue.class);
			return StringValue.of(thisValue.value.getId().getPath());
		}

		/**
		 * Name: <code>&lt;Recipe>.getCraftingType()</code> <br>
		 * Description: This returns the crafting type of the recipe <br>
		 * Returns - String: the crafting type of the recipe, for example:
		 * <code>"crafting", "smelting", "blasting"</code> <br>
		 * Example: <code>recipe.getCraftingType();</code>
		 */
		private Value<?> getCraftingType(Context context, MemberFunction function) throws CodeError {
			RecipeValue thisValue = function.getThis(context, RecipeValue.class);
			Identifier identifier = Registry.RECIPE_TYPE.getId(thisValue.value.getType());
			return identifier == null ? NullValue.NULL : StringValue.of(identifier.getPath());
		}

		/**
		 * Name: <code>&lt;Recipe>.getOutput()</code> <br>
		 * Description: This returns the output of the recipe <br>
		 * Returns - ItemStack: the output of the recipe <br>
		 * Example: <code>recipe.getOutput();</code>
		 */
		private Value<?> getOutput(Context context, MemberFunction function) throws CodeError {
			RecipeValue thisValue = function.getThis(context, RecipeValue.class);
			return new ItemStackValue(thisValue.value.getOutput());
		}

		/**
		 * Name: <code>&lt;Recipe>.getIngredients()</code> <br>
		 * Description: This returns all the possible ingredients of the recipe <br>
		 * Returns - List: list of lists, each inner lists contains possible recipe items <br>
		 * Example: <code>recipe.getIngredients();</code>
		 */
		private Value<?> getIngredients(Context context, MemberFunction function) throws CodeError {
			RecipeValue thisValue = function.getThis(context, RecipeValue.class);
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
