package me.senseiwells.essentialclient.mixins.core;

import me.senseiwells.essentialclient.gui.config.ConfigScreen;
import me.senseiwells.essentialclient.rule.ClientRules;
import me.senseiwells.essentialclient.utils.render.Texts;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GameMenuScreen.class)
public abstract class GameMenuScreenMixin extends Screen {
	protected GameMenuScreenMixin(Text title) {
		super(title);
	}

	@Redirect(method = "initWidgets", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/GridWidget$Adder;add(Lnet/minecraft/client/gui/widget/Widget;I)Lnet/minecraft/client/gui/widget/Widget;"))
	private Widget onInit(GridWidget.Adder instance, Widget widget, int occupiedColumns) {
		if (this.client != null && ClientRules.ESSENTIAL_CLIENT_BUTTON.getValue()) {
			instance.add(ButtonWidget.builder(Texts.CLIENT_MENU, (b) -> this.client.setScreen(new ConfigScreen(this))).width(204).build(), 2);
		}
		return instance.add(widget, occupiedColumns);
	}
}
