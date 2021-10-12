package me.senseiwells.arucas.nodes;

import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.utils.Position;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.values.Value;

public abstract class Node {
    public final Token token;
    public Position startPos;
    public Position endPos;
    public Context context;

    Node(Token token, Position startPos, Position endPos, Context context) {
        this.token = token;
        this.startPos = startPos;
        this.endPos = endPos;
        this.context = context;
    }

    Node(Token token, Context context) {
        this(token, token.startPos, token.endPos, context);
    }

    public abstract Value<?> visit() throws CodeError, ThrowValue;

    @Override
    public String toString() {
        return this.token.toString();
    }
}
