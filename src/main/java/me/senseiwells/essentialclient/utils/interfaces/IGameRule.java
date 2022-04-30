package me.senseiwells.essentialclient.utils.interfaces;

import net.minecraft.server.MinecraftServer;

public interface IGameRule {
	void ruleChanged(String ruleName, MinecraftServer server);
}
