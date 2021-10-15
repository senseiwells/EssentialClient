package essentialclient.feature.clientscript;

import essentialclient.utils.EssentialUtils;
import me.senseiwells.arucas.core.Run;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.functions.FunctionValue;

import java.util.List;

public enum MinecraftEventFunction {

    ON_CLIENT_TICK("_onClientTick_"),

    ON_HEALTH_UPDATE("_onHealthUpdate_"),
    ON_TOTEM("_onTotem_"),
    ON_ATTACK("_onAttack_"),
    ON_USE("_onUse_"),
    ON_PICK_BLOCK("_onPickBlock_"),
    ON_CLOSE_SCREEN("_onCloseScreen_"),

    ON_COMMAND("_onCommand_"),
    ON_OPEN_SCREEN("_onOpenScreen_"),
    ON_PICKUP("_onPickUp_"),
    ON_DROP_ITEM("_onDropItem_"),
    ON_EAT("_onEat_"),
    ON_INTERACT_ITEM("_onInteractItem_"),       // fun _onInteractItem_(itemType) { code }
    ON_INTERACT_BLOCK("_onInteractBlock_"),     // fun _onInteractBlock_(blockType) { code }
    ON_INTERACT_ENTITY("_onInteractEntity_"),   // fun _onInteractEntity_(entityType) {code }
    ON_CHAT_MESSAGE("_onChatMessage_"),         // fun _onChatMessage(message) { code }
    ON_GAMEMODE_CHANGE("_onGamemodeChange_"),   // fun _onGamemodeChange(gamemode) { code }
    ON_CLICK_SLOT("_onClickSlot_"),             // fun _onClickSlot_(slot) { code }
    ON_BLOCK_BROKEN("_onBlockBroken_"),         // fun _onBlockBroken(block, x, y, z) { code }
    ;

    String functionName;

    MinecraftEventFunction(String functionName) {
        this.functionName = functionName;
    }

    public void tryRunFunction(List<Value<?>> arguments) {
        if (!ClientScript.enabled)
            return;
        new Thread(() -> {
            try {
                Value<?> value = Run.symbolTable.get(this.functionName);
                if (!(value instanceof FunctionValue functionValue))
                    return;
                functionValue.execute(arguments);
            }
            catch (ThrowValue | CodeError e) {
                EssentialUtils.sendMessage(e.getMessage());
            }
        }, "Minecraft Event Thread").start();
    }

    public void tryRunFunction() {
        this.tryRunFunction(null);
    }
}
