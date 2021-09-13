package essentialclient.feature.clientmacro;

import essentialclient.utils.EssentialUtils;
import essentialclient.utils.interfaces.MinecraftClientInvoker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.item.ItemStack;

public class ClientMacroHelper {

    private static MouseClick right;
    private static MouseClick left;

    public static void drop(ClientPlayerEntity playerEntity, String action) {
        if (playerEntity.inventory.getStack(playerEntity.inventory.selectedSlot) == ItemStack.EMPTY)
            return;
        boolean dropEntireStack;
        switch (action) {
            case "all":
                dropEntireStack = true;
                break;
            case "one":
                dropEntireStack = false;
                break;
            default:
                return;
        }
        playerEntity.dropSelectedItem(dropEntireStack);
    }

    public static void holdStop(String action, KeyBinding key) {
        switch (action) {
            case "hold":
                key.setPressed(true);
                break;
            case "stop":
                key.setPressed(false);
                break;
        }
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
        switch (actions[1]) {
            case "interval":
                MouseClick newMouse = new MouseClick(getMouseTicks(actions), mouseType);
                switch (mouseType) {
                    case RIGHT:
                        if (right != null && right.holdValue == newMouse.holdValue)
                            return;
                        right = newMouse;
                        return;
                    case LEFT:
                        if (left != null && left.holdValue == newMouse.holdValue)
                            return;
                        left = newMouse;
                }
                break;
            case "hold":
                switch (mouseType) {
                    case RIGHT:
                        client.options.keyUse.setPressed(true);
                        break;
                    case LEFT:
                        client.options.keyAttack.setPressed(true);
                        break;
                }
                break;
            case "stop":
                switch (mouseType) {
                    case RIGHT:
                        client.options.keyUse.setPressed(false);
                        right = null;
                        break;
                    case LEFT:
                        client.options.keyAttack.setPressed(false);
                        left = null;
                        break;
                }
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
            switch (type) {
                case LEFT:
                    ((MinecraftClientInvoker) MinecraftClient.getInstance()).leftClickMouseAccessor();
                    break;
                case RIGHT:
                    ((MinecraftClientInvoker)MinecraftClient.getInstance()).rightClickMouseAccessor();
                    break;
            }
        }

        public static void checkMouse() {
            if (right != null)
                right.tick();
            if (left != null)
                left.tick();
        }
    }
}
