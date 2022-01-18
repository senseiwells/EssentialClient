package essentialclient.clientscript.values;

import essentialclient.clientscript.extensions.ArucasMinecraftExtension;
import essentialclient.utils.inventory.InventoryUtils;
import essentialclient.utils.render.FakeInventoryScreen;
import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.*;
import me.senseiwells.arucas.values.functions.BuiltInFunction;
import me.senseiwells.arucas.values.functions.ConstructorFunction;
import me.senseiwells.arucas.values.functions.FunctionValue;
import me.senseiwells.arucas.values.functions.MemberFunction;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.List;

public class FakeInventoryScreenValue extends ScreenValue {
    public FakeInventoryScreenValue(FakeInventoryScreen value) {
        super(value);
    }

    public FakeInventoryScreen getScreen() {
        return (FakeInventoryScreen) this.value;
    }

    @Override
    public ScreenValue copy(Context context) {
        return this;
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
				return new FakeInventoryScreenValue(new FakeInventoryScreen(player.inventory, context, stringValue.value, numberValue.value.intValue()));
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
				new MemberFunction("getStackForSlot", "slotNum", this::getStackForSlot),
				new MemberFunction("setCursorStack", "stack", this::setCursorStack)
			);
		}

		private Value<?> onClick(Context context, MemberFunction function) throws CodeError {
			FakeInventoryScreenValue fakeScreen = this.getFakeScreen(context, function);
			FunctionValue functionValue = function.getParameterValueOfType(context, FunctionValue.class, 1);
			fakeScreen.getScreen().setFunctionValue(functionValue);
			return NullValue.NULL;
		}

		private Value<?> setStackForSlot(Context context, MemberFunction function) throws CodeError {
			FakeInventoryScreenValue fakeScreen = this.getFakeScreen(context, function);
			NumberValue slot = function.getParameterValueOfType(context, NumberValue.class, 1);
			ItemStackValue stackValue = function.getParameterValueOfType(context, ItemStackValue.class, 2);
			fakeScreen.getScreen().setStack(slot.value.intValue(), stackValue.value);
			return NullValue.NULL;
		}

		private Value<?> getStackForSlot(Context context, MemberFunction function) throws CodeError {
			FakeInventoryScreenValue fakeScreen = this.getFakeScreen(context, function);
			NumberValue slot = function.getParameterValueOfType(context, NumberValue.class, 1);
			return new ItemStackValue(fakeScreen.getScreen().getScreenHandler().slots.get(slot.value.intValue()).getStack());
		}

		private Value<?> setCursorStack(Context context, MemberFunction function) throws CodeError {
			// In 1.17+ this will be done through the screen handler
			MinecraftClient client = ArucasMinecraftExtension.getClient();
			if (client.currentScreen instanceof FakeInventoryScreen) {
				ItemStack itemStack = function.getParameterValueOfType(context, ItemStackValue.class, 1).value;
				return BooleanValue.of(InventoryUtils.setCursorStack(client, itemStack));
			}
			return BooleanValue.FALSE;
		}

		private FakeInventoryScreenValue getFakeScreen(Context context, MemberFunction function) throws CodeError {
			return function.getParameterValueOfType(context, FakeInventoryScreenValue.class, 0);
		}

		@Override
		public Class<?> getValueClass() {
			return FakeInventoryScreenValue.class;
		}
	}
}
