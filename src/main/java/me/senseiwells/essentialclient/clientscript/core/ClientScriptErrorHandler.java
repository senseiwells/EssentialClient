package me.senseiwells.essentialclient.clientscript.core;

import me.senseiwells.arucas.Arucas;
import me.senseiwells.arucas.api.ArucasErrorHandler;
import me.senseiwells.arucas.exceptions.ArucasError;
import me.senseiwells.arucas.exceptions.FatalError;
import me.senseiwells.arucas.exceptions.Propagator;
import me.senseiwells.arucas.interpreter.Interpreter;
import me.senseiwells.arucas.utils.FileUtils;
import me.senseiwells.essentialclient.EssentialClient;
import me.senseiwells.essentialclient.rule.ClientRules;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import me.senseiwells.essentialclient.utils.render.Texts;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;

public enum ClientScriptErrorHandler implements ArucasErrorHandler {
	INSTANCE;

	private static final String ISSUE_TRACKER = "https://github.com/senseiwells/EssentialClient/issues/new";

	@Override
	public void handleArucasError(@NotNull ArucasError arucasError, @NotNull Interpreter interpreter) {
		Identifier identifier = switch (ClientRules.CLIENT_SCRIPT_FONT.getValue()) {
			case "Minecraft" -> Texts.MINECRAFT_MONO;
			case "Jetbrains" -> Texts.JETBRAINS_MONO;
			default -> null;
		};
		EssentialUtils.sendMessage(Texts.literal(arucasError.format(interpreter)).styled(s -> s.withFont(identifier).withColor(Formatting.RED)));
	}

	@Override
	public void handleInvalidPropagator(@NotNull Propagator propagator, @NotNull Interpreter interpreter) {
		ArucasErrorHandler.getDefault().handleInvalidPropagator(propagator, interpreter);
	}

	@Override
	public void handleFatalError(@NotNull FatalError fatalError, @NotNull Interpreter interpreter) {
		this.handleFatalError((Throwable) fatalError, interpreter);
	}

	@Override
	public void handleFatalError(@NotNull Throwable throwable, @NotNull Interpreter interpreter) {
		Text error = Texts.literal("\n").formatted(Formatting.RED).append(Texts.FATAL_ERROR.generate(interpreter.getName()));
		EssentialUtils.sendMessage(error);

		String path = this.writeCrashReport(interpreter, throwable).toAbsolutePath().toString();
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
			Arucas.VERSION,
			interpreter.getName(),
			interpreter.getContent(),
			scriptTrace,
			stacktrace
		);
		String date = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss").format(new Date());
		Path crashPath = EssentialUtils.getEssentialConfigFile().resolve("script-crashes").resolve("crash-" + date + ".txt");
		try {
			Files.writeString(FileUtils.ensureParentExists(crashPath), report);
		} catch (IOException e) {
			EssentialClient.LOGGER.error("Failed to write script crash report:\n{}\n\n{}", report, e);
		}
		return crashPath;
	}
}
