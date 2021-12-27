package essentialclient.mixins.chunkDebug;

import essentialclient.feature.chunkdebug.ChunkDebugScreen;
import essentialclient.feature.chunkdebug.ChunkGrid;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.Window;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
	@Shadow
	@Final
	private Window window;

	@Shadow
	@Nullable
	public Screen currentScreen;

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;render(FJZ)V", shift = At.Shift.AFTER))
	private void onPostRenderGame(boolean tick, CallbackInfo ci) {
		if (ChunkDebugScreen.chunkGrid != null &&  (this.currentScreen == null || this.currentScreen instanceof ChatScreen)) {
			ChunkDebugScreen.chunkGrid.renderMinimap(this.window.getScaledWidth(), this.window.getScaledHeight());
		}
	}
}
