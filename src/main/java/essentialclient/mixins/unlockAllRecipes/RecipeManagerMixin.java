package essentialclient.mixins.unlockAllRecipes;

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
        // TODO this is bad. Breaks vanilla recipe lookup in single player too. Fix item scroller in some other way
        Optional<T> result = instance.findFirst();
//        if (ClientRules.UNLOCK_ALL_RECIPES_ON_JOIN.getValue() && result.isPresent() && RecipeBookCache.isCached(result.get())) {
//            return Optional.empty();
//        }
        return result;
    }
}
