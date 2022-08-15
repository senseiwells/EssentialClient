package me.senseiwells.essentialclient.clientscript.extensions;

import me.senseiwells.arucas.api.docs.ClassDoc;
import me.senseiwells.arucas.api.docs.ConstructorDoc;
import me.senseiwells.arucas.api.docs.FunctionDoc;
import me.senseiwells.arucas.api.docs.MemberDoc;
import me.senseiwells.arucas.api.wrappers.*;
import me.senseiwells.arucas.throwables.BuiltInException;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.impl.ArucasList;
import me.senseiwells.arucas.values.ListValue;
import me.senseiwells.arucas.values.StringValue;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.classes.WrapperClassDefinition;
import me.senseiwells.arucas.values.classes.WrapperClassValue;
import me.senseiwells.arucas.values.functions.FunctionValue;
import me.senseiwells.essentialclient.feature.keybinds.ClientKeyBinds;
import me.senseiwells.essentialclient.feature.keybinds.MultiKeyBind;
import me.senseiwells.essentialclient.utils.keyboard.KeyboardHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;

import java.util.ArrayList;
import java.util.List;

import static me.senseiwells.arucas.utils.ValueTypes.*;
import static me.senseiwells.essentialclient.clientscript.core.MinecraftAPI.KEY_BIND;

@SuppressWarnings("unused")
@ClassDoc(
	name = KEY_BIND,
	desc = {
		"This class allows you to create key binds that can be used, everything is",
		"handled for you internally so you just need to regers the key bind and",
		"the function you want to run when it is pressed."
	}
)
@ArucasClass(name = KEY_BIND)
public class KeyBindWrapper implements IArucasWrappedClass {
	@ArucasDefinition
	public static WrapperClassDefinition DEFINITION; // Checkstyle ignore

	private MultiKeyBind keyBind;
	private Context callbackContext;

	@MemberDoc(
		name = "callback",
		desc = "The callback function that is called when the key bind is pressed",
		type = FUNCTION,
		examples = "keyBind.callback;"
	)
	@ArucasMember(assignable = false)
	public FunctionValue callback;

	@ConstructorDoc(
		desc = "Creates a new key bind",
		params = {STRING, "keyName", "the name of the key"},
		examples = "new KeyBind('MyKey');"
	)
	@ArucasConstructor
	public void constructor(Context context, StringValue keyBindName) {
		this.keyBind = ClientKeyBinds.registerMulti(keyBindName.value, "Scripting Key Binds", this::onPressed);
	}

	@FunctionDoc(
		name = "setKey",
		desc = "Sets the key bind to a new key",
		params = {STRING, "keyName", "the name of the key"},
		examples = "keyBind.setKey('f');"
	)
	@ArucasFunction
	public void setKey(Context context, StringValue keyName) {
		int keyCode = KeyboardHelper.translateStringToKey(keyName.value);
		this.keyBind.clearKey();
		this.keyBind.addKeys(keyCode);
	}

	@FunctionDoc(
		name = "setKeys",
		desc = "Sets the key bind to new keys",
		params = {LIST, "keyNames", "the names of keys"},
		examples = "keyBind.setKeys(['control', 'f']);"
	)
	@ArucasFunction
	public void setKeys(Context context, ListValue listValue) {
		ArucasList list = listValue.value;
		int[] inputKeys = new int[list.size()];
		for (int i = 0; i < inputKeys.length; i++) {
			String keyAsString = list.get(i).getAsString(context);
			int key = KeyboardHelper.translateStringToKey(keyAsString);
			if (key < 0) {
				throw new BuiltInException("Unknown key '%s'".formatted(keyAsString));
			}
			inputKeys[i] = key;
		}

		this.keyBind.clearKey();
		this.keyBind.addKeys(inputKeys);
	}

	@FunctionDoc(
		name = "getKey",
		desc = "Gets the key bind's first key",
		returns = {STRING, "the key bind's key"},
		examples = "keyBind.getKey();"
	)
	@ArucasFunction
	public Value getKey(Context context) {
		return StringValue.of(KeyboardHelper.translateKeyToString(this.keyBind.getFirstKey().getCode()));
	}

	@FunctionDoc(
		name = "getKeys",
		desc = "Gets the all of the keys in the key bind",
		returns = {LIST, "list of strings of all the keys"},
		examples = "keybind.getKeys();"
	)
	@ArucasFunction
	public Value getKeys(Context context) {
		ArucasList list = new ArucasList();
		for (InputUtil.Key key : this.keyBind.getKeys()) {
			list.add(StringValue.of(KeyboardHelper.translateKeyToString(key.getCode())));
		}
		return new ListValue(list);
	}

	@FunctionDoc(
		name = "setCallback",
		desc = "Sets the callback function for the key bind",
		params = {FUNCTION, "callback", "the callback function"},
		examples = "keyBind.setCallback(fun() { print('My key was pressed'); });"
	)
	@ArucasFunction
	public void setCallback(Context context, FunctionValue functionValue) {
		this.callback = functionValue;
		this.callbackContext = context;
	}

	private void onPressed(MinecraftClient client) {
		if (this.callback != null) {
			this.callbackContext.getThreadHandler().runAsyncFunctionInThreadPool(this.callbackContext.createBranch(), context -> {
				this.callback.call(context, new ArrayList<>());
			});
		}
	}

	@Override
	public Object asJavaValue() {
		return this.keyBind;
	}

	public static WrapperClassValue newKeyBindWrapper(MultiKeyBind keyBind, Context context) {
		KeyBindWrapper keyBindWrapper = new KeyBindWrapper();
		keyBindWrapper.keyBind = keyBind;
		return DEFINITION.createNewDefinition(keyBindWrapper, context, List.of());
	}
}
