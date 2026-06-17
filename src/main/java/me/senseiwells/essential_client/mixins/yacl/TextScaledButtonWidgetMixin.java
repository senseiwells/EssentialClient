package me.senseiwells.essential_client.mixins.yacl;

import dev.isxander.yacl3.gui.TextScaledButtonWidget;
import dev.isxander.yacl3.gui.TooltipButtonWidget;
import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.joml.Matrix3x2f;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(TextScaledButtonWidget.class)
public abstract class TextScaledButtonWidgetMixin extends TooltipButtonWidget {
    @Shadow
    public float textScale;

    public TextScaledButtonWidgetMixin(Screen screen, int x, int y, int width, int height, Component message, Component tooltip, OnPress onPress) {
        super(screen, x, y, width, height, message, tooltip, onPress);
    }

    /**
     * @author Sensei
     * @reason Fix this annoying bug in YACL
     */
    @Overwrite
    protected void extractDefaultLabel(@NonNull ActiveTextCollector output) {
        float cx = this.getX() + this.getWidth() / 2.0f;
        float cy = this.getY() + this.getHeight() / 2.0f;
        var newPose = new Matrix3x2f(output.defaultParameters().pose())
            .translate(cx, cy)
            .scale(this.textScale)
            .translate(-cx, -cy - 1);
        output.defaultParameters(output.defaultParameters().withPose(newPose));
        super.extractDefaultLabel(output);
    }
}
