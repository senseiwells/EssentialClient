package me.senseiwells.arucas.values;

public class BooleanValue extends Value<Boolean> {

    public BooleanValue(Boolean value) {
        super(value);
    }

    public BooleanValue isAnd(BooleanValue other) {
        return (BooleanValue) new BooleanValue(this.value && other.value).setContext(this.context);
    }

    public BooleanValue isOr(BooleanValue other) {
        return (BooleanValue) new BooleanValue(this.value || other.value).setContext(this.context);
    }

    public BooleanValue not() {
        return (BooleanValue) new BooleanValue(!this.value).setContext(this.context);
    }

    @Override
    public Value<Boolean> copy() {
        return new BooleanValue(this.value).setPos(this.startPos, this.endPos).setContext(this.context);
    }
}
