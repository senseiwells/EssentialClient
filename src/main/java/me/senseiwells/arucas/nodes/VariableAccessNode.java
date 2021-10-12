package me.senseiwells.arucas.nodes;

import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.throwables.ErrorRuntime;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.values.Value;

public class VariableAccessNode extends Node {
    public VariableAccessNode(Token token, Context context) {
        super(token, token.startPos, token.endPos, context);
    }

    @Override
    public Value<?> visit() throws ErrorRuntime {
        Value<?> value = this.context.symbolTable.get(this.token.content);
        if (value == null)
            throw new ErrorRuntime(this.token.content + " is not defined", this.startPos, this.endPos, this.context);
        value = value.copy();
        value.setPos(this.startPos, this.endPos);
        return value;
    }
}
