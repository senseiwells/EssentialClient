package me.senseiwells.essential_client.mixins.better_accurate_block_placement;

import me.senseiwells.essential_client.features.BetterAccurateBlockPlacement;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.Vec2;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin {
	@ModifyArgs(
		method = "sendPosition",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/network/protocol/game/ServerboundMovePlayerPacket$PosRot;<init>(DDDFFZZ)V"
		)
	)
	private void onConstructPosRot(Args args) {
		Vec2 rotation = BetterAccurateBlockPlacement.getFakeRotation();
		if (rotation != null) {
			args.set(3, rotation.y);
			args.set(4, rotation.x);
		}
	}

	@ModifyArgs(
		method = "sendPosition",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/network/protocol/game/ServerboundMovePlayerPacket$Rot;<init>(FFZZ)V"
		)
	)
	private void onConstructRot(Args args) {
		Vec2 rotation = BetterAccurateBlockPlacement.getFakeRotation();
		if (rotation != null) {
			args.set(0, rotation.y);
			args.set(1, rotation.x);
		}
	}
}
