package me.senseiwells.essential_client.mixins.persistent_chat_history;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import me.senseiwells.essential_client.EssentialClientConfig;
import net.minecraft.client.gui.Hud;
import net.minecraft.client.gui.components.ChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Hud.class)
public class HudMixin {
	@WrapWithCondition(
		method = "onDisconnected",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/components/ChatComponent;clearMessages(Z)V"
		)
	)
	private boolean onClearChat(ChatComponent instance, boolean history) {
		return !EssentialClientConfig.getInstance().getPersistentChatHistory();
	}
}
