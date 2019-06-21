package Compiler.AST.Expr;

import Compiler.AST.AstVisitor;
import Compiler.AST.TokenLocation;

public class UnaryExprNode extends ExprNode {
    public String op;
    public ExprNode iExpr;

    public UnaryExprNode(TokenLocation alocation, ExprNode aiExpr, String aop) {
        location = alocation;
        iExpr = aiExpr;
        op = aop;
    }

    @Override
    public void accept(AstVisitor visitor) {
        visitor.visit(this);
    }
}

