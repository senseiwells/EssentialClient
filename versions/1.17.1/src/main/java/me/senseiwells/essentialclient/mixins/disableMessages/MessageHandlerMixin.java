package me.senseiwells.essentialclient.mixins.disableMessages;

import me.senseiwells.essentialclient.rule.ClientRules;
import me.senseiwells.essentialclient.utils.render.Texts;
import net.minecraft.client.gui.hud.InGameHud; //1.19
import net.minecraft.network.MessageType; //1.19
import net.minecraft.util.registry.BuiltinRegistries; //1.19

import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(InGameHud.class) //1.19

public class MessageHandlerMixin {

	@Inject(method = "addChatMessage", at = @At("HEAD"), cancellable = true)
	private void onChatMessage(MessageType type, Text message, UUID sender, CallbackInfo ci) {
		if (type == MessageType.SYSTEM && ClientRules.DISABLE_OP_MESSAGES.getValue()) {
			ci.cancel();
			return;
		}
		String key = Texts.getTranslatableKey(message);
		if (key != null && ClientRules.DISABLE_JOIN_LEAVE_MESSAGES.getValue()) {
			switch (key) {
				case "multiplayer.player.joined", "multiplayer.player.left", "multiplayer.player.joined.renamed" -> {
					ci.cancel();
				}
			}
		}
	}
}
