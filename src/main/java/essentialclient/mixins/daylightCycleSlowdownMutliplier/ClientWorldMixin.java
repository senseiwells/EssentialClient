package essentialclient.mixins.daylightCycleSlowdownMutliplier;

import essentialclient.config.clientrule.ClientRules;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientWorld.class)
public abstract class ClientWorldMixin {
	@Shadow
	public abstract void setTimeOfDay(long timeOfDay);

	@Inject(method = "tickTime", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;setTimeOfDay(J)V"), cancellable = true)
	private void onSetTimeOfDay(CallbackInfo ci) {
		if (ClientRules.PERMANENT_TIME.getValue() >= 0) {
			this.setTimeOfDay(ClientRules.PERMANENT_TIME.getValue());
			ci.cancel();
		}
	}
}
