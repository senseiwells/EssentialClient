package me.senseiwells.arucas.values;

import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.throwables.Error;
import me.senseiwells.arucas.utils.Position;

public abstract class Value<T> {

    public T value;
    public Position startPos;
    public Position endPos;
    public Context context;

    public Value(T value) {
        this.value = value;
    }

    public Value<?> setPos(Position startPos, Position endPos) {
        this.startPos = startPos;
        this.endPos = endPos;
        return this;
    }

    public Value<?> setContext(Context context) {
        this.context = context;
        return this;
    }

    public Value<?> addTo(Value<?> other) throws Error {
        throw new Error(Error.ErrorType.ILLEGAL_OPERATION_ERROR, "The 'add' operator cannot be applied to " + this + " and " + other, this.startPos, this.endPos);
    }

    public BooleanValue isEqual(Value<?> other) {
        return (BooleanValue) new BooleanValue(this.value.equals(other.value)).setContext(this.context);
    }

    public BooleanValue isNotEqual(Value<?> other) {
        return (BooleanValue) new BooleanValue(!this.value.equals(other.value)).setContext(this.context);
    }

    public abstract Value<?> copy();

    @Override
    public String toString() {
        return this.value.toString();
    }

}
