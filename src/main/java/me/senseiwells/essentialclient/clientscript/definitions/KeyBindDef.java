package me.senseiwells.essentialclient.clientscript.definitions;

import kotlin.Unit;
import me.senseiwells.arucas.api.docs.annotations.*;
import me.senseiwells.arucas.builtin.FunctionDef;
import me.senseiwells.arucas.builtin.ListDef;
import me.senseiwells.arucas.builtin.StringDef;
import me.senseiwells.arucas.classes.CreatableDefinition;
import me.senseiwells.arucas.classes.instance.ClassInstance;
import me.senseiwells.arucas.core.Interpreter;
import me.senseiwells.arucas.exceptions.RuntimeError;
import me.senseiwells.arucas.utils.Arguments;
import me.senseiwells.arucas.utils.ConstructorFunction;
import me.senseiwells.arucas.utils.MemberFunction;
import me.senseiwells.arucas.utils.Util;
import me.senseiwells.arucas.utils.impl.ArucasList;
import me.senseiwells.essentialclient.feature.keybinds.MultiKeyBind;
import me.senseiwells.essentialclient.utils.clientscript.impl.ScriptKeyBind;
import me.senseiwells.essentialclient.utils.keyboard.KeyboardHelper;
import net.minecraft.client.util.InputUtil;

import java.util.List;

import static me.senseiwells.essentialclient.clientscript.core.MinecraftAPI.KEY_BIND;

@ClassDoc(
	name = KEY_BIND,
	desc = {
		"This class allows you to create key binds that can be used, everything is",
		"handled for you internally so you just need to regers the key bind and",
		"the function you want to run when it is pressed."
	},
	language = Util.Language.Java
)
public class KeyBindDef extends CreatableDefinition<ScriptKeyBind> {
	public KeyBindDef(Interpreter interpreter) {
		super(KEY_BIND, interpreter);
	}

	@Override
	public Object asJavaValue(ClassInstance instance) {
		return instance.asPrimitive(this).getKeyBind();
	}

	@Override
	public List<ConstructorFunction> defineConstructors() {
		return List.of(
			ConstructorFunction.of(1, this::construct)
		);
	}

	@ConstructorDoc(
		desc = "Creates a new key bind",
		params = {@ParameterDoc(type = StringDef.class, name = "keyName", desc = "the name of the key")},
		examples = "new KeyBind('MyKey');"
	)
	private Unit construct(Arguments arguments) {
		ClassInstance instance = arguments.next();
		String keyName = arguments.nextPrimitive(StringDef.class);
		instance.setPrimitive(this, new ScriptKeyBind(arguments.getInterpreter(), keyName));
		return null;
	}

	@Override
	public List<MemberFunction> defineMethods() {
		return List.of(
			MemberFunction.of("setKey", 1, this::setKey),
			MemberFunction.arb("setKeys", this::setKeys),
			MemberFunction.of("getKey", this::getKey),
			MemberFunction.of("getKeys", this::getKeys),
			MemberFunction.of("setCallback", 1, this::setCallback)
		);
	}

	@FunctionDoc(
		name = "setKey",
		desc = "Sets the key bind to a new key",
		params = {@ParameterDoc(type = StringDef.class, name = "keyName", desc = "the name of the key")},
		examples = "keyBind.setKey('f');"
	)
	private Void setKey(Arguments arguments) {
		MultiKeyBind keyBind = arguments.nextPrimitive(this).getKeyBind();
		int keyCode = KeyboardHelper.translateStringToKey(arguments.nextPrimitive(StringDef.class));
		keyBind.clearKey();
		keyBind.addKeys(keyCode);
		return null;
	}

	@FunctionDoc(
		name = "setKeys",
		desc = {
			"Sets the key bind to new keys, you may also pass",
			"in a list as the parameter, this is to keep compatability"
		},
		params = @ParameterDoc(type = StringDef.class, name = "keyNames...", desc = "the names of keys", isVarargs = true),
		examples = "keyBind.setKeys('control', 'f');"
	)
	private Void setKeys(Arguments arguments) {
		MultiKeyBind keyBind = arguments.nextPrimitive(this).getKeyBind();
		List<ClassInstance> keys;
		if (arguments.size() == 2 && arguments.isNext(ListDef.class)) {
			keys = arguments.nextPrimitive(ListDef.class);
		} else {
			keys = arguments.getRemaining();
		}
		int[] inputKeys = new int[keys.size()];
		for (int i = 0; i < keys.size(); i++) {
			String keyAsString = keys.get(i).toString(arguments.getInterpreter());
			int key = KeyboardHelper.translateStringToKey(keyAsString);
			if (key < 0) {
				throw new RuntimeError("Invalid key: " + keyAsString);
			}
			inputKeys[i] = key;
		}
		keyBind.clearKey();
		keyBind.addKeys(inputKeys);
		return null;
	}

	@FunctionDoc(
		name = "getKey",
		desc = "Gets the key bind's first key",
		returns = @ReturnDoc(type = StringDef.class, desc = "the key bind's key"),
		examples = "keyBind.getKey();"
	)
	private String getKey(Arguments arguments) {
		MultiKeyBind keyBind = arguments.nextPrimitive(this).getKeyBind();
		return KeyboardHelper.translateKeyToString(keyBind.getFirstKey().getCode());
	}

	@FunctionDoc(
		name = "getKeys",
		desc = "Gets the all of the keys in the key bind",
		returns = @ReturnDoc(type = ListDef.class, desc = "list of strings of all the keys"),
		examples = "keybind.getKeys();"
	)
	private ArucasList getKeys(Arguments arguments) {
		MultiKeyBind keyBind = arguments.nextPrimitive(this).getKeyBind();
		ArucasList list = new ArucasList();
		for (InputUtil.Key key : keyBind.getKeys()) {
			list.add(arguments.getInterpreter().create(StringDef.class, KeyboardHelper.translateKeyToString(key.getCode())));
		}
		return list;
	}

	@FunctionDoc(
		name = "setCallback",
		desc = "Sets the callback function for the key bind",
		params = {@ParameterDoc(type = FunctionDef.class, name = "callback", desc = "the callback function")},
		examples = "keyBind.setCallback(fun() { print('My key was pressed'); });"
	)
	private Void setCallback(Arguments arguments) {
		ScriptKeyBind keyBind = arguments.nextPrimitive(this);
		keyBind.setFunction(arguments.nextPrimitive(FunctionDef.class));
		return null;
	}
}
