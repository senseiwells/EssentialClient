package me.senseiwells.arucas.core;

import java.util.ArrayList;
import java.util.List;

import me.senseiwells.arucas.utils.Position;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.tokens.Token.Type;

public class Lexer {
    private static final LexerContext LEXER;
    
    static {
        LEXER = new LexerContext()
            // Whitespaces
            .addRule(Type.WHITESPACE, i -> i
                .addMultiline("/*", "*/")
                .addRegex("//[^\\r\\n]*")
                .addRegex("[ \t\r\n]")
            )
            
            // Comparisons
            .addRule(Type.EQUALS, i -> i.addString("=="))
            .addRule(Type.NOT_EQUALS, i -> i.addString("!="))
            .addRule(Type.LESS_THAN_EQUAL, i -> i.addString("<="))
            .addRule(Type.MORE_THAN_EQUAL, i -> i.addString(">="))
            .addRule(Type.LESS_THAN, i -> i.addString("<"))
            .addRule(Type.MORE_THAN, i -> i.addString(">"))
            .addRule(Type.NOT, i -> i.addStrings("!", "not"))
            .addRule(Type.AND, i -> i.addStrings("&&", "and"))
            .addRule(Type.OR, i -> i.addStrings("||", "or"))
            
            // Arithmetics
            .addRule(Type.PLUS, i -> i.addString("+"))
            .addRule(Type.MINUS, i -> i.addString("-"))
            .addRule(Type.MULTIPLY, i -> i.addString("*"))
            .addRule(Type.DIVIDE, i -> i.addString("/"))
            .addRule(Type.POWER, i -> i.addString("^"))
            
            // Atoms
            .addRule(Type.IDENTIFIER, i -> i.addRegex("[a-zA-Z_][a-zA-Z0-9_]*"))
            .addRule(Type.BOOLEAN, i -> i.addStrings("true", "false"))
            .addRule(Type.STRING, i -> i.addMultiline("\"", "\\", "\""))
            .addRule(Type.FLOAT, i -> i.addRegexes(
                "[0-9]+[.][0-9]+",
                "[0-9]+"
            ))
            .addRule(Type.NULL, i -> i.addStrings("null"))
            
            // Memory operations
            .addRule(Type.ASSIGN_OPERATOR, i -> i.addString("="))
            
            // Brackets
            .addRule(Type.LEFT_BRACKET, i -> i.addString("("))
            .addRule(Type.RIGHT_BRACKET, i -> i.addString(")"))
            .addRule(Type.LEFT_SQUARE_BRACKET, i -> i.addString("["))
            .addRule(Type.RIGHT_SQUARE_BRACKET, i -> i.addString("]"))
            .addRule(Type.LEFT_CURLY_BRACKET, i -> i.addString("{"))
            .addRule(Type.RIGHT_CURLY_BRACKET, i -> i.addString("}"))
            
            // Delimiters
            .addRule(Type.SEMICOLON, i -> i.addString(";"))
            .addRule(Type.COMMA, i -> i.addString(","))
            
            // Keywords
            .addRule(Type.IF, i -> i.addString("if"))
            .addRule(Type.THEN, i -> i.addStrings("then", "->"))
            .addRule(Type.ELSE, i -> i.addString("else"))
            .addRule(Type.WHILE, i -> i.addString("while"))
            .addRule(Type.CONTINUE, i -> i.addString("continue"))
            .addRule(Type.BREAK, i -> i.addString("break"))
            .addRule(Type.RETURN, i -> i.addString("return"))
            .addRule(Type.VAR, i -> i.addString("var"))
            .addRule(Type.FUN, i -> i.addString("fun"))
        ;
    }
    
    private final String text;
    public final String fileName;

    public Lexer(String text, String fileName) {
        this.text = text;
        this.fileName = fileName;
    }
    
    public List<Token> createTokens() throws CodeError {
        List<Token> tokenList = new ArrayList<>();
        int offset = 0;
        int line = 0;
        int column = 0;
        int length = text.length();
        String input = text;
        
        while (offset < length) {
            LexerContext.LexerToken lexerToken = LEXER.nextToken(input);
            
            if(lexerToken == null) {
                Position errorPos = new Position(offset, line, column, fileName);
                throw new CodeError(CodeError.ErrorType.ILLEGAL_CHAR_ERROR, "Invalid character", errorPos, errorPos);
            }
            
            if (lexerToken.length + offset > length)
                break;
            
            int old_offset = offset;
            int old_line = line;
            int old_column = column;
            
            for (int i = offset; i < offset + lexerToken.length; i++) {
                char c = text.charAt(i);
                
                if (c == '\n') {
                    line ++;
                    column = 0;
                }
                else {
                    column ++;
                }
            }
            
            if(lexerToken.type != Type.WHITESPACE) {
                tokenList.add(new Token(
                    lexerToken.type,
                    lexerToken.content,
                    new Position(old_offset, old_line, old_column, fileName),
                    new Position(offset, line, column, fileName)
                ));
            }
            
            input = input.substring(lexerToken.length);
            offset += lexerToken.length;
        }
    
        tokenList.add(new Token(Type.FINISH, new Position(offset, line, column, fileName)));
        return tokenList;
    }
}
