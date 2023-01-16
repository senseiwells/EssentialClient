package me.senseiwells.essentialclient.utils.mapping;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.ChunkPos;

public class EntityHelper {
	public static float getEntityYaw(Entity entity) {
		//#if MC >= 11700
		return entity.getYaw();
		//#else
		//$$return entity.yaw;
		//#endif
	}

	public static float getEntityPitch(Entity entity) {
		//#if MC >= 11700
		return entity.getPitch();
		//#else
		//$$return entity.pitch;
		//#endif
	}

	public static int getEntityChunkX(Entity entity) {
		//#if MC >= 11700
		return entity.getChunkPos().x;
		//#else
		//$$return entity.chunkX;
		//#endif
	}

	public static int getEntityChunkZ(Entity entity) {
		//#if MC >= 11700
		return entity.getChunkPos().z;
		//#else
		//$$return entity.chunkZ;
		//#endif
	}

	public static ChunkPos getEntityChunkPos(Entity entity) {
		//#if MC >= 11700
		return entity.getChunkPos();
		//#else
		//$$return new ChunkPos(entity.chunkX, entity.chunkZ);
		//#endif
	}

	public static void setEntityYaw(Entity entity, float yaw) {
		//#if MC >= 11700
		entity.setYaw(yaw);
		//#else
		//$$entity.yaw = yaw;
		//#endif
	}

	public static void setEntityPitch(Entity entity, float pitch) {
		//#if MC >= 11700
		entity.setPitch(pitch);
		//#else
		//$$entity.pitch = pitch;
		//#endif
	}
}
