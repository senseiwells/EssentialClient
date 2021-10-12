package me.senseiwells.arucas.values.functions;

import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.values.Value;

public interface FunctionDefinition {

    Value<?> execute(BuiltInFunction builtInFunctionValue) throws CodeError;

}
