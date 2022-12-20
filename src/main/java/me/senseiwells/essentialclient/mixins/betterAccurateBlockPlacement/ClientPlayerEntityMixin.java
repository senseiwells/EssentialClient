package me.senseiwells.essentialclient.mixins.betterAccurateBlockPlacement;

import com.mojang.authlib.GameProfile;
import me.senseiwells.essentialclient.feature.BetterAccurateBlockPlacement;
import me.senseiwells.essentialclient.rule.ClientRules;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.Packet;

//#if MC >= 11901 && MC < 11903
//$$import net.minecraft.network.encryption.PlayerPublicKey;
//#endif

import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends PlayerEntity {
	//#if MC >= 11903
	public ClientPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
		super(world, pos, yaw, gameProfile);
	}
	//#elseif MC >= 11901
	//$$public ClientPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile, PlayerPublicKey publicKey) {
	//$$	super(world, pos, yaw, gameProfile, publicKey);
	//$$}
	//#else
	//$$public ClientPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile profile) {
	//$$	super(world, pos, yaw, profile);
	//$$}
	//#endif

	@Redirect(
		method = "sendMovementPackets",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/Packet;)V",
			ordinal = 0
		),
		slice = @Slice(
			from = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isCamera()Z")
		),
		require = 0
	)
	private void onSendPacketVehicle(ClientPlayNetworkHandler clientPlayNetworkHandler, Packet<?> packet) {
		if (!ClientRules.BETTER_ACCURATE_BLOCK_PLACEMENT.getValue()) {
			clientPlayNetworkHandler.sendPacket(packet);
			return;
		}
		Vec3d vec3d = this.getVelocity();
		clientPlayNetworkHandler.sendPacket(new PlayerMoveC2SPacket.Full(
			vec3d.x, -999, vec3d.y,
			BetterAccurateBlockPlacement.fakeYaw,
			BetterAccurateBlockPlacement.fakePitch,
			this.isOnGround()
		));
	}

	@Redirect(
		method = "sendMovementPackets",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/Packet;)V",
			ordinal = 1
		),
		slice = @Slice(
			from = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isCamera()Z")
		),
		require = 0
	)
	private void onSendPacketAll(ClientPlayNetworkHandler clientPlayNetworkHandler, Packet<?> packet) {
		if (!ClientRules.BETTER_ACCURATE_BLOCK_PLACEMENT.getValue()) {
			clientPlayNetworkHandler.sendPacket(packet);
			return;
		}

		PlayerMoveC2SPacket.Full existing = ((PlayerMoveC2SPacket.Full) packet);
		clientPlayNetworkHandler.sendPacket(new PlayerMoveC2SPacket.Full(
			existing.getX(0), existing.getY(0), existing.getZ(0),
			BetterAccurateBlockPlacement.fakeYaw,
			BetterAccurateBlockPlacement.fakePitch,
			this.isOnGround()
		));
	}

	@SuppressWarnings("ConstantConditions")
	@Redirect(
		method = "sendMovementPackets",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/Packet;)V",
			ordinal = 3
		),
		slice = @Slice(
			from = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isCamera()Z")
		),
		require = 0
	)
	private void onSendPacketLook(ClientPlayNetworkHandler clientPlayNetworkHandler, Packet<?> packet) {
		if (!ClientRules.BETTER_ACCURATE_BLOCK_PLACEMENT.getValue()) {
			clientPlayNetworkHandler.sendPacket(packet);
			return;
		}
		BetterAccurateBlockPlacement.sendLookPacket((ClientPlayerEntity) (Object) this);
	}
}
