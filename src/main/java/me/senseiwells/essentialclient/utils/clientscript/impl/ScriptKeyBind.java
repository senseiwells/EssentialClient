package me.senseiwells.essentialclient.utils.clientscript.impl;

import me.senseiwells.arucas.core.Interpreter;
import me.senseiwells.arucas.utils.ArucasFunction;
import me.senseiwells.essentialclient.feature.keybinds.ClientKeyBinds;
import me.senseiwells.essentialclient.feature.keybinds.MultiKeyBind;

import java.util.List;
import java.util.function.Supplier;

public class ScriptKeyBind {
	private final MultiKeyBind keyBind;
	private ArucasFunction function;

	public ScriptKeyBind(Interpreter interpreter, String name) {
		Interpreter parent = interpreter.branch();
		this.keyBind = ClientKeyBinds.registerMulti(name, "Scripting Key Binds", client -> {
			if (interpreter.isRunning() && this.function != null) {
				interpreter.runSafe((Supplier<Object>) () -> this.function.invoke(parent.branch(), List.of()));
			}
		});
	}

	public MultiKeyBind getKeyBind() {
		return this.keyBind;
	}

	public void setFunction(ArucasFunction function) {
		this.function = function;
	}
}
