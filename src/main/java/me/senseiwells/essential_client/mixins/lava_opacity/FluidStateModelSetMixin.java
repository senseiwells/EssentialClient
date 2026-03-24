package me.senseiwells.essential_client.mixins.lava_opacity;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.senseiwells.essential_client.EssentialClientConfig;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.block.FluidStateModelSet;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.util.ARGB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FluidStateModelSet.class)
public class FluidStateModelSetMixin {
    @Definition(id = "Material", type = Material.class)
    @Definition(id = "withDefaultNamespace", method = "Lnet/minecraft/resources/Identifier;withDefaultNamespace(Ljava/lang/String;)Lnet/minecraft/resources/Identifier;")
    @Expression("new Material(withDefaultNamespace('block/lava_still'))")
    @ModifyExpressionValue(
        method = "<clinit>",
        at = @At("MIXINEXTRAS:EXPRESSION")
    )
    private static Material makeStillLavaTranslucent(Material original) {
        return original.withForceTranslucent(true);
    }

    @Definition(id = "Unbaked", type = FluidModel.Unbaked.class)
    @Definition(id = "LAVA_MODEL", field = "Lnet/minecraft/client/renderer/block/FluidStateModelSet;LAVA_MODEL:Lnet/minecraft/client/renderer/block/FluidModel$Unbaked;")
    @Expression("LAVA_MODEL = new Unbaked(?, ?, ?, @(null))")
    @ModifyExpressionValue(
        method = "<clinit>",
        at = @At("MIXINEXTRAS:EXPRESSION")
    )
    private static Object modifyLavaOpacity(Object original) {
        return (BlockTintSource) (_) -> {
            return ARGB.color(EssentialClientConfig.getInstance().getLavaOpacity(), 0xFFFFFF);
        };
    }
}
