package me.senseiwells.essential_client.mixins.auto_walk;

import me.senseiwells.essential_client.features.AutoWalk;
import net.minecraft.client.Options;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.KeyboardInput;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardInput.class)
public class KeyboardInputMixin {
	@Shadow @Final private Options options;

	@Inject(
		method = "tick",
		at = @At(
			value = "INVOKE_ASSIGN",
			target = "Lnet/minecraft/client/KeyMapping;isDown()Z",
			ordinal = 1
		)
	)
	private void onTick(boolean isSneaking, float sneakingSpeedMultiplier, CallbackInfo ci) {
		AutoWalk.tick((Input) (Object) this, this.options);
	}
}
