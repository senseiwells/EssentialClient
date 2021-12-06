package essentialclient.mixins.toggleTab;

import essentialclient.config.clientrule.ClientRules;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(InGameHud.class)
public class InGameHudMixin {
	@Shadow
	@Final
	private MinecraftClient client;

	@Unique
	private boolean tabVisible = false;

	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;isPressed()Z"))
	private boolean onIsPressed(KeyBinding keyBinding) {
		return ClientRules.TOGGLE_TAB.getValue() || keyBinding.isPressed();
	}

	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/PlayerListHud;setVisible(Z)V"))
	private void onSetVisible(PlayerListHud playerListHud, boolean visible) {
		if (ClientRules.TOGGLE_TAB.getValue()) {
			KeyBinding tabKey = this.client.options.keyPlayerList;
			if (tabKey.isPressed() && !tabKey.wasPressed()) {
				tabKey.setPressed(false);
				this.tabVisible = !this.tabVisible;
			}
			visible = this.tabVisible;
		}
		else {
			this.tabVisible = visible;
		}
		playerListHud.setVisible(visible);
	}

	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/PlayerListHud;render(Lnet/minecraft/client/util/math/MatrixStack;ILnet/minecraft/scoreboard/Scoreboard;Lnet/minecraft/scoreboard/ScoreboardObjective;)V"))
	private void onRender(PlayerListHud playerListHud, MatrixStack matrices, int i, Scoreboard scoreboard, ScoreboardObjective objective) {
		if (this.tabVisible) {
			playerListHud.render(matrices, i, scoreboard, objective);
		}
	}
}
