package me.senseiwells.essentialclient.mixins.clientScript;

import me.senseiwells.essentialclient.clientscript.events.MinecraftScriptEvents;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MerchantScreen.class)
public class MerchantScreenMixin {
	@Shadow
	private int selectedIndex;

	@Inject(method = "method_19896", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/MerchantScreen;syncRecipeIndex()V"))
	private void onButtonPress(CallbackInfo ci) {
		MinecraftScriptEvents.ON_CLICK_TRADE.run(this.selectedIndex);
	}
}
