package me.senseiwells.essential_client.mixins.persistent_chat_history;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import me.senseiwells.essential_client.EssentialClientConfig;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.ChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Gui.class)
public class GuiMixin {
	@WrapWithCondition(
		method = "onDisconnected",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/components/ChatComponent;clearMessages(Z)V"
		)
	)
	private boolean onClearChat(ChatComponent instance, boolean clearSentMsgHistory) {
		return !EssentialClientConfig.getInstance().getPersistentChatHistory();
	}
}
