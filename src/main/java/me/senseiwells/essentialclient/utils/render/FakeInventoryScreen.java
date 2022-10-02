package me.senseiwells.essentialclient.utils.render;

import me.senseiwells.arucas.builtin.NumberDef;
import me.senseiwells.arucas.builtin.StringDef;
import me.senseiwells.arucas.core.Interpreter;
import me.senseiwells.arucas.utils.ArucasFunction;
import me.senseiwells.essentialclient.clientscript.definitions.ItemStackDef;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import me.senseiwells.essentialclient.utils.clientscript.impl.ScriptItemStack;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

import java.util.List;

public class FakeInventoryScreen extends GenericContainerScreen {
	private Interpreter interpreter;
	private ArucasFunction function;

	public FakeInventoryScreen(Interpreter interpreter, PlayerInventory inventory, String title, int rows) {
		super(getHandler(inventory, rows), inventory, Texts.literal(title));
		super.init(EssentialUtils.getClient(), this.width, this.height);
		this.interpreter = interpreter;
	}

	public void setFunctionValue(Interpreter interpreter, ArucasFunction arucasFunction) {
		this.interpreter = interpreter;
		this.function = arucasFunction;
	}

	public void setStack(int slot, ItemStack stack) {
		Inventory handlerInventory = this.handler.getInventory();
		if (slot < handlerInventory.size() && slot >= 0) {
			handlerInventory.setStack(slot, stack);
		}
	}

	public ItemStack getStack(int slot) {
		Inventory handlerInventory = this.handler.getInventory();
		if (slot < handlerInventory.size() && slot >= 0) {
			return handlerInventory.getStack(slot);
		}
		return null;
	}

	public void fakeTick() {
		if (this.interpreter == null || !this.interpreter.getThreadHandler().getRunning()) {
			this.close();
		}
	}

	@Override
	public void close() {
		EssentialUtils.getClient().setScreen(null);
	}

	@Override
	protected void onMouseClick(Slot slot, int slotId, int button, SlotActionType actionType) {
		final int slotNumber = slot == null ? slotId : slot.id;
		final String action = actionType.toString();
		if (this.interpreter != null && this.interpreter.getThreadHandler().getRunning()) {
			List<Slot> slots = this.handler.slots;
			ItemStack stack = slotNumber < slots.size() && slotNumber >= 0 ? slots.get(slotNumber).getStack() : ItemStack.EMPTY;
			Interpreter branch = this.interpreter.branch();
			branch.getThreadHandler().runAsync(() -> {
				this.function.invoke(branch, List.of(
					branch.create(ItemStackDef.class, new ScriptItemStack(stack.copy())),
					branch.create(NumberDef.class, (double) slotNumber),
					branch.create(StringDef.class, action)
				));
				return null;
			});
		}
	}

	private static GenericContainerScreenHandler getHandler(PlayerInventory inventory, int rows) {
		return switch (rows) {
			case 1 -> GenericContainerScreenHandler.createGeneric9x1(0, inventory);
			case 2 -> GenericContainerScreenHandler.createGeneric9x2(0, inventory);
			case 3 -> GenericContainerScreenHandler.createGeneric9x3(0, inventory);
			case 4 -> GenericContainerScreenHandler.createGeneric9x4(0, inventory);
			case 5 -> GenericContainerScreenHandler.createGeneric9x5(0, inventory);
			case 6 -> GenericContainerScreenHandler.createGeneric9x6(0, inventory);
			default -> throw new IllegalArgumentException("Invalid number of rows");
		};
	}
}
