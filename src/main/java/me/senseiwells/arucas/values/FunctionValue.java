package me.senseiwells.arucas.values;

import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.throwables.Error;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.utils.Interpreter;
import me.senseiwells.arucas.nodes.Node;

import java.util.List;

public class FunctionValue extends BaseFunctionValue {

    Node bodyNode;
    List<String> argumentNames;

    public FunctionValue(String name, Node bodyNode, List<String> argumentNames) {
        super(name);
        this.bodyNode = bodyNode;
        this.argumentNames = argumentNames;
    }

    public Value<?> execute(List<Value<?>> arguments) throws Error {
        Interpreter interpreter = new Interpreter();
        Context context = this.generateNewContext();
        this.checkAndPopulateArguments(arguments, this.argumentNames, context);
        try {
            interpreter.visit(this.bodyNode, context);
            return new NullValue();
        }
        catch (ThrowValue tv) {
            return tv.returnValue;
        }
    }

    @Override
    public Value<?> copy() {
        return new FunctionValue(this.value, this.bodyNode, this.argumentNames).setPos(this.startPos, this.endPos).setContext(this.context);
    }

    @Override
    public String toString() {
        return "<function " + this.value + ">";
     }
}
