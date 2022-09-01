package me.senseiwells.essentialclient.mixins.clientScript;

import me.senseiwells.essentialclient.clientscript.events.MinecraftScriptEvents;
import me.senseiwells.essentialclient.utils.keyboard.KeyboardHelper;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class KeyboardMixin {
	@Shadow
	@Final
	private MinecraftClient client;

	@Inject(method = "onKey", at = @At("HEAD"), cancellable = true)
	private void onKey(long window, int key, int scancode, int i, int modifiers, CallbackInfo ci) {
		if (window != this.client.getWindow().getHandle()) {
			return;
		}
		String keyName = KeyboardHelper.translateKeyToString(key);
		if (keyName == null) {
			keyName = "unknown";
		}

		boolean shouldCancel = switch (i) {
			case 0 -> MinecraftScriptEvents.ON_KEY_RELEASE.run(keyName);
			case 1 -> MinecraftScriptEvents.ON_KEY_PRESS.run(keyName);
			default -> false;
		};
		if (shouldCancel) {
			ci.cancel();
		}
	}
}
