package essentialclient.network;

import essentialclient.utils.carpet.CarpetSettingsClientNetworkHandler;
import essentialclient.utils.carpet.Reference;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.util.Identifier;

public class ClientMessageHandler {
    public static void receivedPacket(Identifier channel, PacketByteBuf data) {
        if (Reference.CARPET_CHANNEL_NAME.equals(channel) && data != null) {
            handleData(data);
        }
    }
    
    private static void handleData(PacketByteBuf buffer) {
        int id = buffer.readVarInt();
    
        if (id == Reference.ALL_GUI_INFO) {
            CarpetSettingsClientNetworkHandler.setAllData(buffer);
        }
        if (id == Reference.CHANGE_RULE) {
            CarpetSettingsClientNetworkHandler.updateRule(buffer);
        }
    }
    
    public static void sendPacket(PacketByteBuf data, MinecraftClient client) {
        if (client.getNetworkHandler() == null) {
            return;
        }
        client.getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(Reference.CARPET_CHANNEL_NAME, data));
    }
}
