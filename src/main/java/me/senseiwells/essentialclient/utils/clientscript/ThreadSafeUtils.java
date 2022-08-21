package me.senseiwells.essentialclient.utils.clientscript;

import me.senseiwells.essentialclient.mixins.clientScript.ClientWorldAccessor;
import me.senseiwells.essentialclient.utils.interfaces.IEntityList;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.EntityList;
import net.minecraft.world.World;

import java.util.UUID;

/**
 * Utility class that makes some Minecraft
 * operations, that aren't usually Thread
 * safe, Thread safe.
 */
public class ThreadSafeUtils {
	public static Entity[] getEntitiesSafe(ClientWorld world) {
		EntityList entityList = ((ClientWorldAccessor) world).getEntityList();
		return ((IEntityList) entityList).getAllEntities();
	}

	public static PlayerEntity[] getPlayersSafe(World world) {
		return world.getPlayers().toArray(PlayerEntity[]::new);
	}

	public static PlayerEntity getClosestPlayer(World world, Entity entity, double maxDistance) {
		double d = -1.0;
		double x = entity.getX();
		double y = entity.getY();
		double z = entity.getZ();
		PlayerEntity playerEntity = null;

		for (PlayerEntity player : getPlayersSafe(world)) {
			double e = player.squaredDistanceTo(x, y, z);
			if (playerEntity != entity && (maxDistance < 0.0 || e < maxDistance * maxDistance) && (d == -1.0 || e < d)) {
				d = e;
				playerEntity = player;
			}
		}

		return playerEntity;
	}

	public static PlayerEntity getPlayerByUuid(World world, UUID uuid) {
		for (PlayerEntity player : getPlayersSafe(world)) {
			if (uuid.equals(player.getUuid())) {
				return player;
			}
		}
		return null;
	}
}
