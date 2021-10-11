package me.senseiwells.arucas.nodes;

import essentialclient.feature.clientscript.ClientScript;
import me.senseiwells.arucas.throwables.ThrowStop;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.values.BooleanValue;
import me.senseiwells.arucas.values.NullValue;
import me.senseiwells.arucas.values.Value;

public class WhileNode extends Node {
    public final Node condition;
    public final Node body;

    public WhileNode(Node condition, Node body, Context context) {
        super(condition.token, condition.startPos, body.endPos, context);
        this.condition = condition;
        this.body = body;
    }

    @Override
    public Value<?> visit() throws CodeError, ThrowValue {
        while (true) {
            // Added for SC
            if (!ClientScript.enabled)
                throw new ThrowStop();
            // - Sensei
            Value<?> conditionValue = this.condition.visit();
            if (!(conditionValue instanceof BooleanValue booleanValue))
                throw new CodeError(CodeError.ErrorType.ILLEGAL_OPERATION_ERROR, "Condition must result in either 'true' or 'false'", this.startPos, this.endPos);
            if (!booleanValue.value)
                break;
            try {
                this.body.visit();
            }
            catch (ThrowValue tv) {
                if (tv.shouldContinue)
                    continue;
                if (tv.shouldBreak)
                    break;
            }
        }
        return new NullValue().setContext(this.context);
    }
}
