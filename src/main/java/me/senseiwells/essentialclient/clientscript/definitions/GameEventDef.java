package me.senseiwells.essentialclient.clientscript.definitions;

import me.senseiwells.arucas.api.docs.ClassDoc;
import me.senseiwells.arucas.api.docs.ConstructorDoc;
import me.senseiwells.arucas.api.docs.FunctionDoc;
import me.senseiwells.arucas.builtin.BooleanDef;
import me.senseiwells.arucas.builtin.FunctionDef;
import me.senseiwells.arucas.classes.ClassInstance;
import me.senseiwells.arucas.classes.CreatableDefinition;
import me.senseiwells.arucas.core.Interpreter;
import me.senseiwells.arucas.exceptions.RuntimeError;
import me.senseiwells.arucas.utils.*;
import me.senseiwells.essentialclient.clientscript.events.CancelEvent;
import me.senseiwells.essentialclient.clientscript.events.MinecraftScriptEvent;
import me.senseiwells.essentialclient.clientscript.events.MinecraftScriptEvents;
import me.senseiwells.essentialclient.utils.clientscript.impl.ScriptEvent;
import shadow.kotlin.Unit;

import java.util.List;
import java.util.concurrent.Future;

import static me.senseiwells.arucas.utils.Util.Types.*;
import static me.senseiwells.essentialclient.clientscript.core.MinecraftAPI.GAME_EVENT;

@ClassDoc(
	name = GAME_EVENT,
	desc = "This class allows you to register listeners for game events in Minecraft.",
	importPath = "Minecraft",
	language = Util.Language.Java
)
public class GameEventDef extends CreatableDefinition<ScriptEvent> {
	public GameEventDef(Interpreter interpreter) {
		super(GAME_EVENT, interpreter);
	}

	@Override
	public List<ConstructorFunction> defineConstructors() {
		return List.of(
			ConstructorFunction.of(2, this::construct2),
			ConstructorFunction.of(3, this::construct3)
		);
	}

	@ConstructorDoc(
		desc = "This creates a new GameEvent, that is not cancellable",
		params = {
			STRING, "eventName", "The name of the event, you can find these on the GameEvents page",
			FUNCTION, "onEvent", "The function to run when the event is called, some events may have parameters"
		},
		examples = "new GameEvent('onClientTick', fun() { });"
	)
	private Unit construct2(Arguments arguments) {
		ClassInstance instance = arguments.next();
		String eventName = arguments.nextConstant();
		MinecraftScriptEvent event = MinecraftScriptEvents.getEvent(eventName);
		if (event == null) {
			throw new RuntimeError("No such event '%s'".formatted(eventName));
		}
		ClassInstance function = arguments.next(FunctionDef.class);
		ScriptEvent scriptEvent = new ScriptEvent(arguments.getInterpreter(), event, function, false);
		instance.setPrimitive(this, scriptEvent);
		scriptEvent.register();
		return null;
	}

	@ConstructorDoc(
		desc = "This creates a new GameEvent",
		params = {
			STRING, "eventName", "The name of the event, you can find these on the GameEvents page",
			FUNCTION, "onEvent", "The function to run when the event is called, some events may have parameters",
			BOOLEAN, "cancellable", "Whether or not the event is cancellable, if it is then it will run on the main thread"
		},
		examples = "new GameEvent('onClientTick', fun() { }, true);"
	)
	private Unit construct3(Arguments arguments) {
		ClassInstance instance = arguments.next();
		String eventName = arguments.nextConstant();
		MinecraftScriptEvent event = MinecraftScriptEvents.getEvent(eventName);
		if (event == null) {
			throw new RuntimeError("No such event '%s'".formatted(eventName));
		}
		ClassInstance function = arguments.next(FunctionDef.class);
		boolean cancellable = arguments.nextPrimitive(BooleanDef.class);
		ScriptEvent scriptEvent = new ScriptEvent(arguments.getInterpreter(), event, function, cancellable);
		instance.setPrimitive(this, scriptEvent);
		scriptEvent.register();
		return null;
	}

	@Override
	public List<BuiltInFunction> defineStaticMethods() {
		return List.of(
			BuiltInFunction.of("cancel", this::cancel),
			BuiltInFunction.of("unregisterAll", this::unregisterAll),
			BuiltInFunction.of("future", 1, this::future)
		);
	}

	@FunctionDoc(
		isStatic = true,
		name = "cancel",
		desc = {
			"If called on a cancellable event, this will stop execution and cancel the event,",
			"if called on a non-cancellable event, or not on an event, this will throw an error"
		},
		examples = "GameEvent.cancel();"
	)
	private Void cancel(Arguments arguments) {
		throw CancelEvent.INSTANCE;
	}

	@FunctionDoc(
		isStatic = true,
		name = "unregisterAll",
		desc = "This unregisters all events registered by this script",
		examples = "GameEvent.unregisterAll();"
	)
	private Void unregisterAll(Arguments arguments) {
		MinecraftScriptEvents.clearEventFunctions(arguments.getInterpreter().getProperties().getId());
		return null;
	}

	@FunctionDoc(
		isStatic = true,
		name = "future",
		desc = "This returns a future that allows you to wait for an event to occur",
		returns = {FUTURE, "the future, will complete once the event has occurred"},
		examples = "GameEvent.future('onClientTick').await();"
	)
	private Future<ClassInstance> future(Arguments arguments) {
		String eventName = arguments.nextConstant();
		MinecraftScriptEvent event = MinecraftScriptEvents.getEvent(eventName);
		if (event == null) {
			throw new RuntimeError("No such event '%s'".formatted(eventName));
		}
		return event.registerWaitingEvent(arguments.getInterpreter());
	}

	@Override
	public List<MemberFunction> defineMethods() {
		return List.of(
			MemberFunction.of("isRegistered", this::isRegistered),
			MemberFunction.of("register", this::register),
			MemberFunction.of("unregister", this::unregister)
		);
	}

	@FunctionDoc(
		name = "isRegistered",
		desc = "This returns whether or not the event is registered",
		returns = {BOOLEAN, "Whether or not the event is registered"},
		examples = "gameEvent.isRegistered();"
	)
	private boolean isRegistered(Arguments arguments) {
		return arguments.nextPrimitive(this).isRegistered();
	}

	@FunctionDoc(
		name = "register",
		desc = "This registers the event",
		examples = "gameEvent.register();"
	)
	private Void register(Arguments arguments) {
		arguments.nextPrimitive(this).register();
		return null;
	}

	@FunctionDoc(
		name = "unregister",
		desc = "This unregisters the event",
		examples = "gameEvent.unregister();"
	)
	private boolean unregister(Arguments arguments) {
		return arguments.nextPrimitive(this).unregister();
	}
}
