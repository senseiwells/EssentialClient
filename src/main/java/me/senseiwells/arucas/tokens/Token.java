package me.senseiwells.arucas.tokens;

import me.senseiwells.arucas.utils.Position;

public class Token {

    public Type type;
    public Position startPos;
    public Position endPos;

    public Token(Type type, Position startPos, Position endPos) {
        this.type = type;
        this.startPos = startPos;
        this.endPos = endPos;
    }

    public Token(Type type, Position startPos) {
        this(type, startPos, startPos.copy());
        this.endPos.advance(null);
    }

    @Override
    public String toString() {
        return "Token{" + "type=" + this.type + '}';
    }

    public enum Type {

        INT,
        FLOAT,
        BOOLEAN,
        STRING,
        LIST,

        PLUS,
        MINUS,
        MULTIPLY,
        DIVIDE,
        POWER,
        LEFT_BRACKET,
        RIGHT_BRACKET,
        LEFT_SQUARE_BRACKET,
        RIGHT_SQUARE_BRACKET,

        IDENTIFIER,
        KEYWORD,
        ASSIGN_OPERATOR,

        EQUALS,
        NOT_EQUALS,
        LESS_THAN,
        MORE_THAN,
        LESS_THAN_EQUAL,
        MORE_THAN_EQUAL,
        COMMA,
        NEW_LINE,

        FINISH;

        public boolean isTypeInArray(Type[] types) {
            for (Type type : types)
                if (this == type)
                    return true;
            return false;
        }
    }

}
