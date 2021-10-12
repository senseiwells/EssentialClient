package me.senseiwells.arucas.nodes;

import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.utils.Position;
import me.senseiwells.arucas.values.Value;

public class ContinueNode extends Node {
    public ContinueNode(Position startPos, Position endPos) {
        super(new Token(Token.Type.CONTINUE, startPos, endPos), null);
    }

    @Override
    public Value<?> visit() throws CodeError, ThrowValue {
        ThrowValue value = new ThrowValue();
        value.shouldContinue = true;
        throw value;
    }
}
