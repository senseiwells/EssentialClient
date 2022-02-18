package essentialclient.mixins.customClientCape;

import essentialclient.feature.CustomClientCape;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("ConstantConditions")
@Mixin(AbstractClientPlayerEntity.class)
public class AbstractClientPlayerEntityMixin {
	@Inject(method = "getCapeTexture", at = @At("RETURN"), cancellable = true)
	private void onGetCapeTexture(CallbackInfoReturnable<Identifier> cir) {
		if (CustomClientCape.capeTexture != null && (Object) this instanceof ClientPlayerEntity) {
			cir.setReturnValue(CustomClientCape.capeTexture);
		}
	}

	@Inject(method = "getElytraTexture", at = @At("RETURN"), cancellable = true)
	private void onGetElytraTexture(CallbackInfoReturnable<Identifier> cir) {
		if (CustomClientCape.capeTexture != null && (Object) this instanceof ClientPlayerEntity) {
			cir.setReturnValue(CustomClientCape.capeTexture);
		}
	}
}
