package me.senseiwells.arucas.values;

import java.util.List;

public class ListValue extends Value<List<Value<?>>> {

    public ListValue(List<Value<?>> value) {
        super(value);
    }

    @Override
    public Value<List<Value<?>>> copy() {
        return new ListValue(this.value).setPos(this.startPos, this.endPos).setContext(this.context);
    }
}
