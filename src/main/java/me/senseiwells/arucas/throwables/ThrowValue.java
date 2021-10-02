package me.senseiwells.arucas.throwables;

import me.senseiwells.arucas.values.NullValue;
import me.senseiwells.arucas.values.Value;

public class ThrowValue extends Throwable {

    public Value<?> returnValue;
    public boolean shouldContinue;
    public boolean shouldBreak;

    public ThrowValue() {
        this.reset();
    }

    private void reset() {
        this.returnValue = new NullValue();
        this.shouldContinue = false;
        this.shouldBreak = false;
    }
}
