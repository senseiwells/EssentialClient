package me.senseiwells.arucas.nodes;

import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.values.BooleanValue;
import me.senseiwells.arucas.values.NullValue;
import me.senseiwells.arucas.values.Value;

public class IfNode extends Node {

    public final Node conditionNode;
    public final Node bodyNode;
    public final Node elseNode;

    public IfNode(Node conditionNode, Node bodyNode, Node elseNode, Context context) {
        super(conditionNode.token, conditionNode.startPos, elseNode instanceof NullNode ? conditionNode.endPos : elseNode.endPos, context);
        this.conditionNode = conditionNode;
        this.bodyNode = bodyNode;
        this.elseNode = elseNode;
    }

    @Override
    public Value<?> visit() throws CodeError, ThrowValue {
        if (this.conditionNode instanceof NullNode && !(this.elseNode instanceof NullNode)) {
            this.elseNode.visit();
            return new NullValue().setContext(this.context);
        }
        Value<?> conditionalValue = this.conditionNode.visit();
        if (!(conditionalValue instanceof BooleanValue booleanValue))
            throw new CodeError(CodeError.ErrorType.ILLEGAL_OPERATION_ERROR, "Condition must result in either 'true' or 'false'", this.startPos, this.endPos);
        if (booleanValue.value)
            this.bodyNode.visit();
        else if (!(this.elseNode instanceof NullNode))
            this.elseNode.visit();
        return new NullValue().setContext(this.context);
    }
}
