package me.senseiwells.essentialclient.clientscript.extensions;

import me.senseiwells.arucas.api.ArucasExtension;
import me.senseiwells.arucas.api.docs.annotations.ExtensionDoc;
import me.senseiwells.arucas.api.docs.annotations.FunctionDoc;
import me.senseiwells.arucas.exceptions.RuntimeError;
import me.senseiwells.arucas.functions.builtin.Arguments;
import me.senseiwells.arucas.functions.builtin.BuiltInFunction;
import me.senseiwells.arucas.utils.impl.ArucasThread;
import me.senseiwells.arucas.utils.misc.Language;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@ExtensionDoc(
	name = "MinecraftExtension",
	desc = "Extension that provides simple functions related to Minecraft.",
	language = Language.Java
)
public class ArucasMinecraftExtension implements ArucasExtension {
	@NotNull
	@Override
	public String getName() {
		return "MinecraftExtension";
	}

	@Override
	public List<BuiltInFunction> getBuiltInFunctions() {
		return List.of(
			BuiltInFunction.of("hold", this::hold)
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
		throw new RuntimeError("Thread is not safe to freeze");
	}
}
