package me.senseiwells.essentialclient.mixins.disableNarrator;

import me.senseiwells.essentialclient.rule.ClientRules;
import net.minecraft.client.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(Keyboard.class)
public class KeyboardMixin {
	@ModifyConstant(method = "onKey", constant = @Constant(intValue = 66), require = 0)
	private int disableNarrator(int old) {
		return ClientRules.DISABLE_NARRATOR.getValue() ? -2 : old;
	}
}
