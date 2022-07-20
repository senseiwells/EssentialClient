package me.senseiwells.essentialclient.mixins.core;

import me.senseiwells.essentialclient.feature.keybinds.ClientKeyBinds;
import net.minecraft.client.Keyboard;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class KeyboardMixin {
	@Inject(method = "onKey", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;setKeyPressed(Lnet/minecraft/client/util/InputUtil$Key;Z)V", ordinal = 0))
	private void onRelease(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
		InputUtil.Key inputKey = InputUtil.fromKeyCode(key, scancode);
		ClientKeyBinds.onKeyRelease(inputKey);
	}

	@Inject(method = "onKey", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;setKeyPressed(Lnet/minecraft/client/util/InputUtil$Key;Z)V", ordinal = 2))
	private void onPress(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
		InputUtil.Key inputKey = InputUtil.fromKeyCode(key, scancode);
		ClientKeyBinds.onKeyPress(inputKey);
	}
}
