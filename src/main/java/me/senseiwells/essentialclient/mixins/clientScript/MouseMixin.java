package me.senseiwells.essentialclient.mixins.clientScript;

import me.senseiwells.essentialclient.clientscript.events.MinecraftScriptEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Mouse.class)
public class MouseMixin {
	@Shadow
	@Final
	private MinecraftClient client;

	@Inject(method = "onMouseScroll", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/MinecraftClient;getOverlay()Lnet/minecraft/client/gui/screen/Overlay;"), locals = LocalCapture.CAPTURE_FAILHARD,
		cancellable = true)
	private void mouseScroll(long window, double horizontal, double vertical, CallbackInfo ci, double d) {
		if (window != this.client.getWindow().getHandle()) {
			return;
		}
		if (MinecraftScriptEvents.ON_MOUSE_SCROLL.run(d)) {
			ci.cancel();
		}
	}
}
