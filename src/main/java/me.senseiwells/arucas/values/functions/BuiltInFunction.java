package me.senseiwells.arucas.values.functions;

import essentialclient.feature.clientscript.MinecraftFunction;
import me.senseiwells.arucas.throwables.Error;
import me.senseiwells.arucas.throwables.ErrorRuntime;
import me.senseiwells.arucas.core.Run;
import me.senseiwells.arucas.throwables.ThrowStop;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.values.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class BuiltInFunction extends FunctionValue {

    private static final Set<BuiltInFunction> builtInFunctionSet = new HashSet<>();

    public FunctionDefinition function;
    public List<String> argumentNames;

    public BuiltInFunction(String name, List<String> argumentNames, FunctionDefinition function) {
        super(name);
        this.function = function;
        this.argumentNames = argumentNames;
        builtInFunctionSet.add(this);
    }

    public BuiltInFunction(String name, String argument, FunctionDefinition function) {
        this(name, List.of(argument), function);
    }

    public BuiltInFunction(String name, FunctionDefinition function) {
        this(name, new LinkedList<>(), function);
    }

    public static boolean isFunction(String word) {
        for (BuiltInFunction builtInFunctionValue : builtInFunctionSet)
            if (builtInFunctionValue.value.equals(word))
                return true;
        return false;
    }

    /**
     *  This method is where all functions are defined
     *  If you want to add functions then create a new class e.g. StringFunctionValue
     *  Then at the bottom of this method call a method in your class that is similar to this
     */

    public static Set<BuiltInFunction> initialiseBuiltInFunctions() {
        new BuiltInFunction("run", "path", function -> {
            StringValue stringValue = (StringValue) function.getValueForType(StringValue.class, 0, null);
            String fileName = stringValue.value;
            try {
                String fileContent = Files.readString(Path.of(fileName));
                Run.run(fileName, fileContent);
            }
            catch (IOException | InvalidPathException e) {
                throw new ErrorRuntime("Failed to execute script '" + fileName + "' \n" + e, function.startPos, function.endPos, function.context);
            }
            return new NullValue();
        });

        new BuiltInFunction("stop", function -> {
            throw new ThrowStop();
        });

        new BuiltInFunction("debug", "boolean", function -> {
            Run.debug = (boolean) function.getValueForType(BooleanValue.class, 0, null).value;
            return new NullValue();
        });

        new BuiltInFunction("print", "printValue", function -> {
            System.out.println(function.getValueFromTable(function.argumentNames.get(0)));
            return new NullValue();
        });

        new BuiltInFunction("sleep", "milliseconds", function -> {
            NumberValue numberValue = (NumberValue) function.getValueForType(NumberValue.class, 0, null);
            try {
                Thread.sleep(numberValue.value.longValue());
            }
            catch (InterruptedException e) {
                throw new Error(Error.ErrorType.RUNTIME_ERROR, "An error occurred while trying to call 'sleep()'", function.startPos, function.endPos);
            }
            return new NullValue();
        });

        new BuiltInFunction("schedule", List.of("milliseconds", "function"), function -> {
            NumberValue numberValue = (NumberValue) function.getValueForType(NumberValue.class, 0, null);
            FunctionValue functionValue = (FunctionValue) function.getValueForType(FunctionValue.class, 1, null);
            Thread thread = new Thread(() -> {
                try {
                    Thread.sleep(numberValue.value.longValue());
                    functionValue.execute(null);
                }
                catch (InterruptedException | Error | ThrowValue e) {
                    if (!(e instanceof ThrowStop))
                        System.out.println("WARN: An error was caught in schedule() call, check that you are passing in a valid function");
                }
                Thread.currentThread().interrupt();
            });
            thread.start();
            return new NullValue();
        });

        new BuiltInFunction("random", "bound", function -> {
            NumberValue numValue = (NumberValue) function.getValueForType(NumberValue.class, 0, null);
            return new NumberValue(Math.round(numValue.value));
        });

        new BuiltInFunction("round", "number", function -> {
            NumberValue numValue = (NumberValue) function.getValueForType(NumberValue.class, 0, null);
            return new NumberValue(Math.round(numValue.value));
        });

        new BuiltInFunction("roundUp", "number", function -> {
            NumberValue numValue = (NumberValue) function.getValueForType(NumberValue.class, 0, null);
            return new NumberValue((float) Math.ceil(numValue.value));
        });

        new BuiltInFunction("roundDown", "number", function -> {
            NumberValue numValue = (NumberValue) function.getValueForType(NumberValue.class, 0, null);
            return new NumberValue((float) Math.floor(numValue.value));
        });

        new BuiltInFunction("getTime", (function) -> new StringValue(DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalTime.now())));

        new BuiltInFunction("isString", "value", function -> function.isType(StringValue.class));
        new BuiltInFunction("isNumber", "value", function -> function.isType(NumberValue.class));
        new BuiltInFunction("isBoolean", "value", function -> function.isType(BooleanValue.class));
        new BuiltInFunction("isFunction", "value", function -> function.isType(FunctionValue.class));
        new BuiltInFunction("isList", "value", function -> function.isType(ListValue.class));

        // Injecting method here
        ListFunction.initialiseListFunctions();

        // Added for SC
        MinecraftFunction.initialiseMinecraftFunctions();
        // - Sensei

        return builtInFunctionSet;
    }

    @Override
    public Value<?> execute(List<Value<?>> arguments) throws Error {
        this.context = this.generateNewContext();
        this.checkAndPopulateArguments(arguments, this.argumentNames, this.context);
        return this.function.execute(this);
    }

    private BooleanValue isType(Class<?> classInstance) {
        return new BooleanValue(classInstance.isInstance(this.getValueFromTable(this.argumentNames.get(0))));
    }

    public Value<?> getValueForType(Class<?> clazz, int index, String additionalInfo) throws Error {
        Value<?> value = this.getValueFromTable(this.argumentNames.get(index));
        if (!(clazz.isInstance(value)))
            throw this.throwInvalidParameterError("Must pass " + clazz.getSimpleName() + " into parameter " + (index + 1) + " for " + this.value + "()" + (additionalInfo == null ? "" : "\n" + additionalInfo));
        return value;
    }

    @Override
    public Value<?> copy() {
        return new BuiltInFunction(this.value, this.argumentNames, this.function).setPos(this.startPos, this.endPos).setContext(this.context);
    }
}
