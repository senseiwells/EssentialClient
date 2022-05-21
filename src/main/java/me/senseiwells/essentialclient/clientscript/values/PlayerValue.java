package me.senseiwells.essentialclient.clientscript.values;

import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.Arguments;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.impl.ArucasList;
import me.senseiwells.arucas.values.*;
import me.senseiwells.arucas.values.functions.BuiltInFunction;
import me.senseiwells.arucas.values.functions.FunctionValue;
import me.senseiwells.arucas.values.functions.MemberFunction;
import me.senseiwells.essentialclient.clientscript.extensions.ArucasMinecraftExtension;
import me.senseiwells.essentialclient.feature.BetterAccurateBlockPlacement;
import me.senseiwells.essentialclient.feature.CraftingSharedConstants;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import me.senseiwells.essentialclient.utils.interfaces.MinecraftClientInvoker;
import me.senseiwells.essentialclient.utils.inventory.InventoryUtils;
import me.senseiwells.essentialclient.utils.render.FakeInventoryScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CraftingScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.recipe.StonecuttingRecipe;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.StonecutterScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.Objects;

import static me.senseiwells.essentialclient.clientscript.core.MinecraftAPI.PLAYER;

public class PlayerValue extends AbstractPlayerValue<ClientPlayerEntity> {
	public PlayerValue(ClientPlayerEntity player) {
		super(player);
	}

	@Override
	public EntityValue<ClientPlayerEntity> copy(Context context) {
		return this;
	}

	@Override
	public String getAsString(Context context) {
		return "Player{name=%s}".formatted(this.value.getEntityName());
	}

	@Override
	public String getTypeName() {
		return PLAYER;
	}

	public static class ArucasPlayerClass extends ArucasClassExtension {
		public ArucasPlayerClass() {
			super(PLAYER);
		}

		@Override
		public ArucasFunctionMap<BuiltInFunction> getDefinedStaticMethods() {
			return ArucasFunctionMap.of(
				BuiltInFunction.of("get", this::get)
			);
		}

		private Value get(Arguments arguments) throws CodeError {
			return new PlayerValue(ArucasMinecraftExtension.getPlayer());
		}

		@Override
		public ArucasFunctionMap<MemberFunction> getDefinedMethods() {
			return ArucasFunctionMap.of(
				MemberFunction.of("use", 1, this::use),
				MemberFunction.of("attack", 1, this::attack),
				MemberFunction.of("setSelectedSlot", 1, this::setSelectedSlot),
				MemberFunction.of("say", 1, this::say),
				MemberFunction.of("message", 1, this::message),
				MemberFunction.of("messageActionBar", 1, this::messageActionBar),
				MemberFunction.of("showTitle", 1, this::showTitle),
				MemberFunction.of("openInventory", this::openInventory),
				MemberFunction.of("openScreen", 1, this::openScreen),
				MemberFunction.of("closeScreen", this::closeScreen),
				MemberFunction.of("setWalking", 1, this::setWalking),
				MemberFunction.of("setSneaking", 1, this::setSneaking),
				MemberFunction.of("setSprinting", 1, this::setSprinting),
				MemberFunction.of("dropItemInHand", 1, this::dropItemInHand),
				MemberFunction.of("dropAll", 1, this::dropAll),
				MemberFunction.of("look", 2, this::look),
				MemberFunction.of("lookAtPos", 3, this::lookAtPos),
				MemberFunction.of("lookAtPos", 1, this::lookAtPosPos),
				MemberFunction.of("jump", this::jump),
				MemberFunction.of("getLookingAtEntity", this::getLookingAtEntity),
				MemberFunction.of("swapSlots", 2, this::swapSlots),
				MemberFunction.of("shiftClickSlot", 1, this::shiftClickSlot),
				MemberFunction.of("dropSlot", 1, this::dropSlot),
				MemberFunction.of("getCurrentScreen", this::getCurrentScreen),
				MemberFunction.of("craft", 1, this::craft),
				MemberFunction.of("craftRecipe", 1, this::craftRecipe),
				MemberFunction.of("logout", 1, this::logout),
				MemberFunction.of("attackEntity", 1, this::attackEntity),
				MemberFunction.of("interactWithEntity", 1, this::interactWithEntity),
				MemberFunction.of("anvil", 2, this::anvil),
				MemberFunction.of("anvilRename", 2, this::anvilRename),
				MemberFunction.of("stonecutter", 2, this::stonecutter),
				MemberFunction.of("fakeLook", 4, this::fakeLook),
				MemberFunction.of("swapPlayerSlotWithHotbar", 1, this::swapPlayerSlotWithHotbar),
				MemberFunction.of("updateBreakingBlock", 3, this::updateBreakingBlock),
				MemberFunction.of("updateBreakingBlock", 1, this::updateBreakingBlockPos),
				MemberFunction.of("attackBlock", 4, this::attackBlock),
				MemberFunction.of("attackBlock", 2, this::attackBlockPos),
				MemberFunction.of("interactBlock", 4, this::interactBlock),
				MemberFunction.of("interactBlock", 2, this::interactBlockPos),
				MemberFunction.of("interactBlock", 8, this::interactBlockFull),
				MemberFunction.of("interactBlock", 4, this::interactBlockFullPos),
				MemberFunction.of("getBlockBreakingSpeed", 1, this::getBlockBreakingSpeed),
				MemberFunction.of("swapHands", this::swapHands),
				MemberFunction.of("swingHand", 1, this::swingHand),
				MemberFunction.of("clickSlot", 3, this::clickSlot),
				MemberFunction.of("getSwappableHotbarSlot", this::getSwappableHotbarSlot),
				MemberFunction.of("spectatorTeleport", 1, this::spectatorTeleport),

				// Villager Stuff
				MemberFunction.of("tradeIndex", 1, this::tradeIndex, "Use '<MerchantScreen>.tradeIndex(index)'"),
				MemberFunction.of("getIndexOfTradeItem", 1, this::getIndexOfTrade, "Use '<MerchantScreen>.getIndexOfTradeItem(itemStack)'"),
				MemberFunction.of("getTradeItemForIndex", 1, this::getTradeItemForIndex, "Use '<MerchantScreen>.getTradeItemForIndex(index)'"),
				MemberFunction.of("doesVillagerHaveTrade", 1, this::doesVillagerHaveTrade, "Use '<MerchantScreen>.doesVillagerHaveTrade(itemStack)'"),
				MemberFunction.of("isTradeDisabled", 1, this::isTradeDisabled, "Use '<MerchantScreen>.isTradeDisabled(index)'"),
				MemberFunction.of("getPriceForIndex", 1, this::getPriceForIndex, "Use '<MerchantScreen>.getPriceForIndex(index)'")
			);
		}

		private Value use(Arguments arguments) throws CodeError {
			StringValue stringValue = arguments.skip().getNextString();
			switch (stringValue.value.toLowerCase()) {
				case "hold" -> ArucasMinecraftExtension.getClient().options.keyUse.setPressed(true);
				case "stop" -> ArucasMinecraftExtension.getClient().options.keyUse.setPressed(false);
				case "once" -> ((MinecraftClientInvoker) ArucasMinecraftExtension.getClient()).rightClickMouseAccessor();
				default -> throw arguments.getError("Must pass 'hold', 'stop' or 'once' into use()");
			}
			return NullValue.NULL;
		}

		private Value attack(Arguments arguments) throws CodeError {
			StringValue stringValue = arguments.skip().getNextString();
			switch (stringValue.value.toLowerCase()) {
				case "hold" -> ArucasMinecraftExtension.getClient().options.keyAttack.setPressed(true);
				case "stop" -> ArucasMinecraftExtension.getClient().options.keyAttack.setPressed(false);
				case "once" -> ((MinecraftClientInvoker) ArucasMinecraftExtension.getClient()).leftClickMouseAccessor();
				default -> throw arguments.getError("Must pass 'hold', 'stop' or 'once' into attack()");
			}
			return NullValue.NULL;
		}

		private Value setSelectedSlot(Arguments arguments) throws CodeError {
			ClientPlayerEntity player = this.getPlayer(arguments);
			NumberValue numberValue = arguments.getNextNumber();
			if (numberValue.value < 0 || numberValue.value > 8) {
				throw arguments.getError("Number must be between 0 - 8");
			}
			int selectedSlot = numberValue.value.intValue();
			MinecraftClient client = ArucasMinecraftExtension.getClient();
			ClientPlayNetworkHandler networkHandler = ArucasMinecraftExtension.getNetworkHandler();
			client.execute(() -> {
				networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(selectedSlot));
				player.getInventory().selectedSlot = selectedSlot;
			});
			return NullValue.NULL;
		}

		private Value say(Arguments arguments) throws CodeError {
			this.getPlayer(arguments).sendChatMessage(arguments.getNext().getAsString(arguments.getContext()));
			return NullValue.NULL;
		}

		private Value message(Arguments arguments) throws CodeError {
			ClientPlayerEntity player = this.getPlayer(arguments);
			Value value = arguments.getNext();
			Text text = value instanceof TextValue textValue ? textValue.value : new LiteralText(value.getAsString(arguments.getContext()));
			ArucasMinecraftExtension.getClient().execute(() -> player.sendMessage(text, false));
			return NullValue.NULL;
		}

		private Value messageActionBar(Arguments arguments) throws CodeError {
			ClientPlayerEntity player = this.getPlayer(arguments);
			Value value = arguments.getNext();
			Text text = value instanceof TextValue textValue ? textValue.value : new LiteralText(value.getAsString(arguments.getContext()));
			ArucasMinecraftExtension.getClient().execute(() -> player.sendMessage(text, true));
			return NullValue.NULL;
		}

		private Value showTitle(Arguments arguments) throws CodeError {
			Value value = arguments.skip().getNext();
			Value subValue = arguments.getNext();
			Context context = arguments.getContext();
			Text text = value instanceof TextValue textValue ? textValue.value : value.getValue() == null ? null : new LiteralText(value.getAsString(context));
			Text subText = subValue instanceof TextValue textValue ? textValue.value : subValue.getValue() == null ? null : new LiteralText(subValue.getAsString(context));
			MinecraftClient client = ArucasMinecraftExtension.getClient();
			client.execute(() -> {
				client.inGameHud.setTitle(text);
				client.inGameHud.setSubtitle(subText);
			});
			return NullValue.NULL;
		}

		private Value openInventory(Arguments arguments) throws CodeError {
			ClientPlayerEntity player = this.getPlayer(arguments);
			MinecraftClient client = ArucasMinecraftExtension.getClient();
			client.execute(() -> client.setScreen(new InventoryScreen(player)));
			return NullValue.NULL;
		}

		private Value openScreen(Arguments arguments) throws CodeError {
			MinecraftClient client = ArucasMinecraftExtension.getClient();
			ScreenValue<?> screenValue = arguments.skip().getNext(ScreenValue.class);
			if (screenValue.value instanceof HandledScreen && !(screenValue.value instanceof FakeInventoryScreen)) {
				throw arguments.getError("Opening handled screens is unsafe");
			}
			client.execute(() -> client.setScreen(screenValue.value));
			return NullValue.NULL;
		}

		private Value closeScreen(Arguments arguments) throws CodeError {
			ClientPlayerEntity player = this.getPlayer(arguments);
			MinecraftClient client = ArucasMinecraftExtension.getClient();
			client.execute(player::closeHandledScreen);
			return NullValue.NULL;
		}

		private Value setWalking(Arguments arguments) throws CodeError {
			return this.setKey(arguments, ArucasMinecraftExtension.getClient().options.keyForward);
		}

		private Value setSneaking(Arguments arguments) throws CodeError {
			return this.setKey(arguments, ArucasMinecraftExtension.getClient().options.keySneak);
		}

		private Value setSprinting(Arguments arguments) throws CodeError {
			ClientPlayerEntity player = this.getPlayer(arguments);
			BooleanValue booleanValue = arguments.getNextBoolean();
			ArucasMinecraftExtension.getClient().execute(() -> player.setSprinting(booleanValue.value));
			return NullValue.NULL;
		}

		private Value dropItemInHand(Arguments arguments) throws CodeError {
			ClientPlayerEntity player = this.getPlayer(arguments);
			BooleanValue booleanValue = arguments.getNextBoolean();
			ArucasMinecraftExtension.getClient().execute(() -> player.dropSelectedItem(booleanValue.value));
			return NullValue.NULL;
		}

		private Value dropAll(Arguments arguments) throws CodeError {
			ItemStackValue itemStackValue = arguments.skip().getNext(ItemStackValue.class);
			MinecraftClient client = ArucasMinecraftExtension.getClient();
			client.execute(() -> InventoryUtils.dropAllItemType(client.player, itemStackValue.value.getItem()));
			return NullValue.NULL;
		}

		private Value look(Arguments arguments) throws CodeError {
			ClientPlayerEntity player = this.getPlayer(arguments);
			NumberValue numberValue = arguments.getNextNumber();
			NumberValue numberValue2 = arguments.getNextNumber();
			player.setYaw(numberValue.value.floatValue());
			player.setPitch(numberValue2.value.floatValue());
			return NullValue.NULL;
		}

		private Value lookAtPos(Arguments arguments) throws CodeError {
			ClientPlayerEntity player = this.getPlayer(arguments);
			double x = arguments.getNextGeneric(NumberValue.class);
			double y = arguments.getNextGeneric(NumberValue.class);
			double z = arguments.getNextGeneric(NumberValue.class);
			player.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, new Vec3d(x, y, z));
			return NullValue.NULL;
		}

		private Value lookAtPosPos(Arguments arguments) throws CodeError {
			ClientPlayerEntity player = this.getPlayer(arguments);
			PosValue posValue = arguments.getNext(PosValue.class);
			player.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, posValue.value);
			return NullValue.NULL;
		}

		private Value jump(Arguments arguments) throws CodeError {
			ClientPlayerEntity player = this.getPlayer(arguments);
			ArucasMinecraftExtension.getClient().execute(() -> {
				if (player.isOnGround()) {
					player.jump();
					player.setOnGround(false);
				}
			});
			return NullValue.NULL;
		}

		private Value getLookingAtEntity(Arguments arguments) throws CodeError {
			Entity targetedEntity = ArucasMinecraftExtension.getClient().targetedEntity;
			return arguments.getContext().convertValue(targetedEntity);
		}

		private Value swapSlots(Arguments arguments) throws CodeError {
			ClientPlayerEntity player = this.getPlayer(arguments);
			int slot1 = arguments.getNextGeneric(NumberValue.class).intValue();
			int slot2 = arguments.getNextGeneric(NumberValue.class).intValue();
			ScreenHandler screenHandler = player.currentScreenHandler;
			int size = screenHandler.slots.size();
			if (slot1 >= size || slot1 < 0 || slot2 >= size || slot2 < 0) {
				throw arguments.getError("That slot is out of bounds");
			}
			int firstMapped = InventoryUtils.isSlotInHotbar(screenHandler, slot1);
			int secondMapped = InventoryUtils.isSlotInHotbar(screenHandler, slot2);
			ClientPlayerInteractionManager interactionManager = ArucasMinecraftExtension.getInteractionManager();
			MinecraftClient client = ArucasMinecraftExtension.getClient();
			if (firstMapped == -1) {
				if (secondMapped == -1) {
					client.execute(() -> {
						interactionManager.clickSlot(screenHandler.syncId, slot1, 0, SlotActionType.SWAP, player);
						interactionManager.clickSlot(screenHandler.syncId, slot2, 0, SlotActionType.SWAP, player);
						interactionManager.clickSlot(screenHandler.syncId, slot1, 0, SlotActionType.SWAP, player);
						player.getInventory().updateItems();
					});
				}
				else {
					InventoryUtils.swapSlot(client, screenHandler, slot1, secondMapped);
				}
			}
			else {
				InventoryUtils.swapSlot(client, screenHandler, slot2, firstMapped);
			}
			return NullValue.NULL;
		}

		private Value getSwappableHotbarSlot(Arguments arguments) throws CodeError {
			// Return predicted current swappable hotbar slot
			ClientPlayerEntity player = this.getPlayer(arguments);
			return NumberValue.of(player.getInventory().getSwappableHotbarSlot());
		}

		private Value spectatorTeleport(Arguments arguments) throws CodeError {
			ClientPlayerEntity player = this.getPlayer(arguments);
			if (!player.isSpectator()) {
				return BooleanValue.FALSE;
			}
			EntityValue<?> entityValue = arguments.getNext(EntityValue.class);
			ClientPlayNetworkHandler networkHandler = ArucasMinecraftExtension.getNetworkHandler();
			networkHandler.sendPacket(new SpectatorTeleportC2SPacket(entityValue.value.getUuid()));
			return BooleanValue.TRUE;
		}

		private Value swapHands(Arguments arguments) throws CodeError {
			ClientPlayerEntity player = this.getPlayer(arguments);
			ClientPlayNetworkHandler networkHandler = ArucasMinecraftExtension.getNetworkHandler();
			if (player.isSpectator()) {
				return BooleanValue.FALSE;
			}
			ArucasMinecraftExtension.getClient().execute(() ->
				networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.SWAP_ITEM_WITH_OFFHAND, BlockPos.ORIGIN, Direction.DOWN))
			);
			return BooleanValue.TRUE;
		}

		private Value swingHand(Arguments arguments) throws CodeError {
			ClientPlayerEntity player = this.getPlayer(arguments);
			StringValue handAsString = arguments.getNextString();
			Hand hand = switch (handAsString.value.toLowerCase()) {
				case "main", "main_hand" -> Hand.MAIN_HAND;
				case "off", "off_hand" -> Hand.OFF_HAND;
				default -> throw arguments.getError("Invalid hand '%s'", handAsString);
			};
			player.swingHand(hand);
			return NullValue.NULL;
		}

		private Value clickSlot(Arguments arguments) throws CodeError {
			ClientPlayerEntity player = this.getPlayer(arguments);
			int slot = arguments.getNextGeneric(NumberValue.class).intValue();
			Value clickDataValue = arguments.getNext();
			int clickData;
			if (clickDataValue instanceof StringValue clickDataString) {
				clickData = switch (clickDataString.value) {
					case "right" -> 1;
					case "left" -> 0;
					default -> throw arguments.getError("Invalid clickData must be 'left' or 'right' or a number");
				};
			}
			else if (clickDataValue instanceof NumberValue clickDataNumber) {
				clickData = clickDataNumber.value.intValue();
			}
			else {
				throw arguments.getError("Invalid clickData must be 'left' or 'right' or a number");
			}
			String stringValue = arguments.getNextGeneric(StringValue.class).toLowerCase();
			SlotActionType slotActionType = switch (stringValue) {
				case "click", "pickup" -> SlotActionType.PICKUP;
				case "shift_click", "quick_move" -> SlotActionType.QUICK_MOVE;
				case "swap" -> SlotActionType.SWAP;
				case "middle_click", "clone" -> SlotActionType.CLONE;
				case "throw" -> SlotActionType.THROW;
				case "drag", "quick_craft" -> SlotActionType.QUICK_CRAFT;
				case "double_click", "pickup_all" -> SlotActionType.PICKUP_ALL;
				default -> throw arguments.getError("Invalid slotActionType, see Wiki");
			};
			ScreenHandler screenHandler = player.currentScreenHandler;
			int size = screenHandler.slots.size();
			if (slot != -999 && slot >= size || slot < 0) {
				throw arguments.getError("That slot is out of bounds");
			}
			ClientPlayerInteractionManager interactionManager = ArucasMinecraftExtension.getInteractionManager();
			int finalClickData = clickData;
			ArucasMinecraftExtension.getClient().execute(() -> interactionManager.clickSlot(screenHandler.syncId, slot, finalClickData, slotActionType, player));
			return NullValue.NULL;
		}

		private Value shiftClickSlot(Arguments arguments) throws CodeError {
			ClientPlayerEntity player = this.getPlayer(arguments);
			NumberValue number = arguments.getNextNumber();
			ScreenHandler screenHandler = player.currentScreenHandler;
			int size = screenHandler.slots.size();
			if (number.value >= size || number.value < 0) {
				throw arguments.getError("That slot is out of bounds");
			}
			ClientPlayerInteractionManager interactionManager = ArucasMinecraftExtension.getInteractionManager();
			ArucasMinecraftExtension.getClient().execute(() -> interactionManager.clickSlot(screenHandler.syncId, number.value.intValue(), 0, SlotActionType.QUICK_MOVE, player));
			return NullValue.NULL;
		}

		private Value dropSlot(Arguments arguments) throws CodeError {
			ClientPlayerEntity player = this.getPlayer(arguments);
			NumberValue numberValue = arguments.getNextNumber();
			ArucasMinecraftExtension.getInteractionManager().clickSlot(player.currentScreenHandler.syncId, numberValue.value.intValue(), 1, SlotActionType.THROW, player);
			return NullValue.NULL;
		}

		private Value getCurrentScreen(Arguments arguments) throws CodeError {
			Screen currentScreen = ArucasMinecraftExtension.getClient().currentScreen;
			return arguments.getContext().convertValue(currentScreen);
		}

		private Value craft(Arguments arguments) throws CodeError {
			MinecraftClient client = ArucasMinecraftExtension.getClient();
			ListValue listValue = arguments.skip().getNextList();
			if (!(client.currentScreen instanceof HandledScreen<?> handledScreen)) {
				throw arguments.getError("Must be in a crafting GUI");
			}

			int listSize = listValue.value.size();
			if (listSize == 9) {
				if (!(handledScreen instanceof CraftingScreen)) {
					throw arguments.getError("You must be in a crafting table to craft a 3x3");
				}
			}
			else if (listSize != 4) {
				throw arguments.getError("Recipe must either be 3x3 or 2x2");
			}

			ItemStack[] itemStacks = new ItemStack[listSize];
			for (int i = 0; i < listSize; i++) {
				if (!(listValue.value.get(i) instanceof ItemStackValue itemStackValue)) {
					throw arguments.getError("The recipe must only include items");
				}
				itemStacks[i] = itemStackValue.value;
			}
			client.execute(() -> {
				InventoryUtils.tryMoveItemsToCraftingGridSlots(client, itemStacks, handledScreen);
				InventoryUtils.shiftClickSlot(client, handledScreen, 0);
			});
			return NullValue.NULL;
		}

		private Value craftRecipe(Arguments arguments) throws CodeError {
			MinecraftClient client = ArucasMinecraftExtension.getClient();
			if (!(client.currentScreen instanceof HandledScreen<?> handledScreen)) {
				throw arguments.getError("Must be in a crafting GUI");
			}
			ClientPlayerInteractionManager interactionManager = ArucasMinecraftExtension.getInteractionManager();
			RecipeValue recipeValue = arguments.skip().getNext(RecipeValue.class);
			client.execute(() -> {
				CraftingSharedConstants.IS_SCRIPT_CLICK.set(true);
				interactionManager.clickRecipe(handledScreen.getScreenHandler().syncId, recipeValue.value, true);
				InventoryUtils.shiftClickSlot(client, handledScreen, 0);
			});
			return NullValue.NULL;
		}

		private Value logout(Arguments arguments) throws CodeError {
			String reason = arguments.skip().getNextGeneric(StringValue.class);
			ArucasMinecraftExtension.getNetworkHandler().onDisconnected(new LiteralText(reason));
			return NullValue.NULL;
		}

		private Value attackEntity(Arguments arguments) throws CodeError {
			ClientPlayerEntity player = this.getPlayer(arguments);
			EntityValue<?> entity = arguments.getNext(EntityValue.class);
			ClientPlayerInteractionManager interactionManager = ArucasMinecraftExtension.getInteractionManager();
			ArucasMinecraftExtension.getClient().execute(() -> {
				interactionManager.attackEntity(player, entity.value);
			});
			return NullValue.NULL;
		}

		private Value interactWithEntity(Arguments arguments) throws CodeError {
			ClientPlayerEntity player = this.getPlayer(arguments);
			EntityValue<?> entity = arguments.getNext(EntityValue.class);
			ClientPlayerInteractionManager interactionManager = ArucasMinecraftExtension.getInteractionManager();
			ArucasMinecraftExtension.getClient().execute(() -> {
				interactionManager.interactEntity(player, entity.value, Hand.MAIN_HAND);
			});
			return NullValue.NULL;
		}

		private Value anvil(Arguments arguments) throws CodeError {
			ClientPlayerInteractionManager interactionManager = EssentialUtils.getInteractionManager();
			if (interactionManager == null) {
				return BooleanValue.FALSE;
			}
			ClientPlayerEntity player = this.getPlayer(arguments);
			ScreenHandler handler = player.currentScreenHandler;
			if (!(handler instanceof AnvilScreenHandler anvilHandler)) {
				throw arguments.getError("Not in anvil gui");
			}
			FunctionValue predicate1 = arguments.getNextFunction();
			FunctionValue predicate2 = arguments.getNextFunction();
			boolean firstValid = false;
			boolean secondValid = false;
			MinecraftClient client = ArucasMinecraftExtension.getClient();
			for (Slot slot : anvilHandler.slots) {
				if (firstValid && secondValid) {
					break;
				}
				ItemStack itemStack = slot.getStack();
				if (!firstValid) {
					Value predicateReturn = predicate1.call(arguments.getContext().createBranch(), ArucasList.arrayListOf(new ItemStackValue(itemStack)));
					if (predicateReturn instanceof BooleanValue booleanValue && booleanValue.value) {
						firstValid = true;
						client.execute(() -> {
							interactionManager.clickSlot(anvilHandler.syncId, slot.id, 0, SlotActionType.PICKUP, player);
							interactionManager.clickSlot(anvilHandler.syncId, 0, 0, SlotActionType.PICKUP, player);
						});
						continue;
					}
				}
				if (!secondValid) {
					Value predicateReturn = predicate2.call(arguments.getContext().createBranch(), ArucasList.arrayListOf(new ItemStackValue(itemStack)));
					if (predicateReturn instanceof BooleanValue booleanValue && booleanValue.value) {
						secondValid = true;
						client.execute(() -> {
							interactionManager.clickSlot(anvilHandler.syncId, slot.id, 0, SlotActionType.PICKUP, player);
							interactionManager.clickSlot(anvilHandler.syncId, 1, 0, SlotActionType.PICKUP, player);
						});
					}
				}
			}
			anvilHandler.updateResult();
			if (anvilHandler.getLevelCost() > player.experienceLevel && !player.isCreative()) {
				return NumberValue.of(anvilHandler.getLevelCost());
			}
			interactionManager.clickSlot(anvilHandler.syncId, 2, 0, SlotActionType.QUICK_MOVE, player);
			return BooleanValue.of(firstValid && secondValid);
		}

		private Value anvilRename(Arguments arguments) throws CodeError {
			ClientPlayerInteractionManager interactionManager = EssentialUtils.getInteractionManager();
			if (interactionManager == null) {
				return NullValue.NULL;
			}
			ClientPlayerEntity player = this.getPlayer(arguments);
			ScreenHandler handler = player.currentScreenHandler;
			if (!(handler instanceof AnvilScreenHandler anvilHandler)) {
				throw arguments.getError("Not in anvil gui");
			}
			String newName = arguments.getNextGeneric(StringValue.class);
			FunctionValue functionValue = arguments.getNextFunction();
			boolean valid = false;
			MinecraftClient client = ArucasMinecraftExtension.getClient();
			for (Slot slot : handler.slots) {
				ItemStack itemStack = slot.getStack();
				if (!itemStack.getName().asString().equals(newName)) {
					Value predicateReturn = functionValue.call(arguments.getContext().createBranch(), ArucasList.arrayListOf(new ItemStackValue(itemStack)));
					if (predicateReturn instanceof BooleanValue booleanValue && booleanValue.value) {
						valid = true;
						client.execute(() -> interactionManager.clickSlot(anvilHandler.syncId, slot.id, 0, SlotActionType.QUICK_MOVE, player));
						break;
					}
				}
			}
			anvilHandler.updateResult();
			anvilHandler.setNewItemName(newName);
			EssentialUtils.getNetworkHandler().sendPacket(new RenameItemC2SPacket(newName));
			if (anvilHandler.getLevelCost() > player.experienceLevel && !player.isCreative()) {
				return NumberValue.of(anvilHandler.getLevelCost());
			}
			client.execute(() -> interactionManager.clickSlot(anvilHandler.syncId, 2, 0, SlotActionType.QUICK_MOVE, player));
			return BooleanValue.of(valid);
		}

		private Value stonecutter(Arguments arguments) throws CodeError {
			ClientPlayerInteractionManager interactionManager = EssentialUtils.getInteractionManager();
			if (interactionManager == null) {
				return BooleanValue.FALSE;
			}
			ClientPlayerEntity player = this.getPlayer(arguments);
			ScreenHandler handler = player.currentScreenHandler;
			if (!(handler instanceof StonecutterScreenHandler cutterHandler)) {
				throw arguments.getError("Not in stonecutter gui");
			}
			Item itemInput = arguments.getNextGeneric(ItemStackValue.class).getItem();
			Item itemOutput = arguments.getNextGeneric(ItemStackValue.class).getItem();
			boolean valid = false;
			MinecraftClient client = ArucasMinecraftExtension.getClient();
			for (Slot slot : cutterHandler.slots) {
				if (slot.getStack().getItem() == itemInput) {
					client.execute(() -> interactionManager.clickSlot(cutterHandler.syncId, slot.id, 0, SlotActionType.QUICK_MOVE, player));
					valid = true;
					break;
				}
			}
			if (!valid) {
				return BooleanValue.FALSE;
			}
			List<StonecuttingRecipe> stonecuttingRecipes = cutterHandler.getAvailableRecipes();
			for (int i = 0; i < stonecuttingRecipes.size(); i++) {
				if (stonecuttingRecipes.get(i).getOutput().getItem() == itemOutput) {
					int iFinal = i;
					client.execute(() -> {
						interactionManager.clickButton(cutterHandler.syncId, iFinal);
						interactionManager.clickSlot(cutterHandler.syncId, 1, 0, SlotActionType.QUICK_MOVE, player);
					});
					return BooleanValue.TRUE;
				}
			}
			throw arguments.getError("Recipe does not exist");
		}

		private Value fakeLook(Arguments arguments) throws CodeError {
			NumberValue yaw = arguments.skip().getNextNumber();
			NumberValue pitch = arguments.getNextNumber();
			StringValue directionValue = arguments.getNextString();
			NumberValue durationValue = arguments.getNextNumber();
			Direction direction = Objects.requireNonNullElse(Direction.byName(directionValue.value), Direction.DOWN);
			int duration = MathHelper.ceil(durationValue.value);
			duration = duration > 0 ? duration : 20;
			MinecraftClient client = ArucasMinecraftExtension.getClient();
			int finalDuration = duration;
			client.execute(() -> {
				BetterAccurateBlockPlacement.fakeYaw = yaw.value.floatValue();
				BetterAccurateBlockPlacement.fakePitch = pitch.value.floatValue();
				BetterAccurateBlockPlacement.fakeDirection = direction;
				BetterAccurateBlockPlacement.requestedTicks = finalDuration;
			});
			return NullValue.NULL;
		}

		private Value swapPlayerSlotWithHotbar(Arguments arguments) throws CodeError {
			ClientPlayerEntity player = this.getPlayer(arguments);
			NumberValue slotToSwap = arguments.getNextNumber();
			ClientPlayNetworkHandler networkHandler = ArucasMinecraftExtension.getNetworkHandler();
			if (slotToSwap.value < 0 || slotToSwap.value > player.getInventory().main.size()) {
				throw arguments.getError("That slot is out of bounds");
			}
			int prepareSlot = player.getInventory().getSwappableHotbarSlot();
			ArucasMinecraftExtension.getClient().execute(() -> {
				networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(prepareSlot));
				player.getInventory().swapSlotWithHotbar(slotToSwap.value.intValue());
				networkHandler.sendPacket(new PickFromInventoryC2SPacket(slotToSwap.value.intValue()));
			});
			return NullValue.NULL;
		}

		private Value updateBreakingBlock(Arguments arguments) throws CodeError {
			ClientPlayerInteractionManager interactionManager = ArucasMinecraftExtension.getInteractionManager();
			ClientPlayerEntity player = this.getPlayer(arguments);
			double x = arguments.getNextGeneric(NumberValue.class);
			double y = arguments.getNextGeneric(NumberValue.class);
			double z = arguments.getNextGeneric(NumberValue.class);
			BlockPos blockPos = new BlockPos(x, y, z);
			if (ArucasMinecraftExtension.getWorld().isAir(blockPos)) {
				return NullValue.NULL;
			}
			ArucasMinecraftExtension.getClient().execute(() -> interactionManager.updateBlockBreakingProgress(blockPos, Direction.UP));
			player.swingHand(Hand.MAIN_HAND);
			return NullValue.NULL;
		}

		private Value updateBreakingBlockPos(Arguments arguments) throws CodeError {
			ClientPlayerInteractionManager interactionManager = ArucasMinecraftExtension.getInteractionManager();
			ClientPlayerEntity player = this.getPlayer(arguments);
			PosValue posValue = arguments.getNext(PosValue.class);
			BlockPos blockPos = new BlockPos(posValue.value);
			if (ArucasMinecraftExtension.getWorld().isAir(blockPos)) {
				return NullValue.NULL;
			}
			ArucasMinecraftExtension.getClient().execute(() -> interactionManager.updateBlockBreakingProgress(blockPos, Direction.UP));
			player.swingHand(Hand.MAIN_HAND);
			return NullValue.NULL;
		}

		private Value attackBlock(Arguments arguments) throws CodeError {
			ClientPlayerInteractionManager interactionManager = ArucasMinecraftExtension.getInteractionManager();
			double x = arguments.skip().getNextGeneric(NumberValue.class);
			double y = arguments.getNextGeneric(NumberValue.class);
			double z = arguments.getNextGeneric(NumberValue.class);
			StringValue stringValue = arguments.getNextString();
			Direction direction = Objects.requireNonNullElse(Direction.byName(stringValue.value), Direction.DOWN);
			ArucasMinecraftExtension.getClient().execute(() -> interactionManager.attackBlock(new BlockPos(x, y, z), direction));
			return NullValue.NULL;
		}

		private Value attackBlockPos(Arguments arguments) throws CodeError {
			ClientPlayerInteractionManager interactionManager = ArucasMinecraftExtension.getInteractionManager();
			PosValue posValue = arguments.skip().getNext(PosValue.class);
			StringValue stringValue = arguments.getNextString();
			Direction direction = Objects.requireNonNullElse(Direction.byName(stringValue.value), Direction.DOWN);
			ArucasMinecraftExtension.getClient().execute(() -> interactionManager.attackBlock(new BlockPos(posValue.value), direction));
			return NullValue.NULL;
		}

		private Value interactBlock(Arguments arguments) throws CodeError {
			ClientPlayerEntity player = this.getPlayer(arguments);
			double x = arguments.getNextGeneric(NumberValue.class);
			double y = arguments.getNextGeneric(NumberValue.class);
			double z = arguments.getNextGeneric(NumberValue.class);
			StringValue stringValue = arguments.getNextString();
			Direction direction = Objects.requireNonNullElse(Direction.byName(stringValue.value), Direction.DOWN);
			BlockHitResult hitResult = new BlockHitResult(new Vec3d(x, y, z), direction, new BlockPos(x, y, z), false);
			ClientPlayerInteractionManager interactionManager = ArucasMinecraftExtension.getInteractionManager();
			ClientWorld world = ArucasMinecraftExtension.getWorld();
			ArucasMinecraftExtension.getClient().execute(() -> interactionManager.interactBlock(player, world, Hand.MAIN_HAND, hitResult));
			return NullValue.NULL;
		}

		private Value interactBlockPos(Arguments arguments) throws CodeError {
			ClientPlayerEntity player = this.getPlayer(arguments);
			PosValue posValue = arguments.getNext(PosValue.class);
			StringValue stringValue = arguments.getNextString();
			Direction direction = Objects.requireNonNullElse(Direction.byName(stringValue.value), Direction.DOWN);
			BlockHitResult hitResult = new BlockHitResult(posValue.value, direction, new BlockPos(posValue.value), false);
			ClientPlayerInteractionManager interactionManager = ArucasMinecraftExtension.getInteractionManager();
			ClientWorld world = ArucasMinecraftExtension.getWorld();
			ArucasMinecraftExtension.getClient().execute(() -> interactionManager.interactBlock(player, world, Hand.MAIN_HAND, hitResult));
			return NullValue.NULL;
		}

		private Value interactBlockFull(Arguments arguments) throws CodeError {
			//carpet protocol support but why not client side?
			ClientPlayerEntity player = this.getPlayer(arguments);
			double px = arguments.getNextGeneric(NumberValue.class);
			double py = arguments.getNextGeneric(NumberValue.class);
			double pz = arguments.getNextGeneric(NumberValue.class);
			Direction direction = Objects.requireNonNullElse(Direction.byName(arguments.getNextGeneric(StringValue.class)), Direction.DOWN);
			double bx = arguments.getNextGeneric(NumberValue.class);
			double by = arguments.getNextGeneric(NumberValue.class);
			double bz = arguments.getNextGeneric(NumberValue.class);
			boolean bool = arguments.getNextGeneric(BooleanValue.class);
			BlockHitResult hitResult = new BlockHitResult(new Vec3d(px, py, pz), direction, new BlockPos(bx, by, bz), bool);
			ClientPlayerInteractionManager interactionManager = ArucasMinecraftExtension.getInteractionManager();
			ClientWorld world = ArucasMinecraftExtension.getWorld();
			ArucasMinecraftExtension.getClient().execute(() -> interactionManager.interactBlock(player, world, Hand.MAIN_HAND, hitResult));
			return NullValue.NULL;
		}

		private Value interactBlockFullPos(Arguments arguments) throws CodeError {
			ClientPlayerEntity player = this.getPlayer(arguments);
			PosValue posValue = arguments.getNext(PosValue.class);
			StringValue stringValue = arguments.getNextString();
			PosValue blockPosValue = arguments.getNext(PosValue.class);
			Direction direction = Objects.requireNonNullElse(Direction.byName(stringValue.value), Direction.DOWN);
			BlockHitResult hitResult = new BlockHitResult(posValue.value, direction, new BlockPos(blockPosValue.value), false);
			ClientPlayerInteractionManager interactionManager = ArucasMinecraftExtension.getInteractionManager();
			ClientWorld world = ArucasMinecraftExtension.getWorld();
			ArucasMinecraftExtension.getClient().execute(() -> interactionManager.interactBlock(player, world, Hand.MAIN_HAND, hitResult));
			return NullValue.NULL;
		}

		private Value getBlockBreakingSpeed(Arguments arguments) throws CodeError {
			ClientPlayerEntity player = this.getPlayer(arguments);
			BlockValue blockStateValue = arguments.getNext(BlockValue.class);
			float breakingSpeed = player.getBlockBreakingSpeed(blockStateValue.value);
			return NumberValue.of(breakingSpeed);
		}

		private Value tradeIndex(Arguments arguments) throws CodeError {
			NumberValue numberValue = arguments.skip().getNextNumber();
			if (InventoryUtils.tradeAllItems(ArucasMinecraftExtension.getClient(), numberValue.value.intValue(), false)) {
				throw arguments.getError("Not in merchant gui");
			}
			return NullValue.NULL;
		}

		private Value getIndexOfTrade(Arguments arguments) throws CodeError {
			ItemStackValue itemStackValue = arguments.skip().getNext(ItemStackValue.class);
			int index = InventoryUtils.getIndexOfItemInMerchant(ArucasMinecraftExtension.getClient(), itemStackValue.value.getItem());
			if (index == -1) {
				return NullValue.NULL;
			}
			this.checkVillagerValid(index, arguments);
			return NumberValue.of(index);
		}

		private Value getTradeItemForIndex(Arguments arguments) throws CodeError {
			NumberValue numberValue = arguments.skip().getNextNumber();
			try {
				ItemStack itemStack = InventoryUtils.getTrade(ArucasMinecraftExtension.getClient(), numberValue.value.intValue());
				if (itemStack == null) {
					throw arguments.getError("That trade is out of bounds");
				}
				return new ItemStackValue(itemStack);
			}
			catch (RuntimeException e) {
				throw arguments.getError("Not in merchant gui");
			}
		}

		private Value doesVillagerHaveTrade(Arguments arguments) throws CodeError {
			ItemStackValue itemStackValue = arguments.skip().getNext(ItemStackValue.class);
			int code = InventoryUtils.checkHasTrade(ArucasMinecraftExtension.getClient(), itemStackValue.value.getItem());
			return BooleanValue.of(this.checkVillagerValid(code, arguments));
		}

		private Value isTradeDisabled(Arguments arguments) throws CodeError {
			NumberValue numberValue = arguments.skip().getNextNumber();
			int code = InventoryUtils.checkTradeDisabled(ArucasMinecraftExtension.getClient(), numberValue.value.intValue());
			return BooleanValue.of(this.checkVillagerValid(code, arguments));
		}

		private Value getPriceForIndex(Arguments arguments) throws CodeError {
			NumberValue numberValue = arguments.skip().getNextNumber();
			int price = InventoryUtils.checkPriceForTrade(ArucasMinecraftExtension.getClient(), numberValue.value.intValue());
			this.checkVillagerValid(price, arguments);
			return NumberValue.of(price);
		}

		private boolean checkVillagerValid(int code, Arguments arguments) throws RuntimeError {
			boolean bool = false;
			switch (code) {
				case -2 -> throw arguments.getError("Not in merchant gui");
				case -1 -> throw arguments.getError("That trade is out of bounds");
				case 1 -> bool = true;
			}
			return bool;
		}

		private NullValue setKey(Arguments arguments, KeyBinding keyBinding) throws CodeError {
			BooleanValue booleanValue = arguments.skip().getNextBoolean();
			keyBinding.setPressed(booleanValue.value);
			return NullValue.NULL;
		}

		private ClientPlayerEntity getPlayer(Arguments arguments) throws CodeError {
			ClientPlayerEntity player = arguments.getNextGeneric(PlayerValue.class);
			if (player == null) {
				throw arguments.getError("Player was null");
			}
			return player;
		}

		@Override
		public Class<PlayerValue> getValueClass() {
			return PlayerValue.class;
		}
	}
}
