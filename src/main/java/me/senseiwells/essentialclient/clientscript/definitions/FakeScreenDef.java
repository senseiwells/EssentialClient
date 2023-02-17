package me.senseiwells.essentialclient.clientscript.definitions;

import kotlin.Unit;
import me.senseiwells.arucas.api.docs.annotations.*;
import me.senseiwells.arucas.builtin.FunctionDef;
import me.senseiwells.arucas.builtin.NumberDef;
import me.senseiwells.arucas.builtin.StringDef;
import me.senseiwells.arucas.classes.CreatableDefinition;
import me.senseiwells.arucas.classes.PrimitiveDefinition;
import me.senseiwells.arucas.classes.instance.ClassInstance;
import me.senseiwells.arucas.core.Interpreter;
import me.senseiwells.arucas.exceptions.RuntimeError;
import me.senseiwells.arucas.utils.*;
import me.senseiwells.essentialclient.clientscript.core.MinecraftAPI;
import me.senseiwells.essentialclient.utils.clientscript.impl.ScriptItemStack;
import me.senseiwells.essentialclient.utils.mapping.PlayerHelper;
import me.senseiwells.essentialclient.utils.render.FakeInventoryScreen;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static me.senseiwells.essentialclient.clientscript.core.MinecraftAPI.FAKE_SCREEN;

@ClassDoc(
	name = FAKE_SCREEN,
	desc = {
		"This class extends Screen and so inherits all of their methods too,",
		"this class is used to create client side inventory screens."
	},
	superclass = ScreenDef.class,
	language = Util.Language.Java
)
public class FakeScreenDef extends CreatableDefinition<FakeInventoryScreen> {
	public FakeScreenDef(Interpreter interpreter) {
		super(MinecraftAPI.FAKE_SCREEN, interpreter);
	}

	@NotNull
	@Override
	public PrimitiveDefinition<? super FakeInventoryScreen> superclass() {
		return this.getPrimitiveDef(ScreenDef.class);
	}

	@Override
	public List<ConstructorFunction> defineConstructors() {
		return List.of(
			ConstructorFunction.of(2, this::construct)
		);
	}

	@ConstructorDoc(
		desc = {
			"Creates a FakeScreen instance with given name and given amount of rows,",
			"this will throw an error if the rows are not between 1 and 6"
		},
		params = {
			@ParameterDoc(type = StringDef.class, name = "name", desc = "the name of the screen"),
			@ParameterDoc(type = NumberDef.class, name = "rows", desc = "the number of rows between 1 - 6")
		},
		examples = "new FakeScreen('MyScreen', 6);"
	)
	private Unit construct(Arguments arguments) {
		ClassInstance instance = arguments.next();
		String name = arguments.nextPrimitive(StringDef.class);
		int rows = arguments.nextPrimitive(NumberDef.class).intValue();
		if (rows < 1 || rows > 6) {
			throw new RuntimeError("Rows must be between 1 and 6");
		}
		FakeInventoryScreen screen = new FakeInventoryScreen(arguments.getInterpreter(), PlayerHelper.getPlayerInventory(), name, rows);
		instance.setPrimitive(this, screen);
		return null;
	}

	@Override
	public List<MemberFunction> defineMethods() {
		return List.of(
			MemberFunction.of("onClick", 1, this::onClick),
			MemberFunction.of("setStackForSlot", 2, this::setStackForSlot),
			MemberFunction.of("getStackForSlot", 1, this::getStackForSlot)
		);
	}

	@FunctionDoc(
		name = "onClick",
		desc = {
			"This sets the callback for when a slot is clicked in the inventory.",
			"The callback must have 3 parameters, the first is the item stack that was clicked,",
			"then second is the slot number, third is the action as a string, fourth is the",
			"button string: right, left, middle."
		},
		params = {
			@ParameterDoc(type = FunctionDef.class, name = "function", desc = "the callback function")
		},
		examples =
			"""
				fakeScreen.onClick(fun(item, slotNum, action, button) {
					// Action can be any of the following:
					// 'PICKUP', 'QUICK_MOVE', 'SWAP', 'CLONE', 'THROW', 'QUICK_CRAFT', or 'PICKUP_ALL'
					// Button can be any of the follinwg:
					// 'right', 'left', 'middle', or 'unknown'
					print(action);
				});
				"""
	)
	private Void onClick(Arguments arguments) {
		FakeInventoryScreen fakeScreen = arguments.nextPrimitive(FakeScreenDef.class);
		ArucasFunction function = arguments.nextPrimitive(FunctionDef.class);
		fakeScreen.setFunctionValue(arguments.getInterpreter(), function);
		return null;
	}

	@FunctionDoc(
		name = "setStackForSlot",
		desc = "Sets the stack for the given slot, if the slot is out of bounds it won't be set",
		params = {
			@ParameterDoc(type = NumberDef.class, name = "slotNum", desc = "the slot number"),
			@ParameterDoc(type = ItemStackDef.class, name = "stack", desc = "the stack to set")
		},
		examples = "fakeScreen.setStackForSlot(0, Material.DIAMOND_BLOCK.asItemStack());"
	)
	private Void setStackForSlot(Arguments arguments) {
		FakeInventoryScreen fakeScreen = arguments.nextPrimitive(FakeScreenDef.class);
		int slotNum = arguments.nextPrimitive(NumberDef.class).intValue();
		ScriptItemStack itemStack = arguments.nextPrimitive(ItemStackDef.class);
		fakeScreen.setStack(slotNum, itemStack.stack);
		return null;
	}

	@FunctionDoc(
		name = "getStackForSlot",
		desc = "Gets the stack for the given slot, if the slot is out of bounds it returns null",
		params = {@ParameterDoc(type = NumberDef.class, name = "slotNum", desc = "the slot number")},
		returns = @ReturnDoc(type = ItemStackDef.class, desc = "the stack for the given slot"),
		examples = "fakeScreen.getStackForSlot(0);"
	)
	private ScriptItemStack getStackForSlot(Arguments arguments) {
		FakeInventoryScreen fakeScreen = arguments.nextPrimitive(FakeScreenDef.class);
		int slotNum = arguments.nextPrimitive(NumberDef.class).intValue();
		ItemStack stack = fakeScreen.getStack(slotNum);
		return stack == null ? null : new ScriptItemStack(stack);
	}
}
