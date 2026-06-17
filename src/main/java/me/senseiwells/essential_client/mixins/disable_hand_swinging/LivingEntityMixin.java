package me.senseiwells.essential_client.mixins.disable_hand_swinging;

import me.senseiwells.essential_client.EssentialClientConfig;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Inject(
        method = "getAttackAnim",
        at = @At("HEAD"),
        cancellable = true
    )
    private void overwriteAttackAnim(
        float a,
        CallbackInfoReturnable<Float> cir
    ) {
        if (EssentialClientConfig.getInstance().getDisableHandSwinging()) {
            cir.setReturnValue(0.0F);
        }
    }
}
