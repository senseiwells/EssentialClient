package me.senseiwells.arucas.values;

import me.senseiwells.arucas.throwables.Error;
import me.senseiwells.arucas.throwables.ErrorRuntime;
import me.senseiwells.arucas.tokens.Token;

public class NumberValue extends Value<Float>{

    public NumberValue(float value) {
        super(value);
    }

    @Override
    public Value<Float> addTo(Value<?> other) throws Error {
        if (!(other instanceof NumberValue otherValue))
            throw new Error(Error.ErrorType.ILLEGAL_OPERATION_ERROR, "The 'add' operator cannot be applied to " + this + " and " + other, this.startPos, this.endPos);
        return (NumberValue) new NumberValue(this.value + otherValue.value).setContext(this.context);
    }

    public NumberValue subtractBy(NumberValue other) {
        return (NumberValue) new NumberValue(this.value - other.value).setContext(this.context);
    }

    public NumberValue multiplyBy(NumberValue other) {
        return (NumberValue) new NumberValue(this.value * other.value).setContext(this.context);
    }

    public NumberValue divideBy(NumberValue other) throws ErrorRuntime {
        if (other.value == 0)
            throw new ErrorRuntime("You cannot divide by 0", other.startPos, other.endPos, context);
        return (NumberValue) new NumberValue(this.value / other.value).setContext(this.context);
    }

    public NumberValue powerBy(NumberValue other) throws ErrorRuntime {
        if (this.value < 0 && other.value < 1)
            throw new ErrorRuntime("You cannot calculate imaginary numbers", other.startPos, other.endPos, context);
        return (NumberValue) new NumberValue((float) Math.pow(this.value, other.value)).setContext(this.context);
    }

    public BooleanValue compareNumber(NumberValue other, Token.Type type) {
        boolean bool;
        switch (type) {
            case LESS_THAN -> bool = this.value < other.value;
            case MORE_THAN -> bool = this.value > other.value;
            case MORE_THAN_EQUAL -> bool = this.value >= other.value;
            case LESS_THAN_EQUAL -> bool = this.value <= other.value;
            default -> bool = false;
        }
        return (BooleanValue) new BooleanValue(bool).setContext(this.context);
    }

    @Override
    public Value<?> copy() {
        return new NumberValue(this.value).setPos(this.startPos, this.endPos).setContext(this.context);
    }
}
