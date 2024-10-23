package me.senseiwells.essential_client.mixins.carpet_client;

import carpet.network.CarpetClient;
import carpet.network.ClientNetworkHandler;
import me.senseiwells.essential_client.EssentialClient;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ClientNetworkHandler.class, remap = false)
public class ClientNetworkHandlerMixin {
	@Inject(
		method = "lambda$static$1",
		at = @At("HEAD")
	)
	private static void onSynchronizeRules(LocalPlayer player, Tag tag, CallbackInfo ci) {
		EssentialClient.INSTANCE.synchronizeCarpetRules((CompoundTag) tag);
	}

	@Inject(
		method = "onHi",
		at = @At("TAIL")
	)
	private static void onCarpetHi(String version, CallbackInfo ci) {
		EssentialClient.INSTANCE.setMultiplayerCarpet(CarpetClient.getPlayer().connection);
	}
}
