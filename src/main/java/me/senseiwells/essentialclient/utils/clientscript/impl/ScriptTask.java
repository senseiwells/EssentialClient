package me.senseiwells.essentialclient.utils.clientscript.impl;

import me.senseiwells.arucas.classes.instance.ClassInstance;
import me.senseiwells.arucas.core.Interpreter;
import me.senseiwells.arucas.utils.impl.DelayedFunction;
import me.senseiwells.arucas.utils.impl.Task;
import me.senseiwells.essentialclient.utils.misc.Scheduler;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.function.Supplier;

public class ScriptTask extends Task {
	public ScriptTask(Interpreter interpreter) {
		super(interpreter);
	}

	@NotNull
	@Override
	public Future<ClassInstance> run(Iterator<DelayedFunction> tasks) {
		int totalTicks = 0;
		while (tasks.hasNext()) {
			DelayedFunction next = tasks.next();
			if (!tasks.hasNext()) {
				return Scheduler.schedule(totalTicks + next.getTime(), () -> {
					Interpreter interpreter = this.getInterpreter();
					return interpreter.runSafe((Supplier<ClassInstance>) () -> {
						if (interpreter.isRunning()) {
							return next.getFunction().invoke(interpreter.branch(), List.of());
						}
						return interpreter.getNull();
					});
				});
			}
			totalTicks += next.getTime();
			Scheduler.schedule(totalTicks, () -> {
				Interpreter interpreter = this.getInterpreter();
				interpreter.runSafe((Supplier<Object>) () -> {
					if (interpreter.isRunning()) {
						next.getFunction().invoke(interpreter.branch(), List.of());
					}
					return null;
				});
			});
		}
		return CompletableFuture.completedFuture(this.getInterpreter().getNull());
	}
}
