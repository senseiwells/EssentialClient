package me.senseiwells.arucas.values;

import essentialclient.feature.clientscript.MinecraftFunctionValue;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.throwables.Error;
import me.senseiwells.arucas.throwables.ErrorRuntime;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.utils.SymbolTable;

import java.util.List;

public abstract class BaseFunctionValue extends Value<String> {

    public BaseFunctionValue(String name) {
        super(name);
    }

    public Context generateNewContext() {
        Context context = new Context(this.value, this.context, this.startPos);
        context.symbolTable = new SymbolTable(context.parent.symbolTable);
        return context;
    }

    private void checkArguments(List<Value<?>> arguments, List<String> argumentNames) throws ErrorRuntime {
        int argumentSize = arguments == null ? 0 : arguments.size();
        if (argumentSize > argumentNames.size())
            throw new ErrorRuntime(arguments.size() - argumentNames.size() + " too many arguments passed into " + this.value, this.startPos, this.endPos, this.context);
        if (argumentSize < argumentNames.size())
            throw new ErrorRuntime(argumentNames.size() - argumentSize + " too few arguments passed into " + this.value, this.startPos, this.endPos, this.context);
    }

    private void populateArguments(List<Value<?>> arguments, List<String> argumentNames, Context context) {
        for (int i = 0; i < argumentNames.size(); i++) {
            String argumentName = argumentNames.get(i);
            Value<?> argumentValue = arguments.get(i);
            argumentValue.setContext(context);
            context.symbolTable.set(argumentName, argumentValue);
        }
    }

    public static Enum<?> stringToFunction(String word) {
        Enum<?> function;
        function = BuiltInFunctionValue.BuiltInFunction.stringToFunction(word);
        if (function != null)
            return function;
        function = MinecraftFunctionValue.MinecraftFunction.stringToFunction(word);
        return function;
    }

    public void checkAndPopulateArguments(List<Value<?>> arguments, List<String> argumentNames, Context context) throws ErrorRuntime {
        this.checkArguments(arguments, argumentNames);
        this.populateArguments(arguments, argumentNames, context);
    }

    public Value<?> getValueFromTable(String key) {
        return this.context.symbolTable.get(key);
    }

    public Error throwInvalidParameterError(String details) {
        return new Error(Error.ErrorType.ILLEGAL_SYNTAX_ERROR, details, this.startPos, this.endPos);
    }

    public abstract Value<?> execute(List<Value<?>> arguments) throws Error, ThrowValue;

    @Override
    public abstract Value<?> copy();
}
