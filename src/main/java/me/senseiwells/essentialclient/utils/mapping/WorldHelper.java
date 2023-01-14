package me.senseiwells.essentialclient.utils.mapping;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WorldHelper {
	public static boolean isPositionOutOfWorld(World world, BlockPos pos) {
		//#if MC >= 11700
		return world.isOutOfHeightLimit(pos);
		//#else
		//$$return pos.getY() < 0 || world.getDimensionHeight() < pos.getY();
		//#endif
	}
}
