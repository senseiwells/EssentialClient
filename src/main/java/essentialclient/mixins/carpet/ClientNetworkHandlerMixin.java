package essentialclient.mixins.carpet;

import carpet.network.ClientNetworkHandler;
import essentialclient.feature.EssentialCarpetClient;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = ClientNetworkHandler.class, remap = false)
public class ClientNetworkHandlerMixin {
	@ModifyVariable(method = "lambda$static$0", at = @At("STORE"), ordinal = 1)
	private static NbtCompound onGetRuleNBT(NbtCompound original) {
		EssentialCarpetClient.synchronizeRules(original);
		return original;
	}
}
