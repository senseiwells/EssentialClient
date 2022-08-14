package me.senseiwells.essentialclient.clientscript.core;

import me.senseiwells.arucas.api.ThreadHandler;
import me.senseiwells.arucas.core.Arucas;
import me.senseiwells.arucas.core.Interpreter;
import me.senseiwells.arucas.utils.Util;
import me.senseiwells.essentialclient.EssentialClient;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import me.senseiwells.essentialclient.utils.render.Texts;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

public class ClientScriptThreadHandler extends ThreadHandler {
	private static final String ISSUE_TRACKER = "https://github.com/senseiwells/EssentialClient/issues/new";

	public ClientScriptThreadHandler(Interpreter interpreter) {
		super(interpreter);
	}

	@Override
	protected void handleFatalError(Throwable throwable) {
		EssentialUtils.sendMessage("§cAn error occurred while running '%s'".formatted(this.getInterpreter().getName()));
		EssentialUtils.sendMessage("§cIf you believe this is a bug please report it");

		String path = this.writeCrashReport(throwable).toAbsolutePath().toString();
		Text crashReport = Texts.literal("§cA crash report has been saved to '%s'".formatted(path))
			.styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, path)));

		EssentialUtils.sendMessage(crashReport);

		Text issueTracker = Texts.literal(ISSUE_TRACKER + "\n")
			.formatted(Formatting.UNDERLINE)
			.styled((style) -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, ISSUE_TRACKER)));

		EssentialUtils.sendMessage(issueTracker);
	}

	private Path writeCrashReport(Throwable throwable) {
		String stacktrace = ExceptionUtils.getStackTrace(throwable);
		String report = """
			### Minecraft Version: `%s`
			### Essential Client Version: `%s`
			### Arucas Version: `%s`
			### Script:
			```kt
			// %s
			%s
			```
			### Crash:
			```
			%s
			```
			""".formatted(
			EssentialUtils.getMinecraftVersion(),
			EssentialClient.VERSION,
			Arucas.getVERSION(),
			this.getInterpreter().getName(),
			this.getInterpreter().getContent(),
			stacktrace
		);
		// get current date and time
		String date = LocalDateTime.now().toString();
		Path crashPath = EssentialUtils.getEssentialConfigFile().resolve("script-crashes").resolve("crash-" + date);
		Util.File.INSTANCE.ensureParentExists(crashPath);
		EssentialUtils.throwAsRuntime(() -> Files.write(crashPath, report.getBytes()));
		return crashPath;
	}
}
