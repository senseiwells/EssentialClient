package me.senseiwells.essentialclient.utils.clientscript;

import io.netty.buffer.Unpooled;
import me.senseiwells.arucas.utils.Arguments;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.impl.ArucasList;
import me.senseiwells.arucas.values.*;
import me.senseiwells.essentialclient.EssentialClient;
import me.senseiwells.essentialclient.clientscript.events.MinecraftScriptEvents;
import me.senseiwells.essentialclient.clientscript.values.ItemStackValue;
import me.senseiwells.essentialclient.clientscript.values.TextValue;
import me.senseiwells.essentialclient.utils.network.NetworkHandler;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.util.Identifier;

import java.util.Locale;

import static me.senseiwells.essentialclient.utils.network.NetworkUtils.*;

public class ScriptNetworkHandler extends NetworkHandler {
	private static final Identifier SCRIPT_HANDLER = new Identifier("essentialclient", "clientscript");

	@Override
	public Identifier getNetworkChannel() {
		return SCRIPT_HANDLER;
	}

	@Override
	public int getVersion() {
		return 1_0_0;
	}

	@Override
	protected void onHelloSuccess() {
		EssentialClient.LOGGER.info("Server is accepting client packets");
	}

	@Override
	protected void processData(PacketByteBuf packetByteBuf, ClientPlayNetworkHandler networkHandler) {
		PacketParser parser = new PacketParser(packetByteBuf);
		MinecraftScriptEvents.ON_SCRIPT_PACKET.run(c -> ArucasList.arrayListOf(parser.parseToValues(c)));
	}

	public void sendScriptPacket(Arguments arguments) {
		if (this.getNetworkHandler() == null) {
			throw arguments.getError("Server is not accepting client script packets");
		}

		ArgumentParser parser = new ArgumentParser(arguments);
		this.getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(this.getNetworkChannel(), parser.parse()));
	}

	private record PacketParser(PacketByteBuf buf) {
		private ListValue parseToValues(Context context) {
			ArucasList list = new ArucasList();

			while (this.buf.readableBytes() > 0) {
				list.add(context.convertValue(this.readNext()));
			}

			return new ListValue(list);
		}

		private Object readNext() {
			return switch (this.buf.readByte()) {
				case BOOLEAN -> this.buf.readBoolean();
				case BYTE -> this.buf.readByte();
				case SHORT -> this.buf.readShort();
				case INT -> this.buf.readInt();
				case LONG -> this.buf.readLong();
				case FLOAT -> this.buf.readFloat();
				case DOUBLE -> this.buf.readDouble();
				case BYTE_ARRAY -> this.buf.readByteArray();
				case INT_ARRAY -> this.buf.readIntArray();
				case LONG_ARRAY -> this.buf.readLongArray();
				case STRING -> this.buf.readString();
				case TEXT -> this.buf.readText();
				case UUID -> this.buf.readUuid();
				case IDENTIFIER -> this.buf.readIdentifier();
				case ITEM_STACK -> this.buf.readItemStack();
				case NBT -> this.buf.readNbt();
				case POS -> new PosValue(this.buf.readDouble(), this.buf.readDouble(), this.buf.readDouble());
				default -> null;
			};
		}
	}

	private static class ArgumentParser {
		private final Arguments arguments;
		private final PacketByteBuf buf;

		private ArgumentParser(Arguments arguments) {
			this.arguments = arguments;
			this.buf = new PacketByteBuf(Unpooled.buffer());
			this.buf.writeVarInt(16);
		}

		private PacketByteBuf parse() {
			for (Value value : this.arguments.getRemaining()) {
				if (value instanceof BooleanValue booleanValue) {
					this.buf.writeByte(BOOLEAN);
					this.buf.writeBoolean(booleanValue.value);
					continue;
				}
				if (value instanceof NumberValue number) {
					this.buf.writeByte(DOUBLE);
					this.buf.writeDouble(number.value);
					continue;
				}
				if (value instanceof StringValue stringValue) {
					this.buf.writeByte(STRING);
					this.buf.writeString(stringValue.value);
					continue;
				}
				if (value instanceof TextValue textValue) {
					this.buf.writeByte(TEXT);
					this.buf.writeText(textValue.value);
					continue;
				}
				if (value instanceof ItemStackValue itemStack) {
					this.buf.writeByte(ITEM_STACK);
					this.buf.writeItemStack(itemStack.value);
					continue;
				}
				if (value instanceof PosValue posValue) {
					this.buf.writeByte(POS);
					this.buf.writeDouble(posValue.value.x);
					this.buf.writeDouble(posValue.value.y);
					this.buf.writeDouble(posValue.value.z);
					continue;
				}
				if (value instanceof MapValue map) {
					this.buf.writeByte(NBT);
					this.buf.writeNbt(ClientScriptUtils.mapToNbt(this.arguments.getContext(), map.value, 10));
					continue;
				}
				if (value instanceof ListValue list) {
					this.parseList(list);
				}
			}
			return this.buf;
		}

		private void parseList(ListValue listValue) {
			ArucasList list = listValue.value;
			if (list.isEmpty()) {
				this.buf.writeLongArray(new long[0]);
				return;
			}

			int mod = 0;
			if (list.get(0) instanceof StringValue string) {
				mod++;
				switch (string.value.toLowerCase(Locale.ROOT)) {
					case "b" -> {
						byte[] bytes = new byte[list.size() - 1];
						for (; mod < list.size(); mod++) {
							Value value = list.get(mod);
							if (!(value instanceof NumberValue number)) {
								throw this.arguments.getError("Expected numbers in packet list, got: ", value);
							}
							bytes[mod - 1] = number.value.byteValue();
						}
						this.buf.writeByte(BYTE_ARRAY);
						this.buf.writeByteArray(bytes);
						return;
					}
					case "i" -> {
						int[] ints = new int[list.size() - 1];
						for (; mod < list.size(); mod++) {
							Value value = list.get(mod);
							if (!(value instanceof NumberValue number)) {
								throw this.arguments.getError("Expected numbers in packet list, got: ", value);
							}
							ints[mod - 1] = number.value.intValue();
						}
						this.buf.writeByte(INT_ARRAY);
						this.buf.writeIntArray(ints);
						return;
					}
				}
			}

			long[] longs = new long[list.size() - mod];
			for (int i = 0; i < list.size() - mod; i++) {
				Value value = list.get(i);
				if (!(value instanceof NumberValue number)) {
					throw this.arguments.getError("Expected numbers in packet list, got: ", value);
				}
				longs[i - mod] = number.value.longValue();
			}
			this.buf.writeByte(LONG_ARRAY);
			this.buf.writeLongArray(longs);
		}
	}
}
