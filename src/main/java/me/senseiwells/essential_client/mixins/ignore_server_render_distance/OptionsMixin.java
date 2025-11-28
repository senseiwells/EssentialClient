package me.senseiwells.essential_client.mixins.ignore_server_render_distance;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.senseiwells.essential_client.EssentialClientConfig;
import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Options.class)
public class OptionsMixin {
    @Definition(id = "serverRenderDistance", field = "Lnet/minecraft/client/Options;serverRenderDistance:I")
    @Expression("this.serverRenderDistance > 0")
    @ModifyExpressionValue(
        method = "getEffectiveRenderDistance",
        at = @At("MIXINEXTRAS:EXPRESSION")
    )
    private boolean shouldFactorInServerRenderDistance(boolean original) {
        return !EssentialClientConfig.getInstance().getIgnoreServerRenderDistance() && original;
    }
}
