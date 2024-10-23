package me.senseiwells.essential_client.features

import me.senseiwells.essential_client.EssentialClientConfig
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.Minecraft
import net.minecraft.world.phys.Vec3

object AfkTracker {
    private const val MINIMUM_TIMEOUT = 200

    private var prevPosition = Vec3.ZERO
    private var prevMouseX = 0.0
    private var prevMouseY = 0.0
    private var afkTicks = 0

    internal fun load() {
        ClientTickEvents.END_CLIENT_TICK.register(::tick)
    }

    private fun tick(minecraft: Minecraft) {
        val player = minecraft.player ?: return
        val timeout = EssentialClientConfig.instance.announceAfk
        if (timeout < MINIMUM_TIMEOUT) {
            return
        }

        val position = player.position()
        val mouseX = minecraft.mouseHandler.xpos()
        val mouseY = minecraft.mouseHandler.ypos()
        if (this.prevPosition == position && this.prevMouseX == mouseX && this.prevMouseY == mouseX) {
            if (++this.afkTicks == timeout) {
                player.connection.sendChat(EssentialClientConfig.instance.announceAfkMessage)
            }
            return
        }

        if (this.afkTicks >= timeout) {
            player.connection.sendChat(EssentialClientConfig.instance.announceBackMessage)
        }

        this.prevPosition = position
        this.prevMouseX = mouseX
        this.prevMouseY = mouseY

        this.afkTicks = 0
    }
}