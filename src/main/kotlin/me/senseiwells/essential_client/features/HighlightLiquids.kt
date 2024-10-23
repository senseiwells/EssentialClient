package me.senseiwells.essential_client.features

import me.senseiwells.essential_client.EssentialClient
import me.senseiwells.essential_client.EssentialClientConfig
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener
import net.minecraft.client.renderer.BiomeColors
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.resources.model.Material
import net.minecraft.client.resources.model.ModelBaker
import net.minecraft.client.resources.model.ModelBakery
import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.PackType
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.world.inventory.InventoryMenu
import net.minecraft.world.level.BlockAndTintGetter
import net.minecraft.world.level.material.FluidState
import net.minecraft.world.level.material.Fluids

object HighlightLiquids: SimpleSynchronousResourceReloadListener {
    private val stillWater = Material(InventoryMenu.BLOCK_ATLAS, EssentialClient.id("block/water_still"))
    private val flowingWater = Material(InventoryMenu.BLOCK_ATLAS, EssentialClient.id("block/water_flow"))
    private val stillLava = Material(InventoryMenu.BLOCK_ATLAS, EssentialClient.id("block/lava_still"))
    private val flowingLava = Material(InventoryMenu.BLOCK_ATLAS, EssentialClient.id("block/lava_flow"))

    @JvmStatic lateinit var waterSprites: Array<TextureAtlasSprite>
        private set
    @JvmStatic lateinit var lavaSprites: Array<TextureAtlasSprite>
        private set

    override fun getFabricId(): ResourceLocation {
        return EssentialClient.id("highlight_liquids")
    }

    override fun onResourceManagerReload(manager: ResourceManager) {
        this.waterSprites = arrayOf(this.stillWater.sprite(), this.flowingWater.sprite(), ModelBakery.WATER_OVERLAY.sprite())
        this.lavaSprites = arrayOf(this.stillLava.sprite(), this.flowingLava.sprite())

        if (EssentialClientConfig.instance.highlightWaterSources) {
            FluidRenderHandlerRegistry.INSTANCE.register(Fluids.LAVA, LavaHighlighter)
        }
        if (EssentialClientConfig.instance.highlightLavaSources) {
            FluidRenderHandlerRegistry.INSTANCE.register(Fluids.WATER, WaterHighlighter)
        }
    }

    internal fun load() {
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(this)
    }

    private object WaterHighlighter: FluidRenderHandler {
        override fun getFluidSprites(
            view: BlockAndTintGetter?,
            pos: BlockPos?,
            state: FluidState
        ): Array<TextureAtlasSprite> {
            return waterSprites
        }

        override fun getFluidColor(view: BlockAndTintGetter?, pos: BlockPos?, state: FluidState?): Int {
            if (view != null && pos != null) {
                return BiomeColors.getAverageWaterColor(view, pos)
            }
            return 0x3F76E4
        }
    }

    private object LavaHighlighter: FluidRenderHandler {
        override fun getFluidSprites(
            view: BlockAndTintGetter?,
            pos: BlockPos?,
            state: FluidState
        ): Array<TextureAtlasSprite> {
            return lavaSprites
        }

    }
}