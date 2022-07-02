package me.senseiwells.essentialclient.mixins.longChatMessages;

import me.senseiwells.essentialclient.utils.EssentialUtils;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket;
import net.minecraft.network.packet.c2s.play.RequestChatPreviewC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin({ChatMessageC2SPacket.class, RequestChatPreviewC2SPacket.class, CommandExecutionC2SPacket.class})
public class GenericChatMessageC2SPacketMixin {
	@ModifyConstant(method = "write", constant = @Constant(intValue = 256))
	private static int getMaxLength(int constant) {
		return EssentialUtils.getMaxChatLength(constant);
	}
}
