package me.senseiwells.essentialclient.mixins.keyboard;

import me.senseiwells.essentialclient.feature.keybinds.ClientKeyBinds;
import net.minecraft.client.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(value = Keyboard.class, priority = 1100)
public class KeyboardMixin {
	@ModifyConstant(method = "onKey", constant = @Constant(intValue = 292), require = 0)
	private int onKey292(int original) {
		return ClientKeyBinds.TOGGLE_DEBUG_MENU.getBoundKey().getCode();
	}
}
