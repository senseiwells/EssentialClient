package me.senseiwells.essentialclient.clientscript.core;

import me.senseiwells.arucas.api.ArucasInput;
import me.senseiwells.arucas.api.ArucasOutput;
import me.senseiwells.arucas.core.Interpreter;
import me.senseiwells.arucas.utils.LocatableTrace;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import me.senseiwells.essentialclient.utils.interfaces.Rule;
import me.senseiwells.essentialclient.utils.render.ChatColour;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
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

	@Override
	public String formatStackTrace(Interpreter interpreter, String message, LocatableTrace trace) {
		int maxLength = interpreter.getProperties().getErrorMaxLength();
		List<String> lines = trace.getFileContent().lines().toList();
		int errorLine = trace.getLine() + 1;
		int padSize = (int) (Math.log10(errorLine) + 1.0);
		String padFormat = "%" + padSize + "d";
		String numPadding = " ".repeat(padSize);

		var errorStart = trace.getColumn();
		var errorEnd = errorStart + 1;
		var errorString = lines.get(errorLine - 1);

		if (errorStart > maxLength / 2) {
			int diff = errorStart - (maxLength / 2);
			errorStart -= diff - 4;
			errorEnd -= diff - 4;
			errorString = "... " + errorString.substring(diff);
		}

		if (errorString.length() > maxLength - 4) {
			if (errorEnd > maxLength - 4) {
				errorEnd = maxLength - 4;
			}

			errorString = errorString.substring(0, maxLength - 4) + " ...";
		}

		StringBuilder sb = new StringBuilder("\n");

		sb.append(String.format(padFormat, errorLine)).append(" | ").append(errorString).append("\n");
		sb.append(numPadding).append(" | ").append(" ".repeat(errorStart)).append("^".repeat(errorEnd - errorStart));

		if (message != null) {
			sb.append("\n").append(numPadding).append(" | ").append(" ".repeat(errorStart)).append(message);
		}

		return sb.toString();
	}
}
