package me.senseiwells.essentialclient.utils.clientscript.impl;

import kotlin.Unit;
import me.senseiwells.arucas.classes.ClassInstance;
import me.senseiwells.arucas.core.Interpreter;
import me.senseiwells.arucas.utils.ArucasFunction;
import me.senseiwells.arucas.utils.impl.Task;
import me.senseiwells.essentialclient.utils.clientscript.ClientScriptUtils;
import me.senseiwells.essentialclient.utils.misc.Scheduler;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;

public class ScriptTask extends Task {
	private final ConcurrentLinkedQueue<TickedFunction> tasks = new ConcurrentLinkedQueue<>();
	private boolean hasRun;

	public ScriptTask(Interpreter interpreter) {
		super(interpreter);
	}

	public void addTask(int ticks, ArucasFunction function) {
		this.tasks.add(new TickedFunction(ticks, function));
	}

	@Override
	public void addTask(ArucasFunction arucasFunction) {
		this.addTask(0, arucasFunction);
	}

	@Override
	public boolean canModify() {
		return this.hasRun;
	}

	@Override
	public Future<ClassInstance> run() {
		this.hasRun = true;
		Iterator<TickedFunction> iterator = this.tasks.iterator();
		int totalTicks = 0;
		while (iterator.hasNext()) {
			TickedFunction next = iterator.next();
			if (!iterator.hasNext()) {
				return Scheduler.schedule(totalTicks + next.ticks, () -> {
					Interpreter interpreter = this.getInterpreter();
					return ClientScriptUtils.wrapSafe(() -> {
						if (interpreter.getThreadHandler().getRunning()) {
							return next.function.invoke(interpreter.branch(), List.of());
						}
						return interpreter.getNull();
					}, interpreter);
				});
			}
			totalTicks += next.ticks;
			Scheduler.schedule(totalTicks, () -> {
				Interpreter interpreter = this.getInterpreter();
				ClientScriptUtils.wrapSafe(() -> {
					if (interpreter.getThreadHandler().getRunning()) {
						next.function.invoke(interpreter.branch(), List.of());
					}
				}, interpreter);
			});
		}
		return CompletableFuture.completedFuture(this.getInterpreter().getNull());
	}

	private record TickedFunction(int ticks, ArucasFunction function) { }
}
