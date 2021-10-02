package me.senseiwells.arucas.values;

import me.senseiwells.arucas.throwables.Error;
import me.senseiwells.arucas.throwables.ErrorRuntime;
import me.senseiwells.arucas.core.Run;
import me.senseiwells.arucas.throwables.ThrowStop;
import me.senseiwells.arucas.throwables.ThrowValue;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class BuiltInFunctionValue extends BaseFunctionValue {

    private BuiltInFunction function;

    public BuiltInFunctionValue(String name) {
        super(name);
    }

    @Override
    public Value<?> execute(List<Value<?>> arguments) throws Error {
        this.function = BuiltInFunction.stringToFunction(this.value);
        this.context = this.generateNewContext();
        Value<?> returnValue = new NullValue();
        if (this.function == null)
            throw new ErrorRuntime("Function " + this.value + " is not defined", this.startPos, this.endPos, this.context);
        this.checkAndPopulateArguments(arguments, this.function.argumentNames, this.context);
        switch (this.function) {
            //case RUN -> this.run();
            case STOP -> throw new ThrowStop();
            case DEBUG -> this.toggleDebug();
            case PRINT -> this.print();
            case SLEEP -> this.sleep();
            case SCHEDULE -> this.schedule();
            case RANDOM -> returnValue = this.random();
            case IS_NUMBER -> returnValue = this.isType(NumberValue.class);
            case IS_STRING -> returnValue = this.isType(StringValue.class);
            case IS_BOOLEAN -> returnValue = this.isType(BooleanValue.class);
            case IS_FUNCTION -> returnValue = this.isType(BaseFunctionValue.class);
            case IS_LIST -> returnValue = this.isType(ListValue.class);
            case GET_INDEX -> returnValue = this.modifyListIndex(false);
            case REMOVE_INDEX -> returnValue = this.modifyListIndex(true);
            case APPEND -> returnValue = this.appendList();
            case CONCAT -> returnValue = this.concatList();
            case LEN -> returnValue = this.getListLength();
            case GET_TIME -> returnValue = new StringValue(DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalTime.now()));
        }
        return returnValue;
    }
    /* No use cases here
    private void run() throws Error {
        Value<?> value = this.context.symbolTable.get(this.function.getArgument(0));
        if (!(value instanceof StringValue stringValue))
            throw new Error(Error.ErrorType.ILLEGAL_SYNTAX_ERROR, "Must pass an string (file path) to function", this.startPos, this.endPos);
        String fileName = stringValue.value;
        try {
            String fileContent = Files.readString(Path.of(fileName));

            Run.run(fileName, fileContent);
        }
        catch (IOException | InvalidPathException e) {
            throw new ErrorRuntime("Failed to execute script '" + fileName + "' \n" + e, this.startPos, this.endPos, this.context);
        }
    }
    */
    private void print() {
        System.out.println(this.getValueFromTable(this.function.getArgument(0)));
    }

    private void sleep() throws Error {
        Value<?> numValue = this.getValueFromTable(this.function.getArgument(0));
        if (!(numValue instanceof NumberValue timeValue))
            throw this.throwInvalidParameterError("Must pass an integer into function");
        try {
            Thread.sleep(timeValue.value.longValue());
        }
        catch (InterruptedException e) {
            throw new Error(Error.ErrorType.RUNTIME_ERROR, "An error occurred while trying to call 'sleep()'", this.startPos, this.endPos);
        }
    }

    private void schedule() throws Error {
        Value<?> numValue = this.getValueFromTable(this.function.getArgument(0));
        Value<?> funValue = this.getValueFromTable(this.function.getArgument(1));
        if (!(numValue instanceof NumberValue timeValue))
            throw this.throwInvalidParameterError("Must pass an integer into parameter 1");
        if (!(funValue instanceof BaseFunctionValue functionValue))
            throw this.throwInvalidParameterError("Must pass a function into parameter 2");
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(timeValue.value.longValue());
                functionValue.execute(null);
            }
            catch (InterruptedException | Error | ThrowValue e) {
                System.out.println("WARN: An error was caught in schedule() call, check that you are passing in a valid function");
            }
        });
        thread.start();
    }

    private NumberValue random() throws Error {
        Value<?> numValue = this.getValueFromTable(this.function.getArgument(0));
        if (!(numValue instanceof NumberValue bound))
            throw this.throwInvalidParameterError("Must pass an integer (bound) into function");
        return new NumberValue(new Random().nextInt(bound.value.intValue()));
    }

    private BooleanValue isType(Class<?> classInstance) {
        return new BooleanValue(classInstance.isInstance(this.getValueFromTable(this.function.getArgument(0))));
    }

    private Value<?> modifyListIndex(boolean delete) throws Error {
        Value<?> value = this.getValueFromTable(this.function.getArgument(0));
        Value<?> numValue = this.getValueFromTable(this.function.getArgument(1));
        if (!(value instanceof ListValue listValue))
            throw this.throwInvalidParameterError("Must pass a list into parameter 1");
        if (!(numValue instanceof NumberValue numberValue))
            throw this.throwInvalidParameterError("Must pass an integer into parameter 2");
        int index = numberValue.value.intValue();
        if (index >= listValue.value.size() || index < 0)
            throw this.throwInvalidParameterError("Parameter 2 is out of bounds");
        return delete ? listValue.value.remove(index) : listValue.value.get(index);
    }

    private Value<?> appendList() throws Error {
        Value<?> listValue = this.getValueFromTable(this.function.getArgument(0));
        Value<?> value = this.getValueFromTable(this.function.getArgument(1));
        if (!(listValue instanceof ListValue list))
            throw this.throwInvalidParameterError("Must pass a list into parameter 1 for append()");
        list.value.add(value);
        return list;
    }

    private Value<?> concatList() throws Error {
        Value<?> list1 = this.getValueFromTable(this.function.getArgument(0));
        Value<?> list2 = this.getValueFromTable(this.function.getArgument(1));
        if (!(list1 instanceof ListValue listValue1) || !(list2 instanceof ListValue listValue2))
            throw this.throwInvalidParameterError("Parameters for concat() must both be lists");
        listValue1.value.addAll(listValue2.value);
        return listValue1;
    }

    private NumberValue getListLength() throws Error {
        Value<?> listValue = this.getValueFromTable(this.function.getArgument(0));
        if (!(listValue instanceof ListValue list))
            throw this.throwInvalidParameterError("Parameter for len() must both be a list");
        return new NumberValue(list.value.size());
    }

    private void toggleDebug() throws Error {
        Value<?> value = this.getValueFromTable(this.function.getArgument(0));
        if (!(value instanceof BooleanValue booleanValue))
            throw this.throwInvalidParameterError("Cannot pass " + value.value + "to debug()");
        Run.debug = booleanValue.value;
    }

    @Override
    public Value<?> copy() {
        return new BuiltInFunctionValue(this.value).setPos(this.startPos, this.endPos).setContext(this.context);
    }

    public enum BuiltInFunction {
        //general functions
        RUN("run", "path"),
        STOP("stop"),
        DEBUG("debug", "boolean"),
        PRINT("print", "printValue"),
        SLEEP("sleep", "time"),
        SCHEDULE("schedule", new String[]{"time", "function"}),
        RANDOM("random", "bound"),
        GET_TIME("getTime"),
        IS_NUMBER("isNumber", "value"),
        IS_STRING("isString", "value"),
        IS_BOOLEAN("isBoolean", "value"),
        IS_FUNCTION("isFunction", "value"),
        IS_LIST("isList", "value"),

        //list functions
        GET_INDEX("getIndex", new String[]{"list", "index"}),
        REMOVE_INDEX("removeIndex", new String[]{"list", "index"}),
        APPEND("append", new String[]{"list", "value"}),
        CONCAT("concat", new String[]{"list", "otherList"}),
        LEN("len", "list");

        public String name;
        List<String> argumentNames;

        BuiltInFunction(String name, String[] argumentNames) {
            this.name = name;
            this.argumentNames = Arrays.stream(argumentNames).toList();
        }

        BuiltInFunction(String name, String argumentName) {
            this(name, new String[]{argumentName});
        }

        BuiltInFunction(String name) {
            this.name = name;
            this.argumentNames = new LinkedList<>();
        }

        public static BuiltInFunction stringToFunction(String word) {
            for (BuiltInFunction value : BuiltInFunction.values()) {
                if (word.equals(value.name))
                    return value;
            }
            return null;
        }

        private String getArgument(int index) {
            return this.argumentNames.get(index);
        }
    }
}
