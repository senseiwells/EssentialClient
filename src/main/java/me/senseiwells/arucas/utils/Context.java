package me.senseiwells.arucas.utils;

public class Context {

    public String displayName;
    public Context parentContext;
    public Position parentEntryPosition;
    public SymbolTable symbolTable;

    public Context(String displayName, Context parent, Position parentEntryPos) {
        this.displayName = displayName;
        this.parentContext = parent;
        this.parentEntryPosition = parentEntryPos;
        this.symbolTable = null;
    }
}
