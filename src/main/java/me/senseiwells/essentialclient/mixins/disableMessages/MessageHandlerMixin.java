package me.senseiwells.essentialclient.mixins.disableMessages;

import me.senseiwells.essentialclient.rule.ClientRules;
import me.senseiwells.essentialclient.utils.render.Texts;

//#if MC >= 11901
import net.minecraft.client.network.message.MessageHandler;
//#else
//$$import net.minecraft.client.gui.hud.InGameHud;
//$$import net.minecraft.network.MessageType;
//$$import java.util.UUID;
//#endif

import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//#if MC >= 11901
@Mixin(MessageHandler.class)
//#else
//$$@Mixin(InGameHud.class)
//#endif
public class MessageHandlerMixin {
	//#if MC >= 11901
	@Inject(method = "onGameMessage", at = @At("HEAD"), cancellable = true)
	private void onChatMessage(Text message, boolean overlay, CallbackInfo ci) {
		if (!overlay && ClientRules.DISABLE_OP_MESSAGES.getValue()) {
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
	//#else
	//$$@Inject(method = "addChatMessage", at = @At("HEAD"), cancellable = true)
	//$$private void onChatMessage(MessageType type, Text message, UUID sender, CallbackInfo ci) {
	//$$	if (type == MessageType.SYSTEM && ClientRules.DISABLE_OP_MESSAGES.getValue()) {
	//$$		ci.cancel();
	//$$		return;
	//$$	}
	//$$	String key = Texts.getTranslatableKey(message);
	//$$	if (key != null && ClientRules.DISABLE_JOIN_LEAVE_MESSAGES.getValue()) {
	//$$		switch (key) {
	//$$			case "multiplayer.player.joined", "multiplayer.player.left", "multiplayer.player.joined.renamed" -> {
	//$$				ci.cancel();
	//$$			}
	//$$		}
	//$$	}
	//$$}
	//#endif
}
