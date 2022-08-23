package me.senseiwells.essentialclient.mixins.clientNick;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.senseiwells.essentialclient.rule.ClientRules;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import me.senseiwells.essentialclient.utils.config.ConfigClientNick;
import me.senseiwells.essentialclient.utils.render.Texts;
import net.minecraft.client.font.TextVisitFactory;
import net.minecraft.client.gui.ClientChatListener;
import net.minecraft.client.gui.hud.ChatHudListener;
import net.minecraft.client.network.PlayerListEntry;
//#if MC>= 11901
//$$import net.minecraft.client.network.message.MessageHandler;
//#else
//$$import net.minecraft.network.message.MessageSender;
//#endif
import net.minecraft.network.message.MessageSender;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.text.Text;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;

//#if MC>=11901
//$$@Mixin(MessageHandler.class)
//#else
//$$@Mixin(MessageSender.class)
//#endif
@Mixin(ChatHudListener.class)
public class MessageHandlerMixin {
	//#if MC>= 11901
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
	//#else
	@Inject(method = "onChatMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/message/MessageType$DisplayRule;apply(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSender;)Lnet/minecraft/text/Text;"))
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
	//#endif

}
