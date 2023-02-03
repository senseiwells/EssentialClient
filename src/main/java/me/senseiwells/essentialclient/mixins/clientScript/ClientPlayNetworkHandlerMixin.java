package me.senseiwells.essentialclient.mixins.clientScript;

import me.senseiwells.essentialclient.clientscript.core.ClientScriptIO;
import me.senseiwells.essentialclient.clientscript.events.MinecraftScriptEvents;
import me.senseiwells.essentialclient.utils.clientscript.impl.ScriptBlockState;
import me.senseiwells.essentialclient.utils.clientscript.impl.ScriptItemStack;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;

//#if MC >= 11903
import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.ClientDynamicRegistryType;
import net.minecraft.registry.CombinedDynamicRegistries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.client.network.message.MessageHandler;
//#else
//$$import net.minecraft.util.registry.DynamicRegistryManager;
//$$import net.minecraft.util.registry.Registry;
//$$import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
//$$import java.util.Optional;
//$$import java.util.UUID;
//#endif

//#if MC >= 11901
import net.minecraft.network.message.MessageType;
//#endif

import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.UUID;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {
	@Shadow
	private ClientWorld world;

	@Final
	@Shadow
	private MinecraftClient client;

	//#if MC >= 11903
	@Shadow
	private CombinedDynamicRegistries<ClientDynamicRegistryType> combinedDynamicRegistries;
	//#elseif MC >= 11901
	//$$@Shadow
	//$$private DynamicRegistryManager.Immutable registryManager;
	//#endif

	@Shadow
	@Final
	private Map<UUID, PlayerListEntry> playerListEntries;

	@Inject(method = "onHealthUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/NetworkThreadUtils;forceMainThread(Lnet/minecraft/network/Packet;Lnet/minecraft/network/listener/PacketListener;Lnet/minecraft/util/thread/ThreadExecutor;)V", shift = At.Shift.AFTER))
	private void onHealthUpdate(HealthUpdateS2CPacket packet, CallbackInfo ci) {
		MinecraftScriptEvents.ON_HEALTH_UPDATE.run(packet.getHealth());
	}

	@Inject(method = "onBlockUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/NetworkThreadUtils;forceMainThread(Lnet/minecraft/network/Packet;Lnet/minecraft/network/listener/PacketListener;Lnet/minecraft/util/thread/ThreadExecutor;)V", shift = At.Shift.AFTER))
	private void onBlockUpdate(BlockUpdateS2CPacket packet, CallbackInfo ci) {
		MinecraftScriptEvents.ON_BLOCK_UPDATE.run(new ScriptBlockState(packet.getState(), packet.getPos()));
	}

	@Inject(method = "onEntityStatus", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;showFloatingItem(Lnet/minecraft/item/ItemStack;)V"))
	private void onTotem(CallbackInfo ci) {
		MinecraftScriptEvents.ON_TOTEM.run();
	}

	@Inject(method = "onGameStateChange", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;setGameMode(Lnet/minecraft/world/GameMode;)V"))
	private void onGamemodeChange(GameStateChangeS2CPacket packet, CallbackInfo ci) {
		MinecraftScriptEvents.ON_GAMEMODE_CHANGE.run(GameMode.byId((int) packet.getValue()).getName());
	}

	@Inject(method = "onItemPickupAnimation", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/NetworkThreadUtils;forceMainThread(Lnet/minecraft/network/Packet;Lnet/minecraft/network/listener/PacketListener;Lnet/minecraft/util/thread/ThreadExecutor;)V", shift = At.Shift.AFTER))
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

	//#if MC >= 11903
	@Redirect(method = "onChatMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/message/MessageHandler;onChatMessage(Lnet/minecraft/network/message/SignedMessage;Lcom/mojang/authlib/GameProfile;Lnet/minecraft/network/message/MessageType$Parameters;)V"))
	private void onChatMessage(MessageHandler instance, SignedMessage message, GameProfile sender, MessageType.Parameters params) {
		MessageType messageType = params.type();
		Identifier typeId = this.combinedDynamicRegistries.getCombinedRegistryManager().get(RegistryKeys.MESSAGE_TYPE).getId(messageType);
		String content = message.getContent().getString();
		String type = typeId == null ? "unknown" : typeId.getPath();
		if (!MinecraftScriptEvents.ON_RECEIVE_MESSAGE.run(sender.getId().toString(), content, type)) {
			instance.onChatMessage(message, sender, params);
		}
	}
	//#elseif MC >= 11901
	//$$@SuppressWarnings("OptionalUsedAsFieldOrParameterType") // Not much we can do about that one
	//$$@Inject(method = "onChatMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/message/MessageHandler;onChatMessage(Lnet/minecraft/network/message/SignedMessage;Lnet/minecraft/network/message/MessageType$Parameters;)V", shift = At.Shift.BEFORE), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
	//$$private void onChatMessage(ChatMessageS2CPacket packet, CallbackInfo ci, Optional<MessageType.Parameters> parameters) {
	//$$	MessageType messageType = parameters.orElseThrow().type();
	//$$	Identifier typeId = this.registryManager.get(Registry.MESSAGE_TYPE_KEY).getId(messageType);
	//$$	UUID uuid = packet.message().signedHeader().sender();
	//$$	String content = packet.message().getContent().getString();
	//$$	String type = typeId == null ? "unknown" : typeId.getPath();
	//$$	if (MinecraftScriptEvents.ON_RECEIVE_MESSAGE.run(uuid.toString(), content, type)) {
	//$$		ci.cancel();
	//$$	}
	//$$}
	//#endif

	//#if MC >= 11901
	@Inject(method = "onGameMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/NetworkThreadUtils;forceMainThread(Lnet/minecraft/network/Packet;Lnet/minecraft/network/listener/PacketListener;Lnet/minecraft/util/thread/ThreadExecutor;)V", shift = At.Shift.AFTER), cancellable = true)
	private void onGameMessage(GameMessageS2CPacket packet, CallbackInfo ci) {
		String content = packet.content().getString();
		String type = packet.overlay() ? "overlay" : "system";
		if (MinecraftScriptEvents.ON_RECEIVE_MESSAGE.run(Util.NIL_UUID.toString(), content, type)) {
			ci.cancel();
		}
	}
	//#elseif MC >= 11800
	//$$@Inject(method = "onGameMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;addChatMessage(Lnet/minecraft/network/MessageType;Lnet/minecraft/text/Text;Ljava/util/UUID;)V"), cancellable = true)
	//$$private void onGameMessage(GameMessageS2CPacket packet, CallbackInfo ci) {
	//$$	UUID uuid = packet.getSender();
	//$$	String content = packet.getMessage().getString();
	//$$	String type = packet.getType().name().toLowerCase(); // getLocation
	//$$	if (MinecraftScriptEvents.ON_RECEIVE_MESSAGE.run(uuid.toString(), content, type)) {
	//$$		ci.cancel();
	//$$	}
	//$$}
	//#else
	//$$@Inject(method = "onGameMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;addChatMessage(Lnet/minecraft/network/MessageType;Lnet/minecraft/text/Text;Ljava/util/UUID;)V"), cancellable = true)
	//$$private void onGameMessage(GameMessageS2CPacket packet, CallbackInfo ci) {
	//$$	UUID uuid = packet.getSender();
	//$$	String content = packet.getMessage().getString();
	//$$	String type = packet.getLocation().name().toLowerCase();
	//$$	if (MinecraftScriptEvents.ON_RECEIVE_MESSAGE.run(uuid.toString(), content, type)) {
	//$$		ci.cancel();
	//$$	}
	//$$}
	//#endif

	@Inject(method = "onPlayerList", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/NetworkThreadUtils;forceMainThread(Lnet/minecraft/network/Packet;Lnet/minecraft/network/listener/PacketListener;Lnet/minecraft/util/thread/ThreadExecutor;)V", shift = At.Shift.AFTER))
	private void onPlayerList(PlayerListS2CPacket packet, CallbackInfo info) {
		//#if MC >= 11903
		for (PlayerListS2CPacket.Action action : packet.getActions()) {
			switch (action) {
				case ADD_PLAYER -> packet.getEntries().forEach(entry -> MinecraftScriptEvents.ON_PLAYER_JOIN.run(entry.profile().getName()));
			}
		}
		//#else
		//$$switch (packet.getAction()) {
		//$$	case ADD_PLAYER -> packet.getEntries().forEach(entry -> MinecraftScriptEvents.ON_PLAYER_JOIN.run(entry.getProfile().getName()));
		//$$	case REMOVE_PLAYER -> packet.getEntries().forEach(entry -> MinecraftScriptEvents.ON_PLAYER_LEAVE.run(entry.getProfile().getName()));
		//$$}
		//#endif
	}

	//#if MC >= 11903
	@Inject(method = "onPlayerRemove", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/NetworkThreadUtils;forceMainThread(Lnet/minecraft/network/Packet;Lnet/minecraft/network/listener/PacketListener;Lnet/minecraft/util/thread/ThreadExecutor;)V", shift = At.Shift.AFTER))
	private void onPlayerRemove(PlayerRemoveS2CPacket packet, CallbackInfo ci) {
		for (UUID uuid : packet.profileIds()) {
			MinecraftScriptEvents.ON_PLAYER_LEAVE.run(this.playerListEntries.get(uuid).getProfile().getName());
		}
	}
	//#endif

	@Inject(
		//#if MC >= 11700
		method = "onDeathMessage",
		//#else
		//$$method = "onCombatEvent",
		//#endif
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;getEntityById(I)Lnet/minecraft/entity/Entity;", shift = At.Shift.BEFORE)
	)
	private void onDeath(
		//#if MC >= 11700
		DeathMessageS2CPacket packet,
		//#else
		//$$CombatEventS2CPacket packet,
		//#endif
		CallbackInfo ci
	) {
		//#if MC >= 11700
		Entity entity = this.world.getEntityById(packet.getEntityId());
		//#else
		//$$Entity entity = this.world.getEntityById(packet.entityId);
		//#endif
		if (entity == this.client.player) {
			//#if MC >= 11700
			Entity killer = this.world.getEntityById(packet.getKillerId());
			MinecraftScriptEvents.ON_DEATH.run(killer, packet.getMessage());
			//#else
			//$$Entity killer = this.world.getEntityById(packet.attackerEntityId);
			//$$MinecraftScriptEvents.ON_DEATH.run(killer, packet.deathMessage);
			//#endif
		}
	}

	@Inject(method = "onEntitySpawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;addEntity(ILnet/minecraft/entity/Entity;)V", shift = At.Shift.AFTER))
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

	//#if MC >= 11700
	@Inject(method = "method_37472", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;removeEntity(ILnet/minecraft/entity/Entity$RemovalReason;)V", shift = At.Shift.BEFORE))
	private void onEntityRemoved(int entityId, CallbackInfo ci) {
		Entity entity = this.world.getEntityById(entityId);
		MinecraftScriptEvents.ON_ENTITY_REMOVED.run(entity);
	}
	//#else
	//$$@Redirect(method = "onEntitiesDestroy", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;removeEntity(I)V"))
	//$$private void onEntityRemoved(ClientWorld instance, int i) {
	//$$	Entity entity = instance.getEntityById(i);
	//$$	MinecraftScriptEvents.ON_ENTITY_REMOVED.run(entity);
	//$$	instance.removeEntity(i);
	//$$}
	//#endif


	//#if MC >= 11903
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
	//#endif
}
