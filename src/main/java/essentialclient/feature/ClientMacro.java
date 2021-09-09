package essentialclient.feature;

import essentialclient.gui.keybinds.ClientKeybinds;
import essentialclient.utils.EssentialUtils;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.item.ItemStack;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ClientMacro {

    private static boolean enabled = false;
    private static boolean isPaused = false;
    private static long sleepTime;
    private static String currentLine;
    private static BufferedReader reader;
    private static boolean loop = false;

    public static void registerKeyPress() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (ClientKeybinds.CLIENT_MACRO.getKeyBinding().isPressed()) {
                ClientKeybinds.CLIENT_MACRO.getKeyBinding().setPressed(false);
                enabled = !enabled;
                stopMacro(client);
                EssentialUtils.sendMessageToActionBar("§6Macro is now " + (enabled ? "§aON" : "§cOFF"));
            }
            if (enabled) {
                executeMacro(client);
            }
        });
    }

    private static void executeMacro(MinecraftClient client) {
        Path macroFile = getFile();
        if (client.player == null || !Files.exists(macroFile))
            return;
        else if (isPaused) {
            getAction(currentLine, client);
            return;
        }
        try {
            reader = reader == null ? Files.newBufferedReader(macroFile) : reader;
            while (!isPaused && (currentLine = reader.readLine()) != null) {
                getAction(currentLine, client);
            }
            if (currentLine == null) {
                isPaused = false;
                reader = null;
                if (!loop) {
                    enabled = false;
                    EssentialUtils.sendMessageToActionBar("§6Macro finished!");
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void getAction(String fullAction, MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        assert player != null;
        String[] actions = fullAction.split(" ");
        try {
            switch (actions[0]) {
                case "ATTACK":
                    holdStopOnce(client, actions[1], client.options.keyAttack);
                    break;
                case "USE":
                    holdStopOnce(client, actions[1], client.options.keyUse);
                    break;
                case "SLOT":
                    int slot = Integer.parseInt(actions[1]);
                    if (slot < 0 || slot > 8)
                        return;
                    player.inventory.selectedSlot = slot;
                    break;
                case "SAY":
                    player.sendChatMessage(fullAction.replace("SAY ", ""));
                    break;
                case "SLEEP":
                    sleep(player, Integer.parseInt(actions[1]));
                    break;
                case "LOOP":
                    loop = Boolean.parseBoolean(actions[1]);
                    /* Not usable atm
                case "INVENTORY":
                    useKey(client, client.options.keyInventory);
                    break;
                     */
                case "WALK":
                    holdStopOnce(client, actions[1], client.options.keyForward);
                    break;
                case "JUMP":
                    if (player.isOnGround())
                        player.jump();
                    break;
                case "SPRINT":
                    player.setSprinting(Boolean.parseBoolean(actions[1]));
                    break;
                case "SNEAK":
                    player.setSneaking(Boolean.parseBoolean(actions[1]));
                    break;
                case "LOOK":
                    player.yaw = Float.parseFloat(actions[1]);
                    player.pitch = Float.parseFloat(actions[2]);
                    break;
                case "DROP":
                    drop(player, actions[1]);
                    break;
            }
        }
        catch (Exception e) {
            EssentialUtils.sendMessage("§cYou have an invalid macro file!");
            EssentialUtils.sendMessage("§cPlease check that you have the macro configured properly");
            EssentialUtils.sendMessage("§cThe line: " + fullAction + " most likely caused the issue");
            stopMacro(client);
            enabled = false;
        }
    }

    private static void drop(ClientPlayerEntity playerEntity, String action) {
        if (playerEntity.inventory.getStack(playerEntity.inventory.selectedSlot) == ItemStack.EMPTY)
            return;
        boolean dropEntireStack;
        switch (action) {
            case "ALL":
                dropEntireStack = true;
                break;
            case "ONE":
                dropEntireStack = false;
                break;
            default:
                return;
        }
        playerEntity.dropSelectedItem(dropEntireStack);
    }

    private static void sleep(ClientPlayerEntity playerEntity, int ticks) {
        if (!isPaused) {
            sleepTime = ticks + playerEntity.world.getTime();
            isPaused = true;
        } else if (sleepTime < playerEntity.world.getTime()) {
            isPaused = false;
        }
    }

    private static void holdStopOnce(MinecraftClient client, String action, KeyBinding key) {
        switch (action) {
            case "HOLD":
                key.setPressed(true);
                break;
            case "STOP":
                key.setPressed(false);
                break;
            case "ONCE":
                useKey(client, key);
                break;
        }
    }

    private static void stopMacro(MinecraftClient client) {
        isPaused = false;
        loop = false;
        reader = null;
        client.options.keyForward.setPressed(false);
        client.options.keyAttack.setPressed(false);
        client.options.keyUse.setPressed(false);
    }

    private static void useKey(MinecraftClient client, KeyBinding key) {
        key.setPressed(true);
        sleep(client.player, 1);
        if (!isPaused)
            key.setPressed(false);
    }

    private static Path getFile() {
        return FabricLoader.getInstance().getConfigDir().resolve("EssentialClient").resolve("macro.txt");
    }
}
