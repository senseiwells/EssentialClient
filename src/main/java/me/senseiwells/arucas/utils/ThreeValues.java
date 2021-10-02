package me.senseiwells.arucas.utils;

public record ThreeValues<T, S, E>(T value1, S value2, E value3) {

    public T getValue1() {
        return this.value1;
    }

    public S getValue2() {
        return this.value2;
    }

    public E getValue3() {
        return this.value3;
    }
}
