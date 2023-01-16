package me.senseiwells.essentialclient.mixins.clientScript;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {
	//#if MC >= 11700
	@Shadow
	public World world;

	@Shadow
	public abstract boolean isGlowingLocal();

	@Inject(method = "isGlowing", at = @At("HEAD"), cancellable = true)
	private void checkLocalGlowing(CallbackInfoReturnable<Boolean> cir) {
		if (this.world.isClient() && this.isGlowingLocal()) {
			cir.setReturnValue(true);
		}
	}
	//#endif
}
