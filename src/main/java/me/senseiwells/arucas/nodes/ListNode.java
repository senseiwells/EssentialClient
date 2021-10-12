package me.senseiwells.arucas.nodes;

import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.utils.Position;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.values.ListValue;
import me.senseiwells.arucas.values.Value;

import java.util.ArrayList;
import java.util.List;

public class ListNode extends Node {
    public final List<Node> elementNodes;

    public ListNode(List<Node> elementNodes, Position posStart, Position posEnd, Context context) {
        super(new Token(Token.Type.LIST, posStart, posEnd), context);
        this.elementNodes = elementNodes;
    }

    @Override
    public Value<?> visit() throws CodeError, ThrowValue {
        List<Value<?>> elements = new ArrayList<>();
        for (Node elementNode : this.elementNodes)
            elements.add(elementNode.visit());
        return new ListValue(elements).setPos(this.startPos, this.endPos).setContext(this.context);
    }
}
