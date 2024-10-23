package me.senseiwells.essential_client.mixins.disable_join_leave_messages;

import me.senseiwells.essential_client.EssentialClientConfig;
import net.minecraft.client.multiplayer.chat.ChatListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatListener.class)
public class ChatListenerMixin {
	@Inject(
		method = "handleSystemMessage",
		at = @At("HEAD"),
		cancellable = true
	)
	private void onSystemMessage(Component message, boolean isOverlay, CallbackInfo ci) {
		if (!EssentialClientConfig.getInstance().getDisableJoinLeaveMessages()) {
			return;
		}

		if (message.getContents() instanceof TranslatableContents contents) {
			switch (contents.getKey()) {
				case "multiplayer.player.left":
				case "multiplayer.player.joined":
				case "multiplayer.player.joined.renamed":
					ci.cancel();
			}
		}
	}
}
