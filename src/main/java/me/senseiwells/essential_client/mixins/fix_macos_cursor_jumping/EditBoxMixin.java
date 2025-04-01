package me.senseiwells.essential_client.mixins.fix_macos_cursor_jumping;

import com.mojang.blaze3d.platform.InputConstants;
import me.senseiwells.essential_client.EssentialClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EditBox.class)
public abstract class EditBoxMixin extends AbstractWidget {
    public EditBoxMixin(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
    }

    @Shadow public abstract void insertText(String textToWrite);

    @Shadow public abstract void deleteWords(int num);

    @Shadow public abstract void deleteChars(int num);

    @Shadow public abstract void moveCursorToEnd(boolean select);

    @Shadow public abstract void moveCursorTo(int delta, boolean select);

    @Shadow public abstract int getWordPosition(int numWords);

    @Shadow public abstract void moveCursor(int delta, boolean select);

    @Shadow public abstract void moveCursorToStart(boolean select);

    @Inject(
        method = "deleteText",
        at = @At("HEAD"),
        cancellable = true
    )
    private void onDeleteText(int count, CallbackInfo ci) {
        if (Minecraft.ON_OSX && EssentialClientConfig.getInstance().getFixMacOSCursorJumping()) {
            if (Screen.hasControlDown()) {
                this.insertText("");
            } else if (Screen.hasAltDown()) {
                this.deleteWords(count);
            } else {
                this.deleteChars(count);
            }

            ci.cancel();
        }
    }

    @Inject(
        method = "keyPressed",
        at = @At("HEAD"),
        cancellable = true
    )
    private void onKeyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (this.isActive() && this.isFocused()) {
            if (Minecraft.ON_OSX && EssentialClientConfig.getInstance().getFixMacOSCursorJumping()) {
                switch (keyCode) {
                    case InputConstants.KEY_RIGHT -> {
                        if (Screen.hasControlDown()) {
                            this.moveCursorToEnd(Screen.hasShiftDown());
                        } else if (Screen.hasAltDown()) {
                            this.moveCursorTo(this.getWordPosition(1), Screen.hasShiftDown());
                        } else {
                            this.moveCursor(1, Screen.hasShiftDown());
                        }
                        cir.setReturnValue(true);
                    }
                    case InputConstants.KEY_LEFT ->  {
                        if (Screen.hasControlDown()) {
                            this.moveCursorToStart(Screen.hasShiftDown());
                        } else if (Screen.hasAltDown()) {
                            this.moveCursorTo(this.getWordPosition(-1), Screen.hasShiftDown());
                        } else {
                            this.moveCursor(-1, Screen.hasShiftDown());
                        }
                        cir.setReturnValue(true);
                    }
                }
            }
        }
    }
}
