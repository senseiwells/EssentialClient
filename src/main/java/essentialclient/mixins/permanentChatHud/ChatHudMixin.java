package essentialclient.mixins.permanentChatHud;

import essentialclient.config.clientrule.ClientRules;
import net.minecraft.client.gui.hud.ChatHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatHud.class)
public class ChatHudMixin {
	@Inject(method = "clear", at = @At("HEAD"), cancellable = true)
	private void onClear(boolean clearHistory, CallbackInfo ci) {
		if (ClientRules.PERMANENT_CHAT_HUD.getValue()) {
			ci.cancel();
		}
	}
}
