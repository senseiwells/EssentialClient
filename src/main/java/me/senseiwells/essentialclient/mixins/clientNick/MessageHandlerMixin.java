package me.senseiwells.essentialclient.mixins.clientNick;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.senseiwells.essentialclient.rule.ClientRules;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import me.senseiwells.essentialclient.utils.config.ConfigClientNick;
import me.senseiwells.essentialclient.utils.render.Texts;
import net.minecraft.client.font.TextVisitFactory;
import net.minecraft.client.network.PlayerListEntry;
//#if MC < 11901
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.network.message.MessageSender;
import net.minecraft.client.gui.ClientChatListener;
//#else
//$$import net.minecraft.client.network.message.MessageHandler;
//$$import net.minecraft.network.message.SignedMessage;
//#endif
import net.minecraft.network.message.MessageType;
import net.minecraft.text.Text;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//#if MC < 11901
@Mixin(InGameHud.class)
//#else
//$$@Mixin(MessageHandler.class)
//#endif
public class MessageHandlerMixin {
	//#if MC < 11901
	@Redirect(method = "onChatMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/ClientChatListener;onChatMessage(Lnet/minecraft/network/message/MessageType;Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSender;)V"))
	private void onChatMessage(ClientChatListener instance, MessageType messageType, Text text, MessageSender messageSender) {
		if (ClientRules.COMMAND_CLIENT_NICK.getValue()) {
			PlayerListEntry playerListEntry = EssentialUtils.getNetworkHandler().getPlayerListEntry(messageSender.uuid());
			if (playerListEntry != null) {
				String newName = ConfigClientNick.INSTANCE.get(playerListEntry.getProfile().getName());
				String message = TextVisitFactory.removeFormattingCodes(text);
				String oldName = StringUtils.substringBetween(message, "<", ">");
				text = newName != null && oldName != null ? Texts.literal(message.replaceAll(oldName, newName)) : text;
			}
		}
		instance.onChatMessage(messageType, text, messageSender);
	}
	//#else
	//$$@ModifyExpressionValue(method = "onChatMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/message/MessageType$Parameters;applyChatDecoration(Lnet/minecraft/text/Text;)Lnet/minecraft/text/Text;"))
	//$$private Text modifyChatMessage() {
	//$$	if (ClientRules.COMMAND_CLIENT_NICK.getValue()) {
	//$$		PlayerListEntry playerListEntry = EssentialUtils.getNetworkHandler().getPlayerListEntry(signedMessage.signedHeader().sender());
	//$$		if (playerListEntry != null) {
	//$$			String newName = ConfigClientNick.INSTANCE.get(playerListEntry.getProfile().getName());
	//$$			String message = TextVisitFactory.removeFormattingCodes(text);
	//$$			String oldName = StringUtils.substringBetween(message, "<", ">");
	//$$			text = newName != null && oldName != null ? Texts.literal(message.replaceAll(oldName, newName)) : text;
	//$$		}
	//$$	}
	//$$	return text;
	//$$}
	//#endif
}
