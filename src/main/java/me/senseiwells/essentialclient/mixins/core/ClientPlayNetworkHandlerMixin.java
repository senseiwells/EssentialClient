package me.senseiwells.essentialclient.mixins.core;

import com.mojang.brigadier.CommandDispatcher;
import me.senseiwells.essentialclient.EssentialClient;
import me.senseiwells.essentialclient.clientscript.core.ClientScript;
import me.senseiwells.essentialclient.clientscript.events.MinecraftScriptEvents;
import me.senseiwells.essentialclient.commands.CommandRegister;
import me.senseiwells.essentialclient.rule.ClientRules;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import me.senseiwells.essentialclient.utils.command.CommandHelper;
import me.senseiwells.essentialclient.utils.network.NetworkHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;

//#if MC >= 11900
import net.minecraft.command.CommandRegistryAccess;
//#endif

import net.minecraft.command.CommandSource;
import net.minecraft.network.packet.s2c.play.CommandTreeS2CPacket;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.registry.DynamicRegistryManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ClientPlayNetworkHandler.class, priority = 900)
public class ClientPlayNetworkHandlerMixin {
	@Shadow
	private CommandDispatcher<CommandSource> commandDispatcher;

	@Shadow
	@Final
	private MinecraftClient client;

	//#if MC >= 11900
	@Shadow
	private DynamicRegistryManager.Immutable registryManager;
	//#endif

	@SuppressWarnings("unchecked")
	@Inject(method = "onCommandTree", at = @At("TAIL"))
	public void onOnCommandTree(CommandTreeS2CPacket packet, CallbackInfo ci) {
		//#if MC >= 11900
		CommandRegistryAccess access = new CommandRegistryAccess(this.registryManager);
		CommandHelper.setCommandPacket(packet, access);
		CommandRegister.registerCommands((CommandDispatcher<ServerCommandSource>) (Object) this.commandDispatcher, access);
		//#else
		//$$CommandHelper.setCommandPacket(packet);
		//$$CommandRegister.registerCommands((CommandDispatcher<ServerCommandSource>) (Object) this.commandDispatcher);
		//#endif
	}

	@Inject(method = "onGameJoin", at = @At("TAIL"))
	private void onGameJoin(GameJoinS2CPacket packet, CallbackInfo ci) {
		if (ClientRules.START_SELECTED_SCRIPTS_ON_JOIN.getValue()) {
			ClientScript.INSTANCE.startAllInstances();
		}
		MinecraftScriptEvents.ON_CONNECT.run(EssentialUtils.getPlayer(), EssentialUtils.getWorld());
		if (this.client.getServer() != null) {
			EssentialClient.GAME_RULE_NET_HANDLER.onHelloSinglePlayer();
			EssentialClient.GAME_RULE_NET_HANDLER.processRawData(this.client.getServer().getGameRules().toNbt());
		}
	}

	@SuppressWarnings("ConstantConditions")
	@Inject(method = "onCustomPayload", at = @At("HEAD"), cancellable = true)
	private void onCustomPayload(CustomPayloadS2CPacket packet, CallbackInfo ci) {
		for (NetworkHandler networkHandler : EssentialClient.NETWORK_HANDLERS) {
			if (networkHandler.getNetworkChannel().equals(packet.getChannel())) {
				networkHandler.handlePacket(packet.getData(), (ClientPlayNetworkHandler) (Object) this);
				ci.cancel();
				break;
			}
		}
	}
}
