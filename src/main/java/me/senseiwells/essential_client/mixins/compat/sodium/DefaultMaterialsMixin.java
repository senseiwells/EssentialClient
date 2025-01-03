package me.senseiwells.essential_client.mixins.compat.sodium;

import me.senseiwells.essential_client.EssentialClientConfig;
import net.caffeinemc.mods.sodium.client.render.chunk.terrain.material.DefaultMaterials;
import net.caffeinemc.mods.sodium.client.render.chunk.terrain.material.Material;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DefaultMaterials.class)
public class DefaultMaterialsMixin {
	@Inject(
		method = "forFluidState",
		at = @At("HEAD"),
		cancellable = true
	)
	private static void replaceLavaMaterial(FluidState state, CallbackInfoReturnable<Material> cir) {
		EssentialClientConfig config = EssentialClientConfig.getInstance();
		if (state.is(Fluids.LAVA) && config.getHighlightLavaSources()) {
			cir.setReturnValue(DefaultMaterials.TRANSLUCENT);
		} else if (state.is(FluidTags.LAVA) && config.getLavaOpacity() < 1.0F) {
			cir.setReturnValue(DefaultMaterials.TRANSLUCENT);
		}
	}
}
