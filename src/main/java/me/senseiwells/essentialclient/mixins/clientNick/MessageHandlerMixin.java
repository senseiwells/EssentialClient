package me.senseiwells.essentialclient.mixins.clientNick;

import me.senseiwells.essentialclient.rule.ClientRules;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import me.senseiwells.essentialclient.utils.config.ConfigClientNick;
import me.senseiwells.essentialclient.utils.render.Texts;
import net.minecraft.client.network.PlayerListEntry;

//#if MC >= 11903
import net.minecraft.text.TextVisitFactory;
//#else
//$$import net.minecraft.client.font.TextVisitFactory;
//#endif

//#if MC >= 11901
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.network.message.MessageHandler;
import net.minecraft.network.message.SignedMessage;
//#else
//$$import net.minecraft.client.gui.hud.InGameHud;
//$$import net.minecraft.client.gui.ClientChatListener;
//$$import org.spongepowered.asm.mixin.injection.Redirect;
//$$import java.util.UUID;
//#endif

import net.minecraft.network.message.MessageType;
import net.minecraft.text.Text;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

//#if MC >= 11901
@Mixin(MessageHandler.class)
//#else
//$$@Mixin(InGameHud.class)
//#endif
public class MessageHandlerMixin {
	//#if MC >= 11901
	@ModifyExpressionValue(method = "onChatMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/message/MessageType$Parameters;applyChatDecoration(Lnet/minecraft/text/Text;)Lnet/minecraft/text/Text;"))
	private Text modifyChatMessage(Text text, SignedMessage signedMessage, MessageType.Parameters params) {
		if (ClientRules.COMMAND_CLIENT_NICK.getValue()) {
			//#if MC >= 11903
			PlayerListEntry playerListEntry = EssentialUtils.getNetworkHandler().getPlayerListEntry(signedMessage.getSender());
			//#else
			//$$PlayerListEntry playerListEntry = EssentialUtils.getNetworkHandler().getPlayerListEntry(signedMessage.signedHeader().sender());
			//#endif
			if (playerListEntry != null) {
				String newName = ConfigClientNick.INSTANCE.get(playerListEntry.getProfile().getName());
				String message = TextVisitFactory.removeFormattingCodes(text);
				String oldName = StringUtils.substringBetween(message, "<", ">");
				text = newName != null && oldName != null ? Texts.literal(message.replaceAll(oldName, newName)) : text;
			}
		}
		return text;
	}
	//#else
	//$$@Redirect(method = "addChatMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/ClientChatListener;onChatMessage(Lnet/minecraft/network/MessageType;Lnet/minecraft/text/Text;Ljava/util/UUID;)V"))
	//$$private void onChatMessage(ClientChatListener clientChatListener, MessageType messageType, Text text, UUID sender) {
	//$$	if (ClientRules.COMMAND_CLIENT_NICK.getValue()) {
	//$$		PlayerListEntry playerListEntry = EssentialUtils.getNetworkHandler().getPlayerListEntry(sender);
	//$$		if (playerListEntry != null) {
	//$$			String newName = ConfigClientNick.INSTANCE.get(playerListEntry.getProfile().getName());
	//$$			String message = TextVisitFactory.removeFormattingCodes(text);
	//$$			String oldName = StringUtils.substringBetween(message, "<", ">");
	//$$			text = newName != null && oldName != null ? Texts.literal(message.replaceAll(oldName, newName)) : text;
	//$$		}
	//$$	}
	//$$	clientChatListener.onChatMessage(messageType, text, sender);
	//$$}
	//#endif
}
