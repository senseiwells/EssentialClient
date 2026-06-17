package me.senseiwells.essential_client.mixins.disable_screenshot_message;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import me.senseiwells.essential_client.EssentialClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Screenshot.class)
public class ScreenshotMixin {
	@WrapWithCondition(
		method = "lambda$grab$1",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/Minecraft;showDebugChat(Lnet/minecraft/network/chat/Component;)V"
		)
	)
	private static boolean onConsumeFeedback(Minecraft instance, Component message) {
		return !EssentialClientConfig.getInstance().getDisableScreenshotMessages();
	}
}
