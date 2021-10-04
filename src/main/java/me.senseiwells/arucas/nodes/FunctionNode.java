package me.senseiwells.arucas.nodes;

import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.throwables.Error;
import me.senseiwells.arucas.utils.Interpreter;
import me.senseiwells.arucas.utils.SymbolTable;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.tokens.ValueToken;
import me.senseiwells.arucas.values.functions.BuiltInFunction;
import me.senseiwells.arucas.values.functions.UserDefinedFunction;
import me.senseiwells.arucas.values.NullValue;
import me.senseiwells.arucas.values.Value;

import java.util.LinkedList;
import java.util.List;

public class FunctionNode extends Node {

    static int lambdaNumber = 1;

    Token variableNameToken;
    List<Token> argumentNameToken;
    Node bodyNode;
    boolean shouldAutoReturn;

    public FunctionNode(Token varNameToken, List<Token> argumentNameToken, Node bodyNode, boolean shouldAutoReturn) {
        super(bodyNode.token, varNameToken != null ? varNameToken.startPos : argumentNameToken.size() > 0 ? argumentNameToken.get(0).startPos : bodyNode.startPos, bodyNode.endPos);
        this.variableNameToken = varNameToken;
        this.argumentNameToken = argumentNameToken;
        this.bodyNode = bodyNode;
        this.shouldAutoReturn = shouldAutoReturn;
    }

    @Override
    public Value<?> visit(Interpreter interpreter, Context context) throws Error {
        String functionName = this.variableNameToken != null ? (String) ((ValueToken)this.variableNameToken).tokenValue.value : "lambda_" + lambdaNumber++;
        if (SymbolTable.Literal.stringToLiteral(functionName) != null || BuiltInFunction.isFunction(functionName))
            throw new Error(Error.ErrorType.ILLEGAL_OPERATION_ERROR, "Cannot define " + functionName + "() function as it is a predefined function", this.startPos, this.endPos);
        Node bodyNode = this.bodyNode;
        List<String> argumentNames = new LinkedList<>();
        this.argumentNameToken.forEach(t -> argumentNames.add((String) ((ValueToken)t).tokenValue.value));
        Value<?> functionValue = new UserDefinedFunction(functionName, bodyNode, argumentNames).setContext(context).setPos(this.startPos, this.endPos);
        if (this.variableNameToken != null)
            context.symbolTable.set(functionName, functionValue);
        return this.shouldAutoReturn ? functionValue : new NullValue();
    }
}
