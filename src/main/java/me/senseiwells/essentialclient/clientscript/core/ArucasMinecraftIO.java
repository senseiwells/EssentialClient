package me.senseiwells.essentialclient.clientscript.core;

import me.senseiwells.arucas.api.IArucasInput;
import me.senseiwells.arucas.api.IArucasOutput;
import me.senseiwells.essentialclient.EssentialClient;
import me.senseiwells.essentialclient.utils.EssentialUtils;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public enum ArucasMinecraftIO implements IArucasOutput, IArucasInput {
	INSTANCE;

	private final Consumer<String> outputHandler;
	private CompletableFuture<String> inputFuture;

	ArucasMinecraftIO() {
		this.outputHandler = str -> {
			str = !str.endsWith("\n") ? str : str.substring(0, str.length() - 1);
			EssentialUtils.sendMessage(str);
		};
	}

	public boolean submitInput(String input) {
		if (this.inputFuture != null) {
			this.inputFuture.complete(input);
			this.inputFuture = null;
			return true;
		}
		return false;
	}

	@Override
	public Consumer<String> getOutputHandler() {
		return this.outputHandler;
	}

	@Override
	public Consumer<String> getDebugHandler() {
		return EssentialClient.LOGGER::debug;
	}

	@Override
	public void setFormatting(String error, String boldError, String reset) { }

	@Override
	public String getErrorFormatting() {
		return "§c";
	}

	@Override
	public String getErrorFormattingBold() {
		return "§l§c";
	}

	@Override
	public String getResetFormatting() {
		return "§r";
	}

	@Override
	public CompletableFuture<String> takeInput() {
		return this.inputFuture = new CompletableFuture<>();
	}
}
