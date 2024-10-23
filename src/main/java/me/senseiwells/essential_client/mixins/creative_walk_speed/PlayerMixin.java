package me.senseiwells.essential_client.mixins.creative_walk_speed;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.senseiwells.essential_client.EssentialClientConfig;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Player.class)
public abstract class PlayerMixin {
	@Shadow public abstract boolean isCreative();

	@ModifyReturnValue(
		method = "getSpeed",
		at = @At("RETURN")
	)
	private float onGetPlayerSpeed(float original) {
		if (this.isCreative()) {
			return original * EssentialClientConfig.getInstance().getCreativeWalkSpeed();
		}
		return original;
	}
}
