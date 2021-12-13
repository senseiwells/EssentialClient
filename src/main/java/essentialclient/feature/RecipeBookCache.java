package essentialclient.feature;

import net.minecraft.recipe.Recipe;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public interface RecipeBookCache {
    Set<Recipe<?>> RECIPE_CACHE = new HashSet<>();

    default Set<Recipe<?>> getRecipeCache() {
        return RECIPE_CACHE;
    }

    default void setRecipeCache(List<Recipe<?>> recipeCache) {
        this.RECIPE_CACHE.addAll(recipeCache);
    }

    default void addRecipeToCache(Recipe<?> recipe) {
        this.RECIPE_CACHE.add(recipe);
    }

    default void removeRecipeFromCache(Recipe<?> recipe) {
        this.RECIPE_CACHE.remove(recipe);
    }
}
