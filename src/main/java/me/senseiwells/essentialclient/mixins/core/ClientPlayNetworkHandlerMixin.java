package me.senseiwells.essentialclient.mixins.core;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import me.senseiwells.essentialclient.EssentialClient;
import me.senseiwells.essentialclient.clientscript.core.ClientScript;
import me.senseiwells.essentialclient.clientscript.events.MinecraftScriptEvents;
import me.senseiwells.essentialclient.commands.CommandRegister;
import me.senseiwells.essentialclient.feature.CarpetClient;
import me.senseiwells.essentialclient.rule.ClientRules;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import me.senseiwells.essentialclient.utils.command.CommandHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommonNetworkHandler;
import net.minecraft.client.network.ClientConnectionState;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.s2c.play.CommandTreeS2CPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.server.command.ServerCommandSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ClientPlayNetworkHandler.class, priority = 900)
public abstract class ClientPlayNetworkHandlerMixin extends ClientCommonNetworkHandler {
	@Shadow
	private CommandDispatcher<CommandSource> commandDispatcher;

	@Final
	@Shadow
	private DynamicRegistryManager.Immutable combinedDynamicRegistries;

	@Final
	@Shadow
	private FeatureSet enabledFeatures;

	protected ClientPlayNetworkHandlerMixin(MinecraftClient client, ClientConnection connection, ClientConnectionState connectionState) {
		super(client, connection, connectionState);
	}

	@Shadow
	public abstract boolean sendCommand(String command);

	@SuppressWarnings("unchecked")
	@Inject(method = "onCommandTree", at = @At("TAIL"))
	public void onOnCommandTree(CommandTreeS2CPacket packet, CallbackInfo ci) {
		CommandRegistryAccess access = CommandRegistryAccess.of(this.combinedDynamicRegistries, this.enabledFeatures);
		CommandHelper.setCommandPacket(packet, access);
		CommandRegister.registerCommands((CommandDispatcher<ServerCommandSource>) (Object) this.commandDispatcher, access);
	}

	@Inject(method = "onGameJoin", at = @At("TAIL"))
	private void onGameJoin(GameJoinS2CPacket packet, CallbackInfo ci) {
		if (ClientRules.START_SELECTED_SCRIPTS_ON_JOIN.getValue()) {
			ClientScript.INSTANCE.startAllInstances();
		}
		MinecraftScriptEvents.ON_CONNECT.run(EssentialUtils.getPlayer(), EssentialUtils.getWorld());
		if (this.client.getServer() != null && !EssentialUtils.isModInstalled("essentialaddons")) {
			EssentialClient.GAME_RULE_NET_HANDLER.onHelloSinglePlayer();
			EssentialClient.GAME_RULE_NET_HANDLER.processRawData(this.client.getServer().getGameRules().toNbt());
		}
	}

	@Inject(method = "sendChatCommand", at = @At("HEAD"), cancellable = true)
	private void onCommand(String command, CallbackInfo ci) {
		if (this.onCommand(command)) {
			ci.cancel();
		}
	}

	@Inject(method = "sendCommand", at = @At("HEAD"), cancellable = true)
	private void onCommand(String command, CallbackInfoReturnable<Boolean> cir) {
		if (this.onCommand(command)) {
			cir.setReturnValue(true);
		}
	}

	@Unique
	private boolean onCommand(String command) {
		StringReader reader = new StringReader(command);
		int cursor = reader.getCursor();
		String commandName = reader.canRead() ? reader.readUnquotedString() : "";
		if (CarpetClient.INSTANCE.isCarpetManager(commandName) && ClientRules.CARPET_ALWAYS_SET_DEFAULT.getValue()) {
			reader.skip();
			if (reader.canRead()) {
				String subCommand = reader.readUnquotedString();
				if (reader.canRead() && !"setDefault".equals(subCommand)) {
					this.sendCommand("%s setDefault %s%s".formatted(commandName, subCommand, reader.getRemaining()));
				}
			}
		}
		reader.setCursor(cursor);
		if (CommandHelper.isClientCommand(commandName)) {
			CommandHelper.executeCommand(reader, command);
			return true;
		}
		return false;
	}
}
