package essentialclient.mixins.betterPingDisplay;

import essentialclient.config.clientrule.ClientRules;
import essentialclient.utils.render.PingColour;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(PlayerListHud.class)
public abstract class PlayerListHudMixin {
	@Shadow
	@Final
	private MinecraftClient client;

	@ModifyConstant(method = "render", constant = @Constant(intValue = 13))
	private int on13(int original) {
		return ClientRules.BETTER_PING_DISPLAY.getValue() ? original + 45 : original;
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/PlayerListHud;renderLatencyIcon(Lnet/minecraft/client/util/math/MatrixStack;IIILnet/minecraft/client/network/PlayerListEntry;)V"), locals = LocalCapture.CAPTURE_FAILHARD)
	private void onRenderLatencyBefore(MatrixStack matrices, int i, Scoreboard scoreboard, ScoreboardObjective objective, CallbackInfo ci, ClientPlayNetworkHandler clientPlayNetworkHandler, List<?> list, int j, int k, int m, int n, int o, boolean bl, int r, int s, int t, int u, int v, List<?> list2, List<?> list3, int x, int y, int z, int aa, int ab, int ac, PlayerListEntry playerListEntry2) {
		if (ClientRules.BETTER_PING_DISPLAY.getValue()) {
			String pingString = "%dms".formatted(playerListEntry2.getLatency());
			int pingStringWidth = this.client.textRenderer.getWidth(pingString);
			int textX = s + ab - pingStringWidth - 13;

			textX -= bl ? 9 : 0;

			int pingTextColor = PingColour.calculate(playerListEntry2.getLatency());

			this.client.textRenderer.drawWithShadow(matrices, pingString, (float) textX, (float) ac, pingTextColor);
		}
	}
}

