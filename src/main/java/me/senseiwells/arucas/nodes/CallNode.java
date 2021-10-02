package me.senseiwells.arucas.nodes;

import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.throwables.Error;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.utils.Interpreter;
import me.senseiwells.arucas.values.BaseFunctionValue;
import me.senseiwells.arucas.values.Value;

import java.util.LinkedList;
import java.util.List;

public class CallNode extends Node {

    Node callNode;
    List<Node> argumentNodes;

    public CallNode(Node callNode, List<Node> argumentNodes) {
        super(callNode.token, callNode.startPos, argumentNodes.size() > 0 ? argumentNodes.get(argumentNodes.size() - 1).endPos : callNode.endPos);
        this.callNode = callNode;
        this.argumentNodes = argumentNodes;
    }

    @Override
    public Value<?> visit(Interpreter interpreter, Context context) throws Error, ThrowValue {
        Value<?> callValue = interpreter.visit(this.callNode, context);
        if (!(callValue instanceof BaseFunctionValue))
            return null;
        List<Value<?>> argumentValues = new LinkedList<>();
        callValue = callValue.copy().setPos(this.startPos, this.endPos);
        for (Node node : this.argumentNodes)
            argumentValues.add(interpreter.visit(node, context));
        Value<?> functionValue = ((BaseFunctionValue) callValue).execute(argumentValues);
        return functionValue != null ? functionValue.setPos(this.startPos, this.endPos).setContext(context) : null;
    }
}
