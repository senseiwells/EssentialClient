package me.senseiwells.essential_client.utils

import net.minecraft.core.Direction
import net.minecraft.world.phys.Vec2

object MathUtils {
    val Direction.angles: Vec2
        get() = when (this) {
            Direction.UP -> Vec2(0.0F, -90.0F)
            Direction.DOWN -> Vec2(0.0F, 90.0F)
            Direction.NORTH -> Vec2(180.0F, 0.0F)
            Direction.EAST -> Vec2(-90.0F, 0.0F)
            Direction.SOUTH -> Vec2(0.0F, 0.0F)
            Direction.WEST -> Vec2(90.0F, 0.0F)
        }
}