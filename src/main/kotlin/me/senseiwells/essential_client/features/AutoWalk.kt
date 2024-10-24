package me.senseiwells.essential_client.features

import me.senseiwells.essential_client.EssentialClientConfig
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.client.Options
import net.minecraft.client.player.ClientInput
import net.minecraft.network.chat.Component
import org.jetbrains.annotations.ApiStatus.Internal

object AutoWalk {
    private var holdUp = false
    private var heldTicks = 0

    @Internal
    @JvmStatic
    fun tick(original: Boolean, options: Options): Boolean {
        var up = original

        val wasHoldingUp = this.holdUp
        if (options.keyUp.isDown) {
            val required = EssentialClientConfig.instance.autoWalk
            this.holdUp = required > 0 && required <= ++this.heldTicks
            if (this.holdUp && !wasHoldingUp) {
                this.onActivated()
            }
        } else {
            this.heldTicks = 0
            if (this.holdUp) {
                up = true
            }
        }

        if (options.keyDown.isDown) {
            this.heldTicks = 0
            this.holdUp = false
        }
        return up
    }

    private fun onActivated() {
        val minecraft = Minecraft.getInstance()
        val player = minecraft.player ?: return
        player.displayClientMessage(
            Component.translatable("essential-client.autoWalk.activated").withStyle(ChatFormatting.GREEN),
            true
        )
    }
}