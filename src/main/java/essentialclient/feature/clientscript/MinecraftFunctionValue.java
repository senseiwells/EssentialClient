package essentialclient.feature.clientscript;

import essentialclient.utils.interfaces.MinecraftClientInvoker;
import essentialclient.utils.inventory.InventoryUtils;
import me.senseiwells.arucas.throwables.Error;
import me.senseiwells.arucas.throwables.ErrorRuntime;
import me.senseiwells.arucas.throwables.ThrowStop;
import me.senseiwells.arucas.values.*;
import net.minecraft.block.BlockEntityProvider;
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
    private static final String mustBeItem = "String must be item type, for example \"grass_block\" or \"diamond\"";

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
            case IS_IN_INVENTORY_GUI -> returnValue = this.checkInventoryScreen();
            case IS_INVENTORY_FULL -> returnValue = new BooleanValue(client.player.getInventory().getEmptySlot() != -1);
            case GET_HEALTH -> returnValue = new NumberValue(client.player.getHealth());
            case IS_TRADE_DISABLED -> returnValue = this.isTradeDisabled();
            case DOES_VILLAGER_HAVE_TRADE -> returnValue = this.doesVillagerHaveTradeFor();
            case GET_CURRENT_SLOT -> returnValue = new NumberValue(client.player.getInventory().selectedSlot + 1);
            case GET_HELD_ITEM -> returnValue = new StringValue(Registry.ITEM.getId(client.player.getInventory().getMainHandStack().getItem()).getNamespace());
            case GET_LOOKING_AT_BLOCK -> returnValue = this.getLookingAtBlock();
            case GET_LOOKING_AT_ENTITY -> returnValue = this.getLookingAtEntity();
            case GET_POS -> returnValue = this.getPos();
            case GET_BLOCK_AT -> returnValue = this.getBlockAt();
            case GET_DIMENSION -> returnValue = new StringValue(client.player.world.getRegistryKey().getValue().getPath());
            case IS_BLOCK_ENTITY -> returnValue = this.isBlockEntity();
            case GET_SCRIPTS_PATH -> returnValue = new StringValue(ClientScript.getDir().toString());
            case HOLD -> this.hold();
            case JUMP -> {
                if (client.player.isOnGround())
                    client.player.jump();
            }
        }
        return returnValue;
    }

    private void rightMouseAction() throws Error {
        final String error = "Must pass \"hold\", 'stop\" or \"once\" into rightMouse()";
        StringValue stringValue = (StringValue) this.getValueForType(StringValue.class, 0, error);
        switch (stringValue.value.toLowerCase()) {
            case "hold" -> client.options.keyUse.setPressed(true);
            case "stop" -> client.options.keyUse.setPressed(false);
            case "once" -> ((MinecraftClientInvoker) client).rightClickMouseAccessor();
            default -> throw this.throwInvalidParameterError(error);
        }
    }

    private void leftMouseAction() throws Error {
        final String error = "Must pass \"hold\", 'stop\" or \"once\" into leftMouse()";
        StringValue stringValue = (StringValue) this.getValueForType(StringValue.class, 0, error);
        switch (stringValue.value.toLowerCase()) {
            case "hold" -> client.options.keyAttack.setPressed(true);
            case "stop" -> client.options.keyAttack.setPressed(false);
            case "once" -> ((MinecraftClientInvoker) client).leftClickMouseAccessor();
            default -> throw this.throwInvalidParameterError(error);
        }
    }

    private void setKey(KeyBinding keyBinding) throws Error {
        BooleanValue booleanValue = (BooleanValue) this.getValueForType(BooleanValue.class, 0, null);
        keyBinding.setPressed(booleanValue.value);
    }

    private void setSprint() throws Error {
        BooleanValue booleanValue = (BooleanValue) this.getValueForType(BooleanValue.class, 0, null);
        assert client.player != null;
        client.player.setSprinting(booleanValue.value);
    }

    private void selectSlot() throws Error {
        final String error = "Number must be between 1-9";
        NumberValue numberValue = (NumberValue) this.getValueForType(NumberValue.class, 0, error);
        if (numberValue.value < 1 || numberValue.value > 9)
            throw this.throwInvalidParameterError(error);
        assert client.player != null;
        client.player.getInventory().selectedSlot = numberValue.value.intValue() - 1;
    }

    private void manageInventory() throws Error {
        final String error = "String must be \"open\" or \"close\"";
        StringValue stringValue = (StringValue) this.getValueForType(StringValue.class, 0, error);
        assert client.player != null;
        switch (stringValue.value) {
            case "open" -> client.setScreen(new InventoryScreen(client.player));
            case "close" -> client.player.closeHandledScreen();
            default -> throw this.throwInvalidParameterError(error);
        }
    }

    private void dropHand() throws Error {
        BooleanValue booleanValue = (BooleanValue) this.getValueForType(BooleanValue.class, 0, null);
        assert client.player != null;
        client.player.dropSelectedItem(booleanValue.value);
    }

    private void dropAll() throws Error {
        StringValue stringValue = (StringValue) this.getValueForType(StringValue.class, 0, mustBeItem);
        InventoryUtils.dropAllItemType(client.player, stringValue.value);
    }

    private void tradeIndex() throws Error {
        NumberValue numberValue = (NumberValue) this.getValueForType(NumberValue.class, 0, null);
        BooleanValue booleanValue = (BooleanValue) this.getValueForType(BooleanValue.class, 1, null);
        InventoryUtils.tradeAllItems(client, numberValue.value.intValue(), booleanValue.value);
    }

    private void tradeFor() throws Error {
        StringValue stringValue = (StringValue) this.getValueForType(StringValue.class, 0, mustBeItem);
        BooleanValue booleanValue = (BooleanValue) this.getValueForType(BooleanValue.class, 1, null);
        Item item = Registry.ITEM.get(new Identifier(stringValue.value));
        int index = InventoryUtils.getIndexOfItem(client, item);
        if (index == -1)
            throw new ErrorRuntime("Villager does not have that trade", this.startPos, this.endPos, this.context);
        InventoryUtils.tradeAllItems(client, index, booleanValue.value);
    }

    private void look() throws Error {
        NumberValue numberValue = (NumberValue) this.getValueForType(NumberValue.class, 0, null);
        NumberValue numberValue2 = (NumberValue) this.getValueForType(NumberValue.class, 1, null);
        assert client.player != null;
        client.player.setYaw(numberValue.value);
        client.player.setYaw(numberValue2.value);
    }

    private BooleanValue isTradeDisabled() throws Error {
        final String error = "Parameter for isTradeDisabled() should either be an item type (e.g. \"grass_block\") or an index";
        Value<?> value = this.getValueFromTable(this.function.getArgument(0));
        if (value instanceof NumberValue numberValue)
            return new BooleanValue(InventoryUtils.checkTradeDisabled(client, numberValue.value.intValue()));
        if (value instanceof StringValue stringValue)
            return new BooleanValue(InventoryUtils.checkTradeDisabled(client, Registry.ITEM.get(new Identifier(stringValue.value))));
        throw this.throwInvalidParameterError(error);
    }

    private BooleanValue doesVillagerHaveTradeFor() throws Error {
        StringValue stringValue = (StringValue) this.getValueForType(StringValue.class, 0, mustBeItem);
        return new BooleanValue(InventoryUtils.checkHasTrade(client, Registry.ITEM.get(new Identifier(stringValue.value))));
    }

    private StringValue getLookingAtBlock() {
        assert client.player != null;
        HitResult result = client.player.raycast(20D, 0.0F, true);
        if (result.getType() == HitResult.Type.BLOCK) {
            BlockPos blockPos = ((BlockHitResult) result).getBlockPos();
            return new StringValue(Registry.BLOCK.getId(client.player.world.getBlockState(blockPos).getBlock()).getPath());
        }
        return new StringValue("air");
    }

    private BooleanValue isBlockEntity() throws Error {
        StringValue stringValue = (StringValue) this.getValueForType(StringValue.class, 0, mustBeItem);
        return new BooleanValue(Registry.BLOCK.get(new Identifier(stringValue.value)) instanceof BlockEntityProvider);
    }

    private StringValue getLookingAtEntity() {
        if (client.targetedEntity != null)
            return new StringValue(Registry.ENTITY_TYPE.getId(client.targetedEntity.getType()).getPath());
        return new StringValue("none");
    }

    private NumberValue getPos() throws Error {
        final String error = "String must be \"x\", \"y\", \"z\", \"yaw\", or \"pitch\"";
        StringValue stringValue = (StringValue) this.getValueForType(StringValue.class, 0, error);
        float floatValue;
        assert client.player != null;
        switch (stringValue.value) {
            case "x" -> floatValue = (float) client.player.getX();
            case "y" -> floatValue = (float) client.player.getY();
            case "z" -> floatValue = (float) client.player.getZ();
            case "pitch" -> floatValue = client.player.getPitch();
            case "yaw" -> {
                floatValue = client.player.getYaw() % 360;
                floatValue = floatValue < -180 ? 360 + floatValue : floatValue;
            }
            default -> throw this.throwInvalidParameterError(error);
        }
        return new NumberValue(floatValue);
    }

    private StringValue getBlockAt() throws Error {
        final String error = "Position must be in range of player";
        NumberValue num1 = (NumberValue) this.getValueForType(NumberValue.class, 0, error);
        NumberValue num2 = (NumberValue) this.getValueForType(NumberValue.class, 1, error);
        NumberValue num3 = (NumberValue) this.getValueForType(NumberValue.class, 2, error);
        BlockPos blockPos = new BlockPos(Math.floor(num1.value), num2.value, Math.floor(num3.value));
        assert client.player != null;
        return new StringValue(Registry.BLOCK.getId(client.player.world.getBlockState(blockPos).getBlock()).getPath());
    }

    private BooleanValue checkInventoryScreen() {
        assert client.player != null;
        ScreenHandler screenHandler = client.player.currentScreenHandler;
        return  new BooleanValue(
                screenHandler instanceof GenericContainerScreenHandler ||
                        screenHandler instanceof MerchantScreenHandler ||
                        screenHandler instanceof HopperScreenHandler ||
                        screenHandler instanceof FurnaceScreenHandler ||
                        client.currentScreen instanceof InventoryScreen
        );
    }

    public Value<?> getValueForType(Class<?> clazz, int index, String additionalInfo) throws Error {
        Value<?> value = this.getValueFromTable(this.function.getArgument(index));
        if (!(clazz.isInstance(value)))
            throw this.throwInvalidParameterError("Must pass " + clazz.getSimpleName() + " into parameter " + (index + 1) + " for " + this.function.name + "()" + (additionalInfo == null ? "" : "\n" + additionalInfo));
        return value;
    }

    private void hold() throws ThrowStop {
        try {
            Thread.sleep(Long.MAX_VALUE);
        }
        catch (InterruptedException e) {
            throw new ThrowStop();
        }
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
        HOLD("hold"),

        //Return value
        GET_CURRENT_SLOT("getCurrentSlot"),
        GET_HELD_ITEM("getHeldItem"),
        GET_LOOKING_AT_BLOCK("getLookingAtBlock"),
        GET_LOOKING_AT_ENTITY("getLookingAtEntity"),
        GET_HEALTH("getHealth"),
        GET_POS("getPos", "axis"),
        GET_DIMENSION("getDimension"),
        GET_BLOCK_AT("getBlockAt", new String[]{"x", "y", "z"}),
        GET_SCRIPTS_PATH("getScriptsPath"),

        //Return boolean
        IS_TRADE_DISABLED("isTradeDisabled", "arg"),
        DOES_VILLAGER_HAVE_TRADE("doesVillagerHaveTrade", "itemType"),
        IS_INVENTORY_FULL("isInventoryFull"),
        IS_IN_INVENTORY_GUI("isInInventoryGui"),
        IS_BLOCK_ENTITY("isBlockEntity", "block");

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
