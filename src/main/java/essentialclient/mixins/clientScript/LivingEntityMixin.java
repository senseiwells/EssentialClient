package essentialclient.mixins.clientScript;

import essentialclient.clientscript.extensions.FakeEntityWrapper;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
	@Shadow
	protected abstract float turnHead(float bodyRotation, float headRotation);

	@Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;turnHead(FF)F"))
	private float onTurnHead(LivingEntity instance, float bodyRotation, float headRotation) {
		return FakeEntityWrapper.isFakeEntity(instance.getEntityId()) ? 0 : this.turnHead(bodyRotation, headRotation);
	}
}
