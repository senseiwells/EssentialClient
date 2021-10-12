package me.senseiwells.arucas.nodes;

import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.values.functions.BuiltInFunction;
import me.senseiwells.arucas.values.Value;

public class VariableAssignNode extends Node {
    public final Node node;

    public VariableAssignNode(Token token, Node node, Context context) {
        super(token, token.startPos, token.endPos, context);
        this.node = node;
    }

    @Override
    public Value<?> visit() throws CodeError, ThrowValue {
        String name = this.token.content;
        if (BuiltInFunction.isFunction(name))
            throw new CodeError(CodeError.ErrorType.ILLEGAL_OPERATION_ERROR, "Cannot assign " + name + " value as it is a constant", this.startPos, this.endPos);
        Value<?> value = this.node.visit();
        this.context.symbolTable.set(name, value);
        return value;
    }
}
