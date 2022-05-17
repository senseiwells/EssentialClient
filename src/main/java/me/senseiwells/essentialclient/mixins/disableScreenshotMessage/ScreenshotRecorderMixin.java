package me.senseiwells.essentialclient.mixins.disableScreenshotMessage;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import me.senseiwells.essentialclient.rule.ClientRules;
import net.minecraft.client.util.ScreenshotRecorder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Consumer;

@Mixin(ScreenshotRecorder.class)
public class ScreenshotRecorderMixin {
	@WrapWithCondition(method = "method_1661", at = @At(value = "INVOKE", target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V", remap = false))
	private static boolean onConsumeText(Consumer<?> textConsumer, Object text) {
		return !ClientRules.DISABLE_SCREENSHOT_MESSAGE.getValue();
	}
}
