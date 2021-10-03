package me.senseiwells.arucas.values;

import me.senseiwells.arucas.throwables.Error;
import me.senseiwells.arucas.throwables.ErrorRuntime;
import me.senseiwells.arucas.core.Run;
import me.senseiwells.arucas.throwables.ThrowStop;
import me.senseiwells.arucas.throwables.ThrowValue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
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
            case RUN -> this.run();
            case STOP -> throw new ThrowStop();
            case DEBUG -> Run.debug = (boolean) this.getValueForType(BooleanValue.class, 0).value;
            case PRINT -> this.print();
            case SLEEP -> this.sleep();
            case SCHEDULE -> this.schedule();
            case RANDOM -> returnValue = this.random();
            case ROUND -> returnValue = this.round();
            case ROUND_UP -> returnValue = this.roundUp();
            case ROUND_DOWN -> returnValue = this.roundDown();
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

    private void run() throws Error {
        StringValue stringValue = (StringValue) this.getValueForType(StringValue.class, 0);
        String fileName = stringValue.value;
        try {
            String fileContent = Files.readString(Path.of(fileName));

            Run.run(fileName, fileContent);
        }
        catch (IOException | InvalidPathException e) {
            throw new ErrorRuntime("Failed to execute script '" + fileName + "' \n" + e, this.startPos, this.endPos, this.context);
        }
    }

    private void print() {
        System.out.println(this.getValueFromTable(this.function.getArgument(0)));
    }

    private void sleep() throws Error {
        NumberValue numberValue = (NumberValue) this.getValueForType(NumberValue.class, 0);
        try {
            Thread.sleep(numberValue.value.longValue());
        }
        catch (InterruptedException e) {
            throw new Error(Error.ErrorType.RUNTIME_ERROR, "An error occurred while trying to call 'sleep()'", this.startPos, this.endPos);
        }
    }

    private void schedule() throws Error {
        NumberValue numberValue = (NumberValue) this.getValueForType(NumberValue.class, 0);
        BaseFunctionValue functionValue = (BaseFunctionValue) this.getValueForType(BaseFunctionValue.class, 1);
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
    }

    private NumberValue random() throws Error {
        NumberValue numberValue = (NumberValue) this.getValueForType(NumberValue.class, 0);
        return new NumberValue(new Random().nextInt(numberValue.value.intValue()));
    }

    private NumberValue round() throws Error {
        NumberValue numValue = (NumberValue) this.getValueForType(NumberValue.class, 0);
        return new NumberValue(Math.round(numValue.value));
    }

    private NumberValue roundUp() throws Error {
        NumberValue numValue = (NumberValue) this.getValueForType(NumberValue.class, 0);
        return new NumberValue((float) Math.ceil(numValue.value));
    }

    private NumberValue roundDown() throws Error {
        NumberValue numValue = (NumberValue) this.getValueForType(NumberValue.class, 0);
        return new NumberValue((float) Math.floor(numValue.value));
    }

    private BooleanValue isType(Class<?> classInstance) {
        return new BooleanValue(classInstance.isInstance(this.getValueFromTable(this.function.getArgument(0))));
    }

    private Value<?> modifyListIndex(boolean delete) throws Error {
        ListValue listValue = (ListValue) this.getValueForType(ListValue.class, 0);
        NumberValue numberValue = (NumberValue) this.getValueForType(NumberValue.class, 1);
        int index = numberValue.value.intValue();
        if (index >= listValue.value.size() || index < 0)
            throw this.throwInvalidParameterError("Parameter 2 is out of bounds");
        return delete ? listValue.value.remove(index) : listValue.value.get(index);
    }

    private Value<?> appendList() throws Error {
        ListValue listValue = (ListValue) this.getValueForType(ListValue.class, 0);
        Value<?> value = this.getValueFromTable(this.function.getArgument(1));
        listValue.value.add(value);
        return listValue;
    }

    private Value<?> concatList() throws Error {
        ListValue list1 = (ListValue) this.getValueForType(ListValue.class, 0);
        ListValue list2 = (ListValue) this.getValueForType(ListValue.class, 1);
        list1.value.addAll(list2.value);
        return list1;
    }

    private NumberValue getListLength() throws Error {
        ListValue listValue = (ListValue) this.getValueForType(ListValue.class, 0);
        return new NumberValue(listValue.value.size());
    }

    public Value<?> getValueForType(Class<?> clazz, int index) throws Error {
        Value<?> value = this.getValueFromTable(this.function.getArgument(index));
        if (!(clazz.isInstance(value)))
            throw this.throwInvalidParameterError("Must pass " + clazz.getSimpleName() + " into parameter " + (index + 1) + " for " + this.function.name + "()");
        return value;
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
        ROUND("round", "number"),
        ROUND_UP("roundUp", "number"),
        ROUND_DOWN("roundDown", "number"),
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
