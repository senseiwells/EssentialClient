package me.senseiwells.arucas.nodes;

import essentialclient.feature.clientscript.ClientScript;
import me.senseiwells.arucas.throwables.ThrowStop;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.throwables.Error;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.utils.Interpreter;
import me.senseiwells.arucas.values.BooleanValue;
import me.senseiwells.arucas.values.NullValue;
import me.senseiwells.arucas.values.Value;

public class WhileNode extends Node {

    Node condition;
    Node body;

    public WhileNode(Node condition, Node body) {
        super(condition.token, condition.startPos, body.endPos);
        this.condition = condition;
        this.body = body;
    }

    @Override
    public Value<?> visit(Interpreter interpreter, Context context) throws Error, ThrowValue {
        while (true) {
            // Added for SC
            if (!ClientScript.enabled)
                throw new ThrowStop();
            // - Sensei
            Value<?> conditionValue = interpreter.visit(this.condition, context);
            if (!(conditionValue instanceof BooleanValue booleanValue))
                throw new Error(Error.ErrorType.ILLEGAL_OPERATION_ERROR, "Condition must result in either 'true' or 'false'", this.startPos, this.endPos);
            if (!booleanValue.value)
                break;
            try {
                interpreter.visit(this.body, context);
            }
            catch (ThrowValue tv) {
                if (tv.shouldContinue)
                    continue;
                if (tv.shouldBreak)
                    break;
            }
        }
        return new NullValue();
    }
}
