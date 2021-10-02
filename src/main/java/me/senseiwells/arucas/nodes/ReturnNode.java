package me.senseiwells.arucas.nodes;

import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.throwables.Error;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.utils.Interpreter;
import me.senseiwells.arucas.utils.Position;
import me.senseiwells.arucas.tokens.KeyWordToken;
import me.senseiwells.arucas.values.NullValue;
import me.senseiwells.arucas.values.Value;

public class ReturnNode extends Node {

    Node returnNode;

    public ReturnNode(Node returnNode, Position startPos, Position endPos) {
        super(new KeyWordToken(KeyWordToken.KeyWord.RETURN, startPos, endPos));
        this.returnNode = returnNode;
    }

    @Override
    public Value<?> visit(Interpreter interpreter, Context context) throws Error, ThrowValue {
        Value<?> value = new NullValue();
        if (this.returnNode != null) {
            value = interpreter.visit(this.returnNode, context);
        }
        ThrowValue throwValue = new ThrowValue();
        throwValue.returnValue = value;
        throw throwValue;
    }
}
