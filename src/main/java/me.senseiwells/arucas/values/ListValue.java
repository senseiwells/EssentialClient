package me.senseiwells.arucas.values;

import java.util.List;

public class ListValue extends Value<List<Value<?>>> {

    public ListValue(List<Value<?>> value) {
        super(value);
    }

    @Override
    public Value<?> copy() {
        return new ListValue(this.value).setContext(this.context).setPos(this.startPos, this.endPos);
    }
}
