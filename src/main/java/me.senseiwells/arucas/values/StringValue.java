package me.senseiwells.arucas.values;

import me.senseiwells.arucas.throwables.Error;

public class StringValue extends Value<String> {

    public StringValue(String value) {
        super(value);
    }

    @Override
    public Value<String> addTo(Value<?> other) throws Error {
        if (!(other instanceof StringValue otherValue))
            throw new Error(Error.ErrorType.ILLEGAL_OPERATION_ERROR, "The 'add' operator cannot be applied to " + this + " and " + other, this.startPos, this.endPos);
        return (StringValue) new StringValue(this.value + otherValue.value).setContext(this.context);
    }

    @Override
    public Value<?> copy() {
        return new StringValue(this.value).setPos(this.startPos, this.endPos).setContext(this.context);
    }

    public enum EscapeCharacters {

        N('n', '\n'),
        T('t', '\t');

        public char letter;
        public char replacement;

        EscapeCharacters(char letter, char replacement) {
            this.letter = letter;
            this.replacement = replacement;
        }

        public static char getReplacement(char input) {
            for (EscapeCharacters value : EscapeCharacters.values())
                if (input == value.letter)
                    return value.replacement;
            return input;
        }
    }
}
