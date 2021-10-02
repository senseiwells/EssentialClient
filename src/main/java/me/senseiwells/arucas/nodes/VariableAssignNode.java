package me.senseiwells.arucas.nodes;

import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.throwables.Error;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.utils.Interpreter;
import me.senseiwells.arucas.utils.SymbolTable;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.tokens.ValueToken;
import me.senseiwells.arucas.values.BaseFunctionValue;
import me.senseiwells.arucas.values.BuiltInFunctionValue;
import me.senseiwells.arucas.values.Value;

public class VariableAssignNode extends Node {

    public Node node;
    boolean isConstant;

    public VariableAssignNode(Token token, Node node) {
        super(token, token.startPos, token.endPos);
        this.node = node;
        this.isConstant = false;
    }

    public VariableAssignNode setConstant() {
        this.isConstant = true;
        return this;
    }

    @Override
    public Value<?> visit(Interpreter interpreter, Context context) throws Error, ThrowValue {
        String name = (String) ((ValueToken) this.token).tokenValue.value;
        if (SymbolTable.Literal.stringToLiteral(name) != null || BaseFunctionValue.stringToFunction(name) != null || context.symbolTable.isConstant(name))
            throw new Error(Error.ErrorType.ILLEGAL_OPERATION_ERROR, "Cannot assign " + name + " value as it is a constant", this.startPos, this.endPos);
        Value<?> value = interpreter.visit(this.node, context);
        if (this.isConstant)
            context.symbolTable.setConstant(name, value);
        else
            context.symbolTable.set(name, value);
        return value;
    }
}
