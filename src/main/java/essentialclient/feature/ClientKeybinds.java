package essentialclient.feature;

import essentialclient.clientscript.ClientScript;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public enum ClientKeybinds {
    DEBUG_MENU (new KeyBinding("Toggle Debug Menu", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_F3, "Essential Client")),
    CLIENT_SCRIPT(new KeyBinding("Client Script", GLFW.GLFW_KEY_UNKNOWN, "Essential Client")),
    ACCURATE_REVERSE(new KeyBinding("Accurate Reverse", GLFW.GLFW_KEY_UNKNOWN, "Essential Client")),
    ACCURATE_INTO(new KeyBinding("Accurate Into", GLFW.GLFW_KEY_UNKNOWN, "Essential Client")),
    ;

    private final KeyBinding key;

    ClientKeybinds(KeyBinding keyBinding) {
        this.key = keyBinding;
    }

    public int getKeyCode() {
        return Math.abs(KeyBindingHelper.getBoundKeyOf(this.key).getCode());
    }

    public KeyBinding getKeyBinding() {
        return this.key;
    }

    public static void loadKeybinds() {
        for (ClientKeybinds clientKeybinds : ClientKeybinds.values())
            KeyBindingHelper.registerKeyBinding(clientKeybinds.key);
        ClientScript.getInstance().registerKeyPress();
    }
}
