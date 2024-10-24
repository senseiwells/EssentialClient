package me.senseiwells.essential_client.mixins.essential_client_button;

import com.llamalad7.mixinextras.sugar.Local;
import me.senseiwells.essential_client.EssentialClientConfig;
import me.senseiwells.essential_client.gui.EssentialClientScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PauseScreen.class)
public class PauseScreenMixin extends Screen {
	@Shadow @Final private static int BUTTON_WIDTH_FULL;

	protected PauseScreenMixin(Component title) {
		super(title);
	}

	@Inject(
		method = "createPauseMenu",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/layouts/GridLayout$RowHelper;addChild(Lnet/minecraft/client/gui/layouts/LayoutElement;I)Lnet/minecraft/client/gui/layouts/LayoutElement;"
		)
	)
	private void onCreatePauseMenu(CallbackInfo ci, @Local GridLayout.RowHelper rows) {
		if (EssentialClientConfig.getInstance().getEssentialClientButton()) {
			rows.addChild(Button.builder(Component.translatable("essential-client.menu"), button -> {
				if (this.minecraft != null) {
					this.minecraft.setScreen(new EssentialClientScreen(this));
				}
			}).width(BUTTON_WIDTH_FULL).build(), 2);
		}
	}
}
