package me.senseiwells.arucas.utils;

import essentialclient.feature.clientmacro.MinecraftFunctionValue;
import me.senseiwells.arucas.values.BooleanValue;
import me.senseiwells.arucas.values.BuiltInFunctionValue;
import me.senseiwells.arucas.values.NullValue;
import me.senseiwells.arucas.values.Value;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class SymbolTable {

    public HashMap<String, Value<?>> symbolMap;
    public SymbolTable parent;
    public List<String> constants;

    public SymbolTable(SymbolTable parent) {
        this.symbolMap = new HashMap<>();
        this.parent = parent;
        this.constants = new LinkedList<>();
    }

    public SymbolTable() {
        this(null);
    }

    public SymbolTable setDefaultSymbols(Context context) {
        this.set("true", new BooleanValue(true).setContext(context));
        this.set("false", new BooleanValue(false).setContext(context));
        this.set("null", new NullValue().setContext(context));
        for (BuiltInFunctionValue.BuiltInFunction function : BuiltInFunctionValue.BuiltInFunction.values()) {
            this.set(function.name, new BuiltInFunctionValue(function.name).setContext(context));
        }
        for (MinecraftFunctionValue.MinecraftFunction function : MinecraftFunctionValue.MinecraftFunction.values()) {
            this.set(function.name, new MinecraftFunctionValue(function.name).setContext(context));
        }
        return this;
    }

    public Value<?> get(String name) {
        Value<?> value = this.symbolMap.get(name);
        if (value == null && this.parent != null)
            return this.parent.get(name);
        return value;
    }

    public void set(String name, Value<?> value) {
        this.symbolMap.put(name, value);
    }

    public void setConstant(String name, Value<?> value) {
        this.constants.add(name);
        this.symbolMap.put(name, value);
    }

    public boolean isConstant(String name) {
        for (String constant : this.constants)
            if (constant.equals(name))
                return true;
        return false;
    }

    public enum Literal {

        TRUE("true"),
        FALSE("false"),
        NULL("null");

        String name;

        Literal(String name) {
            this.name = name;
        }

        public static Literal stringToLiteral(String word) {
            for (Literal value : Literal.values()) {
                if (word.equals(value.name))
                    return value;
            }
            return null;
        }
    }
}
