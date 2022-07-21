package me.senseiwells.essentialclient.mixins.mouseScrollRules;

import me.senseiwells.essentialclient.rule.ClientRules;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(Mouse.class)
public class MouseMixin {
	@ModifyConstant(method = "onMouseScroll", constant = @Constant(floatValue = 0.2F))
	private float newScrollLimit(float originalFloat) {
		return ClientRules.INCREASE_SPECTATOR_SCROLL_SPEED.getValue() ? 10.0F : originalFloat;
	}

	@ModifyConstant(method = "onMouseScroll", constant = @Constant(floatValue = 0.005F))
	private float newSensitivityLimit(float originalFloat) {
		int newSensitivity = ClientRules.INCREASE_SPECTATOR_SCROLL_SENSITIVITY.getValue();
		return newSensitivity > 0 ? originalFloat * newSensitivity : originalFloat;
	}
}
