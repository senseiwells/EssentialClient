package me.senseiwells.arucas.utils;

public record TwoValues<T, S>(T value1, S value2) {

    public T getValue1() {
        return this.value1;
    }

    public S getValue2() {
        return this.value2;
    }
}
