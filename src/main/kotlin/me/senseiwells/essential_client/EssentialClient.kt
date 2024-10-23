package me.senseiwells.essential_client

import me.senseiwells.essential_client.features.AfkTracker
import me.senseiwells.essential_client.features.BetterAccurateBlockPlacement
import me.senseiwells.essential_client.features.DisplayStartTime
import me.senseiwells.essential_client.features.HighlightLiquids
import me.senseiwells.essential_client.features.carpet_client.*
import me.senseiwells.essential_client.features.carpet_client.database.CarpetRulesDatabase
import me.senseiwells.essential_client.gui.EssentialClientScreen
import me.senseiwells.keybinds.api.KeybindListener
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientPacketListener
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import org.jetbrains.annotations.ApiStatus.Internal
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object EssentialClient: ModInitializer {
    const val MOD_ID = "essential-client"

    private var carpet: CarpetClient = EmptyCarpetClient

    val logger: Logger = LoggerFactory.getLogger("EssentialClient")

    override fun onInitialize() {
        EssentialClientConfig.load()
        CarpetRulesDatabase.load()
        SpoofedCarpetClientNetworkHandler.load()

        AfkTracker.load()
        BetterAccurateBlockPlacement.load()
        DisplayStartTime.load()
        HighlightLiquids.load()

        this.loadKeybinds()
        this.loadEvents()
    }

    fun id(path: String): ResourceLocation {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path.removePrefix("${MOD_ID}:"))
    }

    fun getCarpetClient(): CarpetClient {
        return this.carpet
    }

    @Internal
    fun synchronizeCarpetRules(tag: CompoundTag) {
        this.carpet.synchronizeRuleData(Minecraft.getInstance(), tag)
    }

    @Internal
    fun setMultiplayerCarpet(connection: ClientPacketListener) {
        this.carpet = MultiplayerCarpetClient(connection)
    }

    private fun loadKeybinds() {
        EssentialClientConfig.essentialMenuKeybind.addListener(KeybindListener.onPress {
            Minecraft.getInstance().setScreen(EssentialClientScreen())
        })
    }

    private fun loadEvents() {
        ClientPlayConnectionEvents.JOIN.register { _, _, client ->
            if (client.isLocalServer && CarpetClient.hasLocalCarpet) {
                this.carpet = SingleplayerCarpetClient
            }
        }
        ClientPlayConnectionEvents.DISCONNECT.register { _, _ ->
            this.carpet = EmptyCarpetClient
        }
    }
}