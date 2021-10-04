package me.senseiwells.arucas.core;

import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.throwables.Error;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.utils.Interpreter;
import me.senseiwells.arucas.utils.Position;
import me.senseiwells.arucas.utils.SymbolTable;
import me.senseiwells.arucas.nodes.Node;
import me.senseiwells.arucas.tokens.Token;

import java.util.List;

public class Run {

    public static SymbolTable symbolTable = new SymbolTable();
    public static boolean debug = false;

    // Changed for SC
    public static void run(String fileName, String line) throws Error {
        if (line.trim().equals("")) {
            Position position = new Position(0, 0, 0, fileName, line);
            throw new Error(Error.ErrorType.ILLEGAL_SYNTAX_ERROR, "Empty file - nothing to run", position, position);
        }
        Context context = new Context(fileName, null, null);
        context.symbolTable = fileName.equals("System.in") ? symbolTable.setDefaultSymbols(context) : new SymbolTable().setDefaultSymbols(context);

        List<Token> values = new Lexer(line, fileName).createTokens();

        Node nodeResult = new Parser(values, context).parse();
        try {
            new Interpreter().visit(nodeResult, context);
        }
        catch (ThrowValue tv) {
            throw new Error(Error.ErrorType.ILLEGAL_OPERATION_ERROR, "Cannot use keywords 'break' or 'continue' outside loop, and cannot use 'return' outside function", nodeResult.startPos, nodeResult.endPos);
        }
    }
    // - Sensei
}