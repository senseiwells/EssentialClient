package me.senseiwells.essentialclient.mixins.disableBossBar;

import me.senseiwells.essentialclient.rule.ClientRules;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.BossBarHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//#else
//$$import net.minecraft.client.util.math.MatrixStack;
//#endif

@Mixin(BossBarHud.class)
public class BossBarHudMixin {
	@Inject(method = "render", at = @At("HEAD"), cancellable = true)
	private void onRender(
		//#if MC >= 12000
		DrawContext context,
		//#else
		//$$MatrixStack matrices,
		//#endif
		CallbackInfo ci
	) {
		if (ClientRules.DISABLE_BOSS_BAR.getValue()) {
			ci.cancel();
		}
	}
}
