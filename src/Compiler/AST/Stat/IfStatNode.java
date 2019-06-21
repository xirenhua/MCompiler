package Compiler.AST.Stat;

import Compiler.AST.AstVisitor;
import Compiler.AST.Expr.ExprNode;
import Compiler.AST.TokenLocation;
import Compiler.entities.LocalScope;

public class IfStatNode extends StatNode {
    public ExprNode condExpr;
    public StatNode thenStat;
    public StatNode elseStat;
    public LocalScope thenScope;
    public LocalScope elseScope;

    public IfStatNode(TokenLocation alocation, ExprNode acondExpr, StatNode athenStat) {
        location = alocation;
        condExpr = acondExpr;
        thenStat = athenStat;
    }

    @Override
    public void accept(AstVisitor visitor) {
        visitor.visit(this);
    }
}
