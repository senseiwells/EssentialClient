package me.senseiwells.essentialclient.clientscript.definitions;

import me.senseiwells.arucas.api.docs.ClassDoc;
import me.senseiwells.arucas.api.docs.ConstructorDoc;
import me.senseiwells.arucas.api.docs.FunctionDoc;
import me.senseiwells.arucas.builtin.FunctionDef;
import me.senseiwells.arucas.builtin.NumberDef;
import me.senseiwells.arucas.builtin.TaskDef;
import me.senseiwells.arucas.classes.ClassInstance;
import me.senseiwells.arucas.classes.CreatableDefinition;
import me.senseiwells.arucas.classes.PrimitiveDefinition;
import me.senseiwells.arucas.core.Interpreter;
import me.senseiwells.arucas.exceptions.RuntimeError;
import me.senseiwells.arucas.utils.*;
import me.senseiwells.essentialclient.utils.clientscript.impl.ScriptTask;
import shadow.kotlin.Unit;

import java.util.List;
import java.util.concurrent.Future;

import static me.senseiwells.arucas.utils.Util.Types.*;
import static me.senseiwells.essentialclient.clientscript.core.MinecraftAPI.MINECRAFT_TASK;

@ClassDoc(
	name = MINECRAFT_TASK,
	desc = {
		"This class is used to create tasks that can be chained and",
		"run on the main Minecraft thread. This ensures that all",
		"behaviors work as intended."
	},
	superclass = TaskDef.class,
	language = Util.Language.Java
)
public class MinecraftTaskDef extends CreatableDefinition<ScriptTask> {
	public MinecraftTaskDef(Interpreter interpreter) {
		super(MINECRAFT_TASK, interpreter);
	}

	@Override
	public PrimitiveDefinition<? super ScriptTask> superclass() {
		return this.getPrimitiveDef(TaskDef.class);
	}

	@Override
	public List<ConstructorFunction> defineConstructors() {
		return List.of(
			ConstructorFunction.of(this::construct)
		);
	}

	@ConstructorDoc(
		desc = "This creates a new empty Minecraft task",
		examples = "task = new MinecraftTask();"
	)
	private Unit construct(Arguments arguments) {
		ClassInstance instance = arguments.next();
		instance.setPrimitive(this, new ScriptTask(arguments.getInterpreter()));
		return null;
	}

	@Override
	public List<MemberFunction> defineMethods() {
		return List.of(
			MemberFunction.of("waitThen", 2, this::waitThen),
			MemberFunction.of("run", this::run)
		);
	}

	@FunctionDoc(
		name = "waitThen",
		desc = {
			"This adds a delay (in ticks) then runs the given task.",
			"This delay is will also affect all following chained function",
			"delays. If this is the last function in the chain, then the",
			"return value will be determined by this function"
		},
		params = {
			NUMBER, "ticks", "the amount of ticks delay before the function runs",
			FUNCTION, "function", "the function to run after the delay"
		},
		returns = {MINECRAFT_TASK, "the task, this allows for chaining"},
		examples =
			"""
				task = new MinecraftTask()
					.then(fun() print("hello"))
					.waitThen(5, fun() print("world"));
				task.run(); // prints 'hello', waits 5 ticks, prints 'world'
				"""
	)
	private ClassInstance waitThen(Arguments arguments) {
		ClassInstance instance = arguments.next();
		int ticks = arguments.nextPrimitive(NumberDef.class).intValue();
		if (ticks < 0) {
			throw new RuntimeError("Cannot have a negative delay");
		}
		ArucasFunction function = arguments.nextPrimitive(FunctionDef.class);
		instance.asPrimitive(this).addTask(ticks, function);
		return instance;
	}
	
	@FunctionDoc(
		name = "waitLoopIf",
		desc = {
			"This loops waits a certain number of ticks before running the function,",
			"if the function returns true then the task will call 'task.run' on itself"
		},
		params = {
			NUMBER, "ticks", "the number of ticks to wait",
			FUNCTION, "boolSupplier", "whether the task should be looped"
		},
		returns = {MINECRAFT_TASK, "the task, this allows for chaining"},
		examples =
			"""
				task = new MinecraftTask()
					.then(fun() print("hello"))
					.waitThen(5, fun() print("world"))
					.waitLoopIf(5, fun() true); // Waits 5 ticks then loops
				"""
	)
	private ClassInstance waitLoopIf(Arguments arguments) {
		ClassInstance instance = arguments.next();
		int ticks = arguments.nextPrimitive(NumberDef.class).intValue();
		if (ticks < 0) {
			throw new RuntimeError("Cannot have a negative delay");
		}
		ArucasFunction supplier = arguments.nextPrimitive(FunctionDef.class);
		MinecraftTask task = instance.asPrimitive(this);
		task.addTask(ticks, BuiltInFunction.of("$lambda", args -> {
			Boolean shouldRun = supplier.invoke(it.interpreter, listOf()).getPrimitive(BooleanDef::class)
			if (shouldRun == null) {
				throw new RuntimeError("'loopIf' check should return type 'Boolean'");
			}
			if (shouldRun) {
				task.run()
			}
		});
		return instance;
	}

	@FunctionDoc(
		name = "run",
		desc = {
			"This runs the task on the main Minecraft thread. It returns a future",
			"which can be awaited, the last function in the chain will be used as",
			"the return value for the future"
		},
		returns = {FUTURE, "the future value that can be awaited"},
		examples =
			"""
				task = new MinecraftTask()
					.then(fun() print("hello"))
					.then(fun() print(" "))
					.then(fun() print("world"))
					.then(fun() 10);
				f = task.run(); // prints 'hello world'
				print(f.await()); // prints 10
				"""
	)
	private Future<ClassInstance> run(Arguments arguments) {
		return arguments.nextPrimitive(this).run();
	}
}
