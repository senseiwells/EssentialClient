package me.senseiwells.arucas.nodes;

import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.values.functions.BuiltInFunction;
import me.senseiwells.arucas.values.functions.UserDefinedFunction;
import me.senseiwells.arucas.values.Value;

import java.util.List;

public class FunctionNode extends Node {
    public final Token variableNameToken;
    public final List<Token> argumentNameToken;
    public final Node bodyNode;

    public FunctionNode(Token varNameToken, List<Token> argumentNameToken, Node bodyNode, Context context) {
        super(bodyNode.token, varNameToken.startPos, bodyNode.endPos, context);
        this.variableNameToken = varNameToken;
        this.argumentNameToken = argumentNameToken;
        this.bodyNode = bodyNode;
    }

    @Override
    public Value<?> visit() throws CodeError {
        String functionName = this.variableNameToken.content;
        if (BuiltInFunction.isFunction(functionName))
            throw new CodeError(CodeError.ErrorType.ILLEGAL_OPERATION_ERROR, "Cannot define " + functionName + "() function as it is a predefined function", this.startPos, this.endPos);
        
        Value<?> functionValue = new UserDefinedFunction(functionName,
            this.bodyNode,
            this.argumentNameToken.stream().map(t -> t.content).toList()
        ).setPos(this.startPos, this.endPos).setContext(this.context);
        this.context.parentContext.symbolTable.set(functionName, functionValue);
        
        return functionValue;
    }
}
