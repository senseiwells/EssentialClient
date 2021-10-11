package me.senseiwells.arucas.throwables;

import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.Position;

public class ErrorRuntime extends CodeError {

    public Context context;

    public ErrorRuntime(String details, Position startPos, Position endPos, Context context) {
        super(ErrorType.RUNTIME_ERROR, details, startPos, endPos);
        this.context = context;
    }

    private String generateTraceback() {
        StringBuilder result = new StringBuilder();
        Position pos = this.startPos;
        Context context = this.context;
        while (context != null) {
            result.insert(0, "File: %s, Line: %d, Column: %d, In: %s\n".formatted(pos.fileName, pos.line + 1, pos.column + 1, context.displayName));
            pos = context.parentEntryPosition;
            context = context.parentContext;
        }
        return "Traceback (most recent call last): \n" + result;
    }

    @Override
    public String toString() {
        return "%s%s - '%s'".formatted(this.generateTraceback(), this.errorType.stringName, this.getMessage());
    }
}
