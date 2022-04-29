package me.senseiwells.essentialclient.mixins.core;

import me.senseiwells.essentialclient.gui.config.ConfigScreen;
import me.senseiwells.essentialclient.rule.ClientRules;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {

	protected TitleScreenMixin(Text title) {
		super(title);
	}

	@ModifyConstant(method = "init", constant = @Constant(intValue = 72))
	private int pushLimit(int original) {
		if (ClientRules.ESSENTIAL_CLIENT_BUTTON.getValue()) {
			return 86;
		}
		return original;
	}

	@Inject(method = "initWidgetsNormal", at = @At("TAIL"))
	private void onInit(CallbackInfo ci) {
		if (this.client != null && ClientRules.ESSENTIAL_CLIENT_BUTTON.getValue()) {
			this.addDrawableChild(new ButtonWidget(
				this.width / 2 - 100,
				this.height / 4 + 120,
				200, 20,
				new LiteralText("Essential Client Menu"),
				b -> this.client.setScreen(new ConfigScreen(this))
			));
		}
	}

	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/TitleScreen;drawStringWithShadow(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;III)V"), require = 0)
	private void onDrawText(MatrixStack matrices, TextRenderer textRenderer, String text, int x, int y, int color) {
		if (ClientRules.ESSENTIAL_CLIENT_BUTTON.getValue()) {
			drawStringWithShadow(matrices, textRenderer, text, x, 5, color);
			return;
		}
		drawStringWithShadow(matrices, textRenderer, text, x, y, color);
	}
}
