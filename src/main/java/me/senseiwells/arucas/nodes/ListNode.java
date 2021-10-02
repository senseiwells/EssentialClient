package me.senseiwells.arucas.nodes;

import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.throwables.Error;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.utils.Interpreter;
import me.senseiwells.arucas.utils.Position;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.values.ListValue;
import me.senseiwells.arucas.values.Value;

import java.util.LinkedList;
import java.util.List;

public class ListNode extends Node {

    List<Node> elementNodes;

    public ListNode(List<Node> elementNodes, Position posStart, Position posEnd) {
        super(new Token(Token.Type.LIST, posStart, posEnd));
        this.elementNodes = elementNodes;
    }

    @Override
    public Value<?> visit(Interpreter interpreter, Context context) throws Error, ThrowValue {
        List<Value<?>> elements = new LinkedList<>();
        for (Node elementNode : this.elementNodes)
            elements.add(elementNode.visit(interpreter, context));
        return new ListValue(elements).setContext(context).setPos(this.startPos, this.endPos);
    }
}
