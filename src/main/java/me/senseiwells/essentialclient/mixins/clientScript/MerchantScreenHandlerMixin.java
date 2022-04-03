package me.senseiwells.essentialclient.mixins.clientScript;

import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.village.Merchant;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MerchantScreenHandler.class)
public interface MerchantScreenHandlerMixin {
	@Accessor("merchant")
	Merchant getMerchant();
}
