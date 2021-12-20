package essentialclient.utils.carpet;

import carpet.CarpetServer;
import carpet.CarpetSettings;
import carpet.helpers.TickSpeed;
import carpet.settings.ParsedRule;
import carpet.utils.Messenger;
import essentialclient.network.ClientMessageHandler;
import io.netty.buffer.Unpooled;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;

public class CarpetSettingsServerNetworkHandler {
    public static void sendGUIInfo(ServerPlayerEntity playerEntity) {
        NbtCompound data = new NbtCompound();

        data.putString("CarpetVersion", CarpetSettings.carpetVersion);
        data.putFloat("Tickrate", TickSpeed.tickrate);
        
        NbtList rulesList = new NbtList();
        for (ParsedRule<?> rule : CarpetServer.settingsManager.getRules()) {
            NbtCompound ruleNBT = new NbtCompound();
            
            ruleNBT.putString("rule" ,rule.name);
            ruleNBT.putString("value", rule.getAsString());
            ruleNBT.putString("default", rule.defaultAsString);
            rulesList.add(ruleNBT);
        }
        data.put("rules", rulesList);
        PacketByteBuf packetBuf = new PacketByteBuf(Unpooled.buffer());
        packetBuf.writeVarInt(Reference.ALL_GUI_INFO);
        packetBuf.writeNbt(data);
        playerEntity.networkHandler.sendPacket(new CustomPayloadS2CPacket(Reference.CARPET_CHANNEL_NAME, packetBuf));
    }
    
    public static void sendRule(ServerPlayerEntity player, PacketByteBuf data) {
        String rule = data.readString();
        String newValue = data.readString();
        if (player.hasPermissionLevel(2)) {
            CarpetServer.settingsManager.getRule(rule).set(player.getCommandSource(), newValue);
            Messenger.m(player.getCommandSource(), "w " + rule + ": " + newValue + ", ", "c [change permanently?]",
                "^w Click to keep the settings in carpet.conf to save across restarts",
                "?/carpet setDefault " + rule + " " + newValue);
        }
        else {
            Messenger.m(player, "r You do not have permissions to change the rules.");
        }
    }
    
    public static void updateCarpetClientRules(String rule, String newValue, ServerPlayerEntity player) {
        PacketByteBuf data = new PacketByteBuf(Unpooled.buffer());

        data.writeVarInt(Reference.CHANGE_RULE);
        data.writeString(rule);
        data.writeString(newValue);
        player.networkHandler.sendPacket(new CustomPayloadS2CPacket(Reference.CARPET_CHANNEL_NAME, data));
    }
    
    public static void ruleChange(String rule, String newValue, MinecraftClient client) {
        PacketByteBuf data = new PacketByteBuf(Unpooled.buffer());
        data.writeVarInt(Reference.RULE_REQUEST);
        data.writeString(rule);
        data.writeString(newValue);
        ClientMessageHandler.sendPacket(data, client);
    }
    
    public static void requestUpdate(MinecraftClient client) {
        PacketByteBuf data = new PacketByteBuf(Unpooled.buffer());
        data.writeVarInt(Reference.ALL_GUI_INFO);
        ClientMessageHandler.sendPacket(data, client);
    }
}
