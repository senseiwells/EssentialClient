package me.senseiwells.essentialclient.mixins.core;

import me.senseiwells.essentialclient.gui.config.ConfigScreen;
import me.senseiwells.essentialclient.rule.ClientRules;
import me.senseiwells.essentialclient.utils.render.Texts;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

//#if MC >= 11903
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.GridWidget;
//#else
//$$import me.senseiwells.essentialclient.utils.render.WidgetHelper;
//$$import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//#endif

@Mixin(GameMenuScreen.class)
public abstract class GameMenuScreenMixin extends Screen {
	protected GameMenuScreenMixin(Text title) {
		super(title);
	}

	//#if MC >= 11903
	@Redirect(method = "initWidgets", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/GridWidget$Adder;add(Lnet/minecraft/client/gui/widget/ClickableWidget;I)Lnet/minecraft/client/gui/widget/ClickableWidget;"))
	private <T extends ClickableWidget> T onInit(GridWidget.Adder instance, T widget, int occupiedColumns) {
		if (this.client != null && ClientRules.ESSENTIAL_CLIENT_BUTTON.getValue()) {
			instance.add(ButtonWidget.builder(Texts.CLIENT_MENU, (b) -> this.client.setScreen(new ConfigScreen(this))).width(204).build(), 2);
		}
		return instance.add(widget, occupiedColumns);
	}
	//#else
	//$$@ModifyConstant(method = "initWidgets", constant = @Constant(intValue = 120))
	//$$private int pushLimit(int original) {
	//$$	return ClientRules.ESSENTIAL_CLIENT_BUTTON.getValue() ? 144 : original;
	//$$}
	//$$
	//$$@Inject(method = "initWidgets", at = @At("TAIL"))
	//$$private void onInit(CallbackInfo ci) {
	//$$	if (this.client == null || !ClientRules.ESSENTIAL_CLIENT_BUTTON.getValue()) {
	//$$		return;
	//$$	}
	//$$	this.addDrawableChild(WidgetHelper.newButton(this.width / 2 - 102, this.height / 4 + 120 - 16, 204, 20, Texts.CLIENT_MENU, (b) -> this.client.setScreen(new ConfigScreen(this))));
	//$$}
	//#endif
}
