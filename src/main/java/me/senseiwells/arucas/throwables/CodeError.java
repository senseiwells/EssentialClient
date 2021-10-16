package me.senseiwells.arucas.throwables;

import me.senseiwells.arucas.utils.Position;

public class CodeError extends Exception {

    public final ErrorType errorType;
    public final Position startPos;
    public final Position endPos;
    
    public CodeError(ErrorType errorType, String details, Position startPos, Position endPos) {
        super(details);
        this.errorType = errorType;
        this.startPos = startPos;
        this.endPos = endPos;
    }
    
    @Override
    public String toString() {
        return "%s - '%s'\nFile: %s, Line: %d, Column: %d".formatted(
            this.errorType.stringName, this.getMessage(),
            this.startPos.fileName, this.startPos.line + 1, this.startPos.column + 1
        );
    }

    public enum ErrorType {

        ILLEGAL_CHAR_ERROR      ("Illegal Character Error"),
        ILLEGAL_SYNTAX_ERROR    ("Illegal Syntax Error"),
        ILLEGAL_OPERATION_ERROR ("Illegal Operation Error"),
        EXPECTED_CHAR_ERROR     ("Expected Character Error"),
        RUNTIME_ERROR           ("Runtime Error"),
        STOP                    ("Program stopped");

        public String stringName;

        ErrorType(String stringName) {
            this.stringName = stringName;
        }
    }
}
