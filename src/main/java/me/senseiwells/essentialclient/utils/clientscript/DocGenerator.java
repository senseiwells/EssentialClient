package me.senseiwells.essentialclient.utils.clientscript;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import me.senseiwells.arucas.api.ArucasAPI;
import me.senseiwells.arucas.api.docs.visitor.ArucasDocParser;
import me.senseiwells.arucas.api.docs.visitor.impl.JsonDocVisitor;
import me.senseiwells.arucas.api.docs.visitor.impl.MarkdownDocVisitor;
import me.senseiwells.arucas.api.docs.visitor.impl.VSCSnippetDocVisitor;
import me.senseiwells.arucas.utils.FileUtils;
import me.senseiwells.arucas.utils.NetworkUtils;
import me.senseiwells.essentialclient.clientscript.core.ClientScriptInstance;
import me.senseiwells.essentialclient.utils.misc.WikiParser;
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
		OptionSpec<String> pathSpec = parser.accepts("generate").withRequiredArg();

		// Minecraft may need more stuff later that we don't want to special-case
		parser.allowsUnrecognizedOptions();
		OptionSet options = parser.parse(launchArgs);

		// If our flag isn't set, continue regular launch
		if (!options.has(pathSpec)) {
			return;
		}

		Path path = FileUtils.ensureExists(Path.of(options.valueOf(pathSpec)));
		ArucasAPI api = ClientScriptInstance.getApi();
		Path libPath = FileUtils.ensureExists(path.resolve("libs"));
		Path docPath = FileUtils.ensureExists(path.getParent().resolve("docs"));
		Path jsonPath = FileUtils.ensureExists(path.resolve("json"));
		Path mdPath = FileUtils.ensureExists(path.resolve("markdown"));
		Path snippetPath = FileUtils.ensureExists(path.resolve("snippets"));

		JsonDocVisitor jsonVisitor = new JsonDocVisitor();
		MarkdownDocVisitor markdownVisitor = new MarkdownDocVisitor();
		VSCSnippetDocVisitor snippetVisitor = new VSCSnippetDocVisitor();

		new ArucasDocParser(api).addVisitors(jsonVisitor, markdownVisitor, snippetVisitor).parse();

		try {
			Files.writeString(jsonPath.resolve("AllDocs.json"), jsonVisitor.getJson());
			api.generateNativeFiles(libPath);

			Files.writeString(snippetPath.resolve("arucas.json"), snippetVisitor.getJson());

			String extensions = markdownVisitor.getExtensions();
			String classes = markdownVisitor.getClasses();
			String events = ScriptMarkdownHelper.parseEvents();
			Files.writeString(mdPath.resolve("Extensions.md"), extensions);
			Files.writeString(mdPath.resolve("Classes.md"), classes);
			Files.writeString(mdPath.resolve("Events.md"), events);

			Files.writeString(FileUtils.ensureParentExists(docPath.resolve("wiki/Home.md")), WikiParser.generateWiki());

			String full = NetworkUtils.getStringFromUrl("https://raw.githubusercontent.com/senseiwells/Arucas/main/docs/FullLang.md");
			full += "\n\n" + extensions;
			full += "\n\n" + classes;
			full += "\n\n" + events;
			Files.writeString(docPath.resolve("Full.md"), full);
		} catch (IOException e) {
			LogManager.getLogger("DocGenerator").info("Failed to generate docs", e);
		}
		System.exit(0);
	}
}
