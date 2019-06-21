package Compiler.AST.Stat;

import Compiler.AST.AstVisitor;
import Compiler.AST.Expr.ExprNode;
import Compiler.AST.TokenLocation;
import Compiler.entities.LocalScope;

public class WhileStatNode extends StatNode {
    public ExprNode condExpr;
    public StatNode thenStat;
    public LocalScope whileScope;

    public WhileStatNode(TokenLocation alocation, ExprNode acondExpr, StatNode athenStat) {
        location = alocation;
        condExpr = acondExpr;
        thenStat = athenStat;
    }
    @Override
    public void accept(AstVisitor visitor) {
        visitor.visit(this);
    }
}
