package me.senseiwells.essentialclient.clientscript.values;

import me.senseiwells.essentialclient.clientscript.extensions.ArucasMinecraftExtension;
import me.senseiwells.essentialclient.utils.render.FakeInventoryScreen;
import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.NullValue;
import me.senseiwells.arucas.values.NumberValue;
import me.senseiwells.arucas.values.StringValue;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.functions.BuiltInFunction;
import me.senseiwells.arucas.values.functions.ConstructorFunction;
import me.senseiwells.arucas.values.functions.FunctionValue;
import me.senseiwells.arucas.values.functions.MemberFunction;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.List;

public class FakeInventoryScreenValue extends ScreenValue<FakeInventoryScreen> {
    public FakeInventoryScreenValue(FakeInventoryScreen value) {
        super(value);
    }

	@Override
	public String getAsString(Context context) {
		return "FakeInventoryScreen@" + this.getHashCode(context);
	}

	@Override
    public ScreenValue<FakeInventoryScreen> copy(Context context) {
        return this;
    }

	@Override
	public String getTypeName() {
		return "FakeScreen";
	}

	/**
	 * FakeScreen class for Arucas. This class extends Screen and so inherits all
	 * of their methods too, this class is used to create client side inventory screens. <br>
	 * Import the class with <code>import FakeScreen from Minecraft;</code> <br>
	 * Fully Documented.
	 * @author senseiwells
	 */
	public static class ArucasFakeInventoryScreenClass extends ArucasClassExtension {
		public ArucasFakeInventoryScreenClass() {
			super("FakeScreen");
		}

		@Override
		public ArucasFunctionMap<ConstructorFunction> getDefinedConstructors() {
			return ArucasFunctionMap.of(
				new ConstructorFunction(List.of("name", "rows"), this::newFakeScreen)
			);
		}

		/**
		 * Name: <code>new FakeScreen(name, rows)</code> <br>
		 * Description: Creates a FakeScreen instance with given name and given amount of rows <br>
		 * Parameter - String, Number: the name of the screen, the number of rows between 1 - 6 <br>
		 * Returns - FakeScreen: the new FakeScreen object <br>
		 * Throws - Error: <code>"..."</code> if the parameters are invalid <br>
		 * Example: <code>new FakeScreen("MyScreen", 6);</code>
		 */
		private Value<?> newFakeScreen(Context context, BuiltInFunction function) throws CodeError {
			ClientPlayerEntity player = ArucasMinecraftExtension.getPlayer();
			StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 0);
			NumberValue numberValue = function.getParameterValueOfType(context, NumberValue.class, 1);
			try {
				return new FakeInventoryScreenValue(new FakeInventoryScreen(player.getInventory(), stringValue.value, numberValue.value.intValue()));
			}
			catch (IllegalArgumentException e) {
				throw new RuntimeError(e.getMessage(), function.syntaxPosition, context);
			}
		}

		@Override
		public ArucasFunctionMap<MemberFunction> getDefinedMethods() {
			return ArucasFunctionMap.of(
				new MemberFunction("onClick", "function", this::onClick),
				new MemberFunction("setStackForSlot", List.of("slotNum", "stack"), this::setStackForSlot),
				new MemberFunction("getStackForSlot", "slotNum", this::getStackForSlot)
			);
		}

		/**
		 * Name: <code>&lt;FakeScreen>.onClick(function)</code> <br>
		 * Description: This sets the callback for when a slot is clicked in the inventory <br>
		 * Parameter - Function: the callback function which must have 3 parameters, which will be passed in
		 * when it is called, item, slotNum, action, being ItemStack, Number, and String respectively <br>
		 * Example: <code>fakeScreen.onClick(fun(i, s, a) { print(a); });</code>
		 */
		private Value<?> onClick(Context context, MemberFunction function) throws CodeError {
			FakeInventoryScreenValue fakeScreen = this.getFakeScreen(context, function);
			FunctionValue functionValue = function.getParameterValueOfType(context, FunctionValue.class, 1);
			fakeScreen.value.setFunctionValue(context, functionValue);
			return NullValue.NULL;
		}

		/**
		 * Name: <code>&lt;FakeScreen>.setStackForSlot(slotNum, stack)</code> <br>
		 * Description: Sets the stack for the given slot, if the slot is out of bounds it won't be set <br>
		 * Parameter - Number, ItemStack: the slot number, the stack to set <br>
		 * Example: <code>fakeScreen.setStackForSlot(0, Material.DIAMOND_BLOCK.asItemStack());</code>
		 */
		private Value<?> setStackForSlot(Context context, MemberFunction function) throws CodeError {
			FakeInventoryScreenValue fakeScreen = this.getFakeScreen(context, function);
			NumberValue slot = function.getParameterValueOfType(context, NumberValue.class, 1);
			ItemStackValue stackValue = function.getParameterValueOfType(context, ItemStackValue.class, 2);
			fakeScreen.value.setStack(slot.value.intValue(), stackValue.value);
			return NullValue.NULL;
		}

		/**
		 * Name: <code>&lt;FakeScreen>.getStackForSlot(slotNum)</code> <br>
		 * Description: Gets the stack for the given slot, if the slot is out of bounds it returns null <br>
		 * Parameter - Number: the slot number <br>
		 * Returns - ItemStack: the stack for the given slot <br>
		 * Example: <code>fakeScreen.getStackForSlot(0);</code>
		 */
		private Value<?> getStackForSlot(Context context, MemberFunction function) throws CodeError {
			FakeInventoryScreenValue fakeScreen = this.getFakeScreen(context, function);
			NumberValue slot = function.getParameterValueOfType(context, NumberValue.class, 1);
			ItemStack stack = fakeScreen.value.getStack(slot.value.intValue());
			return stack == null ? NullValue.NULL : new ItemStackValue(stack);
		}

		private FakeInventoryScreenValue getFakeScreen(Context context, MemberFunction function) throws CodeError {
			return function.getParameterValueOfType(context, FakeInventoryScreenValue.class, 0);
		}

		@Override
		public Class<FakeInventoryScreenValue> getValueClass() {
			return FakeInventoryScreenValue.class;
		}
	}
}
