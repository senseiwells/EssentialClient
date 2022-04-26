package me.senseiwells.essentialclient.clientscript.extensions;

import me.senseiwells.arucas.api.wrappers.*;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.StringValue;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.classes.WrapperClassDefinition;
import me.senseiwells.arucas.values.classes.WrapperClassValue;
import me.senseiwells.arucas.values.functions.FunctionValue;
import me.senseiwells.essentialclient.feature.keybinds.ClientKeyBind;
import me.senseiwells.essentialclient.feature.keybinds.ClientKeyBinds;
import me.senseiwells.essentialclient.utils.keyboard.KeyboardHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
@ArucasClass(name = "KeyBind")
public class KeyBindWrapper implements IArucasWrappedClass {
	@ArucasDefinition
	public static WrapperClassDefinition DEFINITION;

	private ClientKeyBind keyBind;
	private Context callbackContext;
	@ArucasMember(assignable = false)
	public FunctionValue callback;

	@ArucasConstructor
	public void constructor(Context context, StringValue keyBindName) {
		this.keyBind = ClientKeyBinds.register(keyBindName.value, GLFW.GLFW_KEY_UNKNOWN, "Scripting Key Binds", this::onPressed);
	}

	@ArucasFunction
	public void setKey(Context context, StringValue keyName) {
		int keyCode = KeyboardHelper.translateStringToKey(keyName.value);
		this.keyBind.setBoundKey(InputUtil.fromKeyCode(keyCode, 0));
	}

	@ArucasFunction
	public Value<?> getKey(Context context) {
		return StringValue.of(KeyboardHelper.translateKeyToString(this.keyBind.getKeyCode()));
	}

	@ArucasFunction
	public void setCallback(Context context, FunctionValue functionValue) {
		this.callback = functionValue;
		this.callbackContext = context.createBranch();
	}

	private void onPressed(MinecraftClient client) {
		if (this.callback != null) {
			try {
				this.callback.call(this.callbackContext, new ArrayList<>());
			}
			catch (CodeError e) {
				this.callbackContext.getThreadHandler().tryError(this.callbackContext, e);
				this.callbackContext.getThreadHandler().stop();
			}
		}
	}

	@Override
	public Object asJavaValue() {
		return this.keyBind;
	}

	public static WrapperClassValue newKeyBindWrapper(ClientKeyBind keyBind, Context context) throws CodeError {
		KeyBindWrapper keyBindWrapper = new KeyBindWrapper();
		keyBindWrapper.keyBind = keyBind;
		return DEFINITION.createNewDefinition(keyBindWrapper, context, List.of());
	}
}
