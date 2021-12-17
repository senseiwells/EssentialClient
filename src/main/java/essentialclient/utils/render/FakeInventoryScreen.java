package essentialclient.utils.render;

import essentialclient.clientscript.ClientScript;
import essentialclient.clientscript.values.ItemStackValue;
import essentialclient.utils.EssentialUtils;
import me.senseiwells.arucas.values.NumberValue;
import me.senseiwells.arucas.values.StringValue;
import me.senseiwells.arucas.values.functions.FunctionValue;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.LiteralText;

import java.util.List;

public class FakeInventoryScreen extends GenericContainerScreen {
	private FunctionValue functionValue = null;

	public FakeInventoryScreen(PlayerInventory inventory, String title, int rows) {
		super(getHandler(inventory, rows), inventory, new LiteralText(title));
		super.init(EssentialUtils.getClient(), this.width, this.height);
	}

	public void setFunctionValue(FunctionValue functionValue) {
		this.functionValue = functionValue;
	}

	public void setStack(int slot, ItemStack stack) {
		Inventory handlerInventory = this.handler.getInventory();
		if (slot < handlerInventory.size() && slot >= 0) {
			handlerInventory.setStack(slot, stack);
		}
	}

	@Override
	public void tick() {
		if (!ClientScript.getInstance().isScriptRunning()) {
			this.onClose();
		}
	}

	@Override
	public void onClose() {
		EssentialUtils.getClient().openScreen(null);
	}

	@Override
	protected void onMouseClick(Slot slot, int slotId, int button, SlotActionType actionType) {
		final int slotNumber = slot == null ? slotId : slot.id;
		final String action = actionType.toString();
		if (this.functionValue != null && ClientScript.getInstance().isScriptRunning()) {
			List<Slot> slots = this.handler.slots;
			ItemStack stack = slotNumber < slots.size() && slotNumber >= 0 ? slots.get(slotNumber).getStack() : ItemStack.EMPTY;
			ClientScript.getInstance().runBranchAsyncFunction(
				context -> this.functionValue.call(context, List.of(
					new ItemStackValue(stack),
					new NumberValue(slotNumber),
					new StringValue(action)
				))
			);
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
