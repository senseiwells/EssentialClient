package me.senseiwells.essential_client.mixins.disable_screenshot_message;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import me.senseiwells.essential_client.EssentialClientConfig;
import net.minecraft.client.Screenshot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Consumer;

@Mixin(Screenshot.class)
public class ScreenshotMixin {
	@WrapWithCondition(
		method = "method_1661",
		at = @At(
			value = "INVOKE",
			target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V",
			remap = false
		)
	)
	private static boolean onConsumeFeedback(Consumer<?> instance, Object t) {
		return !EssentialClientConfig.getInstance().getDisableScreenshotMessages();
	}
}
