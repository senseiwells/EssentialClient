package me.senseiwells.essentialclient.mixins.disableMessages;

import me.senseiwells.essentialclient.rule.ClientRules;
import me.senseiwells.essentialclient.utils.render.Texts;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.network.message.MessageSender;
import net.minecraft.network.message.MessageType;
import net.minecraft.text.Text;
import net.minecraft.util.registry.BuiltinRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {
	@Inject(method = "onChatMessage", at = @At("HEAD"), cancellable = true)
	private void onChatMessage(MessageType type, Text message, MessageSender sender, CallbackInfo ci) {
		if (type == BuiltinRegistries.MESSAGE_TYPE.get(MessageType.SYSTEM) && ClientRules.DISABLE_OP_MESSAGES.getValue()) {
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
