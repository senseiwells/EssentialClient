package me.senseiwells.essentialclient.mixins.core;

import me.senseiwells.essentialclient.feature.keybinds.ClientKeyBinds;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
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

	@Inject(method = "onKey", at = @At(value = "HEAD"))
	private void onKey(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
		if (this.client.getWindow().getHandle() == window) {
			InputUtil.Key inputKey = InputUtil.fromKeyCode(key, scancode);
			boolean isInGui = this.client.currentScreen != null;
			if (action == GLFW.GLFW_RELEASE) {
				ClientKeyBinds.onKeyRelease(inputKey, isInGui);
			} else {
				ClientKeyBinds.onKeyPress(inputKey, isInGui);
			}
		}
	}
}
