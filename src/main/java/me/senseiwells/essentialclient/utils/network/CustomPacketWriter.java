package me.senseiwells.essentialclient.utils.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record CustomPacketWriter(
	Identifier id,
	PacketWriter writer
) implements CustomPayload {
	@Override
	public void write(PacketByteBuf buf) {
		this.writer.write(buf);
	}

	@Override
	public Identifier id() {
		return this.id;
	}
}
