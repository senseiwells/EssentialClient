package me.senseiwells.arucas.nodes;

import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.values.BooleanValue;
import me.senseiwells.arucas.values.NumberValue;
import me.senseiwells.arucas.values.Value;

public class UnaryOperatorNode extends Node {
    public final Node node;

    public UnaryOperatorNode(Token token, Node node, Context context) {
        super(token, context);
        this.node = node;
    }

    @Override
    public Value<?> visit() throws CodeError, ThrowValue {
        Value<?> value = this.node.visit();
        try {
            switch(this.token.type) {
                case MINUS -> value = ((NumberValue) value).multiplyBy(new NumberValue(-1));
                case NOT -> value = ((BooleanValue) value).not();
            }
            return value.setPos(node.startPos, node.endPos);
        }
        catch (ClassCastException classCastException) {
            throw new CodeError(CodeError.ErrorType.ILLEGAL_OPERATION_ERROR, "The operation '" + this.token.type + "' cannot be applied to '" + value.value + "'", this.startPos, this.endPos);
        }
    }

    @Override
    public String toString() {
        return "(%s, %s)".formatted(this.token, this.node);
    }
}
