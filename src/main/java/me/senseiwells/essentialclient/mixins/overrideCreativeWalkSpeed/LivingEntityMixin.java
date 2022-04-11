package me.senseiwells.essentialclient.mixins.overrideCreativeWalkSpeed;

import me.senseiwells.essentialclient.clientrule.ClientRules;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
	@Shadow
	private float movementSpeed;

	@SuppressWarnings("ConstantConditions")
	@Inject(method = "getMovementSpeed(F)F", at = @At("HEAD"), cancellable = true)
	private void overrideMovementSpeed(float slipperiness, CallbackInfoReturnable<Float> cir) {
		float speed = ClientRules.OVERRIDE_CREATIVE_WALK_SPEED.getValue().floatValue();
		if (speed > 0 && (Object) this instanceof PlayerEntity playerEntity && playerEntity.isCreative()) {
			cir.setReturnValue(this.movementSpeed * speed);
		}
	}
}
