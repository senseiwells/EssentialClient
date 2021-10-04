package me.senseiwells.arucas.utils;

public class Context {

    public String displayName;
    public Context parent;
    public Position parentEntryPosition;
    public SymbolTable symbolTable;

    public Context(String displayName, Context parent, Position parentEntryPos) {
        this.displayName = displayName;
        this.parent = parent;
        this.parentEntryPosition = parentEntryPos;
        this.symbolTable = null;
    }
}
