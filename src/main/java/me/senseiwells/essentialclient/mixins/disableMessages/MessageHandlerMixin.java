package me.senseiwells.essentialclient.mixins.disableMessages;

import me.senseiwells.essentialclient.rule.ClientRules;
import me.senseiwells.essentialclient.utils.render.Texts;

//#if MC >= 11901
import net.minecraft.client.network.message.MessageHandler;
//#elseif MC >= 11900
//$$import net.minecraft.network.message.MessageSender; //1.19
//$$import net.minecraft.network.message.MessageType; //1.19
//$$import net.minecraft.util.registry.BuiltinRegistries; //1.19
//#else
//$$import net.minecraft.network.MessageType;
//#endif
//#if MC < 11901
//$$import net.minecraft.client.gui.hud.InGameHud;
//$$import java.util.UUID;
//#endif
import net.minecraft.network.message.MessageType;
import net.minecraft.text.Text;
import net.minecraft.util.registry.BuiltinRegistries;
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
	//#elseif MC >= 11900
	//$$@Inject(method = "onChatMessage", at = @At("HEAD"), cancellable = true)
	//$$private void onChatMessage(MessageType type, Text message, MessageSender sender, CallbackInfo ci) {
	//$$	if (type == BuiltinRegistries.MESSAGE_TYPE.get(MessageType.SYSTEM) && ClientRules.DISABLE_OP_MESSAGES.getValue()) {
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
