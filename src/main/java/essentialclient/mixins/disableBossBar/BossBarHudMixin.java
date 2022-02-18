package essentialclient.mixins.disableBossBar;

import essentialclient.clientrule.ClientRules;
import net.minecraft.client.gui.hud.BossBarHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BossBarHud.class)
public class BossBarHudMixin {
	@Inject(method = "render", at = @At("HEAD"), cancellable = true)
	private void onRender(MatrixStack matrices, CallbackInfo ci) {
		if (ClientRules.DISABLE_BOSS_BAR.getValue()) {
			ci.cancel();
		}
	}
}
