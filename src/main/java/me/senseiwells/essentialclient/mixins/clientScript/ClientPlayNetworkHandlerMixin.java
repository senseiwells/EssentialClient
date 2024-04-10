package me.senseiwells.essentialclient.mixins.clientScript;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.mojang.authlib.GameProfile;
import me.senseiwells.essentialclient.clientscript.core.ClientScriptIO;
import me.senseiwells.essentialclient.clientscript.events.MinecraftScriptEvents;
import me.senseiwells.essentialclient.utils.clientscript.impl.ScriptBlockState;
import me.senseiwells.essentialclient.utils.clientscript.impl.ScriptItemStack;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.*;
import net.minecraft.client.network.message.MessageHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.registry.CombinedDynamicRegistries;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin extends ClientCommonNetworkHandler {
	@Shadow
	private ClientWorld world;

	@Final
	@Shadow
	private DynamicRegistryManager.Immutable combinedDynamicRegistries;

	@Shadow
	@Final
	private Map<UUID, PlayerListEntry> playerListEntries;

	protected ClientPlayNetworkHandlerMixin(MinecraftClient client, ClientConnection connection, ClientConnectionState connectionState) {
		super(client, connection, connectionState);
	}

	@Inject(
		method = "onHealthUpdate",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/network/NetworkThreadUtils;forceMainThread(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/listener/PacketListener;Lnet/minecraft/util/thread/ThreadExecutor;)V",
			shift = At.Shift.AFTER
		)
	)
	private void onHealthUpdate(HealthUpdateS2CPacket packet, CallbackInfo ci) {
		MinecraftScriptEvents.ON_HEALTH_UPDATE.run(packet.getHealth());
	}

	@Inject(
		method = "onBlockUpdate",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/network/NetworkThreadUtils;forceMainThread(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/listener/PacketListener;Lnet/minecraft/util/thread/ThreadExecutor;)V",
			shift = At.Shift.AFTER
		)
	)
	private void onBlockUpdate(BlockUpdateS2CPacket packet, CallbackInfo ci) {
		MinecraftScriptEvents.ON_BLOCK_UPDATE.run(new ScriptBlockState(packet.getState(), packet.getPos()));
	}

	@Inject(
		method = "onEntityStatus",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/render/GameRenderer;showFloatingItem(Lnet/minecraft/item/ItemStack;)V"
		)
	)
	private void onTotem(CallbackInfo ci) {
		MinecraftScriptEvents.ON_TOTEM.run();
	}

	@Inject(
		method = "onGameStateChange",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;setGameMode(Lnet/minecraft/world/GameMode;)V"
		)
	)
	private void onGamemodeChange(GameStateChangeS2CPacket packet, CallbackInfo ci) {
		MinecraftScriptEvents.ON_GAMEMODE_CHANGE.run(GameMode.byId((int) packet.getValue()).getName());
	}

	@Inject(
		method = "onItemPickupAnimation",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/network/NetworkThreadUtils;forceMainThread(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/listener/PacketListener;Lnet/minecraft/util/thread/ThreadExecutor;)V",
			shift = At.Shift.AFTER
		)
	)
	private void onPickUp(ItemPickupAnimationS2CPacket packet, CallbackInfo ci) {
		Entity entity = this.world.getEntityById(packet.getEntityId());
		LivingEntity livingEntity = (LivingEntity) this.world.getEntityById(packet.getCollectorEntityId());
		if (entity instanceof ItemEntity itemEntity && this.client.player != null && this.client.player.equals(livingEntity)) {
			MinecraftScriptEvents.ON_PICK_UP_ITEM.run(new ScriptItemStack(itemEntity.getStack().copy()));
		}
	}

	@Inject(method = "onPlayerRespawn", at = @At("TAIL"))
	private void onPlayerRespawn(PlayerRespawnS2CPacket packet, CallbackInfo ci) {
		MinecraftScriptEvents.ON_RESPAWN.run(this.client.player);
	}

	@WrapWithCondition(
		method = "onChatMessage",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/network/message/MessageHandler;onChatMessage(Lnet/minecraft/network/message/SignedMessage;Lcom/mojang/authlib/GameProfile;Lnet/minecraft/network/message/MessageType$Parameters;)V"
		)
	)
	private boolean onChatMessage(MessageHandler instance, SignedMessage message, GameProfile sender, MessageType.Parameters params) {
		MessageType messageType = params.type();
		Identifier typeId = this.combinedDynamicRegistries.get(RegistryKeys.MESSAGE_TYPE).getId(messageType);
		String content = message.getContent().getString();
		String type = typeId == null ? "unknown" : typeId.getPath();
		return !MinecraftScriptEvents.ON_RECEIVE_MESSAGE.run(sender.getId().toString(), content, type);
	}

	@Inject(
		method = "onGameMessage",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/network/NetworkThreadUtils;forceMainThread(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/listener/PacketListener;Lnet/minecraft/util/thread/ThreadExecutor;)V",
			shift = At.Shift.AFTER
		),
		cancellable = true
	)
	private void onGameMessage(GameMessageS2CPacket packet, CallbackInfo ci) {
		String content = packet.content().getString();
		String type = packet.overlay() ? "overlay" : "system";
		if (MinecraftScriptEvents.ON_RECEIVE_MESSAGE.run(Util.NIL_UUID.toString(), content, type)) {
			ci.cancel();
		}
	}

	@Inject(
		method = "onPlayerList",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/network/NetworkThreadUtils;forceMainThread(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/listener/PacketListener;Lnet/minecraft/util/thread/ThreadExecutor;)V",
			shift = At.Shift.AFTER
		)
	)
	private void onPlayerList(PlayerListS2CPacket packet, CallbackInfo info) {
		for (PlayerListS2CPacket.Action action : packet.getActions()) {
			if (action == PlayerListS2CPacket.Action.ADD_PLAYER) {
				packet.getEntries().forEach(entry -> MinecraftScriptEvents.ON_PLAYER_JOIN.run(entry.profile().getName(), entry.profile().getId().toString()));
			}
		}
	}

	@Inject(
		method = "onPlayerRemove",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/network/NetworkThreadUtils;forceMainThread(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/listener/PacketListener;Lnet/minecraft/util/thread/ThreadExecutor;)V",
			shift = At.Shift.AFTER
		)
	)
	private void onPlayerRemove(PlayerRemoveS2CPacket packet, CallbackInfo ci) {
		for (UUID uuid : packet.profileIds()) {
			PlayerListEntry entry = this.playerListEntries.get(uuid);
			if (entry != null) {
				MinecraftScriptEvents.ON_PLAYER_LEAVE.run(entry.getProfile().getName(), uuid.toString());
			}
		}
	}

	@Inject(
		method = "onDeathMessage",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;getEntityById(I)Lnet/minecraft/entity/Entity;", shift = At.Shift.BEFORE)
	)
	private void onDeath(
		DeathMessageS2CPacket packet,
		CallbackInfo ci
	) {
		Entity entity = this.world.getEntityById(packet.getEntityId());
		if (entity == this.client.player) {
			Entity killer = null;
			MinecraftScriptEvents.ON_DEATH.run(killer, packet.getMessage());
		}
	}

	@Inject(
		method = "onEntitySpawn",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/world/ClientWorld;addEntity(Lnet/minecraft/entity/Entity;)V",
			shift = At.Shift.AFTER
		)
	)
	private void onEntitySpawn(EntitySpawnS2CPacket packet, CallbackInfo ci) {
		Entity entity = this.world.getEntityById(packet.getId());
		if (entity instanceof LivingEntity) {
			MinecraftScriptEvents.ON_MOB_SPAWN.run(entity);
			return;
		}
		if (entity != null) {
			MinecraftScriptEvents.ON_ENTITY_SPAWN.run(entity);
		}
	}

	@Inject(
		method = "method_37472",
		at = @At(
			value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;removeEntity(ILnet/minecraft/entity/Entity$RemovalReason;)V",
			shift = At.Shift.BEFORE
		)
	)
	private void onEntityRemoved(int entityId, CallbackInfo ci) {
		Entity entity = this.world.getEntityById(entityId);
		MinecraftScriptEvents.ON_ENTITY_REMOVED.run(entity);
	}

	@Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true) // Checkstyle Ignore
	private void onChatMessage(String message, CallbackInfo ci) {
		if (ClientScriptIO.INSTANCE.submitInput(message) || MinecraftScriptEvents.ON_SEND_MESSAGE.run(message)) {
			ci.cancel();
		}
	}

	@Inject(method = "sendChatCommand", at = @At("HEAD"), cancellable = true)
	private void onCommand(String command, CallbackInfo ci) {
		if (ClientScriptIO.INSTANCE.submitInput("/" + command) || MinecraftScriptEvents.ON_SEND_MESSAGE.run("/" + command)) {
			ci.cancel();
		}
	}

	@Inject(method = "sendCommand", at = @At("HEAD"), cancellable = true)
	private void onCommand(String command, CallbackInfoReturnable<Boolean> cir) {
		if (MinecraftScriptEvents.ON_SEND_MESSAGE.run("/" + command)) {
			cir.setReturnValue(true);
		}
	}
}
