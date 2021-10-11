package me.senseiwells.arucas.utils;

public class Position {
    
    public final String fileName;
    public final int index;
    public final int line;
    public final int column;
    
    public Position(int index, int line, int column, String fileName) {
        this.index = index;
        this.line = line;
        this.column = column;
        this.fileName = fileName;
    }
}
