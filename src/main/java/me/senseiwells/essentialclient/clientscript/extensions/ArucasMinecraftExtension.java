package me.senseiwells.essentialclient.clientscript.extensions;

import me.senseiwells.arucas.api.ArucasExtension;
import me.senseiwells.arucas.api.docs.FunctionDoc;
import me.senseiwells.arucas.exceptions.RuntimeErrorKt;
import me.senseiwells.arucas.utils.Arguments;
import me.senseiwells.arucas.utils.BuiltInFunction;
import me.senseiwells.arucas.utils.impl.ArucasThread;

import java.util.List;

public class ArucasMinecraftExtension implements ArucasExtension {
	@Override
	public String getName() {
		return "MinecraftExtension";
	}

	@Override
	public List<BuiltInFunction> getBuiltInFunctions() {
		return List.of(
			BuiltInFunction.of("hold", this::hold, "")
		);
	}

	@FunctionDoc(
		name = "hold",
		desc = "This freezes the current thread and halts execution, same functionality as 'Thread.freeze()'",
		examples = "hold();"
	)
	private Void hold(Arguments arguments) {
		Thread thread = Thread.currentThread();
		if (thread instanceof ArucasThread arucasThread) {
			arucasThread.freeze();
			return null;
		}
		RuntimeErrorKt.runtimeError("Thread is not safe to freeze");
		return null;
	}
}
