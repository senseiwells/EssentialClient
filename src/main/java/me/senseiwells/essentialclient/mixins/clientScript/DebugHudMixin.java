package me.senseiwells.essentialclient.mixins.clientScript;

import me.senseiwells.essentialclient.clientscript.core.ClientScript;
import me.senseiwells.essentialclient.clientscript.core.ClientScriptInstance;
import net.minecraft.client.gui.hud.DebugHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(DebugHud.class)
public class DebugHudMixin {
	@Inject(method = "getLeftText", at = @At("RETURN"))
	private void onGetDebug(CallbackInfoReturnable<List<String>> cir) {
		List<String> debugInfo = cir.getReturnValue();
		debugInfo.add("[EssentialClient] Scripts Running: %d".formatted(
			ClientScript.INSTANCE.getScriptInstances().stream().filter(ClientScriptInstance::isScriptRunning).count())
		);
	}
}
