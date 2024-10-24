package me.senseiwells.essential_client.mixins.disable_hotbar_scrolling;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import me.senseiwells.essential_client.EssentialClientConfig;
import net.minecraft.client.MouseHandler;
import net.minecraft.world.entity.player.Inventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {
	@WrapWithCondition(
		method = "onScroll",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/player/Inventory;setSelectedHotbarSlot(I)V"
		)
	)
	private boolean shouldScrollHotbar(Inventory instance, int i) {
		return !EssentialClientConfig.getInstance().getDisableHotbarScrolling();
	}
}
