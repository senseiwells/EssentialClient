package me.senseiwells.essential_client.features

import me.senseiwells.essential_client.EssentialClient
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener
import net.minecraft.client.model.geom.builders.UVPair
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.resources.model.Material
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.PackType
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.world.inventory.InventoryMenu
import org.jetbrains.annotations.ApiStatus.Internal

object HighlightLiquids: SimpleSynchronousResourceReloadListener {
    private val highlight = Material(InventoryMenu.BLOCK_ATLAS, EssentialClient.id("block/liquid_highlight"))

    private lateinit var sprite: TextureAtlasSprite

    @Internal
    @JvmStatic
    lateinit var spriteUVs: List<UVPair>

    override fun getFabricId(): ResourceLocation {
        return EssentialClient.id("highlight_liquids")
    }

    override fun onResourceManagerReload(manager: ResourceManager) {
        this.sprite = this.highlight.sprite()
        this.spriteUVs = listOf(
            UVPair(this.sprite.u0, this.sprite.v0),
            UVPair(this.sprite.u0, this.sprite.v1),
            UVPair(this.sprite.u1, this.sprite.v1),
            UVPair(this.sprite.u1, this.sprite.v0)
        )
    }

    internal fun load() {
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(this)
    }
}