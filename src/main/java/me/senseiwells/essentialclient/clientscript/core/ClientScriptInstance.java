package me.senseiwells.essentialclient.clientscript.core;

import me.senseiwells.arucas.api.ContextBuilder;
import me.senseiwells.arucas.api.docs.parser.JsonParser;
import me.senseiwells.arucas.core.Arucas;
import me.senseiwells.arucas.discord.DiscordAPI;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.ExceptionUtils;
import me.senseiwells.arucas.utils.impl.ArucasThread;
import me.senseiwells.essentialclient.EssentialClient;
import me.senseiwells.essentialclient.clientscript.events.MinecraftScriptEvents;
import me.senseiwells.essentialclient.feature.keybinds.ClientKeyBind;
import me.senseiwells.essentialclient.feature.keybinds.ClientKeyBinds;
import me.senseiwells.essentialclient.rule.ClientRules;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import me.senseiwells.essentialclient.utils.command.CommandHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public class ClientScriptInstance {
	private static final ContextBuilder BUILDER;

	static {
		BUILDER = new ContextBuilder()
			.setDisplayName("Arucas Client")
			.setArucasAPI(new ImplArucasAPI())
			.addDefault();
		MinecraftAPI.addMinecraftAPI(BUILDER);
		DiscordAPI.addDiscordAPI(BUILDER);
		ExceptionUtils.runSafe(BUILDER::generateArucasFiles);

		if (EssentialUtils.isDev()) {
			generateJson();
		}
	}

	private final String scriptName;
	private final String content;
	private final Path fileLocation;
	private final ClientKeyBind keyBind;
	private ArucasThread mainScriptThread;
	private UUID instanceId;
	private boolean isStopping;

	private ClientScriptInstance(String scriptName, String content, Path fileLocation) {
		this.scriptName = scriptName;
		this.content = content;
		this.fileLocation = fileLocation;
		this.keyBind = ClientKeyBinds.register(scriptName, GLFW.GLFW_KEY_UNKNOWN, "Script Toggles", client -> this.toggleScript());
		ClientScript.INSTANCE.addInstance(this);
	}

	public ClientScriptInstance(String scriptName, Path fileLocation) {
		this(scriptName, null, fileLocation);
	}

	public Path getFileLocation() {
		return this.fileLocation;
	}

	public ClientKeyBind getKeyBind() {
		return this.keyBind;
	}

	public boolean isTemporary() {
		return this.fileLocation == null;
	}

	public boolean isScriptRunning() {
		return this.mainScriptThread != null;
	}

	@SuppressWarnings("UnusedReturnValue")
	public synchronized boolean toggleScript() {
		if (EssentialUtils.getClient().player == null || this.isScriptRunning()) {
			this.stopScript();
			return false;
		}
		this.executeScript();
		return true;
	}

	public synchronized void stopScript() {
		if (!this.isScriptRunning() || this.isStopping) {
			return;
		}
		this.isStopping = true;
		MinecraftScriptEvents.ON_SCRIPT_END.run(this.instanceId);

		this.mainScriptThread.interrupt();
		this.mainScriptThread = null;
		this.instanceId = null;
		MinecraftClient client = EssentialUtils.getClient();
		if (CommandHelper.getCommandPacket() != null) {
			client.execute(() -> {
				ClientPlayNetworkHandler networkHandler = EssentialUtils.getNetworkHandler();
				if (networkHandler != null) {
					networkHandler.onCommandTree(CommandHelper.getCommandPacket());
				}
			});
		}

		if (ClientRules.CLIENT_SCRIPT_ANNOUNCEMENTS.getValue()) {
			EssentialUtils.sendMessage("§6Script '%s' has §cFINISHED".formatted(this.scriptName));
		}
		this.isStopping = false;
	}

	private synchronized void executeScript() {
		String fileContent;
		try {
			fileContent = this.fileLocation == null ? this.content : Files.readString(this.fileLocation);
			if (fileContent == null) {
				throw new IOException("File content was null!");
			}
			// This is super hacky...
			// Will be removed in future
			if (!fileContent.contains("import * from Minecraft;")) {
				if (this.fileLocation != null) {
					fileContent = "import * from Minecraft;\n\n" + fileContent;
					Files.writeString(this.fileLocation, fileContent);
				}
				else {
					EssentialUtils.sendMessage("You should add 'import * from Minecraft;' to the top of '%s'".formatted(this.scriptName));
					fileContent = "import * from Minecraft;" + fileContent;
				}
			}
		}
		catch (IOException e) {
			EssentialUtils.sendMessage("§cAn error occurred while trying to read '%s'".formatted(this.scriptName));
			e.printStackTrace();
			return;
		}

		// Create a new context for the file we should run.
		Context context = BUILDER.build();

		context.getThreadHandler().setFatalErrorHandler((c, t, s) -> {
			if (s.isEmpty()) {
				this.sendReportMessage(t);
				return;
			}
			this.sendReportMessage(t, s);
		}).addShutdownEvent(this::stopScript);

		if (ClientRules.CLIENT_SCRIPT_ANNOUNCEMENTS.getValue()) {
			EssentialUtils.sendMessage("§6Script '%s' has §aSTARTED".formatted(this.scriptName));
		}
		this.mainScriptThread = context.getThreadHandler().runOnMainThread(context, this.scriptName, fileContent);
		this.instanceId = context.getContextId();
	}

	private void sendReportMessage(Throwable t) {
		this.sendReportMessage(t, null);
	}

	private void sendReportMessage(Throwable t, String content) {
		String gitReport = this.getGithubLink(t, content);
		EssentialUtils.sendMessage("§cAn error occurred while running '%s'".formatted(this.scriptName));
		EssentialUtils.sendMessage("§cIf you believe this is a bug please report it");
		EssentialUtils.sendMessage(
			Text.of("https://github.com/senseiwells/EssentialClient/issues/new").copy()
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
			Arucas.VERSION,
			this.scriptName,
			content == null || content.length() > charsLeft ? "'Script could not be included please send it manually" : content,
			stacktrace
		);
		return "https://github.com/senseiwells/EssentialClient/issues/new?title=ClientScript%20Crash&body=" + URLEncoder.encode(report, StandardCharsets.UTF_8);
	}

	@Override
	public String toString() {
		return this.scriptName;
	}

	public static void runFromContent(String scriptName, String scriptContent) {
		ClientScriptInstance instance = new ClientScriptInstance(scriptName, scriptContent, null);
		instance.toggleScript();
	}

	private static void generateJson() {
		EssentialClient.LOGGER.info("Generating Documentation...");
		JsonParser.of(BUILDER).write(() -> {
			Path path = ClientScript.INSTANCE.getScriptDirectory().resolve("json");
			if (!Files.exists(path)) {
				Files.createDirectories(path);
			}
			return path.resolve("AllDocs.json");
		});
	}
}
