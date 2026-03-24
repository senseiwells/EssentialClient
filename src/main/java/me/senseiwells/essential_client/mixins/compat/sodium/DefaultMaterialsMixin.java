package me.senseiwells.essential_client.mixins.compat.sodium;

import net.caffeinemc.mods.sodium.client.render.chunk.terrain.material.DefaultMaterials;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(DefaultMaterials.class)
public class DefaultMaterialsMixin {
	// @Inject(
	// 	method = "forFluidState",
	// 	at = @At("HEAD"),
	// 	cancellable = true
	// )
	// private static void replaceLavaMaterial(FluidState state, CallbackInfoReturnable<Material> cir) {
	// 	EssentialClientConfig config = EssentialClientConfig.getInstance();
	// 	if (state.is(Fluids.LAVA) && config.getHighlightLavaSources()) {
	// 		cir.setReturnValue(DefaultMaterials.TRANSLUCENT);
	// 	} else if (state.is(FluidTags.LAVA) && config.getLavaOpacity() < 1.0F) {
	// 		cir.setReturnValue(DefaultMaterials.TRANSLUCENT);
	// 	}
	// }
}
