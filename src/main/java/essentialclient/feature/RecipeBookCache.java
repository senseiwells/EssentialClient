package essentialclient.feature;

import net.minecraft.recipe.Recipe;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RecipeBookCache {
    private static final Set<Recipe<?>> RECIPE_CACHE = new HashSet<>();

    public static boolean isCached(Recipe<?> recipe) {
        return RECIPE_CACHE.contains(recipe);
    }

    public static void setRecipeCache(List<Recipe<?>> recipeCache) {
        recipeCache.forEach(RecipeBookCache::addRecipeToCache);
    }

    public static void addRecipeToCache(Recipe<?> recipe) {
        RECIPE_CACHE.add(recipe);
    }

    public static void removeRecipeFromCache(Recipe<?> recipe) {
        RECIPE_CACHE.remove(recipe);
    }
}
