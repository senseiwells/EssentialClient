package me.senseiwells.essentialclient.mixins.core;

import me.senseiwells.essentialclient.feature.keybinds.ClientKeyBinds;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.*;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Set;

@Mixin(Keyboard.class)
public class KeyboardMixin {
	@Shadow
	@Final
	private MinecraftClient client;
	private final static Set<Class<? extends Screen>> whiteListedScreenClass = Set.of(
		CraftingScreen.class, HopperScreen.class, InventoryScreen.class, MerchantScreen.class, BrewingStandScreen.class
	);

	@Inject(method = "onKey", at = @At(value = "HEAD"))
	private void onKeyPressOrRelease(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
		if (client.getWindow().getHandle() == window && (client.currentScreen == null || whiteListedScreenClass.contains(client.currentScreen.getClass()))){
			InputUtil.Key inputKey = InputUtil.fromKeyCode(key, scancode);
			if(action == 0){
				ClientKeyBinds.onKeyRelease(inputKey);
			}
			else {
				ClientKeyBinds.onKeyPress(inputKey);
			}
		}

	}
}
