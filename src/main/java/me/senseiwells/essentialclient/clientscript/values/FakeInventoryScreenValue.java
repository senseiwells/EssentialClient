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

		private Value<?> onClick(Context context, MemberFunction function) throws CodeError {
			FakeInventoryScreenValue fakeScreen = this.getFakeScreen(context, function);
			FunctionValue functionValue = function.getParameterValueOfType(context, FunctionValue.class, 1);
			fakeScreen.value.setFunctionValue(context, functionValue);
			return NullValue.NULL;
		}

		private Value<?> setStackForSlot(Context context, MemberFunction function) throws CodeError {
			FakeInventoryScreenValue fakeScreen = this.getFakeScreen(context, function);
			NumberValue slot = function.getParameterValueOfType(context, NumberValue.class, 1);
			ItemStackValue stackValue = function.getParameterValueOfType(context, ItemStackValue.class, 2);
			fakeScreen.value.setStack(slot.value.intValue(), stackValue.value);
			return NullValue.NULL;
		}

		private Value<?> getStackForSlot(Context context, MemberFunction function) throws CodeError {
			FakeInventoryScreenValue fakeScreen = this.getFakeScreen(context, function);
			NumberValue slot = function.getParameterValueOfType(context, NumberValue.class, 1);
			return new ItemStackValue(fakeScreen.value.getScreenHandler().slots.get(slot.value.intValue()).getStack());
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
