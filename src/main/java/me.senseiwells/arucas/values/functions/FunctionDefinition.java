package me.senseiwells.arucas.values.functions;

import me.senseiwells.arucas.throwables.Error;
import me.senseiwells.arucas.values.Value;

public interface FunctionDefinition {

    Value<?> execute(BuiltInFunction builtInFunctionValue) throws Error;

}
