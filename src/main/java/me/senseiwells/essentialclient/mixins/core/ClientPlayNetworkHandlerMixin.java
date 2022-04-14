package me.senseiwells.essentialclient.mixins.core;

import com.mojang.brigadier.CommandDispatcher;
import me.senseiwells.essentialclient.EssentialClient;
import me.senseiwells.essentialclient.rule.ClientRules;
import me.senseiwells.essentialclient.clientscript.core.ClientScript;
import me.senseiwells.essentialclient.clientscript.events.MinecraftScriptEvents;
import me.senseiwells.essentialclient.clientscript.values.PlayerValue;
import me.senseiwells.essentialclient.clientscript.values.WorldValue;
import me.senseiwells.essentialclient.commands.CommandRegister;
import me.senseiwells.essentialclient.feature.chunkdebug.ChunkClientNetworkHandler;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import me.senseiwells.essentialclient.utils.command.CommandHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.command.CommandSource;
import net.minecraft.network.MessageType;
import net.minecraft.network.packet.s2c.play.CommandTreeS2CPacket;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(value = ClientPlayNetworkHandler.class, priority = 900)
public class ClientPlayNetworkHandlerMixin {
	@Shadow
	private CommandDispatcher<CommandSource> commandDispatcher;

	@SuppressWarnings("unchecked")
	@Inject(method = "onCommandTree", at = @At("TAIL"))
	public void onOnCommandTree(CommandTreeS2CPacket packet, CallbackInfo ci) {
		CommandHelper.setCommandPacket(packet);
		CommandRegister.registerCommands((CommandDispatcher<ServerCommandSource>) (Object) this.commandDispatcher);
	}

	@Redirect(method = "onGameMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;addChatMessage(Lnet/minecraft/network/MessageType;Lnet/minecraft/text/Text;Ljava/util/UUID;)V"))
	private void checkMessages(InGameHud inGameHud, MessageType type, Text message, UUID sender) {
		if (type == MessageType.SYSTEM && ClientRules.DISABLE_OP_MESSAGES.getValue()) {
			return;
		}
		if (message instanceof TranslatableText && ClientRules.DISABLE_JOIN_LEAVE_MESSAGES.getValue()) {
			switch (((TranslatableText) message).getKey()) {
				case "multiplayer.player.joined", "multiplayer.player.left", "multiplayer.player.joined.renamed" -> {
					return;
				}
			}
		}
		inGameHud.addChatMessage(type, message, sender);
	}

	@Inject(method = "onGameJoin", at = @At("TAIL"))
	private void onGameJoin(GameJoinS2CPacket packet, CallbackInfo ci) {
		if (ClientRules.START_SELECTED_SCRIPTS_ON_JOIN.getValue()) {
			ClientScript.INSTANCE.startAllInstances();
		}
		MinecraftScriptEvents.ON_CONNECT.run(new PlayerValue(EssentialUtils.getPlayer()), new WorldValue(EssentialUtils.getWorld()));
	}

	@Inject(method = "onCustomPayload", at = @At("HEAD"), cancellable = true)
	private void onCustomPayload(CustomPayloadS2CPacket packet, CallbackInfo ci) {
		if (ChunkClientNetworkHandler.ESSENTIAL_CHANNEL.equals(packet.getChannel())) {
			EssentialClient.CHUNK_NET_HANDLER.handlePacket(packet.getData(), (ClientPlayNetworkHandler) (Object) this);
			ci.cancel();
		}
	}
}
