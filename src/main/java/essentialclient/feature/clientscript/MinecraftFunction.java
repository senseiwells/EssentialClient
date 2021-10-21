package essentialclient.feature.clientscript;

import me.senseiwells.arucas.extensions.BuiltInFunction;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.functions.FunctionDefinition;

import java.util.List;

public class MinecraftFunction extends BuiltInFunction {
    public MinecraftFunction(String name, List<String> argumentNames, FunctionDefinition function) {
        super(name, argumentNames, function);
    }

    public MinecraftFunction(String name, String argument, FunctionDefinition function) {
        this(name, List.of(argument), function);
    }

    public MinecraftFunction(String name, FunctionDefinition function) {
        this(name, List.of(), function);
    }

    @Override
    public Value<?> execute(Context context, List<Value<?>> arguments) throws CodeError {
        this.checkAndPopulateArguments(context, arguments, this.argumentNames);
        return this.function.execute(context, this);
    }

    @Override
    public Value<?> copy() {
        return new MinecraftFunction(this.value, this.argumentNames, this.function).setPos(this.startPos, this.endPos);
    }
}
