package essentialclient.mixins.clientNick;

import essentialclient.feature.clientrule.ClientRules;
import essentialclient.utils.EssentialUtils;
import essentialclient.utils.command.ClientNickHelper;
import net.minecraft.client.font.TextVisitFactory;
import net.minecraft.client.gui.ClientChatListener;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.network.MessageType;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.UUID;

@Mixin(InGameHud.class)
public class InGameHudMixin {
	@Redirect(method = "addChatMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/ClientChatListener;onChatMessage(Lnet/minecraft/network/MessageType;Lnet/minecraft/text/Text;Ljava/util/UUID;)V"))
	private void onChatMessage(ClientChatListener clientChatListener, MessageType messageType, Text text, UUID sender) {
		if (ClientRules.COMMAND_CLIENT_NICK.getBoolean()) {
			PlayerListEntry playerListEntry = EssentialUtils.getNetworkHandler().getPlayerListEntry(sender);
			if (playerListEntry != null) {
				String newName = ClientNickHelper.getRename(playerListEntry.getProfile().getName());
				String message = TextVisitFactory.removeFormattingCodes(text);
				String oldName = StringUtils.substringBetween(message, "<", ">");
				text = newName != null && oldName != null ? new LiteralText(message.replaceAll(oldName, newName)) : text;
			}
		}
		clientChatListener.onChatMessage(messageType, text, sender);
	}
}
