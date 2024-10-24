package me.senseiwells.essential_client.mixins.spectator_scrolling;

import me.senseiwells.essential_client.EssentialClientConfig;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {
	@ModifyConstant(
		method = "onScroll",
		constant = @Constant(floatValue = 0.005F)
	)
	private float getScrollSensitivity(float constant) {
		return constant * EssentialClientConfig.getInstance().getSpectatorScrollSensitivity();
	}

	@ModifyConstant(
		method = "onScroll",
		constant = @Constant(floatValue = 0.2F)
	)
	private float getMaxScrollSpeed(float constant) {
		return constant * EssentialClientConfig.getInstance().getSpectatorScrollMaxSpeed();
	}
}
