package essentialclient.clientscript.extensions;

import essentialclient.clientscript.values.EntityValue;
import essentialclient.clientscript.values.ItemStackValue;
import essentialclient.clientscript.values.PlayerValue;
import essentialclient.clientscript.values.ScreenValue;
import essentialclient.utils.interfaces.MinecraftClientInvoker;
import essentialclient.utils.inventory.InventoryUtils;
import me.senseiwells.arucas.api.IArucasExtension;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.*;
import me.senseiwells.arucas.values.functions.AbstractBuiltInFunction;
import me.senseiwells.arucas.values.functions.MemberFunction;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.Set;

public class ArucasPlayerMembers implements IArucasExtension {

	@Override
	public Set<? extends AbstractBuiltInFunction<?>> getDefinedFunctions() {
		return this.playerFunctions;
	}

	@Override
	public String getName() {
		return "PlayerMemberFunctions";
	}

	private final Set<? extends AbstractBuiltInFunction<?>> playerFunctions = Set.of(
		new MemberFunction("use", "type", this::use),
		new MemberFunction("attack", "type", this::attack),
		new MemberFunction("setSelectedSlot", "slotNum", this::setSelectedSlot),
		new MemberFunction("say", "text", this::say),
		new MemberFunction("message", "text", this::message),
		new MemberFunction("messageActionBar", "text", this::messageActionBar),
		new MemberFunction("openInventory", this::openInventory),
		new MemberFunction("closeInventory", this::closeInventory),
		new MemberFunction("setWalking", "boolean", (context, function) -> this.setKey(context, function, ArucasMinecraftExtension.getClient().options.keyForward)),
		new MemberFunction("setSneaking", "boolean", (context, function) -> this.setKey(context, function, ArucasMinecraftExtension.getClient().options.keySneak)),
		new MemberFunction("setSprinting", "boolean", this::setSprinting),
		new MemberFunction("dropItemInHand", "boolean", this::dropItemInHand),
		new MemberFunction("dropAll", "itemStack", this::dropAll),
		new MemberFunction("look", List.of("yaw", "pitch"), this::look),
		new MemberFunction("lookAtPos", List.of("x", "y", "z"), this::lookAtPos),
		new MemberFunction("jump", this::jump),
		new MemberFunction("getLookingAtEntity", this::getLookingAtEntity),
		new MemberFunction("swapSlots", List.of("slot1", "slot2"), this::swapSlots),
		new MemberFunction("shiftClickSlot", "slot", this::shiftClickSlot),
		new MemberFunction("dropSlot", "slot", this::dropSlot),
		new MemberFunction("getCurrentScreen", this::getCurrentScreen),
		new MemberFunction("craft", List.of("recipe"), this::craft),
		new MemberFunction("logout", this::logout),
		new MemberFunction("interactWithEntity", "entity", this::interactWithEntity),

		// Villager Stuff
		new MemberFunction("tradeIndex", "index", this::tradeIndex),
		new MemberFunction("getIndexOfTradeItem", "itemStack", this::getIndexOfTrade),
		new MemberFunction("getTradeItemForIndex", "index", this::getTradeItemForIndex),
		new MemberFunction("doesVillagerHaveTrade", "itemStack", this::doesVillagerHaveTrade),
		new MemberFunction("isTradeDisabled", "index", this::isTradeDisabled),
		new MemberFunction("getPriceForIndex", "value", this::getPriceForIndex)
	);

	private Value<?> use(Context context, MemberFunction function) throws CodeError {
		this.checkMainPlayer(context, function);
		final String error = "Must pass \"hold\", \"stop\" or \"once\" into use()";
		StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 1, error);
		switch (stringValue.value.toLowerCase()) {
			case "hold" -> ArucasMinecraftExtension.getClient().options.keyUse.setPressed(true);
			case "stop" -> ArucasMinecraftExtension.getClient().options.keyUse.setPressed(false);
			case "once" -> ((MinecraftClientInvoker) ArucasMinecraftExtension.getClient()).rightClickMouseAccessor();
			default -> throw function.throwInvalidParameterError(error, context);
		}
		return new NullValue();
	}

	private Value<?> attack(Context context, MemberFunction function) throws CodeError {
		this.checkMainPlayer(context, function);
		final String error = "Must pass \"hold\", 'stop\" or \"once\" into attack()";
		StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 1, error);
		switch (stringValue.value.toLowerCase()) {
			case "hold" -> ArucasMinecraftExtension.getClient().options.keyAttack.setPressed(true);
			case "stop" -> ArucasMinecraftExtension.getClient().options.keyAttack.setPressed(false);
			case "once" -> ((MinecraftClientInvoker) ArucasMinecraftExtension.getClient()).leftClickMouseAccessor();
			default -> throw function.throwInvalidParameterError(error, context);
		}
		return new NullValue();
	}

	private Value<?> setSelectedSlot(Context context, MemberFunction function) throws CodeError {
		final String error = "Number must be between 0-8";
		NumberValue numberValue = function.getParameterValueOfType(context, NumberValue.class, 1, error);
		if (numberValue.value < 0 || numberValue.value > 8) {
			throw function.throwInvalidParameterError(error, context);
		}
		this.getPlayer(context, function).inventory.selectedSlot = numberValue.value.intValue();
		return new NullValue();
	}

	private Value<?> say(Context context, MemberFunction function) throws CodeError {
		this.getPlayer(context, function).sendChatMessage(function.getParameterValue(context, 1).toString());
		return new NullValue();
	}

	private Value<?> message(Context context, MemberFunction function) throws CodeError {
		Value<?> value = function.getParameterValue(context, 1);
		final ClientPlayerEntity player = this.getPlayer(context, function);
		ArucasMinecraftExtension.getClient().execute(() -> player.sendMessage(new LiteralText(value.toString()), false));
		return new NullValue();
	}

	private Value<?> messageActionBar(Context context, MemberFunction function) throws CodeError {
		Value<?> value = function.getParameterValue(context, 1);
		final ClientPlayerEntity player = this.getPlayer(context, function);
		ArucasMinecraftExtension.getClient().execute(() -> player.sendMessage(new LiteralText(value.toString()), true));
		return new NullValue();
	}

	private Value<?> openInventory(Context context, MemberFunction function) throws CodeError {
		final ClientPlayerEntity player = this.getPlayer(context, function);
		final MinecraftClient client = ArucasMinecraftExtension.getClient();
		client.execute(() -> client.openScreen(new InventoryScreen(player)));
		return new NullValue();
	}

	private Value<?> closeInventory(Context context, MemberFunction function) throws CodeError {
		final ClientPlayerEntity player = this.getPlayer(context, function);
		final MinecraftClient client = ArucasMinecraftExtension.getClient();
		client.execute(player::closeHandledScreen);
		return new NullValue();
	}

	private Value<?> setSprinting(Context context, MemberFunction function) throws CodeError {
		this.checkMainPlayer(context, function);
		BooleanValue booleanValue = function.getParameterValueOfType(context, BooleanValue.class, 1);
		final ClientPlayerEntity player = this.getPlayer(context, function);
		ArucasMinecraftExtension.getClient().execute(() -> player.setSprinting(booleanValue.value));
		return new NullValue();
	}

	private Value<?> dropItemInHand(Context context, MemberFunction function) throws CodeError {
		BooleanValue booleanValue = function.getParameterValueOfType(context, BooleanValue.class, 1);
		this.getPlayer(context, function).dropSelectedItem(booleanValue.value);
		return new NullValue();
	}

	private Value<?> dropAll(Context context, MemberFunction function) throws CodeError {
		this.checkMainPlayer(context, function);
		ItemStackValue itemStackValue = function.getParameterValueOfType(context, ItemStackValue.class, 1);
		MinecraftClient client = ArucasMinecraftExtension.getClient();
		client.execute(() -> InventoryUtils.dropAllItemType(client.player, itemStackValue.value.getItem()));
		return new NullValue();
	}

	private Value<?> look(Context context, MemberFunction function) throws CodeError {
		NumberValue numberValue = function.getParameterValueOfType(context, NumberValue.class, 1);
		NumberValue numberValue2 = function.getParameterValueOfType(context, NumberValue.class, 2);
		ClientPlayerEntity player = this.getPlayer(context, function);
		player.yaw = numberValue.value.floatValue();
		player.pitch = numberValue2.value.floatValue();
		return new NullValue();
	}

	private Value<?> lookAtPos(Context context, MemberFunction function) throws CodeError {
		ClientPlayerEntity player = this.getPlayer(context, function);
		double x = function.getParameterValueOfType(context, NumberValue.class, 1).value;
		double y = function.getParameterValueOfType(context, NumberValue.class, 2).value;
		double z = function.getParameterValueOfType(context, NumberValue.class, 3).value;
		player.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, new Vec3d(x, y, z));
		return new NullValue();
	}

	private Value<?> jump(Context context, MemberFunction function) throws CodeError {
		ClientPlayerEntity player = this.getPlayer(context, function);
		ArucasMinecraftExtension.getClient().execute(() -> {
			if (player.isOnGround()) {
				player.jump();
			}
		});
		return new NullValue();
	}

	private Value<?> getLookingAtEntity(Context context, MemberFunction function) throws CodeError {
		this.checkMainPlayer(context, function);
		Entity targetedEntity = ArucasMinecraftExtension.getClient().targetedEntity;
		return EntityValue.getEntityValue(targetedEntity);
	}

	private Value<?> swapSlots(Context context, MemberFunction function) throws CodeError {
		NumberValue numberValue1 = function.getParameterValueOfType(context, NumberValue.class, 1);
		NumberValue numberValue2 = function.getParameterValueOfType(context, NumberValue.class, 2);
		ScreenHandler screenHandler = this.getPlayer(context, function).currentScreenHandler;
		int tempSlot = (this.getPlayer(context, function).inventory.selectedSlot) % 9;
		int size = screenHandler.slots.size();
		if (numberValue1.value > size || numberValue1.value < 1 || numberValue2.value > size || numberValue2.value < 1) {
			throw new RuntimeError("That slot is out of bounds", function.syntaxPosition, context);
		}
		ClientPlayerInteractionManager interactionManager = ArucasMinecraftExtension.getInteractionManager();
		interactionManager.clickSlot(screenHandler.syncId, numberValue1.value.intValue(), tempSlot, SlotActionType.SWAP, this.getPlayer(context, function));
		interactionManager.clickSlot(screenHandler.syncId, numberValue2.value.intValue(), tempSlot, SlotActionType.SWAP, this.getPlayer(context, function));
		interactionManager.clickSlot(screenHandler.syncId, numberValue1.value.intValue(), tempSlot, SlotActionType.SWAP, this.getPlayer(context, function));
		return new NullValue();
	}

	private Value<?> shiftClickSlot(Context context, MemberFunction function) throws CodeError {
		NumberValue numberValue1 = function.getParameterValueOfType(context, NumberValue.class, 1);
		ScreenHandler screenHandler = this.getPlayer(context, function).currentScreenHandler;
		int size = screenHandler.slots.size();
		if (numberValue1.value > size || numberValue1.value < 1)
			throw new RuntimeError("That slot is out of bounds", function.syntaxPosition, context);
		ArucasMinecraftExtension.getInteractionManager().clickSlot(screenHandler.syncId, numberValue1.value.intValue() - 1, 0, SlotActionType.QUICK_MOVE, this.getPlayer(context, function));
		return new NullValue();
	}

	private Value<?> dropSlot(Context context, MemberFunction function) throws CodeError {
		NumberValue numberValue = function.getParameterValueOfType(context, NumberValue.class, 1);
		ArucasMinecraftExtension.getInteractionManager().clickSlot(this.getPlayer(context, function).currentScreenHandler.syncId, numberValue.value.intValue() - 1, 1, SlotActionType.THROW, ArucasMinecraftExtension.getClient().player);
		return new NullValue();
	}

	private Value<?> getCurrentScreen(Context context, MemberFunction function) throws CodeError {
		this.checkMainPlayer(context, function);
		Screen currentScreen = ArucasMinecraftExtension.getClient().currentScreen;
		if (currentScreen == null) {
			return new NullValue();
		}
		return new ScreenValue(currentScreen);
	}

	private Value<?> craft(Context context, MemberFunction function) throws CodeError {
		this.checkMainPlayer(context, function);
		ListValue listValue = function.getParameterValueOfType(context, ListValue.class, 1);
		if (!(ArucasMinecraftExtension.getClient().currentScreen instanceof HandledScreen<?> handledScreen)) {
			return new NullValue();
		}
		ItemStack[] itemStacks = new ItemStack[9];
		for (int i = 0; i < listValue.value.size(); i++) {
			if (!(listValue.value.get(i) instanceof ItemStackValue itemStackValue)) {
				throw new RuntimeError("The recipe must only include items", function.syntaxPosition, context);
			}
			itemStacks[i] = itemStackValue.value;
		}
		InventoryUtils.tryMoveItemsToCraftingGridSlots(ArucasMinecraftExtension.getClient(), itemStacks, handledScreen);
		InventoryUtils.shiftClickSlot(ArucasMinecraftExtension.getClient(), handledScreen, 0);
		return new NullValue();
	}

	private Value<?> logout(Context context, MemberFunction function) throws CodeError {
		this.checkMainPlayer(context, function);
		ArucasMinecraftExtension.getWorld().disconnect();
		return new NullValue();
	}

	private Value<?> interactWithEntity(Context context, MemberFunction function) throws CodeError {
		ClientPlayerEntity player =  this.getPlayer(context, function);
		EntityValue<?> entity  = function.getParameterValueOfType(context, EntityValue.class, 1);
		player.interact(entity.value, Hand.MAIN_HAND);
		return new NullValue();
	}

	private Value<?> tradeIndex(Context context, MemberFunction function) throws CodeError {
		this.checkMainPlayer(context, function);
		NumberValue numberValue = function.getParameterValueOfType(context, NumberValue.class, 1);
		if (!InventoryUtils.tradeAllItems(ArucasMinecraftExtension.getClient(), numberValue.value.intValue(), false)) {
			throw new RuntimeError("Not in merchant gui", function.syntaxPosition, context);
		}
		return new NullValue();
	}

	private Value<?> getIndexOfTrade(Context context, MemberFunction function) throws CodeError {
		this.checkMainPlayer(context, function);
		ItemStackValue itemStackValue = function.getParameterValueOfType(context, ItemStackValue.class, 1);
		int index = InventoryUtils.getIndexOfItemInMerchant(ArucasMinecraftExtension.getClient(), itemStackValue.value.getItem());
		this.checkVillagerValid(index, function, context);
		return new NumberValue(index);
	}

	private Value<?> getTradeItemForIndex(Context context, MemberFunction function) throws CodeError {
		this.checkMainPlayer(context, function);
		NumberValue numberValue = function.getParameterValueOfType(context, NumberValue.class, 1);
		ItemStack itemStack = InventoryUtils.getTrade(ArucasMinecraftExtension.getClient(), numberValue.value.intValue());
		if (itemStack == null) {
			throw new RuntimeError("Could not find trade", function.syntaxPosition, context);
		}
		return new ItemStackValue(itemStack);
	}

	private Value<?> doesVillagerHaveTrade(Context context, MemberFunction function) throws CodeError {
		this.checkMainPlayer(context, function);
		ItemStackValue itemStackValue = function.getParameterValueOfType(context, ItemStackValue.class, 1);
		int code = InventoryUtils.checkHasTrade(ArucasMinecraftExtension.getClient(), itemStackValue.value.getItem());
		return new BooleanValue(this.checkVillagerValid(code, function, context));
	}

	private Value<?> isTradeDisabled(Context context, MemberFunction function) throws CodeError {
		this.checkMainPlayer(context, function);
		NumberValue numberValue = function.getParameterValueOfType(context, NumberValue.class, 1);
		int code = InventoryUtils.checkTradeDisabled(ArucasMinecraftExtension.getClient(), numberValue.value.intValue());
		return new BooleanValue(this.checkVillagerValid(code, function, context));
	}

	private Value<?> getPriceForIndex(Context context, MemberFunction function) throws CodeError {
		this.checkMainPlayer(context, function);
		NumberValue numberValue = function.getParameterValueOfType(context, NumberValue.class, 1);
		int price = InventoryUtils.checkPriceForTrade(ArucasMinecraftExtension.getClient(), numberValue.value.intValue());
		this.checkVillagerValid(price, function, context);
		return new NumberValue(price);
	}

	public boolean checkVillagerValid(int code, AbstractBuiltInFunction<?> function, Context context) throws RuntimeError {
		boolean bool = false;
		switch (code) {
			case -2 -> throw new RuntimeError("You are not in merchant GUI", function.syntaxPosition, context);
			case -1 -> throw new RuntimeError("That trade is out of bounds", function.syntaxPosition, context);
			case 1 -> bool = true;
		}
		return bool;
	}

	private NullValue setKey(Context context, MemberFunction function, KeyBinding keyBinding) throws CodeError {
		this.checkMainPlayer(context, function);
		BooleanValue booleanValue = function.getParameterValueOfType(context, BooleanValue.class, 1);
		keyBinding.setPressed(booleanValue.value);
		return new NullValue();
	}

	private void checkMainPlayer(Context context, MemberFunction function) throws CodeError {
		function.getParameterValueOfType(context, PlayerValue.class, 0);
	}

	private ClientPlayerEntity getPlayer(Context context, MemberFunction function) throws CodeError {
		ClientPlayerEntity player = function.getParameterValueOfType(context, PlayerValue.class, 0).value;
		if (player == null) {
			throw new RuntimeError("Player was null", function.syntaxPosition, context);
		}
		return player;
	}
}
