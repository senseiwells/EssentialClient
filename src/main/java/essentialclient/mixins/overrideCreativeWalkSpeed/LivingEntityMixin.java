package essentialclient.mixins.overrideCreativeWalkSpeed;

import essentialclient.feature.clientrule.ClientRules;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Shadow private float movementSpeed;

    @SuppressWarnings("ConstantConditions")
    @Inject(method = "getMovementSpeed(F)F", at = @At("HEAD"), cancellable = true)
    private void overrideMovementSpeed(float slipperiness, CallbackInfoReturnable<Float> cir) {
        float speed = (float) ClientRules.OVERRIDE_CREATIVE_WALK_SPEED.getDouble();
        if (speed > 0 && (Object) this instanceof PlayerEntity && ((PlayerEntity) (Object) this).isCreative()) {
            cir.setReturnValue(movementSpeed * speed);
        }
    }
}
