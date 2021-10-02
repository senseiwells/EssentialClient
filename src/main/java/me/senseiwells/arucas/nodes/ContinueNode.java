package me.senseiwells.arucas.nodes;

import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.throwables.Error;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.utils.Interpreter;
import me.senseiwells.arucas.utils.Position;
import me.senseiwells.arucas.tokens.KeyWordToken;
import me.senseiwells.arucas.values.Value;

public class ContinueNode extends Node {

    public ContinueNode(Position startPos, Position endPos) {
        super(new KeyWordToken(KeyWordToken.KeyWord.CONTINUE, startPos, endPos));
    }

    @Override
    public Value<?> visit(Interpreter interpreter, Context context) throws Error, ThrowValue {
        ThrowValue value = new ThrowValue();
        value.shouldContinue = true;
        throw value;
    }
}
