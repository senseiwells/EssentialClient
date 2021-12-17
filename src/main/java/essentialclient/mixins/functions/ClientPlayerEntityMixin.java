package essentialclient.mixins.functions;

import essentialclient.clientscript.events.MinecraftScriptEvents;
import essentialclient.clientscript.values.ScreenValue;
import essentialclient.utils.EssentialUtils;
import me.senseiwells.arucas.values.StringValue;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {
	@Inject(method = "sendChatMessage", at = @At("HEAD"))
	public void onChatMessage(String message, CallbackInfo ci) {
		MinecraftScriptEvents.ON_SEND_MESSAGE.run(new StringValue(message));
	}

	@Inject(method = "closeScreen", at = @At("HEAD"))
	private void onCloseScreen(CallbackInfo ci) {
		MinecraftScriptEvents.ON_CLOSE_SCREEN.run(new ScreenValue(EssentialUtils.getClient().currentScreen));
	}
}
