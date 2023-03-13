package me.senseiwells.essentialclient.clientscript.core;

import me.senseiwells.arucas.api.ArucasInput;
import me.senseiwells.arucas.api.ArucasOutput;
import me.senseiwells.arucas.compiler.LocatableTrace;
import me.senseiwells.arucas.interpreter.Interpreter;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import me.senseiwells.essentialclient.utils.render.ChatColour;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

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

	@NotNull
	@Override
	public CompletableFuture<String> takeInput() {
		return this.inputFuture = new CompletableFuture<>();
	}

	@NotNull
	@Override
	public String formatError(@NotNull String s) {
		return ChatColour.RED + s + ChatColour.RESET;
	}

	@NotNull
	@Override
	public String formatErrorBold(@NotNull String s) {
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
		this.log("");
	}

	@Override
	public void logln(Object o) {
		this.log(o);
	}

	@Override
	public void logError(Object o) {
		this.logln(this.formatErrorBold(String.valueOf(o)));
	}

	@NotNull
	@Override
	public String formatStackTrace(@NotNull Interpreter interpreter, String message, @NotNull LocatableTrace trace) {
		return ArucasOutput.defaultFormatStackTrace(interpreter, message, trace);
	}
}
