package me.senseiwells.essentialclient.feature;

import net.minecraft.recipe.RecipeEntry;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RecipeBookCache {
	private static final Set<RecipeEntry<?>> RECIPE_CACHE = new HashSet<>();

	public static boolean isCached(RecipeEntry<?> recipe) {
		return RECIPE_CACHE.contains(recipe);
	}

	public static void setRecipeCache(List<RecipeEntry<?>> recipeCache) {
		recipeCache.forEach(RecipeBookCache::addRecipeToCache);
	}

	public static void addRecipeToCache(RecipeEntry<?> recipe) {
		RECIPE_CACHE.add(recipe);
	}

	public static void removeRecipeFromCache(RecipeEntry<?> recipe) {
		RECIPE_CACHE.remove(recipe);
	}
}
