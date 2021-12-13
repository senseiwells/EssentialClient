package essentialclient.feature;

import net.minecraft.recipe.Recipe;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public interface RecipeBookCache {
    Set<Recipe<?>> RECIPE_CACHE = new HashSet<>();

    static boolean isCached(Recipe<?> recipe) {
        return RECIPE_CACHE.contains(recipe);
    }

    default void setRecipeCache(List<Recipe<?>> recipeCache) {
        recipeCache.forEach(this::addRecipeToCache);
    }

    default void addRecipeToCache(Recipe<?> recipe) {
        this.RECIPE_CACHE.add(recipe);
    }

    default void removeRecipeFromCache(Recipe<?> recipe) {
        this.RECIPE_CACHE.remove(recipe);
    }
}
