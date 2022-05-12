package me.senseiwells.essentialclient.utils.misc;

import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.functions.FunctionValue;
import net.minecraft.text.ClickEvent;

import java.util.ArrayList;

public class FunctionClickEvent extends ClickEvent {
	private final Context context;
	private final FunctionValue function;

	public FunctionClickEvent(Context context, FunctionValue function) {
		super(Action.RUN_COMMAND, "");
		this.context = context.createBranch();
		this.function = function;
	}

	public void executeFunction() {
		Context branch = this.context.createBranch();
		branch.getThreadHandler().runAsyncFunctionInThreadPool(branch, ctx -> this.function.call(ctx, new ArrayList<>()));
	}
}
