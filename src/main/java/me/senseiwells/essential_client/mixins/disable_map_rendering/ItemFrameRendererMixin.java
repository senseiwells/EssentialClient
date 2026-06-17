package me.senseiwells.essential_client.mixins.disable_map_rendering;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import me.senseiwells.essential_client.EssentialClientConfig;
import net.minecraft.client.renderer.MapRenderer;
import net.minecraft.client.renderer.entity.ItemFrameRenderer;
import net.minecraft.client.renderer.state.MapRenderState;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemFrameRenderer.class)
public class ItemFrameRendererMixin {
	@WrapWithCondition(
		method = "extractRenderState(Lnet/minecraft/world/entity/decoration/ItemFrame;Lnet/minecraft/client/renderer/entity/state/ItemFrameRenderState;F)V",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/renderer/MapRenderer;extractRenderState(Lnet/minecraft/world/level/saveddata/maps/MapId;Lnet/minecraft/world/level/saveddata/maps/MapItemSavedData;Lnet/minecraft/client/renderer/state/MapRenderState;)V"
		)
	)
	private boolean onRenderMap(MapRenderer instance, MapId mapId, MapItemSavedData mapData, MapRenderState mapRenderState) {
		return !EssentialClientConfig.getInstance().getDisableMapRendering();
	}
}
