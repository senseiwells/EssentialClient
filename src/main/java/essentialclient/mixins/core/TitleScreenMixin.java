package essentialclient.mixins.core;

import essentialclient.gui.ConfigScreen;
import essentialclient.gui.clientrule.ClientRules;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
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
        if (ClientRules.ESSENTIAL_CLIENT_MAIN_MENU.getBoolean())
            return 86;
        return original;
    }

    @Inject(method = "initWidgetsNormal", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        if (this.client == null || !ClientRules.ESSENTIAL_CLIENT_MAIN_MENU.getBoolean())
            return;
        ButtonWidget buttonWidget = this.addButton(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 120, 200, 20, new LiteralText("Essential Client Menu"), (b) -> this.client.openScreen(new ConfigScreen(this))));
        buttonWidget.active = true;
    }
}
