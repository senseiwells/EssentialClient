package me.senseiwells.essential_client.features

import me.senseiwells.essential_client.EssentialClient
import net.fabricmc.fabric.api.resource.v1.ResourceLoader
import net.minecraft.client.Minecraft
import net.minecraft.client.model.geom.builders.UVPair
import net.minecraft.client.renderer.Sheets
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.server.packs.PackType
import net.minecraft.server.packs.resources.PreparableReloadListener
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.server.packs.resources.ResourceManagerReloadListener
import org.jetbrains.annotations.ApiStatus.Internal
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor

object HighlightLiquids: ResourceManagerReloadListener {
    private val highlight = Sheets.BLOCKS_MAPPER.apply(EssentialClient.id("liquid_highlight"))

    @Internal
    @JvmStatic
    lateinit var sprite: TextureAtlasSprite

    @Internal
    @JvmStatic
    lateinit var spriteUVs: List<UVPair>

    override fun reload(
        sharedState: PreparableReloadListener.SharedState,
        executor: Executor,
        preparationBarrier: PreparableReloadListener.PreparationBarrier,
        executor2: Executor
    ): CompletableFuture<Void> {

        return super.reload(sharedState, executor, preparationBarrier, executor2)
    }

    override fun onResourceManagerReload(manager: ResourceManager) {
        val minecraft = Minecraft.getInstance()
        this.sprite = minecraft.atlasManager.get(this.highlight)
        this.spriteUVs = listOf(
            UVPair(this.sprite.u0, this.sprite.v0),
            UVPair(this.sprite.u0, this.sprite.v1),
            UVPair(this.sprite.u1, this.sprite.v1),
            UVPair(this.sprite.u1, this.sprite.v0)
        )
    }

    internal fun load() {
        ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloader(
            EssentialClient.id("highlight_liquids"), this
        )
    }
}