package me.senseiwells.essentialclient.clientscript.core;

import me.senseiwells.arucas.api.ArucasAPI;
import me.senseiwells.arucas.api.ArucasLibrary;
import me.senseiwells.arucas.api.ImplArucasLibrary;
import me.senseiwells.arucas.api.ThreadHandler;
import me.senseiwells.arucas.core.Interpreter;
import me.senseiwells.arucas.utils.Properties;
import me.senseiwells.essentialclient.EssentialClient;
import me.senseiwells.essentialclient.clientscript.api.ClientScriptAPI;
import me.senseiwells.essentialclient.clientscript.events.MinecraftScriptEvents;
import me.senseiwells.essentialclient.feature.keybinds.ClientKeyBind;
import me.senseiwells.essentialclient.feature.keybinds.ClientKeyBinds;
import me.senseiwells.essentialclient.feature.keybinds.MultiKeyBind;
import me.senseiwells.essentialclient.rule.ClientRules;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import me.senseiwells.essentialclient.utils.command.CommandHelper;
import me.senseiwells.essentialclient.utils.render.Texts;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.util.InputUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class ClientScriptInstance {
	private static List<ClientScriptAPI> APIS = new LinkedList<>();
	private static ArucasAPI API;

	private final String content;
	private Path fileLocation;
	private String scriptName;
	private MultiKeyBind keyBind;
	private Interpreter interpreter;
	private boolean isStopping;

	private ClientScriptInstance(String scriptName, String content, Path fileLocation) {
		this.scriptName = scriptName;
		this.content = content;
		this.fileLocation = fileLocation;
		this.keyBind = ClientKeyBinds.registerMulti(scriptName, "Script Toggles", client -> this.toggleScript());
		ClientScript.INSTANCE.addInstance(this);
	}

	public ClientScriptInstance(String scriptName, Path fileLocation) {
		this(scriptName, null, fileLocation);
	}

	public Path getFileLocation() {
		return this.fileLocation;
	}

	public MultiKeyBind getKeyBind() {
		return this.keyBind;
	}

	public boolean isTemporary() {
		return this.fileLocation == null;
	}

	public void renameScript(String newName, Path newLocation) {
		ClientScript.INSTANCE.replaceSelectedInstance(this.scriptName, newName);
		ClientKeyBind old = ClientKeyBinds.unregisterKeyBind(this.scriptName);
		Collection<InputUtil.Key> keys = old != null ? old.getKeys() : List.of();
		this.keyBind = ClientKeyBinds.registerMulti(newName, "Script Toggles", client -> this.toggleScript(), keys);
		this.scriptName = newName;
		this.fileLocation = newLocation;
	}

	public void delete() throws IOException {
		this.stopScript();
		Files.delete(this.fileLocation);
		ClientScript.INSTANCE.removeInstance(this);
		ClientKeyBinds.unregisterKeyBind(this.scriptName);
	}

	public boolean isScriptRunning() {
		return this.interpreter != null;
	}

	public synchronized void toggleScript() {
		if (EssentialUtils.getClient().player == null || this.isScriptRunning()) {
			this.stopScript();
			return;
		}
		this.executeScript();
	}

	public synchronized void stopScript() {
		if (!this.isScriptRunning() || this.isStopping) {
			return;
		}
		this.isStopping = true;
		MinecraftScriptEvents.ON_SCRIPT_END.run(this.interpreter.getProperties().getId());
		this.interpreter.getThreadHandler().stop();

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
			EssentialUtils.sendMessage(Texts.SCRIPT_STATUS.generate(this.scriptName, Texts.FINISHED));
		}
		this.interpreter = null;
		this.isStopping = false;
	}

	private synchronized void executeScript() {
		String fileContent;
		try {
			fileContent = this.fileLocation == null ? this.content : Files.readString(this.fileLocation);
			if (fileContent == null) {
				throw new IOException("File content was null!");
			}
		} catch (IOException e) {
			EssentialUtils.sendMessage(Texts.READ_ERROR.generate(this.scriptName));
			e.printStackTrace();
			return;
		}

		this.interpreter = Interpreter.of(fileContent, this.scriptName, API, ClientScriptThreadHandler::new);

		ThreadHandler threadHandler = this.interpreter.getThreadHandler();
		threadHandler.addShutdownEvent(this::stopScript);
		threadHandler.executeAsync();

		if (ClientRules.CLIENT_SCRIPT_ANNOUNCEMENTS.getValue()) {
			EssentialUtils.sendMessage(Texts.SCRIPT_STATUS.generate(this.scriptName, Texts.STARTED));
		}
	}

	public String getName() {
		return this.scriptName;
	}

	public static void load() {
		ArucasLibrary library = new ImplArucasLibrary(
			ClientScript.INSTANCE.getLibraryDirectory()
		);

		ArucasAPI.Builder builder = new ArucasAPI.Builder()
			.addDefault()
			.setObfuscator(new ClientScriptObfuscator())
			.setInput(ClientScriptIO.INSTANCE)
			.setOutput(ClientScriptIO.INSTANCE)
			.setLibraryManager(library)
			.setInterpreterProperties(() -> {
				Properties properties = new Properties();
				properties.setErrorMaxLength(40);
				properties.setLogDeprecated(true);
				return properties;
			});

		MinecraftAPI.addMinecraftAPI(builder);

		APIS.forEach(a -> a.apply(builder));
		APIS = null;

		try {
			API = builder.build();
		} catch (Exception e) {
			EssentialClient.LOGGER.error("Failed to load Script API", e);
			EssentialUtils.getClient().scheduleStop();
			throw e;
		}

		generate();
	}

	public static ArucasAPI getApi() {
		return API;
	}

	public static void addApi(ClientScriptAPI api) {
		if (APIS == null) {
			throw new IllegalStateException("ClientScript API has been locked, register your api earlier");
		}
		APIS.add(api);
	}

	public static void runFromContent(String scriptName, String scriptContent) {
		ClientScriptInstance instance = new ClientScriptInstance(scriptName, scriptContent, null);
		instance.toggleScript();
	}

	private static void generate() {
		try {
			API.generateNativeFiles(API.getLibraryManager().getImportPath());
		} catch (Exception e) {
			// This isn't fatal
			EssentialClient.LOGGER.error("Failed to generate native files", e);
		}
	}
}
