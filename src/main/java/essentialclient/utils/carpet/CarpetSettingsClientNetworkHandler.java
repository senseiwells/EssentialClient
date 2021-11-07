package essentialclient.utils.carpet;

import carpet.CarpetServer;
import essentialclient.config.ConfigScreen;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;

public class CarpetSettingsClientNetworkHandler
{
    public static PacketByteBuf data;
    private static MinecraftServer server;
    
    public static void setAllData(PacketByteBuf buf) {
        CarpetSettingsClientNetworkHandler.data = buf;
        split();
    }
    
    public static void attachServer(MinecraftServer server) {
        CarpetSettingsClientNetworkHandler.server = server;
    }
    
    private static void split() {
        NbtCompound compound = data.readNbt();
        if (compound == null) return;
        String carpetServerVersion = compound.getString("CarpetVersion");
    
        NbtList rulesList = compound.getList("rules", 10);
        for (NbtElement tag : rulesList) {
            NbtCompound ruleNBT = (NbtCompound) tag;
            String rule = ruleNBT.getString("rule");
            String value = ruleNBT.getString("value");
            CarpetServer.settingsManager.getRule(rule).set(server.getCommandSource(), value);
        }
        ConfigScreen.setCarpetServerVersion(carpetServerVersion);
        Reference.isCarpetServer = true;
    }
    
    public static void updateRule(PacketByteBuf data) {
        String rule = data.readString();
        String newValue = data.readString();
        CarpetServer.settingsManager.getRule(rule).set(server.getCommandSource(), newValue);
    }
}
