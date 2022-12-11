package me.senseiwells.essentialclient.utils.command;

import net.minecraft.world.World;

//#if MC >= 11903
import net.minecraft.registry.RegistryKey;
//#else
//$$import net.minecraft.util.registry.RegistryKey;
//#endif

public enum WorldEnum {
	OVERWORLD(World.OVERWORLD),
	THE_NETHER(World.NETHER),
	THE_END(World.END);

	private final RegistryKey<World> registryKey;

	WorldEnum(RegistryKey<World> registryKey) {
		this.registryKey = registryKey;
	}

	public static WorldEnum fromRegistryKey(RegistryKey<World> registryKey) {
		for (WorldEnum worldEnum : WorldEnum.values()) {
			if (worldEnum.registryKey.equals(registryKey)) {
				return worldEnum;
			}
		}
		return null;
	}
}
