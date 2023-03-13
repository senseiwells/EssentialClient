package me.senseiwells.essentialclient.utils.clientscript;

import me.senseiwells.arucas.functions.ArucasFunction;
import me.senseiwells.arucas.interpreter.Interpreter;
import net.minecraft.text.ClickEvent;

import java.util.List;
import java.util.function.Supplier;

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
		branch.runSafe((Supplier<Object>) () -> this.function.invoke(branch, List.of()));
	}
}
