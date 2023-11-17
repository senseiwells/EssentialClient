package me.senseiwells.essentialclient.mixins.disableBossBar;

import me.senseiwells.essentialclient.rule.ClientRules;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.BossBarHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BossBarHud.class)
public class BossBarHudMixin {
	@Inject(method = "render", at = @At("HEAD"), cancellable = true)
	private void onRender(
		DrawContext context,
		CallbackInfo ci
	) {
		if (ClientRules.DISABLE_BOSS_BAR.getValue()) {
			ci.cancel();
		}
	}
}
