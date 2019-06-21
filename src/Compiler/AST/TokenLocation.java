package Compiler.AST;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;

public class TokenLocation {
    public final int row;
    public final int column;

    public TokenLocation(Token token) {
        row = token.getLine();
        column = token.getCharPositionInLine();
    }

    public TokenLocation(int arow, int acolumn) {
        row = arow;
        column = acolumn;
    }

    public TokenLocation(TerminalNode node) {
        this(node.getSymbol());
    }

    public TokenLocation(ParserRuleContext ctx) {
        this(ctx.start);
    }

    @Override
    public String toString() {
        return "(" + row + "," + column + ")";
    }
}
