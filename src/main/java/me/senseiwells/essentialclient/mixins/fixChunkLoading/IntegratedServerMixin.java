package me.senseiwells.essentialclient.mixins.fixChunkLoading;

import me.senseiwells.essentialclient.rule.ClientRules;
import net.minecraft.server.integrated.IntegratedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(IntegratedServer.class)
public class IntegratedServerMixin {
	@ModifyConstant(method = "tick", constant = @Constant(intValue = -1))
	private int getRenderModifier(int constant) {
		return ClientRules.FIX_CHUNK_LOADING.getValue() ? 0 : constant;
	}
}