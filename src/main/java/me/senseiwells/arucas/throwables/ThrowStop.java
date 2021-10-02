package me.senseiwells.arucas.throwables;

public class ThrowStop extends Error {

    public ThrowStop() {
        super(ErrorType.STOP, null, null, null);
    }

    @Override
    public String toString() {
        return "Program has stopped";
    }
}
