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
import me.senseiwells.essentialclient.utils.network.NetworkHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;

//#if MC >= 11903
import net.minecraft.client.network.ClientDynamicRegistryType;
import net.minecraft.registry.CombinedDynamicRegistries;
import net.minecraft.resource.featuretoggle.FeatureSet;
//#elseif MC >= 11901
//$$import net.minecraft.util.registry.DynamicRegistryManager;
//#endif

//#if MC >= 11901
import net.minecraft.command.CommandRegistryAccess;
//#endif

import net.minecraft.command.CommandSource;
import net.minecraft.network.packet.s2c.play.CommandTreeS2CPacket;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
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
public abstract class ClientPlayNetworkHandlerMixin {
	@Shadow
	private CommandDispatcher<CommandSource> commandDispatcher;

	@Shadow
	@Final
	private MinecraftClient client;

	//#if MC >= 11903
	@Shadow
	private CombinedDynamicRegistries<ClientDynamicRegistryType> combinedDynamicRegistries;

	@Shadow
	private FeatureSet enabledFeatures;
	//#elseif MC >= 11901
	//$$@Shadow
	//$$private DynamicRegistryManager.Immutable registryManager;
	//#endif

	//#if MC >= 11903
	@Shadow
	public abstract boolean sendCommand(String par1);
	//#endif

	@SuppressWarnings("unchecked")
	@Inject(method = "onCommandTree", at = @At("TAIL"))
	public void onOnCommandTree(CommandTreeS2CPacket packet, CallbackInfo ci) {
		//#if MC >= 11903
		CommandRegistryAccess access = CommandRegistryAccess.of(this.combinedDynamicRegistries.getCombinedRegistryManager(), this.enabledFeatures);
		//#elseif MC >= 11901
		//$$CommandRegistryAccess access = new CommandRegistryAccess(this.registryManager);
		//#endif

		//#if MC >= 11901
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
	@Inject(method = "onCustomPayload", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/packet/s2c/play/CustomPayloadS2CPacket;getChannel()Lnet/minecraft/util/Identifier;"), cancellable = true)
	private void onCustomPayload(CustomPayloadS2CPacket packet, CallbackInfo ci) {
		for (NetworkHandler networkHandler : EssentialClient.NETWORK_HANDLERS) {
			if (networkHandler.getNetworkChannel().equals(packet.getChannel())) {
				networkHandler.handlePacket(packet.getData(), (ClientPlayNetworkHandler) (Object) this);
				ci.cancel();
				break;
			}
		}
	}

	//#if MC >= 11903
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
	//#endif
}
