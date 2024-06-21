package me.senseiwells.essentialclient.mixins.toggleTab;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import me.senseiwells.essentialclient.rule.ClientRules;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
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
	@Unique
	private boolean tabVisible = false;

	@ModifyExpressionValue(
		method = "renderPlayerList",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/option/KeyBinding;isPressed()Z"
		)
	)
	private boolean isPressed(boolean original) {
		return false;
	}

	@WrapWithCondition(
		method = "renderPlayerList",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/hud/PlayerListHud;setVisible(Z)V",
			ordinal = 0
		)
	)
	private boolean shouldSetVisible(PlayerListHud instance, boolean shouldBeVisible) {
		return false;
	}

	@Inject(
		method = "renderPlayerList",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/scoreboard/Scoreboard;getObjectiveForSlot(Lnet/minecraft/scoreboard/ScoreboardDisplaySlot;)Lnet/minecraft/scoreboard/ScoreboardObjective;",
			shift = At.Shift.BEFORE
		)
	)
	private void onRenderTab(
		DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci
	) {
		if (this.client.world == null || this.client.player == null) {
			return;
		}

		Scoreboard scoreboard = this.client.world.getScoreboard();
		ScoreboardObjective objective = scoreboard.getObjectiveForSlot(ScoreboardDisplaySlot.LIST);
		boolean shouldRender = ClientRules.TOGGLE_TAB.getValue() || this.client.options.playerListKey.isPressed();

		this.setPlayerListHudVisible(shouldRender);
		if (this.tabVisible) {
			this.playerListHud.render(context, context.getScaledWindowWidth(), scoreboard, objective);
		}
	}

	@Unique
	private void setPlayerListHudVisible(boolean visible) {
		if (ClientRules.TOGGLE_TAB.getValue()) {
			KeyBinding tabKey = this.client.options.playerListKey;
			if (tabKey.isPressed() && !tabKey.wasPressed()) {
				tabKey.setPressed(false);
				this.tabVisible = !this.tabVisible;
			}
			visible = this.tabVisible;
		} else {
			this.tabVisible = visible;
		}
		this.playerListHud.setVisible(visible);
	}
}
