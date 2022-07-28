package me.senseiwells.essentialclient.mixins.clientNick;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.senseiwells.essentialclient.rule.ClientRules;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import me.senseiwells.essentialclient.utils.config.ConfigClientNick;
import me.senseiwells.essentialclient.utils.render.Texts;
import net.minecraft.client.font.TextVisitFactory;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.network.message.MessageHandler;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.text.Text;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MessageHandler.class)
public class MessageHandlerMixin {
	@ModifyExpressionValue(method = "onChatMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/message/MessageType$Parameters;applyChatDecoration(Lnet/minecraft/text/Text;)Lnet/minecraft/text/Text;"))
	private Text modifyChatMessage(Text text, SignedMessage signedMessage, MessageType.Parameters params) {
		if (ClientRules.COMMAND_CLIENT_NICK.getValue()) {
			PlayerListEntry playerListEntry = EssentialUtils.getNetworkHandler().getPlayerListEntry(signedMessage.signedHeader().sender());
			if (playerListEntry != null) {
				String newName = ConfigClientNick.INSTANCE.get(playerListEntry.getProfile().getName());
				String message = TextVisitFactory.removeFormattingCodes(text);
				String oldName = StringUtils.substringBetween(message, "<", ">");
				text = newName != null && oldName != null ? Texts.literal(message.replaceAll(oldName, newName)) : text;
			}
		}
		return text;
	}
}
