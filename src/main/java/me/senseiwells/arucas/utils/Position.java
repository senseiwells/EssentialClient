package me.senseiwells.arucas.utils;

public class Position {

    public int index;
    public int line;
    public int column;
    public final String fileName;
    public final String fileText;

    public Position(int index, int line, int column, String fileName, String fileText) {
        this.index = index;
        this.line = line;
        this.column = column;
        this.fileName = fileName;
        this.fileText = fileText;
    }

    public Position advance(Character currentCharacter) {
        this.index++;
        this.column++;
        if (currentCharacter != null && currentCharacter.equals('\n')) {
            this.line++;
            this.column = 0;
        }
        return this;
    }

    public void recede() {
        if (this.index > 0 && this.column > 0) {
            this.index--;
            this.column--;
        }
    }

    public Position copy() {
        return new Position(this.index, this.line, this.column, this.fileName, this.fileText);
    }
}
