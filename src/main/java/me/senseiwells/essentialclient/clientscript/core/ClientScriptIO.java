package me.senseiwells.essentialclient.clientscript.core;

import me.senseiwells.arucas.api.ArucasInput;
import me.senseiwells.arucas.api.ArucasOutput;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import me.senseiwells.essentialclient.utils.render.ChatColour;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.CompletableFuture;

public enum ClientScriptIO implements ArucasInput, ArucasOutput {
	INSTANCE;

	private final Logger logger = LogManager.getLogger("ClientScript");
	private CompletableFuture<String> inputFuture;

	public boolean submitInput(String input) {
		if (this.inputFuture != null) {
			this.inputFuture.complete(input);
			this.inputFuture = null;
			return true;
		}
		return false;
	}

	@Override
	public CompletableFuture<String> takeInput() {
		return this.inputFuture = new CompletableFuture<>();
	}

	@Override
	public String formatError(String s) {
		return ChatColour.RED + s + ChatColour.RESET;
	}

	@Override
	public String formatErrorBold(String s) {
		return ChatColour.BOLD + this.formatError(s);
	}

	@Override
	public void print(Object o) {
		EssentialUtils.sendMessage(String.valueOf(o));
	}

	@Override
	public void println() {
		this.println("");
	}

	@Override
	public void println(Object o) {
		this.print(o);
	}

	@Override
	public void printError(Object o) {
		this.println(this.formatErrorBold(String.valueOf(o)));
	}

	@Override
	public void log(Object o) {
		this.logger.info(o);
	}

	@Override
	public void logln() {
		this.log("\n");
	}

	@Override
	public void logln(Object o) {
		this.log(o + "\n");
	}

	@Override
	public void logError(Object o) {
		this.logln(this.formatErrorBold(String.valueOf(o)));
	}
}
