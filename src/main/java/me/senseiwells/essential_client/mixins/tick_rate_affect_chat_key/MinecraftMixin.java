package me.senseiwells.essential_client.mixins.tick_rate_affect_chat_key;

import me.senseiwells.essential_client.EssentialClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.Overlay;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
	@Shadow @Nullable public Screen screen;
	@Shadow @Final public Options options;
	@Shadow private @Nullable Overlay overlay;

	@Shadow protected abstract void openChatScreen(String defaultText);

	@Inject(
		method = "runTick",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/util/profiling/ProfilerFiller;pop()V",
			ordinal = 0
		),
		slice = @Slice(
			from = @At(
				value = "INVOKE",
				target = "Lnet/minecraft/client/Minecraft;tick()V"
			)
		)
	)
	private void onTick(boolean renderLevel, CallbackInfo ci) {
		if (!EssentialClientConfig.getInstance().getTickRateAffectsChatKey()) {
			while (this.options.keyChat.consumeClick()) {
				this.openChatScreen("");
			}

			if (this.screen == null && this.overlay == null && this.options.keyCommand.consumeClick()) {
				this.openChatScreen("/");
			}
		}
	}
}
