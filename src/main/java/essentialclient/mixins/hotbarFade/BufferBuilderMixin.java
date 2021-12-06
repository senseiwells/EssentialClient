package essentialclient.mixins.hotbarFade;

import net.minecraft.client.render.BufferBuilder;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BufferBuilder.class)
public class BufferBuilderMixin {
    /* TODO
    @ModifyVariable(method = "vertex", at = @At("HEAD"), ordinal = 6)
    private float onVertex(float original) {
        return RenderHelper.isRenderingHotBarGui ? 0.4F : original;
    }

    @ModifyVariable(method = "color", at = @At("HEAD"), ordinal = 3)
    private int onAlpha(int original) {
        return RenderHelper.isRenderingHotBarGui ? 102 : original;
    }
     */
}
