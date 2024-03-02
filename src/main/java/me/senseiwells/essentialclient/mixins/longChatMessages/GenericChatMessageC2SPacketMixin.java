package me.senseiwells.essentialclient.mixins.longChatMessages;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;

//#if MC >= 11903
import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket;
//#elseif MC >= 11901
//$$import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket;
//$$import net.minecraft.network.packet.c2s.play.RequestChatPreviewC2SPacket;
//#endif

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

//#if MC >= 11903
@Mixin({ChatMessageC2SPacket.class, CommandExecutionC2SPacket.class})
//#elseif MC >= 11901
//$$@Mixin({ChatMessageC2SPacket.class, RequestChatPreviewC2SPacket.class, CommandExecutionC2SPacket.class})
//#else
//$$@Mixin(ChatMessageC2SPacket.class)
//#endif
public class GenericChatMessageC2SPacketMixin {
	//#if MC >= 11901
	@ModifyExpressionValue(method = "write", at = @At(value = "CONSTANT", args = "intValue=256"))
	//#else
	//$$@ModifyExpressionValue(method = "<init>(Ljava/lang/String;)V", at = @At(value = "CONSTANT", args = "intValue=256"))
	//#endif
	private int getMaxLength(int constant) {
		return EssentialUtils.getMaxChatLength(constant);
	}
}
