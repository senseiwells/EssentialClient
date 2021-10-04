package me.senseiwells.arucas.tokens;

import me.senseiwells.arucas.utils.Position;

public class KeyWordToken extends Token {

    public KeyWord keyWord;

    public KeyWordToken(KeyWord keyword, Position startPos, Position endPos) {
        super(Type.KEYWORD, startPos, endPos);
        this.keyWord = keyword;
    }

    public KeyWordToken(KeyWord keyword, Position startPos) {
        super(Type.KEYWORD, startPos, startPos.copy().advance(null));
        this.keyWord = keyword;
    }

    public enum KeyWord {

        VAR("var"),
        CONST("const"),
        FUN("fun"),
        AND("and"),
        OR("or"),
        NOT("not"),
        IF("if"),
        THEN("then"),
        ELSE("else"),
        WHILE("while"),
        RETURN("return"),
        CONTINUE("continue"),
        BREAK("break"),
        START("start"),
        END("end");

        private final String name;

        KeyWord(String name) {
            this.name = name;
        }

        public static KeyWord stringToKeyWord(String word) {
            for (KeyWord keyWord : KeyWord.values())
                if (word.equals(keyWord.name))
                    return keyWord;
            return null;
        }
    }
}
