package Compiler.AST.Stat;

import Compiler.AST.AstVisitor;
import Compiler.AST.Expr.ExprNode;
import Compiler.AST.TokenLocation;
import Compiler.entities.LocalScope;

public class ForStatNode extends StatNode {
    public ExprNode initExpr;
    public ExprNode condExpr;
    public ExprNode updateExpr;
    public StatNode thenStat;
    public LocalScope forScope;

    public ForStatNode(TokenLocation alocation, StatNode athenStat) {
        location = alocation;
        thenStat = athenStat;
    }

    @Override
    public void accept(AstVisitor visitor) {
        visitor.visit(this);
    }
}
