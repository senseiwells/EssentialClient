package me.senseiwells.essentialclient.mixins.gameRuleSync;

import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GameRules.Rule.class)
public interface RuleInvoker {
	@Invoker("deserialize")
	void deserialize(String value);
}
