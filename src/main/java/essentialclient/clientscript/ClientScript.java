package essentialclient.clientscript;

import essentialclient.EssentialClient;
import essentialclient.clientscript.events.MinecraftScriptEvents;
import essentialclient.clientscript.extensions.ArucasMinecraftExtension;
import essentialclient.clientscript.values.*;
import essentialclient.config.clientrule.ClientRules;
import essentialclient.feature.ClientKeybinds;
import essentialclient.utils.EssentialUtils;
import essentialclient.utils.command.CommandHelper;
import me.senseiwells.arucas.api.ContextBuilder;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.ExceptionUtils;
import me.senseiwells.arucas.utils.impl.ArucasThread;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
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
	private static final ClientScript instance = new ClientScript();

	private ArucasThread mainScriptThread;

	public void register() {
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			KeyBinding clientKeybind = ClientKeybinds.CLIENT_SCRIPT.getKeyBinding();
			if (clientKeybind.wasPressed()) {
				this.toggleScript();
			}
		});
	}

	public static ClientScript getInstance() {
		return instance;
	}

	public boolean isScriptRunning() {
		return this.mainScriptThread != null;
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
		this.executeScript();
	}

	public synchronized void stopScript() {
		if (!this.isScriptRunning()) {
			return;
		}
		this.mainScriptThread.interrupt();
		this.mainScriptThread = null;
		CommandHelper.functionCommands.clear();
		CommandHelper.functionCommandNodes.clear();
		MinecraftClient client = EssentialUtils.getClient();
		if (CommandHelper.getCommandPacket() != null) {
			client.execute(() -> {
				ClientPlayNetworkHandler networkHandler = EssentialUtils.getNetworkHandler();
				if (networkHandler != null) {
					networkHandler.onCommandTree(CommandHelper.getCommandPacket());
				}
			});
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
			.addClasses(
				JsonValue.ArucasJsonClass::new,
				MinecraftClientValue.ArucasMinecraftClientMembers::new,
				PlayerValue.ArucasPlayerClass::new,
				EntityValue.ArucasEntityClass::new,
				OtherPlayerValue.ArucasAbstractPlayerClass::new,
				LivingEntityValue.ArucasLivingEntityClass::new,
				BlockValue.ArucasBlockClass::new,
			 	ItemStackValue.ArucasItemStackClass::new,
				WorldValue.ArucasWorldClass::new,
				ScreenValue.ArucasScreenClass::new,
				FakeInventoryScreenValue.ArucasFakeInventoryScreenClass::new,
				TextValue.ArucasTextClass::new,
				MaterialValue.ArucasMaterialClass::new,
				PosValue.ArucasPosClass::new,
				RecipeValue.ArucasRecipeClass::new,
				MerchantScreenValue.ArucasMerchantScreenClass::new,
				TradeValue.ArucasTradeOfferClass::new
			)
			.addExtensions(
				ArucasMinecraftExtension::new
			)
			.addDefault();

		Context context = contextBuilder.build();

		context.getThreadHandler()
			.setErrorHandler(this::messageError)
			.setFinalHandler(this::stopScript)
			.setFatalErrorHandler((c, t, s) -> {
				if (s.isEmpty()) {
					this.sendReportMessage(t);
					return;
				}
				this.sendReportMessage(t, s);
			});

		this.mainScriptThread = context.getThreadHandler().runOnThread(context, fileName, fileContent, null);

	}

	private void messageError(String error) {
		EssentialUtils.sendMessage("§cAn error occurred while running the script");
		EssentialUtils.sendMessage("§c--------------------------------------------\n" + error);
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
}
