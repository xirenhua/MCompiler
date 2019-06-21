package Compiler.AST.Expr;

import Compiler.AST.AstVisitor;
import Compiler.AST.TokenLocation;

public class PrefixExprNode extends ExprNode {
    public ExprNode iExpr;
    public String op;

    public PrefixExprNode(TokenLocation alocation, ExprNode aiExpr, String aop) {
        location = alocation;
        op = aop;
        iExpr = aiExpr;
    }

    @Override
    public void accept(AstVisitor visitor) {
        visitor.visit(this);
    }
}
