package me.senseiwells.essential_client.mixins.toggle_tab;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.senseiwells.essential_client.EssentialClient;
import me.senseiwells.essential_client.EssentialClientConfig;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Gui.class)
public class GuiMixin {
	@Shadow @Final private Minecraft minecraft;

	@Unique private boolean isTabToggled = false;

	@ModifyExpressionValue(
		method = "renderTabList",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/KeyMapping;isDown()Z"
		)
	)
	private boolean isTabToggled(boolean original) {
		if (!EssentialClientConfig.getInstance().getToggleTab()) {
			return original;
		}
		KeyMapping mapping = this.minecraft.options.keyPlayerList;
		while (mapping.consumeClick()) {
			this.isTabToggled = !this.isTabToggled;
		}
		return this.isTabToggled;
	}
}
