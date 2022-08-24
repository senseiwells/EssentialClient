package me.senseiwells.essentialclient.mixins.gameRuleSync;

import me.senseiwells.essentialclient.utils.interfaces.IGameRule;
import me.senseiwells.essentialclient.utils.render.Texts;
import net.minecraft.network.MessageType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.world.GameRules;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(GameRules.Rule.class)
public abstract class RuleMixin<T extends GameRules.Rule<T>> implements IGameRule {
	@Shadow
	@Final
	protected GameRules.Type<T> type;

	@Shadow
	public abstract String serialize();

	@Shadow
	protected abstract void changed(@Nullable MinecraftServer server);

	/**
	 * We can't @Invoke this method because it
	 * leads to some weird recursion, idk.
	 */
	@Override
	public void ruleChanged(String ruleName, MinecraftServer server) {
		Text text = Texts.literal("Set Game Rule %s to %s".formatted(ruleName, this.serialize()));
		server.getPlayerManager().broadcast(text,MessageType.SYSTEM, null);
		this.changed(server);
	}
}
