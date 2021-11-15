package essentialclient.clientscript.events;

import com.google.common.collect.Sets;
import essentialclient.clientscript.ClientScript;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.functions.FunctionValue;

import java.util.List;
import java.util.Set;

public class MinecraftScriptEvent {
	private final Set<EventFunction> functionValues = Sets.newHashSet();
	private int functionId = 0;

	public MinecraftScriptEvent(String eventName) {
		MinecraftScriptEvents.addEventToMap(eventName, this);
	}

	public synchronized int addFunction(Context context, FunctionValue functionValue) {
		this.functionId++;
		this.functionValues.add(new EventFunction(context, functionValue, this.functionId));
		return this.functionId;
	}

	public synchronized boolean removeFunction(int id) {
		return functionValues.removeIf(eventFunction -> eventFunction.id == id);
	}

	public synchronized void clearFunctions() {
		this.functionValues.clear();
	}

	public synchronized void run() {
		this.run(List.of());
	}

	public synchronized void run(Value<?> value) {
		this.run(List.of(value));
	}

	public synchronized void run(List<Value<?>> arguments) {
		this.functionValues.forEach(eventFunction -> {
			Context fileContext = eventFunction.context.createRootBranch();
			FunctionValue functionValue = eventFunction.functionValue;
			ClientScript.getInstance().runAsyncFunctionInContext(fileContext, context -> functionValue.call(context, arguments));
		});
	}

	private static record EventFunction(Context context, FunctionValue functionValue, int id) { }
}
