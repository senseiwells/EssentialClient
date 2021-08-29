package essentialclient.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.LiteralText;

public class EssentialUtils {

    public static ClientPlayerEntity playerEntity = MinecraftClient.getInstance().player;

    public static void sendMessageToActionBar(String message) {
        if (playerEntity == null)
            return;
        playerEntity.sendMessage(new LiteralText(message), true);
    }
    public static void sendMessage(String message) {
        if (playerEntity == null)
            return;
        playerEntity.sendMessage(new LiteralText(message), false);
    }
}
