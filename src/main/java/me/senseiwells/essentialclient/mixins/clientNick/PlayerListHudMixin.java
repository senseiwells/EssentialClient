package me.senseiwells.essentialclient.mixins.clientNick;

import me.senseiwells.essentialclient.rule.ClientRules;
import me.senseiwells.essentialclient.utils.config.ConfigClientNick;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerListHud.class)
public abstract class PlayerListHudMixin {
	@Shadow
	protected abstract Text applyGameModeFormatting(PlayerListEntry entry, MutableText name);

	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/PlayerListHud;getPlayerName(Lnet/minecraft/client/network/PlayerListEntry;)Lnet/minecraft/text/Text;"))
	private Text onGetName(PlayerListHud playerListHud, PlayerListEntry entry) {
		if (ClientRules.COMMAND_CLIENT_NICK.getValue()) {
			String playerName = entry.getProfile().getName();
			String newName = ConfigClientNick.INSTANCE.get(playerName);
			if (newName != null) {
				return this.applyGameModeFormatting(entry, Team.decorateName(entry.getScoreboardTeam(), new LiteralText(newName)));
			}
		}
		return entry.getDisplayName() != null ? this.applyGameModeFormatting(entry, entry.getDisplayName().shallowCopy()) : this.applyGameModeFormatting(entry, Team.decorateName(entry.getScoreboardTeam(), new LiteralText(entry.getProfile().getName())));
	}
}
