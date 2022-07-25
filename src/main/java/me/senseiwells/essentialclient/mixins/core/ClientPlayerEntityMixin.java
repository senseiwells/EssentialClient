package me.senseiwells.essentialclient.mixins.core;

import com.mojang.brigadier.StringReader;
import me.senseiwells.essentialclient.feature.CarpetClient;
import me.senseiwells.essentialclient.rule.ClientRules;
import me.senseiwells.essentialclient.utils.command.CommandHelper;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin {
	@Shadow
	public abstract void sendCommand(String command);

	@Inject(method = "sendCommand(Ljava/lang/String;Lnet/minecraft/text/Text;)V", at = @At("HEAD"), cancellable = true)
	public void onChatMessage(String command, Text preview, CallbackInfo ci) {
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
			ci.cancel();
		}
	}
}
