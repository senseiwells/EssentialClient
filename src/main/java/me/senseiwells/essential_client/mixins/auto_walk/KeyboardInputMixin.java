package me.senseiwells.essential_client.mixins.auto_walk;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.senseiwells.essential_client.features.AutoWalk;
import net.minecraft.client.Options;
import net.minecraft.client.player.KeyboardInput;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;


@Mixin(KeyboardInput.class)
public class KeyboardInputMixin {
	@Shadow @Final private Options options;

	@ModifyExpressionValue(
		method = "tick",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/KeyMapping;isDown()Z",
			ordinal = 0
		)
	)
	private boolean onTick(boolean original) {
		return AutoWalk.tick(original, this.options);
	}
}
