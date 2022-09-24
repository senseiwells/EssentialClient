package me.senseiwells.essentialclient.utils.clientscript;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import me.senseiwells.arucas.api.ArucasAPI;
import me.senseiwells.arucas.utils.Util;
import me.senseiwells.essentialclient.clientscript.core.ClientScriptInstance;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

// Ripped from carpet
public class DocGenerator implements ModInitializer {
	@Override
	public void onInitialize() {
		String[] launchArgs = FabricLoader.getInstance().getLaunchArguments(true);

		// Prepare an OptionParser for our parameters
		OptionParser parser = new OptionParser();
		OptionSpec<String> pathSpec = parser.accepts("scriptDocGenerate").withRequiredArg();

		// Minecraft may need more stuff later that we don't want to special-case
		parser.allowsUnrecognizedOptions();
		OptionSet options = parser.parse(launchArgs);

		// If our flag isn't set, continue regular launch
		if (!options.has(pathSpec)) {
			return;
		}

		Util.File file = Util.File.INSTANCE;
		Path path = file.ensureExists(Path.of(options.valueOf(pathSpec)));
		ArucasAPI api = ClientScriptInstance.getApi();
		Path jsonPath = file.ensureExists(path.resolve("json"));
		Path mdPath = file.ensureExists(path.resolve("markdown"));
		Path libPath = file.ensureExists(path.resolve("libs"));
		try {
			Files.writeString(jsonPath.resolve("AllDocs.json"), ScriptJsonParser.scriptOf(api).parse());

			ScriptMarkdownParser markdownParser = ScriptMarkdownParser.scriptOf(api);
			Files.writeString(mdPath.resolve("Extensions.md"), markdownParser.parseExtensions());
			Files.writeString(mdPath.resolve("Classes.md"), markdownParser.parseClasses());
			Files.writeString(mdPath.resolve("Events.md"), markdownParser.parseEvents());

			api.generateNativeFiles(libPath);
		} catch (IOException e) {
			LogManager.getLogger("DocGenerator").info("Failed to generate docs", e);
		}
		System.exit(0);
	}
}
