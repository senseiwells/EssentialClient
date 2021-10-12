package essentialclient.feature.clientscript;

import essentialclient.utils.EssentialUtils;
import me.senseiwells.arucas.core.Run;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.functions.FunctionValue;

public enum MinecraftEventFunction {

    ON_CLIENT_TICK("_onClientTick_"),
    ON_CHAT_MESSAGE("_onChatMessage_"),
    ON_HEALTH_UPDATE("_onHealthUpdate_"),
    ON_ITEM_CHANGE("_onItemChange_"),
    ON_TOTEM("_onTotem_"),
    ON_ATTACK("_onAttack_"),
    ON_USE("_onUse_"),
    ON_BLOCK_BROKEN("_onBlockBroken_"),
    ON_PICK_BLOCK("_onPickBlock_"),
    ;

    String functionName;

    MinecraftEventFunction(String functionName) {
        this.functionName = functionName;
    }

    public void tryRunFunction() {
        if (!ClientScript.enabled)
            return;
        new Thread(() -> {
            Value<?> value = Run.symbolTable.get(this.functionName);
            if (!(value instanceof FunctionValue functionValue))
                return;
            try {
                functionValue.execute(null);
            }
            catch (ThrowValue | CodeError e) {
                EssentialUtils.sendMessage(e.getMessage());
            }
            finally {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

}
