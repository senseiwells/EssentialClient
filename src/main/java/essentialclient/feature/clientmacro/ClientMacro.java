package essentialclient.feature.clientmacro;

import essentialclient.gui.keybinds.ClientKeybinds;
import essentialclient.utils.EssentialUtils;
import essentialclient.utils.inventory.InventoryUtils;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.ScreenshotUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

public class ClientMacro {

    protected static boolean enabled = false;
    protected static boolean isPaused = false;
    protected static ClientMacroHelper.MouseClick right;
    protected static ClientMacroHelper.MouseClick left;
    protected static long sleepTime;
    protected static String currentLine;
    protected static BufferedReader reader;
    protected static boolean loop = false;

    public static void registerKeyPress() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (ClientKeybinds.CLIENT_MACRO.getKeyBinding().isPressed()) {
                ClientKeybinds.CLIENT_MACRO.getKeyBinding().setPressed(false);
                enabled = !enabled;
                ClientMacroHelper.stopMacro(client);
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
        if (right != null)
            right.tick();
        if (left != null)
            left.tick();
        if (isPaused) {
            action(currentLine, client);
            return;
        }
        try {
            reader = reader == null ? Files.newBufferedReader(macroFile) : reader;
            while (!isPaused && reader != null && (currentLine = reader.readLine()) != null) {
                action(currentLine, client);
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

    private static void action(String fullAction, MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        assert player != null;
        String[] actions = fullAction.toLowerCase(Locale.ROOT).trim().split(" ");
        if (actions[0].equals("}")) {
            ClientMacroConditions.inIf = false;
            ClientMacroConditions.isIfTrue = false;
            return;
        }
        else if (ClientMacroConditions.inIf && !ClientMacroConditions.isIfTrue)
            return;
        try {
            switch (actions[0]) {
                case "attack":
                    ClientMacroHelper.mouseClickAction(client, actions, ClientMacroHelper.MouseType.LEFT);
                    break;
                case "use":
                    ClientMacroHelper.mouseClickAction(client, actions, ClientMacroHelper.MouseType.RIGHT);
                    break;
                case "slot":
                    int slot = Integer.parseInt(actions[1]);
                    if (slot < 0 || slot > 8)
                        return;
                    player.inventory.selectedSlot = slot;
                    break;
                case "say":
                    player.sendChatMessage(fullAction.replace("say ", ""));
                    break;
                case "sleep":
                    ClientMacroHelper.sleep(player, Integer.parseInt(actions[1]));
                    break;
                case "loop":
                    loop = Boolean.parseBoolean(actions[1]);
                    break;
                case "inventory":
                    ClientMacroHelper.screenHandler(client, player, actions[1]);
                    break;
                case "walk":
                    ClientMacroHelper.holdStop(actions[1], client.options.keyForward);
                    break;
                case "jump":
                    if (player.isOnGround())
                        player.jump();
                    break;
                case "sprint":
                    player.setSprinting(Boolean.parseBoolean(actions[1]));
                    break;
                case "sneak":
                    player.setSneaking(Boolean.parseBoolean(actions[1]));
                    break;
                case "look":
                    player.yaw = Float.parseFloat(actions[1]);
                    player.pitch = Float.parseFloat(actions[2]);
                    break;
                case "drop":
                    ClientMacroHelper.drop(player, actions[1]);
                    break;
                case "drop_all":
                    InventoryUtils.dropAllItemType(player, actions[1]);
                    break;
                case "continue":
                    reader = null;
                    ClientMacroConditions.inIf = false;
                    ClientMacroConditions.canElse = false;
                    break;
                case "screenshot":
                    ScreenshotUtils.saveScreenshot(client.runDirectory, client.getWindow().getWidth(), client.getWindow().getHeight(), client.getFramebuffer(), text -> client.execute(() -> client.inGameHud.getChatHud().addMessage(text)));
                    break;
                case "logout":
                    ClientMacroHelper.tryLogout(client);
                    break;
                case "stop":
                    enabled = false;
                    ClientMacroHelper.stopMacro(client);
                    return;
                case "if":
                    ClientMacroConditions.conditional(client, actions, fullAction, 1);
                    break;
                case "else":
                    ClientMacroConditions.elseConditional(client, actions, fullAction);
                    break;
            }
        }
        catch (Exception e) {
            EssentialUtils.sendMessage("§cYou have an invalid macro file!");
            EssentialUtils.sendMessage("§cPlease check that you have the macro configured properly");
            EssentialUtils.sendMessage("§cThe line: \"" + fullAction + "\" most likely caused the issue");
            ClientMacroHelper.stopMacro(client);
            enabled = false;
        }
    }

    private static Path getFile() {
        return FabricLoader.getInstance().getConfigDir().resolve("EssentialClient").resolve("macro.txt");
    }
}
