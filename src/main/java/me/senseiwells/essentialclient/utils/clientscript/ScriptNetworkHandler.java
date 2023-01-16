package me.senseiwells.essentialclient.utils.clientscript;

import io.netty.buffer.Unpooled;
import me.senseiwells.arucas.builtin.*;
import me.senseiwells.arucas.classes.ClassInstance;
import me.senseiwells.arucas.core.Interpreter;
import me.senseiwells.arucas.exceptions.RuntimeError;
import me.senseiwells.arucas.utils.Arguments;
import me.senseiwells.arucas.utils.impl.ArucasList;
import me.senseiwells.essentialclient.EssentialClient;
import me.senseiwells.essentialclient.clientscript.definitions.MaterialDef;
import me.senseiwells.essentialclient.clientscript.definitions.PosDef;
import me.senseiwells.essentialclient.clientscript.definitions.TextDef;
import me.senseiwells.essentialclient.clientscript.events.MinecraftScriptEvents;
import me.senseiwells.essentialclient.utils.clientscript.impl.ScriptPos;
import me.senseiwells.essentialclient.utils.network.NetworkHandler;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import java.util.List;

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
		MinecraftScriptEvents.ON_SCRIPT_PACKET.run(i -> List.of(parser.parseToValues(i)));
	}

	public void sendScriptPacket(Arguments arguments) {
		if (this.getNetworkHandler() == null) {
			throw new RuntimeError("Server is not accepting client script packets");
		}

		ArgumentParser parser = new ArgumentParser(arguments);
		this.getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(this.getNetworkChannel(), parser.parse()));
	}

	private record PacketParser(PacketByteBuf buf) {
		private ClassInstance parseToValues(Interpreter interpreter) {
			ArucasList list = new ArucasList();

			while (this.buf.readableBytes() > 0) {
				list.add(interpreter.convertValue(this.readNext()));
			}

			return interpreter.create(ListDef.class, list);
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
				//#if MC >= 11700
				case LONG_ARRAY -> this.buf.readLongArray();
				//#else
				//$$case LONG_ARRAY -> this.buf.readLongArray(null);
				//#endif
				case STRING -> this.buf.readString();
				case TEXT -> this.buf.readText();
				case UUID -> this.buf.readUuid();
				case IDENTIFIER -> this.buf.readIdentifier();
				case ITEM_STACK -> this.buf.readItemStack();
				case NBT -> this.buf.readNbt();
				case POS -> new Vec3d(this.buf.readDouble(), this.buf.readDouble(), this.buf.readDouble());
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
			while (this.arguments.hasNext()) {
				if (this.arguments.isNext(BooleanDef.class)) {
					this.buf.writeByte(BOOLEAN);
					this.buf.writeBoolean(this.arguments.nextPrimitive(BooleanDef.class));
				} else if (this.arguments.isNext(NumberDef.class)) {
					this.buf.writeByte(DOUBLE);
					this.buf.writeDouble(this.arguments.nextPrimitive(NumberDef.class));
				} else if (this.arguments.isNext(StringDef.class)) {
					this.buf.writeByte(STRING);
					this.buf.writeString(this.arguments.nextPrimitive(StringDef.class));
				} else if (this.arguments.isNext(TextDef.class)) {
					this.buf.writeByte(TEXT);
					this.buf.writeText(this.arguments.nextPrimitive(TextDef.class));
				} else if (this.arguments.isNext(MaterialDef.class)) {
					this.buf.writeByte(ITEM_STACK);
					this.buf.writeItemStack(this.arguments.nextPrimitive(MaterialDef.class).asItemStack());
				} else if (this.arguments.isNext(PosDef.class)) {
					ScriptPos pos = this.arguments.nextPrimitive(PosDef.class);
					this.buf.writeByte(POS);
					this.buf.writeDouble(pos.getX());
					this.buf.writeDouble(pos.getY());
					this.buf.writeDouble(pos.getZ());
				} else if (this.arguments.isNext(MapDef.class)) {
					this.buf.writeByte(NBT);
					this.buf.writeNbt(
						ClientScriptUtils.mapToNbt(this.arguments.getInterpreter(), this.arguments.nextPrimitive(MapDef.class), 10)
					);
				} else if (this.arguments.isNext(ListDef.class)) {
					this.parseList(this.arguments.nextPrimitive(ListDef.class));
				} else {
					throw new RuntimeError("Cannot serialize unknown type: '%s'".formatted(this.arguments.next().getDefinition().getName()));
				}
			}
			return this.buf;
		}

		private void parseList(ArucasList list) {
			if (list.isEmpty()) {
				this.buf.writeLongArray(new long[0]);
				return;
			}

			int mod = 0;
			ClassInstance first = list.get(0);
			String integerType = first.getPrimitive(StringDef.class);
			if (integerType != null) {
				mod++;
				switch (integerType.toLowerCase()) {
					case "b" -> {
						byte[] bytes = new byte[list.size() - 1];
						for (; mod < list.size(); mod++) {
							Double value = list.get(mod).getPrimitive(NumberDef.class);
							if (value == null) {
								throw new RuntimeError("Expected numbers in packet list, got: %s".formatted(value));
							}
							bytes[mod - 1] = value.byteValue();
						}
						this.buf.writeByte(BYTE_ARRAY);
						this.buf.writeByteArray(bytes);
						return;
					}
					case "i" -> {
						int[] ints = new int[list.size() - 1];
						for (; mod < list.size(); mod++) {
							Double value = list.get(mod).getPrimitive(NumberDef.class);
							if (value == null) {
								throw new RuntimeError("Expected numbers in packet list, got: %s".formatted(value));
							}
							ints[mod - 1] = value.intValue();
						}
						this.buf.writeByte(INT_ARRAY);
						this.buf.writeIntArray(ints);
						return;
					}
				}
			}

			long[] longs = new long[list.size() - mod];
			for (int i = 0; i < list.size() - mod; i++) {
				Double value = list.get(i).getPrimitive(NumberDef.class);
				if (value == null) {
					throw new RuntimeError("Expected numbers in packet list, got: %s".formatted(value));
				}
				longs[i - mod] = value.longValue();
			}
			this.buf.writeByte(LONG_ARRAY);
			this.buf.writeLongArray(longs);
		}
	}
}
