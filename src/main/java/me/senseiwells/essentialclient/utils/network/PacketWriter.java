package me.senseiwells.essentialclient.utils.network;

import net.minecraft.network.PacketByteBuf;

@FunctionalInterface
public interface PacketWriter {
	void write(PacketByteBuf buf);
}
