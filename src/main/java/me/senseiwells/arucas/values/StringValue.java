package me.senseiwells.arucas.values;

import me.senseiwells.arucas.throwables.CodeError;

public class StringValue extends Value<String> {

    public StringValue(String value) {
        super(value);
    }

    @Override
    public StringValue addTo(Value<?> other) throws CodeError {
        if (!(other instanceof StringValue otherValue))
            throw new CodeError(CodeError.ErrorType.ILLEGAL_OPERATION_ERROR, "The 'add' operator cannot be applied to " + this + " and " + other, this.startPos, this.endPos);
        return (StringValue) new StringValue(this.value + otherValue.value).setContext(this.context);
    }

    @Override
    public Value<String> copy() {
        return new StringValue(this.value).setPos(this.startPos, this.endPos).setContext(this.context);
    }
}
