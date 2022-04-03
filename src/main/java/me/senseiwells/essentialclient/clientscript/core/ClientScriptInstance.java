package me.senseiwells.essentialclient.clientscript.core;

import me.senseiwells.arucas.api.ContextBuilder;
import me.senseiwells.arucas.core.Arucas;
import me.senseiwells.arucas.discord.DiscordAPI;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.ExceptionUtils;
import me.senseiwells.arucas.utils.impl.ArucasThread;
import me.senseiwells.essentialclient.EssentialClient;
import me.senseiwells.essentialclient.clientrule.ClientRules;
import me.senseiwells.essentialclient.clientscript.events.MinecraftScriptEvents;
import me.senseiwells.essentialclient.clientscript.extensions.ArucasMinecraftExtension;
import me.senseiwells.essentialclient.clientscript.extensions.BoxShapeWrapper;
import me.senseiwells.essentialclient.clientscript.extensions.FakeEntityWrapper;
import me.senseiwells.essentialclient.clientscript.extensions.GameEventWrapper;
import me.senseiwells.essentialclient.clientscript.values.*;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import me.senseiwells.essentialclient.utils.command.CommandHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class ClientScriptInstance {
	private static final ContextBuilder BUILDER;

	static {
		BUILDER = new ContextBuilder()
			.setDisplayName("Arucas Client")
			.setOutputHandler(ArucasMinecraftOutput.INSTANCE)
			.setImportPath(ClientScript.INSTANCE.getScriptDirectory().resolve("libs"))
			.addClasses(
				"Minecraft",
				JsonValue.ArucasJsonClass::new,
				MinecraftClientValue.ArucasMinecraftClientMembers::new,
				CommandBuilderValue.CommandBuilderClass::new,
				PlayerValue.ArucasPlayerClass::new,
				EntityValue.ArucasEntityClass::new,
				OtherPlayerValue.ArucasAbstractPlayerClass::new,
				OtherPlayerValue.ArucasOtherPlayerClass::new,
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
			.addWrappers(
				"Minecraft",
				GameEventWrapper::new,
				BoxShapeWrapper::new,
				FakeEntityWrapper::new
			)
			.addExtensions(
				ArucasMinecraftExtension::new
			)
			.addDefault();
		DiscordAPI.addDiscordAPI(BUILDER);
		ExceptionUtils.runSafe(BUILDER::generateArucasFiles);
	}

	private final String scriptName;
	private final String content;
	private final Path fileLocation;
	private ArucasThread mainScriptThread;
	private Context context;

	private ClientScriptInstance(String scriptName, String content, Path fileLocation) {
		this.scriptName = scriptName;
		this.content = content;
		this.fileLocation = fileLocation;
		ClientScript.INSTANCE.addInstance(this);
	}

	public ClientScriptInstance(String scriptName, Path fileLocation) {
		this(scriptName, null, fileLocation);
	}

	public Path getFileLocation() {
		return this.fileLocation;
	}

	public boolean isTemporary() {
		return this.fileLocation == null;
	}

	public boolean isScriptRunning() {
		return this.mainScriptThread != null;
	}

	@SuppressWarnings("UnusedReturnValue")
	public synchronized boolean startScript() {
		if (EssentialUtils.getClient().player == null || this.isScriptRunning()) {
			this.stopScript();
			return false;
		}
		this.executeScript();
		return true;
	}

	public synchronized void stopScript() {
		if (!this.isScriptRunning()) {
			return;
		}
		this.mainScriptThread.interrupt();
		this.mainScriptThread = null;
		if (this.context != null) {
			FakeEntityWrapper.clearFakeEntities(this.context);
			BoxShapeWrapper.clearBoxesToRender(this.context);
			MinecraftScriptEvents.clearEventFunctions(this.context);
			CommandHelper.removeComplexCommand(this.context);
			this.context = null;
		}
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
				EssentialUtils.sendMessage("You should add 'import * from Minecraft;' to the top of '%s'".formatted(this.scriptName));
				fileContent = "import * from Minecraft;" + fileContent;
			}
		}
		catch (IOException e) {
			EssentialUtils.sendMessage("§cAn error occurred while trying to read '%s'".formatted(this.scriptName));
			e.printStackTrace();
			return;
		}

		// Create a new context for the file we should run.
		Context context = BUILDER.build();
		this.context = context;

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
		this.mainScriptThread = context.getThreadHandler().runOnThread(context, this.scriptName, fileContent, null);
	}

	private void sendReportMessage(Throwable t) {
		this.sendReportMessage(t, null);
	}

	private void sendReportMessage(Throwable t, String content) {
		String gitReport = this.getGithubLink(t, content);
		EssentialUtils.sendMessage("§cAn error occurred while running '%s'".formatted(this.scriptName));
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
		instance.startScript();
	}
}
