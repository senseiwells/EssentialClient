package me.senseiwells.essentialclient.mixins.clientNick;

import me.senseiwells.essentialclient.rule.ClientRules;
import me.senseiwells.essentialclient.utils.config.ConfigClientNick;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerListHud.class)
public abstract class PlayerListHudMixin {
	@Shadow
	protected abstract Text applyGameModeFormatting(PlayerListEntry entry, MutableText name);

	@Inject(method = "getPlayerName", at = @At("HEAD"))
	private void onGetPlayerName(PlayerListEntry entry, CallbackInfoReturnable<Text> cir) {
		if (ClientRules.COMMAND_CLIENT_NICK.getValue()) {
			String playerName = entry.getProfile().getName();
			String newName = ConfigClientNick.INSTANCE.get(playerName);
			if (newName != null) {
				cir.setReturnValue(this.applyGameModeFormatting(entry, Team.decorateName(entry.getScoreboardTeam(), Text.of(newName))));
			}
		}
	}
}
