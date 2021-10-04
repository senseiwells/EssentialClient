package essentialclient.feature.clientscript;

import essentialclient.gui.clientrule.ClientRules;
import essentialclient.gui.keybinds.ClientKeybinds;
import essentialclient.utils.EssentialUtils;
import me.senseiwells.arucas.core.Run;
import me.senseiwells.arucas.throwables.Error;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ClientScript {

    public static boolean enabled = false;
    private static Thread thread;

    public static void registerKeyPress() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            KeyBinding clientKeybind = ClientKeybinds.CLIENT_SCRIPT.getKeyBinding();
            if (clientKeybind.isPressed() && !clientKeybind.wasPressed()) {
                clientKeybind.setPressed(false);
                enabled = !enabled;
                EssentialUtils.sendMessageToActionBar("§6Macro is now " + (enabled ? "§aON" : "§cOFF"));
                if (enabled)
                    thread = executeMacro();
                else if (thread != null) {
                    thread.interrupt();
                }
            }
        });
    }

    private static Thread executeMacro() {
        Thread thread = new Thread(() -> {
            try {
                Path macroFile = getFile();
                String file = Files.readString(macroFile);
                Run.run(ClientRules.CLIENT_SCRIPT_FILENAME.getString(), file);
                enabled = false;
                EssentialUtils.sendMessageToActionBar("§6Macro has finished executing");
            }
            catch (IOException | Error e) {
                if (!enabled)
                    return;
                EssentialUtils.sendMessage("§cAn error occurred while trying to read the macro");
                if (e instanceof Error)
                    EssentialUtils.sendMessage("§c--------------------------------------------\n" + e);
                enabled = false;
                EssentialUtils.sendMessageToActionBar("§6Macro now §cOFF");
            }
            finally {
                resetKeys(MinecraftClient.getInstance());
                Thread.currentThread().interrupt();
            }
        });
        thread.start();
        return thread;
    }

    public static void resetKeys(MinecraftClient client) {
        client.options.keySneak.setPressed(false);
        client.options.keyForward.setPressed(false);
        client.options.keyAttack.setPressed(false);
        client.options.keyUse.setPressed(false);
    }

    public static Path getFile() {
        return getDir().resolve(ClientRules.CLIENT_SCRIPT_FILENAME.getString() + ".me.senseiwells.arucas");
    }

    public static Path getDir() {
        return EssentialUtils.getEssentialConfigFile().resolve("Scripts");
    }
}
