package me.senseiwells.arucas.utils;

import me.senseiwells.arucas.values.functions.BuiltInFunction;
import me.senseiwells.arucas.values.Value;

import java.util.*;

public class SymbolTable {
    private final Map<String, Value<?>> symbolMap;
    public SymbolTable parentTable;

    public SymbolTable(SymbolTable parent) {
        this.symbolMap = new HashMap<>();
        this.parentTable = parent;
    }

    public SymbolTable() {
        this(null);
    }

    public SymbolTable setDefaultSymbols(Context context) {
        if (!this.symbolMap.isEmpty())
            return this;
        
        for (BuiltInFunction function : BuiltInFunction.getBuiltInFunctions()) {
            this.set(function.value, function.setContext(context));
        }
        
        return this;
    }

    public Value<?> get(String name) {
        Value<?> value = this.symbolMap.get(name);
        if (value == null && this.parentTable != null)
            return this.parentTable.get(name);
        return value;
    }
    
    public SymbolTable getParent(String name) {
        SymbolTable parentTable = this.parentTable;
        if (parentTable != null) {
            if (parentTable.get(name) != null) {
                return parentTable;
            }
            else
                return parentTable.getParent(name);
        }
        return null;
    }

    public void set(String name, Value<?> value) {
        SymbolTable parentTable = this.getParent(name);
        if (parentTable == null) {
            this.symbolMap.put(name, value);
            return;
        }
        parentTable.symbolMap.put(name, value);
    }
}
