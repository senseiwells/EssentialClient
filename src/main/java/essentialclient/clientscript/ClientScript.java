package essentialclient.clientscript;

import essentialclient.EssentialClient;
import essentialclient.clientscript.events.MinecraftScriptEvents;
import essentialclient.clientscript.extensions.*;
import essentialclient.config.clientrule.ClientRules;
import essentialclient.feature.ClientKeybinds;
import essentialclient.utils.EssentialUtils;
import essentialclient.utils.command.CommandHelper;
import me.senseiwells.arucas.api.ContextBuilder;
import me.senseiwells.arucas.core.Run;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.ThrowStop;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.ExceptionUtils;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class ClientScript {
	/**
	 * This is the ThreadGroup that all script threads should be inside.
	 *
	 * If a script thread is not generated inside a thread it is possible
	 * it will never stop executing.
	 */
	private static final ClientScript instance = new ClientScript();

	public final ThreadGroup arucasThreadGroup = new ThreadGroup("Arucas Thread Group");
	public final Object ERROR_LOCK = new Object();

	private Context context;
	private Thread thread;
	private boolean hasErrored;

	public void register() {
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			KeyBinding clientKeybind = ClientKeybinds.CLIENT_SCRIPT.getKeyBinding();
			if (clientKeybind.isPressed() && !clientKeybind.wasPressed()) {
				clientKeybind.setPressed(false);
				this.toggleScript();
			}
		});
	}

	public static ClientScript getInstance() {
		return instance;
	}
	
	public boolean isScriptRunning() {
		return this.context != null;
	}
	
	public void toggleScript() {
		boolean running = this.isScriptRunning();
		if (running) {
			this.stopScript();
		}
		else {
			this.startScript();
		}
		
		EssentialUtils.sendMessageToActionBar("§6Script is now " + (running ? "§cOFF" : "§aON"));
	}
	
	public synchronized void startScript() {
		if (isScriptRunning()) {
			this.stopScript();
		}
		this.hasErrored = false;
		this.executeScript();
	}
	
	public synchronized void stopScript() {
		this.arucasThreadGroup.interrupt();

		this.context = null;
		this.thread = null;

		CommandHelper.functionCommands.clear();
		CommandHelper.functionCommandNodes.clear();
		MinecraftClient client = EssentialUtils.getClient();
		if (CommandHelper.getCommandPacket() != null && client.getNetworkHandler() != null) {
			client.execute(() -> client.getNetworkHandler().onCommandTree(CommandHelper.getCommandPacket()));
		}
		MinecraftScriptEvents.clearEventFunctions();
		this.resetKeys(client);

		EssentialUtils.sendMessageToActionBar("§6Script is now §cOFF");
	}
	
	public static Path getFile() {
		return getDir().resolve(ClientRules.CLIENT_SCRIPT_FILENAME.getValue() + ".arucas");
	}
	
	public static Path getDir() {
		return EssentialUtils.getEssentialConfigFile().resolve("Scripts");
	}
	
	private synchronized void executeScript() {
		final String fileName = ClientRules.CLIENT_SCRIPT_FILENAME.getValue();
		final String fileContent;
		try {
			fileContent = Files.readString(getFile());
		}
		catch (IOException e) {
			EssentialUtils.sendMessage("§cAn error occurred while trying to read the script");
			e.printStackTrace();
			return;
		}

		// Create a new context for the file we should run.
		ContextBuilder contextBuilder = new ContextBuilder()
			.setDisplayName("Arucas client")
			.setOutputHandler(s -> {
				s = !s.endsWith("\n") ? s : s.substring(0, s.length() - 1);
				EssentialUtils.sendMessage(s);
			})
			.addValueExtensions(
				ArucasMinecraftClientMembers::new,
				ArucasEntityMembers::new,
				ArucasLivingEntityMembers::new,
				ArucasAbstractPlayerMembers::new,
				ArucasPlayerMembers::new,
				ArucasBlockStateMembers::new,
				ArucasItemStackMembers::new,
				ArucasWorldMembers::new,
				ArucasScreenMembers::new,
				ArucasFakeInventoryScreenMembers::new,
				ArucasTextMembers::new
			)
			.addExtensions(
				ArucasMinecraftExtension::new
			)
			.addDefault()
			;

		// Local variable is needed, it will never be null
		Context context = contextBuilder.build();
		this.context = context;

		// Create a new deamon thread.
		this.thread = new Thread(this.arucasThreadGroup, () -> {
			try {
				Run.run(context, fileName, fileContent);
			}
			catch (ThrowStop e) {
				EssentialUtils.sendMessage("§c%s".formatted(e.toString(context)));
			}
			catch (CodeError e) {
				this.tryError(e);
			}
			catch (Throwable t) {
				this.sendReportMessage(t, fileContent);
				t.printStackTrace();
			}
			finally {
				this.stopScript();
			}
		}, "Client Script Thread");
		this.thread.setDaemon(true);
		this.thread.start();
	}

	public synchronized void runAsyncFunctionInContext(Context context, ThrowableConsumer<Context> consumer) {
		if (!this.isScriptRunning()) {
			return;
		}

		this.runAsyncFunction(context, consumer);
	}
	
	public synchronized void runBranchAsyncFunction(ThrowableConsumer<Context> consumer) {
		this.runAsyncFunctionInContext(this.context.createBranch(), consumer);
	}
	
	private synchronized void runAsyncFunction(final Context context, ThrowableConsumer<Context> consumer) {
		Thread thread = new Thread(this.arucasThreadGroup, () -> {
			try {
				consumer.accept(context);
				return;
			}
			catch (CodeError e) {
				this.tryError(e);
			}
			catch (ThrowValue tv) {
				this.tryError();
			}
			catch (Throwable t) {
				this.sendReportMessage(t);
				t.printStackTrace();
			}
			this.stopScript();
		}, "Client Runnable Thread");
		thread.setDaemon(true);
		thread.start();
	}

	private void tryError(CodeError error) {
		if (error.errorType != CodeError.ErrorType.INTERRUPTED_ERROR) {
			synchronized (this.ERROR_LOCK) {
				if (this.hasErrored) {
					return;
				}
				EssentialUtils.sendMessage("§cAn error occurred while running the script");
				EssentialUtils.sendMessage("§c--------------------------------------------\n" + error.toString(context));
				this.hasErrored = true;
			}
		}
	}

	private void tryError() {
		synchronized (this.ERROR_LOCK) {
			if (this.hasErrored) {
				return;
			}
			EssentialUtils.sendMessage("§cAn error occurred while running the script");
			EssentialUtils.sendMessage("§c--------------------------------------------\nInvalid position for a break, continue or return");
			this.hasErrored = true;
		}
	}

	private void sendReportMessage(Throwable t) {
		this.sendReportMessage(t, null);
	}

	private void sendReportMessage(Throwable t, String content) {
		String gitReport = getGithubLink(t, content);
		EssentialUtils.sendMessage("§cAn error occurred while running the script");
		EssentialUtils.sendMessage("§cIf you believe this is a bug please report it");
		EssentialUtils.sendMessage(
			new LiteralText("https://github.com/senseiwells/EssentialClient/issues/new")
				.formatted(Formatting.UNDERLINE)
				.styled((style) -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL,
					gitReport))
				)
			.append("\n")
		);
	}

	private String getGithubLink(Throwable t, String content) {
		String stacktrace = ExceptionUtils.getStackTrace(t);
		int charsLeft = 1400 - stacktrace.length();
		String report = """
		### Minecraft Version: `%s`
		### Essential Client Version: `%s`
		### Arucas Version: `%s`
		### Script:
		```kotlin
		%s
		```
		### Crash:
		```
		%s
		```
		""".formatted(
			EssentialUtils.getMinecraftVersion(),
			EssentialClient.VERSION,
			EssentialUtils.getArucasVersion(),
			content == null || content.length() > charsLeft ? "'Script could not be included please send it manually" : content,
			stacktrace
		);
		return "https://github.com/senseiwells/EssentialClient/issues/new?title=ClientScript%20Crash&body=" + URLEncoder.encode(report, StandardCharsets.UTF_8);
	}

	public void resetKeys(MinecraftClient client) {
		client.options.keySneak.setPressed(false);
		client.options.keyForward.setPressed(false);
		client.options.keyAttack.setPressed(false);
		client.options.keyUse.setPressed(false);
	}

	public interface ThrowableConsumer<T> {
		void accept(T obj) throws Throwable;
	}
}
