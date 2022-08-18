package me.senseiwells.essentialclient.utils.misc;

import me.senseiwells.arucas.core.Interpreter;
import me.senseiwells.arucas.utils.ArucasFunction;
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
		this.function.invoke(branch, List.of());
	}
}
