package essentialclient.mixins.disableNightVisionFlash;

import essentialclient.feature.clientrule.ClientRules;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(method = "getNightVisionStrength", at = @At("RETURN"), cancellable = true)
    private static void removeFlash(LivingEntity livingEntity, float f, CallbackInfoReturnable<Float> cir) {
        float i = Objects.requireNonNull(livingEntity.getStatusEffect(StatusEffects.NIGHT_VISION)).getDuration();
        if (ClientRules.DISABLE_NIGHT_VISION_FLASH.getBoolean()) { cir.setReturnValue(i > 0 ? 1 : i); }
    }
}
