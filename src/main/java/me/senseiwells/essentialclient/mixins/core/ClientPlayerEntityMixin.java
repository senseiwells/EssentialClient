package me.senseiwells.essentialclient.mixins.core;

import com.mojang.brigadier.StringReader;
import me.senseiwells.essentialclient.clientrule.ClientRules;
import me.senseiwells.essentialclient.utils.command.CommandHelper;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin {
	@Shadow
	public abstract void sendChatMessage(String message);

	@Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
	public void onChatMessage(String message, CallbackInfo ci) {
		if (message.startsWith("/")) {
			StringReader reader = new StringReader(message);
			reader.skip();
			int cursor = reader.getCursor();
			String commandName = reader.canRead() ? reader.readUnquotedString() : "";
			if (commandName.equals("carpet") && ClientRules.CARPET_ALWAYS_SET_DEFAULT.getValue()) {
				reader.skip();
				String subCommand = reader.readUnquotedString();
				if (reader.canRead() && !subCommand.equals("setDefault")) {
					this.sendChatMessage("/carpet setDefault " + subCommand + reader.getRemaining());
				}
			}
			reader.setCursor(cursor);
			if (CommandHelper.isClientCommand(commandName)) {
				CommandHelper.executeCommand(reader, message);
				ci.cancel();
			}
			if (CommandHelper.tryRunFunctionCommand(message)) {
				ci.cancel();
			}
		}
	}
}
