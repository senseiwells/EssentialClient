package me.senseiwells.essentialclient.utils.command;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.server.command.ServerCommandSource;

public class FakeCommandSource extends ServerCommandSource {
	public FakeCommandSource(ClientPlayerEntity playerEntity) {
		super(playerEntity, playerEntity.getPos(), playerEntity.getRotationClient(), null, 0, playerEntity.getNameForScoreboard(), playerEntity.getName(), null, playerEntity);
	}
}
