package me.senseiwells.arucas.nodes;

import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.Interpreter;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.tokens.ValueToken;
import me.senseiwells.arucas.values.Value;

public class StringNode extends Node {

    public StringNode(Token token) {
        super(token);
    }

    @Override
    public Value<?> visit(Interpreter interpreter, Context context) {
        ValueToken token = ((ValueToken)this.token);
        return token.tokenValue.setPos(this.startPos, this.endPos).setContext(context);
    }
}
