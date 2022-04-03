package me.senseiwells.essentialclient.clientscript.core;

import me.senseiwells.essentialclient.EssentialClient;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import me.senseiwells.arucas.api.IArucasOutput;

import java.util.function.Consumer;

public class ArucasMinecraftOutput implements IArucasOutput {
	public static ArucasMinecraftOutput INSTANCE = new ArucasMinecraftOutput();

	private final Consumer<String> outputHandler;

	private ArucasMinecraftOutput() {
		this.outputHandler = str -> {
			str = !str.endsWith("\n") ? str : str.substring(0, str.length() - 1);
			EssentialUtils.sendMessage(str);
		};
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
}
