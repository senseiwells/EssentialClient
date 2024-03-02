package me.senseiwells.essentialclient.mixins.longChatMessages;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({ChatMessageC2SPacket.class, CommandExecutionC2SPacket.class})
public class GenericChatMessageC2SPacketMixin {
	@ModifyExpressionValue(method = "write", at = @At(value = "CONSTANT", args = "intValue=256"))
	private int getMaxLength(int constant) {
		return EssentialUtils.getMaxChatLength(constant);
	}
}
