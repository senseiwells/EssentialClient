package essentialclient.feature.clientmacro;

import essentialclient.utils.interfaces.MinecraftClientInvoker;
import essentialclient.utils.inventory.InventoryUtils;
import me.senseiwells.arucas.throwables.Error;
import me.senseiwells.arucas.throwables.ErrorRuntime;
import me.senseiwells.arucas.values.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.ScreenshotRecorder;
import net.minecraft.item.Item;
import net.minecraft.screen.*;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class MinecraftFunctionValue extends BaseFunctionValue {

    private static final MinecraftClient client = MinecraftClient.getInstance();

    private MinecraftFunction function;

    public MinecraftFunctionValue(String name) {
        super(name);
    }

    @Override
    public Value<?> execute(List<Value<?>> arguments) throws Error {
        if (client.player == null)
            throw new ErrorRuntime("Player is null", this.startPos, this.endPos, this.context);
        this.function = MinecraftFunction.stringToFunction(this.value);
        this.context = this.generateNewContext();
        Value<?> returnValue = new NullValue();
        if (this.function == null)
            throw new ErrorRuntime("Function " + this.value + " is not defined", this.startPos, this.endPos, this.context);
        this.checkAndPopulateArguments(arguments, this.function.argumentNames, this.context);
        switch (this.function) {
            case USE -> this.rightMouseAction();
            case ATTACK -> this.leftMouseAction();
            case SET_SELECT_SLOT -> this.selectSlot();
            case SAY -> client.player.sendChatMessage(this.getValueFromTable(MinecraftFunction.SAY.getArgument(0)).value.toString());
            case MESSAGE -> client.player.sendMessage(new LiteralText(this.getValueFromTable(MinecraftFunction.SAY.getArgument(0)).value.toString()), false);
            case INVENTORY -> this.manageInventory();
            case SET_WALKING -> this.setKey(client.options.keyForward);
            case SET_SPRINTING -> this.setSprint();
            case SET_SNEAKING -> this.setKey(client.options.keySneak);
            case DROP_ITEM_IN_HAND -> this.dropHand();
            case DROP_ALL -> this.dropAll();
            case TRADE_INDEX -> this.tradeIndex();
            case TRADE_FOR -> this.tradeFor();
            case SCREENSHOT -> ScreenshotRecorder.saveScreenshot(client.runDirectory, client.getFramebuffer(), text -> client.execute(() -> client.inGameHud.getChatHud().addMessage(text)));
            case LOOK -> this.look();
            case IS_IN_INVENTORY_GUI -> returnValue = new BooleanValue(this.checkInventoryScreen());
            case IS_INVENTORY_FULL -> returnValue = new BooleanValue(client.player.getInventory().getEmptySlot() != -1);
            case GET_HEALTH -> returnValue = new NumberValue(client.player.getHealth());
            case IS_TRADE_DISABLED -> returnValue = new BooleanValue(this.isTradeDisabled());
            case DOES_VILLAGER_HAVE_TRADE -> returnValue = new BooleanValue(this.doesVillagerHaveTradeFor());
            case GET_CURRENT_SLOT -> returnValue = new NumberValue(client.player.getInventory().selectedSlot + 1);
            case GET_HELD_ITEM -> returnValue = new StringValue(Registry.ITEM.getId(client.player.getInventory().getMainHandStack().getItem()).getNamespace());
            case GET_LOOKING_AT_BLOCK -> returnValue = new StringValue(this.getLookingAtBlock());
            case GET_LOOKING_AT_ENTITY -> returnValue = new StringValue(this.getLookingAtEntity());
            case JUMP -> {
                if (client.player.isOnGround())
                    client.player.jump();
            }
        }
        return returnValue;
    }

    private void rightMouseAction() throws Error {
        Value<?> value = this.getValueFromTable(this.function.getArgument(0));
        if (!(value instanceof StringValue stringValue))
            throw this.throwInvalidParameterError("Must pass \"hold\", \"stop\" or \"once\" into rightMouse()");
        switch (stringValue.value.toLowerCase()) {
            case "hold" -> client.options.keyUse.setPressed(true);
            case "stop" -> client.options.keyUse.setPressed(false);
            case "once" -> ((MinecraftClientInvoker) client).rightClickMouseAccessor();
            default -> throw this.throwInvalidParameterError("Must pass \"hold\", \"stop\" or \"once\" into rightMouse()");
        }
    }

    private void leftMouseAction() throws Error {
        Value<?> value = this.getValueFromTable(this.function.getArgument(0));
        if (!(value instanceof StringValue stringValue))
            throw this.throwInvalidParameterError("Must pass \"hold\", 'stop\" or \"once\" into leftMouse()");
        switch (stringValue.value.toLowerCase()) {
            case "hold" -> client.options.keyAttack.setPressed(true);
            case "stop" -> client.options.keyAttack.setPressed(false);
            case "once" -> ((MinecraftClientInvoker) client).leftClickMouseAccessor();
            default -> throw this.throwInvalidParameterError("Must pass \"hold\", \"stop\" or \"once\" into leftMouse()");
        }
    }

    private void setKey(KeyBinding keyBinding) throws Error {
        Value<?> value = this.getValueFromTable(this.function.getArgument(0));
        if (!(value instanceof BooleanValue booleanValue))
            throw this.throwInvalidParameterError("Must pass true or false into set...()");
        keyBinding.setPressed(booleanValue.value);
    }

    private void setSprint() throws Error {
        Value<?> value = this.getValueFromTable(this.function.getArgument(0));
        if (!(value instanceof BooleanValue booleanValue))
            throw this.throwInvalidParameterError("Must pass true or false into setSprinting()");
        assert client.player != null;
        client.player.setSprinting(booleanValue.value);
    }

    private void selectSlot() throws Error {
        Value<?> value = this.getValueFromTable(this.function.getArgument(0));
        if (!(value instanceof NumberValue numberValue) || numberValue.value < 1 || numberValue.value > 9)
            throw this.throwInvalidParameterError("Must pass an integer between 1-9 into setSelectSlot()");
        assert client.player != null;
        client.player.getInventory().selectedSlot = numberValue.value.intValue() - 1;
    }

    private void manageInventory() throws Error {
        Value<?> value = this.getValueFromTable(this.function.getArgument(0));
        if (!(value instanceof StringValue stringValue))
            throw this.throwInvalidParameterError("Must pass 'close' or 'open' into inventory()");
        assert client.player != null;
        switch (stringValue.value) {
            case "open" -> client.setScreen(new InventoryScreen(client.player));
            case "close" -> client.player.closeHandledScreen();
            default -> throw this.throwInvalidParameterError("Must pass 'close' or 'open' into inventory()");
        }
    }

    private void dropHand() throws Error {
        Value<?> value = this.getValueFromTable(this.function.getArgument(0));
        if (!(value instanceof BooleanValue booleanValue))
            throw this.throwInvalidParameterError("Must pass true or false into dropItemInHand()");
        assert client.player != null;
        client.player.dropSelectedItem(booleanValue.value);
    }

    private void dropAll() throws Error {
        Value<?> value = this.getValueFromTable(this.function.getArgument(0));
        if (!(value instanceof StringValue stringValue))
            throw this.throwInvalidParameterError("Must pass item type (e.g. 'grass_block') into inventory()");
        InventoryUtils.dropAllItemType(client.player, stringValue.value);
    }

    private void tradeIndex() throws Error {
        Value<?> value = this.getValueFromTable(this.function.getArgument(0));
        if (!(value instanceof NumberValue numberValue))
            throw this.throwInvalidParameterError("Must pass an integer into parameter 1 for tradeIndex()");
        Value<?> value2 = this.getValueFromTable(this.function.getArgument(1));
        if (!(value2 instanceof BooleanValue booleanValue))
            throw this.throwInvalidParameterError("Must pass true or false into parameter 2 for tradeIndex()");
        InventoryUtils.tradeAllItems(client, numberValue.value.intValue(), booleanValue.value);
    }

    private void tradeFor() throws Error {
        Value<?> value = this.getValueFromTable(this.function.getArgument(0));
        if (!(value instanceof StringValue stringValue))
            throw this.throwInvalidParameterError("Must pass an item type (e.g. 'grass_block') into parameter 1 for tradeFor()");
        Value<?> value2 = this.getValueFromTable(this.function.getArgument(1));
        if (!(value2 instanceof BooleanValue booleanValue))
            throw this.throwInvalidParameterError("Must pass true or false into parameter 2 for tradeFor()");
        Item item = Registry.ITEM.get(new Identifier(stringValue.value));
        int index = InventoryUtils.getIndexOfItem(client, item);
        if (index == -1)
            throw new ErrorRuntime("Villager does not have that trade", this.startPos, this.endPos, this.context);
        InventoryUtils.tradeAllItems(client, index, booleanValue.value);
    }

    private void look() throws Error {
        Value<?> value = this.getValueFromTable(this.function.getArgument(0));
        Value<?> value2 = this.getValueFromTable(this.function.getArgument(1));
        if (!(value instanceof NumberValue numberValue) || !(value2 instanceof NumberValue numberValue2))
            throw this.throwInvalidParameterError("Both parameters must be numbers for look()");
        assert client.player != null;
        client.player.setYaw(numberValue.value);
        client.player.setPitch(numberValue2.value);
    }

    private boolean isTradeDisabled() throws Error {
        Value<?> value = this.getValueFromTable(this.function.getArgument(0));
        if (value instanceof NumberValue numberValue)
            return InventoryUtils.checkTradeDisabled(client, numberValue.value.intValue());
        if (value instanceof StringValue stringValue)
            return InventoryUtils.checkTradeDisabled(client, Registry.ITEM.get(new Identifier(stringValue.value)));
        throw this.throwInvalidParameterError("Parameter for isTradeDisabled() should either be an item type (e.g. 'grass_block') or an index");
    }

    private boolean doesVillagerHaveTradeFor() throws Error {
        Value<?> value = this.getValueFromTable(this.function.getArgument(0));
        if (!(value instanceof StringValue stringValue))
            throw this.throwInvalidParameterError("Must pass an item type (e.g. 'grass_block') into parameter 1 for doesVillagerHaveTradeFor()");
        return InventoryUtils.checkHasTrade(client, Registry.ITEM.get(new Identifier(stringValue.value)));
    }

    private String getLookingAtBlock() {
        assert client.player != null;
        HitResult result = client.player.raycast(20D, 0.0F, true);
        if (result.getType() == HitResult.Type.BLOCK) {
            BlockPos blockPos = ((BlockHitResult) result).getBlockPos();
            return Registry.BLOCK.getId(client.player.world.getBlockState(blockPos).getBlock()).getPath();
        }
        return "air";
    }

    private String getLookingAtEntity() {
        if (client.targetedEntity != null)
            return Registry.ENTITY_TYPE.getId(client.targetedEntity.getType()).getPath();
        return "air";
    }

    private boolean checkInventoryScreen() {
        assert client.player != null;
        ScreenHandler screenHandler = client.player.currentScreenHandler;
        return  screenHandler instanceof GenericContainerScreenHandler ||
                screenHandler instanceof MerchantScreenHandler ||
                screenHandler instanceof HopperScreenHandler ||
                screenHandler instanceof FurnaceScreenHandler ||
                client.currentScreen instanceof InventoryScreen;
    }

    @Override
    public Value<?> copy() {
        return new MinecraftFunctionValue(this.value).setPos(this.startPos, this.endPos).setContext(this.context);
    }

    public enum MinecraftFunction {

        //Action
        USE("use", "type"),
        ATTACK("attack", "type"),
        SET_SELECT_SLOT("setSelectSlot", "slotNum"),
        SAY("say", "text"),
        MESSAGE("message", "text"),
        INVENTORY("inventory", "action"),
        SET_WALKING("setWalking", "boolean"),
        SET_SPRINTING("setSprinting", "boolean"),
        SET_SNEAKING("setSneaking", "boolean"),
        DROP_ITEM_IN_HAND("dropItemInHand", "boolean"),
        DROP_ALL("dropAll", "itemType"),
        TRADE_INDEX("tradeIndex", new String[]{"index", "boolean"}),
        TRADE_FOR("tradeFor", new String[]{"itemType", "boolean"}),
        SCREENSHOT("screenshot"),
        LOOK("look", new String[]{"yaw", "pitch"}),
        JUMP("jump"),

        //Return value
        GET_CURRENT_SLOT("getCurrentSlot"),
        GET_HELD_ITEM("getHeldItem"),
        GET_LOOKING_AT_BLOCK("getLookingAtBlock"),
        GET_LOOKING_AT_ENTITY("getLookingAtEntity"),
        GET_HEALTH("getHealth"),

        //Return boolean
        IS_TRADE_DISABLED("isTradeDisabled", "arg"),
        DOES_VILLAGER_HAVE_TRADE("doesVillagerHaveTrade", "itemType"),
        IS_INVENTORY_FULL("isInventoryFull"),
        IS_IN_INVENTORY_GUI("isInInventoryGui");

        public String name;
        List<String> argumentNames;

        MinecraftFunction(String name, String[] argumentNames) {
            this.name = name;
            this.argumentNames = Arrays.stream(argumentNames).toList();
        }

        MinecraftFunction(String name, String argumentName) {
            this(name, new String[]{argumentName});
        }

        MinecraftFunction(String name) {
            this.name = name;
            this.argumentNames = new LinkedList<>();
        }

        public static MinecraftFunction stringToFunction(String word) {
            for (MinecraftFunction value : MinecraftFunction.values()) {
                if (word.equals(value.name))
                    return value;
            }
            return null;
        }

        private String getArgument(int index) {
            return this.argumentNames.get(index);
        }
    }
}
