package me.senseiwells.essential_client.mixins.fix_macos_cursor_jumping;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mojang.blaze3d.platform.InputConstants;
import me.senseiwells.essential_client.EssentialClientConfig;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.InputQuirks;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EditBox.class)
public abstract class EditBoxMixin extends AbstractWidget {
    public EditBoxMixin(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
    }

    @Shadow public abstract void deleteWords(int dir);

    @Shadow public abstract void deleteChars(int dir);

    @Shadow public abstract void moveCursorToEnd(boolean hasShiftDown);

    @Shadow public abstract void moveCursorTo(int dir, boolean extendSelection);

    @Shadow public abstract int getWordPosition(int dir);

    @Shadow public abstract void moveCursor(int dir, boolean hasShiftDown);

    @Shadow public abstract void moveCursorToStart(boolean hasShiftDown);

    @Shadow public abstract void setValue(String value);

    @WrapWithCondition(
        method = "keyPressed",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/components/EditBox;deleteText(IZ)V"
        )
    )
    private boolean fixMacOSCursorJumping(EditBox instance, int dir, boolean wholeWord, KeyEvent event) {
        if (InputQuirks.REPLACE_CTRL_KEY_WITH_CMD_KEY && EssentialClientConfig.getInstance().getFixMacOSCursorJumping()) {
            if (event.hasControlDownWithQuirk()) {
                this.setValue("");
            } else if (event.hasAltDown()) {
                this.deleteWords(dir);
            } else {
                this.deleteChars(dir);
            }
            return false;
        }
        return true;
    }

    @Inject(
        method = "keyPressed",
        at = @At("HEAD"),
        cancellable = true
    )
    private void onKeyPressed(KeyEvent event, CallbackInfoReturnable<Boolean> cir) {
        if (this.isActive() && this.isFocused()) {
            if (InputQuirks.REPLACE_CTRL_KEY_WITH_CMD_KEY && EssentialClientConfig.getInstance().getFixMacOSCursorJumping()) {
                switch (event.key()) {
                    case InputConstants.KEY_RIGHT -> {
                        if (event.hasControlDownWithQuirk()) {
                            this.moveCursorToEnd(event.hasShiftDown());
                        } else if (event.hasAltDown()) {
                            this.moveCursorTo(this.getWordPosition(1), event.hasShiftDown());
                        } else {
                            this.moveCursor(1, event.hasShiftDown());
                        }
                        cir.setReturnValue(true);
                    }
                    case InputConstants.KEY_LEFT ->  {
                        if (event.hasControlDownWithQuirk()) {
                            this.moveCursorToStart(event.hasShiftDown());
                        } else if (event.hasAltDown()) {
                            this.moveCursorTo(this.getWordPosition(-1), event.hasShiftDown());
                        } else {
                            this.moveCursor(-1, event.hasShiftDown());
                        }
                        cir.setReturnValue(true);
                    }
                }
            }
        }
    }
}
