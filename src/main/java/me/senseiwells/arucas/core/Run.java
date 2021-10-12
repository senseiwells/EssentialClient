package me.senseiwells.arucas.core;

import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.utils.Position;
import me.senseiwells.arucas.utils.SymbolTable;
import me.senseiwells.arucas.nodes.Node;
import me.senseiwells.arucas.tokens.Token;

import java.util.List;

public class Run {

    // Changed for SC
    public static SymbolTable symbolTable = new SymbolTable();
    public static boolean debug = false;

    public static void run(String fileName, String line) throws CodeError {
        if (line.trim().equals("")) {
            Position position = new Position(0, 0, 0, fileName);
            throw new CodeError(CodeError.ErrorType.ILLEGAL_SYNTAX_ERROR, "Empty file - nothing to run", position, position);
        }
        Context context = new Context(fileName, null, null);
        context.symbolTable = symbolTable.setDefaultSymbols(context);

        List<Token> values = new Lexer(line, fileName).createTokens();

        Node nodeResult = new Parser(values, context).parse();
        //symbolTable = nodeResult.context.symbolTable;

        try {
            nodeResult.visit();
        }
        catch (ThrowValue tv) {
            throw new CodeError(CodeError.ErrorType.ILLEGAL_OPERATION_ERROR, "Cannot use keywords 'break' or 'continue' outside loop, and cannot use 'return' outside function", nodeResult.startPos, nodeResult.endPos);
        }
    }
    // - Sensei
}