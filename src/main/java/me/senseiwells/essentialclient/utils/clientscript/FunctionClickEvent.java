package me.senseiwells.essentialclient.utils.clientscript;

import me.senseiwells.arucas.core.Interpreter;
import me.senseiwells.arucas.utils.ArucasFunction;
import me.senseiwells.essentialclient.utils.clientscript.ClientScriptUtils;
import net.minecraft.text.ClickEvent;

import java.util.List;

public class FunctionClickEvent extends ClickEvent {
	private final Interpreter interpreter;
	private final ArucasFunction function;

	public FunctionClickEvent(Interpreter interpreter, ArucasFunction function) {
		super(Action.RUN_COMMAND, "");
		this.interpreter = interpreter.branch();
		this.function = function;
	}

	public void executeFunction() {
		Interpreter branch = this.interpreter.branch();
		branch.runSafe(() -> this.function.invoke(branch, List.of()));
	}
}
