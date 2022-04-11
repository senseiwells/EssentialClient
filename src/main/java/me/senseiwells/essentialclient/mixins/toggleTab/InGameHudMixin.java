package me.senseiwells.essentialclient.mixins.toggleTab;

import com.mojang.blaze3d.systems.RenderSystem;
import me.senseiwells.essentialclient.clientrule.ClientRules;
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
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {
	@Shadow
	@Final
	private MinecraftClient client;
	@Shadow
	@Final
	private PlayerListHud playerListHud;
	@Shadow
	private int scaledWidth;
	@Unique
	private boolean tabVisible = false;

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/scoreboard/Scoreboard;getObjectiveForSlot(I)Lnet/minecraft/scoreboard/ScoreboardObjective;", shift = At.Shift.BEFORE), cancellable = true)
	private void onRenderTab(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
		if (this.client.world == null || this.client.player == null) {
			return;
		}

		Scoreboard scoreboard = this.client.world.getScoreboard();
		ScoreboardObjective objective = scoreboard.getObjectiveForSlot(0);
		boolean shouldRender = ClientRules.TOGGLE_TAB.getValue() || this.client.options.keyPlayerList.isPressed();

		this.setPlayerListHudVisible(shouldRender);
		if (this.tabVisible) {
			this.playerListHud.render(matrices, this.scaledWidth, scoreboard, objective);
		}

		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		ci.cancel();
	}

	@Unique
	private void setPlayerListHudVisible(boolean visible) {
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
		this.playerListHud.setVisible(visible);
	}
}
