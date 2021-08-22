package essentialclient.utils.file;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileHelper {
    public static void checkIfEssentialClientDirExists() {
        Path path = FabricLoader.getInstance().getConfigDir().resolve("EssentialClient");
        if (!Files.exists(path)) {
            try {
                Files.createDirectory(path);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
