package me.senseiwells.essentialclient.utils.clientscript.impl;

import me.senseiwells.arucas.builtin.FunctionDef;
import me.senseiwells.arucas.classes.instance.ClassInstance;
import me.senseiwells.arucas.compiler.Trace;
import me.senseiwells.arucas.interpreter.Interpreter;
import me.senseiwells.essentialclient.clientscript.events.CancelEvent;
import me.senseiwells.essentialclient.clientscript.events.MinecraftScriptEvent;
import me.senseiwells.essentialclient.utils.EssentialUtils;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public class ScriptEvent {
	private final Interpreter interpreter;
	private final MinecraftScriptEvent event;
	private final ClassInstance function;
	private final boolean cancellable;

	public ScriptEvent(Interpreter interpreter, MinecraftScriptEvent event, ClassInstance function, boolean cancellable) {
		this.interpreter = interpreter.branch();
		this.event = event;
		this.function = function;
		this.cancellable = cancellable;
	}

	public UUID getId() {
		return this.interpreter.getProperties().getId();
	}

	public Interpreter getInterpreter() {
		return this.interpreter.branch();
	}

	public boolean isRegistered() {
		return this.event.isEventRegistered(this);
	}

	public void register() {
		this.event.registerEvent(this);
	}

	public boolean unregister() {
		return this.event.unregisterEvent(this);
	}

	public boolean invoke(List<ClassInstance> arguments) {
		Interpreter branch = this.interpreter.branch();
		int count = this.function.asPrimitive(FunctionDef.class).getCount();
		List<ClassInstance> newArgs = count >= 0 && count < arguments.size() ? arguments.subList(0, count) : arguments;

		if (this.event.isThreadDefinable() && !this.cancellable && EssentialUtils.getClient().isOnThread()) {
			branch.runAsync(() -> {
				branch.call(this.function, newArgs, Trace.INTERNAL);
				return null;
			});
			return false;
		}
		return branch.runSafe(false, (Supplier<Boolean>) () -> {
			try {
				branch.call(this.function, newArgs, Trace.INTERNAL);
				return Boolean.FALSE;
			} catch (CancelEvent cancelEvent) {
				if (this.event.canCancel() && EssentialUtils.getClient().isOnThread()) {
					return Boolean.TRUE;
				}
				throw cancelEvent;
			}
		});
	}
}
