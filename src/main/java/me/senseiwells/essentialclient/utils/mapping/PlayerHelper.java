package me.senseiwells.essentialclient.utils.mapping;

import me.senseiwells.essentialclient.utils.EssentialUtils;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;

// Checkstyle Off
public class PlayerHelper extends EntityHelper {
	public static PlayerInventory getPlayerInventory() {
		return getPlayerInventory(EssentialUtils.getPlayer());
	}

	public static void setPlayerYaw(float yaw) {
		setEntityYaw(EssentialUtils.getPlayer(), yaw);
	}

	public static void setPlayerPitch(float pitch) {
		setEntityPitch(EssentialUtils.getPlayer(), pitch);
	}

	public static PlayerInventory getPlayerInventory(PlayerEntity player) {
		//#if MC >= 11700
		return player.getInventory();
		//#else
		//$$return player.inventory;
		//#endif
	}

	public static PlayerAbilities getPlayerAbilities(PlayerEntity player) {
		//#if MC >= 11700
		return player.getAbilities();
		//#else
		//$$return player.abilities;
		//#endif
	}
}
