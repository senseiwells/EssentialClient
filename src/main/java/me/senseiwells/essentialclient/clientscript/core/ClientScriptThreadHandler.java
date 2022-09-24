package me.senseiwells.essentialclient.clientscript.core;

import me.senseiwells.arucas.api.ThreadHandler;
import me.senseiwells.arucas.core.Arucas;
import me.senseiwells.arucas.core.Interpreter;
import me.senseiwells.arucas.exceptions.FatalError;
import me.senseiwells.arucas.utils.Util;
import me.senseiwells.essentialclient.EssentialClient;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import me.senseiwells.essentialclient.utils.render.Texts;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ClientScriptThreadHandler extends ThreadHandler {
	private static final String ISSUE_TRACKER = "https://github.com/senseiwells/EssentialClient/issues/new";

	public ClientScriptThreadHandler(Interpreter interpreter) {
		super(interpreter);
	}

	@Override
	protected void handleFatalError(FatalError fatalError, Interpreter interpreter) {
		this.handleFatalError((Throwable) fatalError, interpreter);
	}

	@Override
	protected void handleFatalError(Throwable throwable, Interpreter interpreter) {
		Text error = Texts.literal("\n").formatted(Formatting.RED).append(Texts.FATAL_ERROR.generate(this.getInterpreter().getName()));
		EssentialUtils.sendMessage(error);

		String path = this.writeCrashReport(this.getInterpreter(), throwable).toAbsolutePath().toString();
		Text crashReport = Texts.CRASH_REPORT.generate(
			Texts.literal("\n" + path + "\n")
				.formatted(Formatting.UNDERLINE)
				.styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, path)))
		).formatted(Formatting.RED);
		EssentialUtils.sendMessage(crashReport);

		EssentialUtils.sendMessage(Texts.CRASH_BUG);
		Text issueTracker = Texts.literal(ISSUE_TRACKER + "\n")
			.formatted(Formatting.UNDERLINE)
			.styled((style) -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, ISSUE_TRACKER)));

		EssentialUtils.sendMessage(issueTracker);
	}

	private Path writeCrashReport(Interpreter interpreter, Throwable throwable) {
		String stacktrace = ExceptionUtils.getStackTrace(throwable);
		String scriptTrace = "";
		if (throwable instanceof FatalError error) {
			scriptTrace = """
				### StackTrace:
				```
				%s
				```
				""".formatted(error.format(interpreter));
		}
		String report = """
			### Minecraft Version: `%s`
			### Essential Client Version: `%s`
			### Arucas Version: `%s`
			### Script:
			```kt
			// %s
			%s
			```
			%s### Crash:
			```
			%s
			```
			""".formatted(
			EssentialUtils.getMinecraftVersion(),
			EssentialClient.VERSION,
			Arucas.getVERSION(),
			this.getInterpreter().getName(),
			this.getInterpreter().getContent(),
			scriptTrace,
			stacktrace
		);
		String date = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss").format(new Date());
		Path crashPath = EssentialUtils.getEssentialConfigFile().resolve("script-crashes").resolve("crash-" + date + ".txt");
		Util.File.INSTANCE.ensureParentExists(crashPath);
		try {
			Files.writeString(crashPath, report);
		} catch (IOException e) {
			EssentialClient.LOGGER.error("Failed to write script crash report:\n{}\n\n{}", report, e);
		}
		return crashPath;
	}
}
