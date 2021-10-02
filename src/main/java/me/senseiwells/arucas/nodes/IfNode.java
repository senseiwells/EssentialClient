package me.senseiwells.arucas.nodes;

import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.throwables.Error;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.utils.Interpreter;
import me.senseiwells.arucas.values.BooleanValue;
import me.senseiwells.arucas.values.NullValue;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.utils.ThreeValues;
import me.senseiwells.arucas.utils.TwoValues;

import java.util.List;

public class IfNode extends Node {

    List<ThreeValues<Node, Node, Boolean>> nodes;
    TwoValues<Node, Boolean> elseNode;

    public IfNode(List<ThreeValues<Node, Node, Boolean>> nodes, TwoValues<Node, Boolean> elseNode) {
        super(nodes.get(0).getValue1().token, nodes.get(0).getValue1().startPos, elseNode == null ? nodes.get(nodes.size() - 1).getValue1().endPos : elseNode.getValue1().endPos);
        this.nodes = nodes;
        this.elseNode = elseNode;
    }

    @Override
    public Value<?> visit(Interpreter interpreter, Context context) throws Error, ThrowValue {
        for (ThreeValues<Node, Node, Boolean> nodes : this.nodes) {
            Value<?> conditionValue = interpreter.visit(nodes.getValue1(), context);
            if (!(conditionValue instanceof BooleanValue booleanValue))
                throw new Error(Error.ErrorType.ILLEGAL_OPERATION_ERROR, "Condition must result in either 'true' or 'false'", this.startPos, this.endPos);
            if (booleanValue.value) {
                Value<?> value = interpreter.visit(nodes.getValue2(), context);
                return nodes.getValue3() ? new NullValue() : value;
            }
        }
        if (this.elseNode != null) {
            Value<?> value = interpreter.visit(this.elseNode.getValue1(), context);
            return elseNode.getValue2() ? new NullValue() : value;
        }
        return new NullValue();
    }
}
