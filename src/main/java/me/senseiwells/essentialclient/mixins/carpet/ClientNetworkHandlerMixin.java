package me.senseiwells.essentialclient.mixins.carpet;

import carpet.network.ClientNetworkHandler;
import io.netty.buffer.Unpooled;
import me.senseiwells.essentialclient.feature.CarpetClient;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ClientNetworkHandler.class, remap = false)
public class ClientNetworkHandlerMixin {
	@SuppressWarnings({"MixinAnnotationTarget", "InvalidInjectorMethodSignature"})
	@ModifyVariable(method = "lambda$static$0", at = @At("STORE"), ordinal = 1)
	private static NbtCompound onGetRuleNBT(NbtCompound original) {
		CarpetClient.INSTANCE.syncCarpetRule(original);
		return original;
	}

	@Inject(method = "clientCommand", at = @At("HEAD"), cancellable = true)
	private static void onClientCommand(String command, CallbackInfo ci) {
		NbtCompound tag = new NbtCompound();
		tag.putString("id", command);
		tag.putString("command", command);
		NbtCompound outer = new NbtCompound();
		outer.put("clientCommand", tag);

		EssentialUtils.getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(
			carpet.network.CarpetClient.CARPET_CHANNEL,
			new PacketByteBuf(Unpooled.buffer()).writeVarInt(carpet.network.CarpetClient.DATA).writeNbt(outer)
		));

		ci.cancel();
	}
}
