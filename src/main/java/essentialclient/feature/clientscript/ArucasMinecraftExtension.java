package essentialclient.feature.clientscript;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import essentialclient.utils.command.CommandHelper;
import essentialclient.utils.interfaces.ChatHudAccessor;
import essentialclient.utils.interfaces.MinecraftClientInvoker;
import essentialclient.utils.inventory.InventoryUtils;
import me.senseiwells.arucas.api.IArucasExtension;
import me.senseiwells.arucas.extensions.BuiltInFunction;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.ErrorRuntime;
import me.senseiwells.arucas.throwables.ThrowStop;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.Position;
import me.senseiwells.arucas.values.*;
import me.senseiwells.arucas.values.functions.FunctionValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.ScreenshotUtils;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.*;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class ArucasMinecraftExtension implements IArucasExtension {
	private static final String mustBeItem = "String must be item type, for example \"grass_block\" or \"diamond\"";
	
	@Override
	public String getName() {
		return "Minecraft Extension";
	}
	
	@Override
	public Set<BuiltInFunction> getDefinedFunctions() {
		return Set.of(
			new MinecraftFunction("use", "type", (context, function) -> {
				final String error = "Must pass \"hold\", \"stop\" or \"once\" into rightMouse()";
				StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 0, error);
				switch (stringValue.value.toLowerCase()) {
					case "hold" -> this.client().options.keyUse.setPressed(true);
					case "stop" -> this.client().options.keyUse.setPressed(false);
					case "once" -> ((MinecraftClientInvoker)this.client()).rightClickMouseAccessor();
					default -> throw function.throwInvalidParameterError(error);
				}
				return new NullValue();
			}),
			
			new MinecraftFunction("attack", "type", (context, function) -> {
				final String error = "Must pass \"hold\", 'stop\" or \"once\" into leftMouse()";
				StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 0, error);
				switch (stringValue.value.toLowerCase()) {
					case "hold" -> this.client().options.keyAttack.setPressed(true);
					case "stop" -> this.client().options.keyAttack.setPressed(false);
					case "once" -> ((MinecraftClientInvoker)this.client()).leftClickMouseAccessor();
					default -> throw function.throwInvalidParameterError(error);
				}
				return new NullValue();
			}),
			
			new MinecraftFunction("setSelectSlot", "slotNum", (context, function) -> {
				final String error = "Number must be between 1-9";
				NumberValue numberValue = function.getParameterValueOfType(context, NumberValue.class, 0, error);
				if (numberValue.value < 1 || numberValue.value > 9) {
                    throw function.throwInvalidParameterError(error);
                }
				this.player().inventory.selectedSlot = numberValue.value.intValue() - 1;
				return new NullValue();
			}),
			
			new MinecraftFunction("say", "text", (context, function) -> {
                this.player().sendChatMessage(function.getParameterValue(context, 0).value.toString());
				return new NullValue();
			}),
			
			new MinecraftFunction("message", "text", (context, function) -> {
				Value<?> value = function.getParameterValue(context, 0);
                final ClientPlayerEntity player = this.player();
                this.client().execute(() -> {
                    player.sendMessage(new LiteralText(value.toString()), false);
                });
				return new NullValue();
			}),
			
			new MinecraftFunction("messageActionBar", "text", (context, function) -> {
				Value<?> value = function.getParameterValue(context, 0);
                final ClientPlayerEntity player = this.player();
                this.client().execute(() -> {
                    player.sendMessage(new LiteralText(value.toString()), true);
                });
				return new NullValue();
			}),
			
			new MinecraftFunction("inventory", "action", (context, function) -> {
				final String error = "String must be \"open\" or \"close\"";
				StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 0, error);
				switch (stringValue.value) {
					case "open" -> this.client().openScreen(new InventoryScreen(this.player()));
					case "close" -> this.player().closeHandledScreen();
					default -> throw function.throwInvalidParameterError(error);
				}
				return new NullValue();
			}),
			
			new MinecraftFunction("setWalking", "boolean", (context, function) -> setKey(context, function, this.client().options.keyForward)),
			new MinecraftFunction("setSneaking", "boolean", (context, function) -> setKey(context, function, this.client().options.keySneak)),
			
			new MinecraftFunction("setSprinting", "boolean", (context, function) -> {
				BooleanValue booleanValue = function.getParameterValueOfType(context, BooleanValue.class, 0);
				this.player().setSprinting(booleanValue.value);
				return new NullValue();
			}),
			
			new MinecraftFunction("dropItemInHand", "boolean", (context, function) -> {
				BooleanValue booleanValue = function.getParameterValueOfType(context, BooleanValue.class, 0);
				this.player().dropSelectedItem(booleanValue.value);
				return new NullValue();
			}),
			
			new MinecraftFunction("dropAll", "itemType", (context, function) -> {
				StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 0, mustBeItem);
				InventoryUtils.dropAllItemType(this.client().player, stringValue.value);
				return new NullValue();
			}),
			
			new MinecraftFunction("tradeIndex", List.of("index", "boolean"), (context, function) -> {
				NumberValue numberValue = function.getParameterValueOfType(context, NumberValue.class, 0);
				BooleanValue booleanValue = function.getParameterValueOfType(context, BooleanValue.class, 1);
				InventoryUtils.tradeAllItems(this.client(), numberValue.value.intValue(), booleanValue.value);
				return new NullValue();
			}),
			
			new MinecraftFunction("getIndexOfTrade", "itemType", (context, function) -> {
				StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 0, mustBeItem);
				Item item = Registry.ITEM.get(new Identifier(stringValue.value));
				int index = InventoryUtils.getIndexOfItemInMerchant(this.client(), item);
				return new NumberValue(index);
			}),
			
			new MinecraftFunction("tradeFor", List.of("itemType", "boolean"), (context, function) -> {
				StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 0, mustBeItem);
				BooleanValue booleanValue = function.getParameterValueOfType(context, BooleanValue.class, 1);
				Item item = Registry.ITEM.get(new Identifier(stringValue.value));
				int index = InventoryUtils.getIndexOfItemInMerchant(this.client(), item);
				if (index != -1) {
                    InventoryUtils.tradeAllItems(this.client(), index, booleanValue.value);
                }
				return new NullValue();
			}),
			
			new MinecraftFunction("getEnchantmentsForTrade", "value", (context, function) -> {
				Value<?> value = function.getParameterValue(context, 0);
				if (value instanceof NumberValue numberValue) {
                    return new ListValue(InventoryUtils.checkEnchantmentForTrade(this.client(), numberValue.value.intValue()));
                }
				if (value instanceof StringValue stringValue) {
					int index = InventoryUtils.getIndexOfItemInMerchant(this.client(), Registry.ITEM.get(new Identifier(stringValue.value)));
					if (index != -1) {
                        return new ListValue(InventoryUtils.checkEnchantmentForTrade(this.client(), index));
                    }
				}
				return new NullValue();
			}),
			
			new MinecraftFunction("screenshot", (context, function) -> {
				final MinecraftClient client = this.client();
				ScreenshotUtils.saveScreenshot(
					client.runDirectory,
					client.getWindow().getWidth(),
					client.getWindow().getHeight(),
					client.getFramebuffer(),
					text -> client.execute(() -> client.inGameHud.getChatHud().addMessage(text))
				);
				return new NullValue();
			}),
			
			new MinecraftFunction("look", List.of("yaw", "pitch"), (context, function) -> {
				NumberValue numberValue = function.getParameterValueOfType(context, NumberValue.class, 0);
				NumberValue numberValue2 = function.getParameterValueOfType(context, NumberValue.class, 1);
				this.player().yaw = numberValue.value.floatValue();
				this.player().pitch = numberValue2.value.floatValue();
				return new NullValue();
			}),
			
			new MinecraftFunction("jump", (context, function) -> {
				if (this.player().isOnGround()) {
                    this.player().jump();
                }
				return new NullValue();
			}),
			
			new MinecraftFunction("hold", (context, function) -> {
				try {
					Thread.sleep(Long.MAX_VALUE);
				}
				catch (InterruptedException e) {
					throw new ThrowStop();
				}
				return new NullValue();
			}),
			
			new MinecraftFunction("getCurrentSlot", (context, function) -> {
				return new NumberValue(this.player().inventory.selectedSlot + 1);
			}),
			
			new MinecraftFunction("getHeldItem", (context, function) -> {
				return new StringValue(Registry.ITEM.getId(this.player().inventory.getMainHandStack().getItem()).getNamespace());
			}),
			
			new MinecraftFunction("getStatusEffects", (context, function) -> { // (could throw NullPointerException)
				List<Value<?>> potionList = new ArrayList<>();
				this.player().getStatusEffects().forEach(s -> potionList.add(new StringValue(Objects.requireNonNull(Registry.STATUS_EFFECT.getId(s.getEffectType())).getPath())));
				return new ListValue(potionList);
			}),
			
			new MinecraftFunction("getLookingAtBlock", (context, function) -> {
				HitResult result = this.player().raycast(20D, 0.0F, true);
				if (result.getType() == HitResult.Type.BLOCK) {
					BlockPos blockPos = ((BlockHitResult) result).getBlockPos();
					return new StringValue(Registry.BLOCK.getId(this.world().getBlockState(blockPos).getBlock()).getPath());
				}
				return new StringValue("air");
			}),
			
			new MinecraftFunction("getLookingAtEntity", (context, function) -> {
                Entity targetedEntity = this.client().targetedEntity;
				if (targetedEntity != null) {
                    return new StringValue(Registry.ENTITY_TYPE.getId(targetedEntity.getType()).getPath());
                }
				return new StringValue("none");
			}),
			
			new MinecraftFunction("getHealth", (context, function) -> {
                return new NumberValue(this.player().getHealth());
            }),
			
			new MinecraftFunction("getPos", (context, function) -> {
				List<Value<?>> list = new ArrayList<>();
				list.add(new NumberValue(this.player().getX()));
				list.add(new NumberValue(this.player().getY()));
				list.add(new NumberValue(this.player().getZ()));
				float yaw = this.player().yaw % 360;
				list.add(new NumberValue(yaw < -180 ? 360 + yaw : yaw));
				list.add(new NumberValue(this.player().pitch));
				return new ListValue(list);
			}),
			
			new MinecraftFunction("getDimension", (context, function) -> {
				return new StringValue(this.world().getRegistryKey().getValue().getPath());
			}),
			
			new MinecraftFunction("getBlockAt", List.of("x", "y", "z"), (context, function) -> {
				final String error = "Position must be in range of player";
				NumberValue num1 = function.getParameterValueOfType(context, NumberValue.class, 0, error);
				NumberValue num2 = function.getParameterValueOfType(context, NumberValue.class, 1, error);
				NumberValue num3 = function.getParameterValueOfType(context, NumberValue.class, 2, error);
				BlockPos blockPos = new BlockPos(Math.floor(num1.value), num2.value, Math.floor(num3.value));
				return new StringValue(Registry.BLOCK.getId(this.world().getBlockState(blockPos).getBlock()).getPath());
			}),
			
			new MinecraftFunction("getScriptsPath", (context, function) -> new StringValue(ClientScript.getDir().toString())),
			
			new MinecraftFunction("isTradeDisabled", "arg", (context, function) -> {
				final String error = "Parameter for isTradeDisabled() should either be an item type (e.g. \"grass_block\") or an index";
				Value<?> value = function.getParameterValue(context, 0);
				if (value instanceof NumberValue numberValue) {
                    return new BooleanValue(InventoryUtils.checkTradeDisabled(this.client(), numberValue.value.intValue()));
                }
				if (value instanceof StringValue stringValue) {
                    return new BooleanValue(InventoryUtils.checkTradeDisabled(this.client(), Registry.ITEM.get(new Identifier(stringValue.value))));
                }
                throw function.throwInvalidParameterError(error);
			}),
			
			new MinecraftFunction("doesVillagerHaveTrade", "itemType", (context, function) -> {
				StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 0, mustBeItem);
				return new BooleanValue(InventoryUtils.checkHasTrade(this.client(), Registry.ITEM.get(new Identifier(stringValue.value))));
			}),
			
			new MinecraftFunction("isInventoryFull", (context, function) -> {
				return new BooleanValue(this.player().inventory.getEmptySlot() == -1);
			}),
			
			new MinecraftFunction("isInInventoryGui", (context, function) -> {
				ScreenHandler screenHandler = this.player().currentScreenHandler;
				return new BooleanValue(
                    this.client().currentScreen instanceof InventoryScreen
                    || screenHandler instanceof GenericContainerScreenHandler
                    || screenHandler instanceof MerchantScreenHandler
                    || screenHandler instanceof HopperScreenHandler
                    || screenHandler instanceof FurnaceScreenHandler
				);
			}),
			
			new MinecraftFunction("isBlockEntity", "block", (context, function) -> {
				StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 0, mustBeItem);
				return new BooleanValue(Registry.BLOCK.get(new Identifier(stringValue.value)).hasBlockEntity());
			}),
			
			new MinecraftFunction("getSlotFor", "itemType", (context, function) -> {
				StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 0, mustBeItem);
				ItemStack itemStack = new ItemStack(Registry.ITEM.get(new Identifier(stringValue.value)));
				ScreenHandler screenHandler = this.player().currentScreenHandler;
				for (Slot slot : screenHandler.slots) {
					if (slot.getStack().getItem() == itemStack.getItem()) {
                        return new NumberValue(slot.id + 1);
                    }
				}
				return new NullValue();
			}),
			
			new MinecraftFunction("getAllSlotsFor", "itemType", (context, function) -> {
				StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 0, mustBeItem);
				ItemStack itemStack = new ItemStack(Registry.ITEM.get(new Identifier(stringValue.value)));
				ScreenHandler screenHandler = this.player().currentScreenHandler;
				List<Value<?>> slotList = new ArrayList<>();
				for (Slot slot : screenHandler.slots) {
					if (slot.getStack().getItem() == itemStack.getItem()) {
                        slotList.add(new NumberValue(slot.id + 1));
                    }
				}
				return new ListValue(slotList);
			}),
			
			new MinecraftFunction("swapSlots", List.of("slot1", "slot2"), (context, function) -> {
				NumberValue numberValue1 = function.getParameterValueOfType(context, NumberValue.class, 0);
				NumberValue numberValue2 = function.getParameterValueOfType(context, NumberValue.class, 1);
				ScreenHandler screenHandler = this.player().currentScreenHandler;
				int tempSlot = (this.player().inventory.selectedSlot + 1) % 9;
				int size = screenHandler.slots.size();
				if (numberValue1.value > size || numberValue1.value < 1 || numberValue2.value > size || numberValue2.value < 1) {
                    throw new ErrorRuntime("That slot is out of bounds!", function.startPos, function.endPos, context);
                }
				this.client().interactionManager.clickSlot(screenHandler.syncId, numberValue1.value.intValue() - 1, tempSlot, SlotActionType.SWAP, this.client().player);
				this.client().interactionManager.clickSlot(screenHandler.syncId, numberValue2.value.intValue() - 1, tempSlot, SlotActionType.SWAP, this.client().player);
				this.client().interactionManager.clickSlot(screenHandler.syncId, numberValue1.value.intValue() - 1, tempSlot, SlotActionType.SWAP, this.client().player);
				return new NullValue();
			}),
			
			new MinecraftFunction("dropSlot", "slot", (context, function) -> {
				NumberValue numberValue = function.getParameterValueOfType(context, NumberValue.class, 0);
				this.client().interactionManager.clickSlot(this.player().currentScreenHandler.syncId, numberValue.value.intValue() - 1, 1, SlotActionType.THROW, this.client().player);
				return new NullValue();
			}),
			
			new MinecraftFunction("getTotalSlots", (context, function) -> {
				ScreenHandler screenHandler = this.player().currentScreenHandler;
				return new NumberValue(screenHandler.slots.size());
			}),
			
			new MinecraftFunction("getItemForSlot", "slotnum", (context, function) -> {
				NumberValue numberValue = function.getParameterValueOfType(context, NumberValue.class, 0);
				ScreenHandler screenHandler = this.player().currentScreenHandler;
				int index = numberValue.value.intValue() - 1;
				if (index > screenHandler.slots.size() || index < 0) {
                    return new NullValue();
                }
				ItemStack itemStack = screenHandler.slots.get(index).getStack();
				return new ListValue(List.of(new StringValue(Registry.ITEM.getId(itemStack.getItem()).getPath()), new NumberValue(itemStack.getCount())));
			}),
			
			new MinecraftFunction("getEnchantmentsForSlot", "slotNum", (context, function) -> {
				NumberValue numberValue = function.getParameterValueOfType(context, NumberValue.class, 0);
				return new ListValue(InventoryUtils.checkEnchantment(this.client(), numberValue.value.intValue() - 1));
			}),
			
			new MinecraftFunction("getDurabilityForSlot", "slotNum", (context, function) -> {
				NumberValue numberValue = function.getParameterValueOfType(context, NumberValue.class, 0);
				return new NumberValue(InventoryUtils.getDurability(this.client(), numberValue.value.intValue() - 1));
			}),
			
			new MinecraftFunction("getLatestChatMessage", (context, function) -> {
				List<ChatHudLine<Text>> chat = ((ChatHudAccessor) this.client().inGameHud.getChatHud()).getMessages();
				if (chat.size() == 0) {
                    return new NullValue();
                }
				List<Value<?>> list = new ArrayList<>();
				list.add(new NumberValue(chat.get(0).getCreationTick()));
				list.add(new StringValue(chat.get(0).getText().getString()));
				return new ListValue(list);
			}),
			
			new MinecraftFunction("getOnlinePlayers", (context, function) -> {
				List<Value<?>> players = new ArrayList<>();
				ClientPlayNetworkHandler networkHandler = this.client().getNetworkHandler();
				if (networkHandler == null || networkHandler.getPlayerList() == null) {
                    return new NullValue();
                }
				networkHandler.getPlayerList().forEach(p -> players.add(new StringValue(p.getProfile().getName())));
				return new ListValue(players);
			}),
			
			new MinecraftFunction("getGamemode", (context, function) -> {
				return new StringValue(this.client().interactionManager.getCurrentGameMode().getName());
			}),
			
			new MinecraftFunction("addCommand", List.of("commandName", "arguments"), (context, function) -> {
				StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 0);
				NumberValue numberValue = function.getParameterValueOfType(context, NumberValue.class, 1);
				int argumentNum = numberValue.value.intValue();
				if (argumentNum < 0 || argumentNum > 5) {
                    throw new ErrorRuntime("Invalid number of arguments", function.startPos, function.endPos, context);
                }
				CommandHelper.functionCommand.add(stringValue.value);
				LiteralArgumentBuilder<ServerCommandSource> command = CommandManager.literal(stringValue.value);
				while (argumentNum > 0) {
					command = command.then(CommandManager.argument(String.valueOf(argumentNum), StringArgumentType.greedyString()));
					argumentNum--;
				}
				CommandHelper.functionCommands.add(command.build());
				CommandHelper.needUpdate = true;
				return new NullValue();
			}),
			
			new MinecraftFunction("getPlayerName", (context, function) -> {
				return new StringValue(this.player().getEntityName());
			}),
			
			new MinecraftFunction("getWeather", (context, function) -> {
				if (this.world().isThundering()) {
                    return new StringValue("thunder");
                }
				if (this.world().isRaining()) {
                    return new StringValue("rain");
                }
				return new StringValue("clear");
			}),
			
			new MinecraftFunction("getTimeOfDay", (context, function) -> {
				return new NumberValue(this.world().getTimeOfDay());
			}),
			
			new MinecraftFunction("isSneaking", (context, function) -> {
				return new BooleanValue(this.player().isSneaking());
			}),
			
			new MinecraftFunction("isSprinting", (context, function) -> {
				return new BooleanValue(this.player().isSprinting());
			}),
			
			new MinecraftFunction("isFalling", (context, function) -> {
				return new BooleanValue(this.player().fallDistance > 0);
			}),
			
			new MinecraftFunction("clearChat", (context, function) -> {
				this.client().inGameHud.getChatHud().clear(true);
				return new NullValue();
			}),
			
			new MinecraftFunction("craft", List.of("recipe"), (context, function) -> {
				ListValue listValue = function.getParameterValueOfType(context, ListValue.class, 0);
				if (!(this.client().currentScreen instanceof HandledScreen<?> handledScreen)) {
                    return new NullValue();
                }
				ItemStack[] itemStacks = new ItemStack[9];
				for (int i = 0; i < listValue.value.size(); i++) {
					if (!(listValue.value.get(i) instanceof StringValue stringValue)) {
                        throw new ErrorRuntime("The recipe must only include strings", function.startPos, function.endPos, context);
                    }
					itemStacks[i] = Registry.ITEM.get(new Identifier(stringValue.value)).getDefaultStack();
				}
				InventoryUtils.tryMoveItemsToCraftingGridSlots(this.client(), itemStacks, handledScreen);
				InventoryUtils.shiftClickSlot(this.client(), handledScreen, 0);
				return new NullValue();
			}),
			
			new MinecraftFunction("schedule", List.of("milliseconds", "function"), (context, function) -> {
				NumberValue numberValue = function.getParameterValueOfType(context, NumberValue.class, 0);
				FunctionValue functionValue = function.getParameterValueOfType(context, FunctionValue.class, 1);
				
                ClientScript.runBranchAsyncFunction((branchContext) -> {
                    Thread.sleep(numberValue.value.longValue());
                    functionValue.call(branchContext, List.of());
                    // System.out.println("WARN: An error was caught in schedule() call, check that you are passing in a valid function");
                });
				return new NullValue();
			}),
			
			new MinecraftFunction("addGameEvent", List.of("eventName", "function"), (context, function) -> {
				String eventName = function.getParameterValueOfType(context, StringValue.class, 0).value;
				FunctionValue functionValue = function.getParameterValueOfType(context, FunctionValue.class, 1);
				eventName = eventName.startsWith("_") ? eventName : "_" + eventName;
				eventName = eventName.endsWith("_") ? eventName : eventName + "_";
				if (!MinecraftEventFunction.isEvent(eventName)) {
                    throw new ErrorRuntime("The event name must be a predefined event", function.startPos, function.endPos, context);
                }
                context.getSymbolTable().getRoot().set(eventName, functionValue);
				return new NullValue();
			})
		);
	}
	
	public MinecraftClient client() throws CodeError {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client == null) {
            throw new ErrorRuntime("MinecraftClient was null", new Position(0, 0, 0, ""), new Position(0, 0, 0, ""));
        }
		return client;
	}
	
	public ClientPlayerEntity player() throws CodeError {
		ClientPlayerEntity player = this.client().player;
		if (player == null) {
			throw new ErrorRuntime("MinecraftClient.player was null", new Position(0, 0, 0, ""), new Position(0, 0, 0, ""));
        }
		return player;
	}
    
    public ClientWorld world() throws CodeError {
        ClientWorld world = this.client().world;
        if (world == null) {
            throw new ErrorRuntime("MinecraftClient.world was null", new Position(0, 0, 0, ""), new Position(0, 0, 0, ""));
        }
        return world;
    }
    
	private static NullValue setKey(Context context, BuiltInFunction function, KeyBinding keyBinding) throws CodeError {
		BooleanValue booleanValue = function.getParameterValueOfType(context, BooleanValue.class, 0);
		keyBinding.setPressed(booleanValue.value);
		return new NullValue();
	}
}
