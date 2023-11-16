package me.senseiwells.essentialclient.mixins.customClientCape;

import me.senseiwells.essentialclient.feature.CustomClientCape;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.SkinTextures;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayerEntity.class)
public class AbstractClientPlayerEntityMixin {
	@SuppressWarnings("ConstantConditions")
	@Inject(method = "getSkinTextures", at = @At("RETURN"), cancellable = true)
	private void onGetCapeTexture(CallbackInfoReturnable<SkinTextures> cir) {
		if ((Object) this instanceof ClientPlayerEntity) {
			if (CustomClientCape.getCurrentCape() != null) {
				SkinTextures old = cir.getReturnValue();
				cir.setReturnValue(new SkinTextures(
					old.texture(),
					old.textureUrl(),
					CustomClientCape.getCurrentCape(),
					CustomClientCape.getCurrentCape(),
					old.model(),
					old.secure()
				));
			}
		}
	}

}
