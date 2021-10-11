package me.senseiwells.arucas.values;

public class NullValue extends Value<Object> {

    public NullValue() {
        super(null);
    }

    @Override
    public BooleanValue isEqual(Value<?> other) {
        return (BooleanValue) new BooleanValue(other.value == null).setContext(this.context);
    }

    @Override
    public BooleanValue isNotEqual(Value<?> other) {
        return (BooleanValue) new BooleanValue(other.value != null).setContext(this.context);
    }

    @Override
    public Value<?> copy() {
        return new NullValue().setPos(this.startPos, this.endPos).setContext(this.context);
    }

    @Override
    public String toString() {
        return "null";
    }
}
