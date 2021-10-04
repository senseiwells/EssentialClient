package me.senseiwells.arucas.values;

public class NullValue extends Value<Object> {

    public NullValue() {
        super(null);
    }

    //Must override these methods since 'null' isn't an object, so it cannot call .equals
    @Override
    public BooleanValue isEqual(Value<?> other) {
        return (BooleanValue) new BooleanValue(this.value == other.value).setContext(this.context);
    }

    @Override
    public BooleanValue isNotEqual(Value<?> other) {
        return (BooleanValue) new BooleanValue(this.value != other.value).setContext(this.context);
    }

    @Override
    public Value<?> copy() {
        return new NullValue().setContext(this.context).setPos(this.startPos, this.endPos);
    }

    @Override
    public String toString() {
        return "null";
    }
}
