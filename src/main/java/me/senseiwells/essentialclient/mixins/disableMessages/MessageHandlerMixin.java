package me.senseiwells.essentialclient.mixins.disableMessages;

import me.senseiwells.essentialclient.rule.ClientRules;
import me.senseiwells.essentialclient.utils.render.Texts;
//#if MC < 11901
import net.minecraft.client.gui.hud.InGameHud; //1.19
import net.minecraft.network.message.MessageSender; //1.19
import net.minecraft.network.message.MessageType; //1.19
import net.minecraft.util.registry.BuiltinRegistries; //1.19
//#else
//$$import net.minecraft.client.network.message.MessageHandler; //1.19.1
//#endif

import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//#if MC < 11901
@Mixin(InGameHud.class) //1.19
//#else
//$$@Mixin(MessageHandler.class) //1.19.1
//#endif

public class MessageHandlerMixin {
	//#if MC < 11901
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
	//#else
	//$$@Inject(method = "onGameMessage", at = @At("HEAD"), cancellable = true)
	//$$private void onChatMessage(Text message, boolean overlay, CallbackInfo ci) {
	//$$	if (!overlay && ClientRules.DISABLE_OP_MESSAGES.getValue()) {
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
