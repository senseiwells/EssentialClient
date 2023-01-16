package me.senseiwells.essentialclient.mixins.clientScript;

import me.senseiwells.essentialclient.utils.clientscript.FunctionClickEvent;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Style;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Screen.class)
public class ScreenMixin {
	@Inject(method = "handleTextClick", at = @At("HEAD"), cancellable = true)
	private void onTextClick(Style style, CallbackInfoReturnable<Boolean> cir) {
		if (style != null && style.getClickEvent() instanceof FunctionClickEvent functionEvent) {
			functionEvent.executeFunction();
			cir.setReturnValue(true);
		}
	}
}
