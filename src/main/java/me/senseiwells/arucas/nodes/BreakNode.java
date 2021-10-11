package me.senseiwells.arucas.nodes;

import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.utils.Position;
import me.senseiwells.arucas.values.Value;

public class BreakNode extends Node {
    public BreakNode(Position startPos, Position endPos) {
        super(new Token(Token.Type.BREAK, startPos, endPos), null);
    }

    @Override
    public Value<?> visit() throws ThrowValue {
        ThrowValue throwValue = new ThrowValue();
        throwValue.shouldBreak = true;
        throw throwValue;
    }
}
