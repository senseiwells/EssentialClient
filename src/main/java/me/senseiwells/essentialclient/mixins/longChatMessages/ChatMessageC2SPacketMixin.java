package me.senseiwells.essentialclient.mixins.longChatMessages;

import me.senseiwells.essentialclient.utils.EssentialUtils;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ChatMessageC2SPacket.class)
public class ChatMessageC2SPacketMixin {
	@ModifyConstant(method = "<init>(Ljava/lang/String;)V", constant = @Constant(intValue = 256))
	private int onMaxLength(int constant) {
		return EssentialUtils.getMaxChatLength(constant);
	}
}
