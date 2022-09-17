package me.senseiwells.essentialclient.mixins.core;

import me.senseiwells.essentialclient.gui.config.ConfigScreen;
import me.senseiwells.essentialclient.rule.ClientRules;
import me.senseiwells.essentialclient.utils.render.Texts;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
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
				Texts.CLIENT_MENU,
				b -> this.client.setScreen(new ConfigScreen(this))
			));
		}
	}

	//#if MC >= 11800
	@ModifyArg(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/PressableTextWidget;<init>(IIIILnet/minecraft/text/Text;Lnet/minecraft/client/gui/widget/ButtonWidget$PressAction;Lnet/minecraft/client/font/TextRenderer;)V"), index = 1)
	private int onPressableText(int y) {
		return ClientRules.TITLE_TEXT_TO_TOP.getValue() ? 5 : y;
	}
	//#endif

	@ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/TitleScreen;drawStringWithShadow(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;III)V"), index = 4)
	private int onDrawText(int y) {
		return ClientRules.TITLE_TEXT_TO_TOP.getValue() ? 5 : y;
	}
}
