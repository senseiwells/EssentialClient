package me.senseiwells.essentialclient.utils.command;

import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;

public record PlayerData(Vec3d pos, Vec2f rotation, WorldEnum world, GameMode gamemode) { }
