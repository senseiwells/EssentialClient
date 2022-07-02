package me.senseiwells.essentialclient.mixins.longChatMessages;

import me.senseiwells.essentialclient.utils.EssentialUtils;
import net.minecraft.util.StringHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(StringHelper.class)
public class StringHelperMixin {
	@ModifyConstant(method = "truncateChat", constant = @Constant(intValue = 256))
	private static int getMaxLength(int original) {
		return EssentialUtils.getMaxChatLength(original);
	}
}
