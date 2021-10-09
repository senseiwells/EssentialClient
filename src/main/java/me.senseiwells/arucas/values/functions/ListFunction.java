package me.senseiwells.arucas.values.functions;

import me.senseiwells.arucas.throwables.Error;
import me.senseiwells.arucas.values.ListValue;
import me.senseiwells.arucas.values.NumberValue;
import me.senseiwells.arucas.values.Value;

import java.util.List;

public class ListFunction extends BuiltInFunction {

    public ListFunction(String name, String argument, FunctionDefinition function) {
        super(name, List.of("list", argument), function);
    }

    public ListFunction(String name, FunctionDefinition function) {
        super(name, "list", function);
    }

    public static void initialiseListFunctions() {
        new ListFunction("getIndex", "index", (function) -> modifyListIndex(function, false));
        new ListFunction("removeIndex", "index", (function) -> modifyListIndex(function, true));

        new ListFunction("append", "value", (function) -> {
            ListValue listValue = (ListValue) function.getValueForType(ListValue.class, 0, null);
            Value<?> value = function.getValueFromTable(function.argumentNames.get(1));
            listValue.value.add(value);
            return listValue;
        });

        new ListFunction("concat", "otherList", (function) -> {
            ListValue list1 = (ListValue) function.getValueForType(ListValue.class, 0, null);
            ListValue list2 = (ListValue) function.getValueForType(ListValue.class, 1, null);
            list1.value.addAll(list2.value);
            return list1;
        });
    }

    private static Value<?> modifyListIndex(BuiltInFunction function, boolean delete) throws Error {
        ListValue listValue = (ListValue) function.getValueForType(ListValue.class, 0, null);
        NumberValue numberValue = (NumberValue) function.getValueForType(NumberValue.class, 1, null);
        int index = numberValue.value.intValue();
        if (index >= listValue.value.size() || index < 0)
            throw function.throwInvalidParameterError("Parameter 2 is out of bounds");
        return delete ? listValue.value.remove(index) : listValue.value.get(index);
    }
}

