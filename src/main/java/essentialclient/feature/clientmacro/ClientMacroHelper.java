package essentialclient.feature.clientmacro;

import essentialclient.utils.EssentialUtils;
import essentialclient.utils.interfaces.MinecraftClientInvoker;
import essentialclient.utils.inventory.InventoryUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import java.lang.reflect.Array;

public class ClientMacroHelper {

    private static MouseClick right;
    private static MouseClick left;

    public static void drop(ClientPlayerEntity playerEntity, String action) {
        if (playerEntity.inventory.getStack(playerEntity.inventory.selectedSlot) == ItemStack.EMPTY)
            return;
        playerEntity.dropSelectedItem(action.equals("all"));
    }

    public static void holdStop(String action, KeyBinding key) {
        key.setPressed(action.equals("hold"));
    }

    public static void screenHandler(MinecraftClient client, ClientPlayerEntity playerEntity, String action) {
        switch (action) {
            case "open":
                client.openScreen(new InventoryScreen(playerEntity));
                break;
            case "close":
                playerEntity.closeHandledScreen();
                break;
        }
    }

    private static int getMouseTicks(String[] array) {
        try {
            return Integer.parseInt(array[2]);
        }
        catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
            return 4;
        }
    }

    public static void mouseClickAction(MinecraftClient client, String[] actions, MouseType mouseType) {
        KeyBinding key = mouseType == MouseType.RIGHT ? client.options.keyUse : client.options.keyAttack;
        switch (actions[1]) {
            case "interval":
                MouseClick newMouse = new MouseClick(getMouseTicks(actions), mouseType);
                if (mouseType == MouseType.RIGHT) {
                    if (right != null && right.holdValue == newMouse.holdValue)
                        return;
                    right = newMouse;
                }
                else {
                    if (left != null && left.holdValue == newMouse.holdValue)
                        return;
                    left = newMouse;
                }
                break;
            case "hold":
                key.setPressed(true);
                break;
            case "stop":
                if (mouseType == MouseType.RIGHT)
                    right = null;
                else
                    left = null;
                key.setPressed(false);
                break;
            case "once":
                MouseClick.clickMouse(mouseType);
        }
    }

    public static void sleep(ClientPlayerEntity playerEntity, int ticks) {
        if (!ClientMacro.isPaused) {
            ClientMacro.sleepTime = ticks + playerEntity.world.getTime();
            ClientMacro.isPaused = true;
        }
        else if (ClientMacro.sleepTime < playerEntity.world.getTime() + 2) {
            ClientMacro.isPaused = false;
        }
    }

    public static void tryLogout(MinecraftClient client) {
        if (client.isInSingleplayer()) {
            EssentialUtils.sendMessage("§cYou cannot logout in singleplayer!");
            return;
        }
        ClientMacro.enabled = false;
        ClientMacroHelper.stopMacro(client);
        client.disconnect();
    }

    public static void trade(MinecraftClient client, String[] actions) {
        int index = Integer.parseInt(actions[1]);
        boolean shouldDrop = actions.length > 2 && actions[2].equals("drop");
        InventoryUtils.tradeAllItems(client, index, shouldDrop);
    }

    public static void stopMacro(MinecraftClient client) {
        ClientMacro.isPaused = false;
        ClientMacro.loop = false;
        ClientMacro.reader = null;
        left = null;
        right = null;
        ClientMacroCondition.removeAllIf();
        ClientMacroCondition.lastIf = null;
        client.options.keyForward.setPressed(false);
        client.options.keyAttack.setPressed(false);
        client.options.keyUse.setPressed(false);
    }

    enum MouseType {
        RIGHT, LEFT
    }

    public static class MouseClick {

        private int ticks;
        private final int holdValue;
        private final MouseType mouseType;

        private MouseClick(int holdValue, MouseType mouseType) {
            this.holdValue = holdValue;
            this.mouseType = mouseType;
        }

        private void tick() {
            this.ticks++;
            if (this.ticks < this.holdValue) {
                return;
            }
            this.ticks = 0;
            clickMouse(this.mouseType);
        }

        private static void clickMouse(MouseType type) {
            if (type == MouseType.RIGHT)
                ((MinecraftClientInvoker)MinecraftClient.getInstance()).rightClickMouseAccessor();
            else
                ((MinecraftClientInvoker) MinecraftClient.getInstance()).leftClickMouseAccessor();
        }

        public static void checkMouse() {
            if (right != null)
                right.tick();
            if (left != null)
                left.tick();
        }
    }
}
