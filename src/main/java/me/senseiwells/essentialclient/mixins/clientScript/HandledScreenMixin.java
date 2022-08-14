package me.senseiwells.essentialclient.mixins.clientScript;

import me.senseiwells.essentialclient.utils.render.FakeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin {
	@SuppressWarnings("ConstantConditions")
	@Inject(method = "tick", at = @At("HEAD"), cancellable = true)
	private void onTick(CallbackInfo ci) {
		if ((Object) this instanceof FakeInventoryScreen fakeInventoryScreen) {
			fakeInventoryScreen.fakeTick();
			ci.cancel();
		}
	}
}
