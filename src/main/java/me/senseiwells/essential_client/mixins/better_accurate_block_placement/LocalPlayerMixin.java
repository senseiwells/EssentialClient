package me.senseiwells.essential_client.mixins.better_accurate_block_placement;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.senseiwells.essential_client.features.BetterAccurateBlockPlacement;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.Vec2;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin {
	@ModifyExpressionValue(
		method = "sendPosition",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/player/LocalPlayer;getXRot()F"
		)
	)
	private float overrideWithAccuratePlacementXRot(float original) {
		Vec2 rotation = BetterAccurateBlockPlacement.getFakeRotation();
		if (rotation != null) {
			return rotation.x;
		}
		return original;
	}

	@ModifyExpressionValue(
		method = "sendPosition",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/player/LocalPlayer;getYRot()F"
		)
	)
	private float overrideWithAccuratePlacementYRot(float original) {
		Vec2 rotation = BetterAccurateBlockPlacement.getFakeRotation();
		if (rotation != null) {
			return rotation.y;
		}
		return original;
	}
}
