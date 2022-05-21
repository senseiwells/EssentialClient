package me.senseiwells.essentialclient.clientscript.values;

import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.api.docs.ClassDoc;
import me.senseiwells.arucas.api.docs.ConstructorDoc;
import me.senseiwells.arucas.api.docs.FunctionDoc;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.Arguments;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.NullValue;
import me.senseiwells.arucas.values.NumberValue;
import me.senseiwells.arucas.values.StringValue;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.functions.ConstructorFunction;
import me.senseiwells.arucas.values.functions.FunctionValue;
import me.senseiwells.arucas.values.functions.MemberFunction;
import me.senseiwells.essentialclient.clientscript.extensions.ArucasMinecraftExtension;
import me.senseiwells.essentialclient.utils.render.FakeInventoryScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;

import static me.senseiwells.essentialclient.clientscript.core.MinecraftAPI.*;

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
		return FAKE_SCREEN;
	}

	@ClassDoc(
		name = FAKE_SCREEN,
		desc = {
			"This class extends Screen and so inherits all of their methods too,",
			"this class is used to create client side inventory screens."
		},
		importPath = "Minecraft"
	)
	public static class ArucasFakeInventoryScreenClass extends ArucasClassExtension {
		public ArucasFakeInventoryScreenClass() {
			super(FAKE_SCREEN);
		}

		@Override
		public ArucasFunctionMap<ConstructorFunction> getDefinedConstructors() {
			return ArucasFunctionMap.of(
				ConstructorFunction.of(2, this::newFakeScreen)
			);
		}

		@ConstructorDoc(
			desc = "Creates a FakeScreen instance with given name and given amount of rows",
			params = {
				STRING, "name", "the name of the screen",
				NUMBER, "rows", "the number of rows between 1 - 6"
			},
			example = "new FakeScreen('MyScreen', 6);"
		)
		private Value newFakeScreen(Arguments arguments) throws CodeError {
			ClientPlayerEntity player = ArucasMinecraftExtension.getPlayer();
			StringValue stringValue = arguments.getNextString();
			NumberValue numberValue = arguments.getNextNumber();
			try {
				return new FakeInventoryScreenValue(new FakeInventoryScreen(player.getInventory(), stringValue.value, numberValue.value.intValue()));
			}
			catch (IllegalArgumentException e) {
				throw arguments.getError(e.getMessage());
			}
		}

		@Override
		public ArucasFunctionMap<MemberFunction> getDefinedMethods() {
			return ArucasFunctionMap.of(
				MemberFunction.of("onClick", 1, this::onClick),
				MemberFunction.of("setStackForSlot", 2, this::setStackForSlot),
				MemberFunction.of("getStackForSlot", 1, this::getStackForSlot)
			);
		}

		@FunctionDoc(
			name = "onClick",
			desc = "This sets the callback for when a slot is clicked in the inventory",
			params = {
				FUNCTION, "function", "the callback function which must have 3 parameters, which will be passed " +
				"in when it is called, item, slotNum, action, being ItemStack, Number, and String respectively"
			},
			example = """
			fakeScreen.onClick(fun(item, slotNum, action) {
			    // action can be any of the following:
			    // 'PICKUP', 'QUICK_MOVE', 'SWAP', 'CLONE', 'THROW', 'QUICK_CRAFT', or 'PICKUP_ALL'
			    print(action);
			});
			"""
		)
		private Value onClick(Arguments arguments) throws CodeError {
			FakeInventoryScreenValue fakeScreen = arguments.getNext(FakeInventoryScreenValue.class);
			FunctionValue functionValue = arguments.getNextFunction();
			fakeScreen.value.setFunctionValue(arguments.getContext(), functionValue);
			return NullValue.NULL;
		}

		@FunctionDoc(
			name = "setStackForSlot",
			desc = "Sets the stack for the given slot, if the slot is out of bounds it won't be set",
			params = {
				NUMBER, "slotNum", "the slot number",
				ITEM_STACK, "stack", "the stack to set"
			},
			example = "fakeScreen.setStackForSlot(0, Material.DIAMOND_BLOCK.asItemStack());"
		)
		private Value setStackForSlot(Arguments arguments) throws CodeError {
			FakeInventoryScreenValue fakeScreen = arguments.getNext(FakeInventoryScreenValue.class);
			NumberValue slot = arguments.getNextNumber();
			ItemStackValue stackValue = arguments.getNext(ItemStackValue.class);
			fakeScreen.value.setStack(slot.value.intValue(), stackValue.value);
			return NullValue.NULL;
		}

		@FunctionDoc(
			name = "getStackForSlot",
			desc = "Gets the stack for the given slot, if the slot is out of bounds it returns null",
			params = {NUMBER, "slotNum", "the slot number"},
			returns = {ITEM_STACK, "the stack for the given slot"},
			example = "fakeScreen.getStackForSlot(0);"
		)
		private Value getStackForSlot(Arguments arguments) throws CodeError {
			FakeInventoryScreenValue fakeScreen = arguments.getNext(FakeInventoryScreenValue.class);
			NumberValue slot = arguments.getNextNumber();
			ItemStack stack = fakeScreen.value.getStack(slot.value.intValue());
			return stack == null ? NullValue.NULL : new ItemStackValue(stack);
		}

		@Override
		public Class<FakeInventoryScreenValue> getValueClass() {
			return FakeInventoryScreenValue.class;
		}
	}
}
