package me.senseiwells.essential_client.gui

import me.senseiwells.chunkdebug.client.ChunkDebugClient
import me.senseiwells.chunkdebug.client.gui.ChunkDebugScreen
import me.senseiwells.essential_client.EssentialClient
import me.senseiwells.essential_client.EssentialClientConfig
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.StringWidget
import net.minecraft.client.gui.components.Tooltip
import net.minecraft.client.gui.layouts.*
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.CommonComponents
import net.minecraft.network.chat.Component

class EssentialClientScreen(
    private val parent: Screen? = null
): Screen(Component.translatable("essential-client.menu")) {
    private val layout = HeaderAndFooterLayout(this, 32, 64)

    override fun init() {
        val minecraft = this.minecraft!!

        val width = 204
        val grid = GridLayout()
        grid.defaultCellSetting().padding(4, 4, 4, 0)
        val rows = grid.createRowHelper(1)
        rows.addChild(Button.builder(Component.translatable("essential-client.menu.config")) {
            minecraft.setScreen(EssentialClientConfig.screen(this))
        }.width(width).build())
        rows.addChild(Button.builder(Component.translatable("essential-client.menu.carpetConfig")) {
            minecraft.setScreen(EssentialClient.getCarpetClient().createConfig(minecraft).generateScreen(this))
        }.width(width).tooltip(Tooltip.create(Component.translatable("essential-client.menu.carpetConfig.tooltip"))).build())
        val chunks = rows.addChild(Button.builder(Component.translatable("essential-client.menu.chunkDebugMap")) {
            minecraft.setScreen(createChunkDebugScreen(this))
        }.width(width).tooltip(Tooltip.create(Component.translatable("essential-client.menu.chunkDebugMap.tooltip"))).build())
        chunks.active = hasChunkDebug && canUseChunkDebug()

        this.layout.addToHeader(StringWidget(this.title, this.font)) { settings -> settings.alignVerticallyBottom() }
        this.layout.addToContents(grid) { settings -> settings.alignVerticallyTop() }
        this.layout.addToFooter(Button.builder(CommonComponents.GUI_DONE) {
            this.onClose()
        }.build())

        this.layout.visitWidgets(this::addRenderableWidget)
        this.repositionElements()
    }

    override fun repositionElements() {
        this.layout.arrangeElements()
    }

    override fun onClose() {
        this.minecraft!!.setScreen(this.parent)
    }

    companion object {
        private val hasChunkDebug = FabricLoader.getInstance().isModLoaded("chunk-debug")

        private fun canUseChunkDebug(): Boolean {
            return this.createChunkDebugScreen() != null
        }

        private fun createChunkDebugScreen(parent: Screen? = null): ChunkDebugScreen? {
            return ChunkDebugClient.getInstance().createChunkDebugScreen(parent)
        }
    }
}