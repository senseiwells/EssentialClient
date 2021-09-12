package essentialclient.feature.clientmacro;

import essentialclient.utils.interfaces.MinecraftClientInvoker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.item.ItemStack;

public class ClientMacroHelper {
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
                        if (ClientMacro.right != null && ClientMacro.right.holdValue == newMouse.holdValue)
                            return;
                        ClientMacro.right = newMouse;
                        return;
                    case LEFT:
                        if (ClientMacro.left != null && ClientMacro.left.holdValue == newMouse.holdValue)
                            return;
                        ClientMacro.left = newMouse;
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
                        ClientMacro.right = null;
                        break;
                    case LEFT:
                        client.options.keyAttack.setPressed(false);
                        ClientMacro.left = null;
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
        } else if (ClientMacro.sleepTime < playerEntity.world.getTime()) {
            ClientMacro.isPaused = false;
        }
    }

    public static void stopMacro(MinecraftClient client) {
        ClientMacro.isPaused = false;
        ClientMacro.loop = false;
        ClientMacro.reader = null;
        ClientMacro.left = null;
        ClientMacro.right = null;
        ClientMacroConditions.isIfTrue = false;
        ClientMacroConditions.inIf = false;
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

        protected void tick() {
            this.ticks++;
            if (this.ticks < this.holdValue) {
                return;
            }
            this.ticks = 0;
            clickMouse(this.mouseType);
        }

        public static void clickMouse(MouseType type) {
            switch (type) {
                case LEFT:
                    ((MinecraftClientInvoker) MinecraftClient.getInstance()).leftClickMouseAccessor();
                    break;
                case RIGHT:
                    ((MinecraftClientInvoker)MinecraftClient.getInstance()).rightClickMouseAccessor();
                    break;
            }
        }
    }
}
