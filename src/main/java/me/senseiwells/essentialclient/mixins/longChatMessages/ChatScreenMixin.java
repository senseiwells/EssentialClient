package me.senseiwells.essentialclient.mixins.longChatMessages;

import me.senseiwells.essentialclient.utils.EssentialUtils;
import net.minecraft.client.gui.screen.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ChatScreen.class)
public class ChatScreenMixin {
	@ModifyConstant(method = "init", constant = @Constant(intValue = 256))
	private int getMaxLength(int constant) {
		return EssentialUtils.getMaxChatLength(constant);
	}
}
