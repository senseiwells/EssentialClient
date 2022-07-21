package me.senseiwells.essentialclient.mixins.disableHotbarScrolling;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import me.senseiwells.essentialclient.rule.ClientRules;
import net.minecraft.client.Mouse;
import net.minecraft.entity.player.PlayerInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Mouse.class)
public class MouseMixin {
	@WrapWithCondition(method = "onMouseScroll", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerInventory;scrollInHotbar(D)V"))
	private boolean onScrollHotbar(PlayerInventory playerInventory, double scrollAmount) {
		return !ClientRules.DISABLE_HOTBAR_SCROLLING.getValue();
	}
}
