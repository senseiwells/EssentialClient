package me.senseiwells.essentialclient.mixins.longChatMessages;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import net.minecraft.util.StringHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(StringHelper.class)
public class StringHelperMixin {
	//#if MC >= 11901
	@ModifyExpressionValue(method = "truncateChat", at = @At(value = "CONSTANT", args = "intValue=256"))
	private static int getMaxLength(int original) {
		return EssentialUtils.getMaxChatLength(original);
	}
	//#endif
}
