package me.senseiwells.arucas.nodes;

import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.values.NullValue;
import me.senseiwells.arucas.values.functions.FunctionValue;
import me.senseiwells.arucas.values.Value;

import java.util.ArrayList;
import java.util.List;

public class CallNode extends Node {
    public final List<Node> argumentNodes;
    public final Node callNode;

    public CallNode(Node callNode, List<Node> argumentNodes, Context context) {
        super(callNode.token, callNode.startPos, argumentNodes.size() > 0 ? argumentNodes.get(argumentNodes.size() - 1).endPos : callNode.endPos, context);
        this.argumentNodes = argumentNodes;
        this.callNode = callNode;
    }

    @Override
    public Value<?> visit() throws CodeError, ThrowValue {
        Value<?> callValue = this.callNode.visit();
        if (!(callValue instanceof FunctionValue))
            return new NullValue().setContext(this.context);
        
        List<Value<?>> argumentValues = new ArrayList<>();
        for (Node node : this.argumentNodes)
            argumentValues.add(node.visit());
        
        callValue = callValue.copy().setPos(this.startPos, this.endPos);
        Value<?> functionValue = ((FunctionValue) callValue).execute(argumentValues);
        return functionValue.setPos(this.startPos, this.endPos).setContext(this.context);
    }
}
