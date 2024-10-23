package me.senseiwells.essential_client.features

import me.senseiwells.essential_client.EssentialClientConfig
import me.senseiwells.essential_client.utils.MathUtils.angles
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.Minecraft
import net.minecraft.core.Direction
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket
import net.minecraft.util.Mth
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.Vec2
import org.jetbrains.annotations.ApiStatus.Internal

object BetterAccurateBlockPlacement {
    private var rotation: Vec2? = null
    private var direction: Direction? = null

    @Internal
    @JvmStatic
    fun getFakeRotation(): Vec2? {
        return this.rotation
    }

    @Internal
    @JvmStatic
    fun getFakeDirection(): Direction? {
        return this.direction
    }

    internal fun load() {
        ClientTickEvents.END_CLIENT_TICK.register(::tick)
    }

    private fun tick(minecraft: Minecraft) {
        if (!EssentialClientConfig.instance.betterAccurateBlockPlacement) {
            this.rotation = null
            this.direction = null
            return
        }

        val player = minecraft.player ?: return
        var facing = Direction.orderedByNearest(player).first()

        var rotation = player.rotationVector
        if (EssentialClientConfig.accurateIntoKeybind.isHeld) {
            val hit = minecraft.hitResult
            if (hit is BlockHitResult) {
                rotation = hit.direction.angles
                facing = hit.direction
            }
        }

        if (EssentialClientConfig.accurateReverseKeybind.isHeld) {
            val (x, y) = if (facing.axis.isHorizontal) 0 to 180 else 180 to 0
            rotation = Vec2(Mth.wrapDegrees(rotation.x + x), Mth.wrapDegrees(rotation.y + y))
            facing = facing.opposite
        }

        if (this.rotation != rotation) {
            this.rotation = rotation
            player.connection.send(ServerboundMovePlayerPacket.Rot(rotation.y, rotation.x, player.onGround()))
        }
        this.direction = facing
    }
}