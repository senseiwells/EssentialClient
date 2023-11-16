package me.senseiwells.essentialclient.mixins.carpet;

import carpet.network.ClientNetworkHandler;
import me.senseiwells.essentialclient.feature.CarpetClient;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = ClientNetworkHandler.class, remap = false)
public class ClientNetworkHandlerMixin {
	@ModifyVariable(method = "lambda$static$1", at = @At("STORE"), ordinal = 1)
	private static NbtCompound onGetRuleNBT(NbtCompound original) {
		CarpetClient.INSTANCE.syncCarpetRule(original);
		return original;
	}
}
