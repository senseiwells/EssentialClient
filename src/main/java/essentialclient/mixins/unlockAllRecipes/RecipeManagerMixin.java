package essentialclient.mixins.unlockAllRecipes;

import essentialclient.config.clientrule.ClientRules;
import essentialclient.feature.RecipeBookCache;
import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Optional;
import java.util.stream.Stream;

@Mixin(RecipeManager.class)
public class RecipeManagerMixin {
    @Redirect(method = "getFirstMatch", at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;findFirst()Ljava/util/Optional;"))
    public <C extends Inventory, T extends Recipe<C>> Optional<T> onCall(Stream<T> instance) {
        Optional<T> result = instance.findFirst();
        if (ClientRules.UNLOCK_ALL_RECIPES_ON_JOIN.getValue() && result.isPresent() && RecipeBookCache.RECIPE_CACHE.contains(result.get())) {
            return Optional.empty();
        }
        return result;
    }
}
