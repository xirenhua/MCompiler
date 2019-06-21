package Compiler.AST.Expr;

import Compiler.AST.AstVisitor;
import Compiler.AST.TokenLocation;

public class BinaryExprNode extends ExprNode {
    public String op;
    public ExprNode lhs;
    public ExprNode rhs;

    public BinaryExprNode(TokenLocation alocation, ExprNode alhs, ExprNode arhs, String aop) {
        location = alocation;
        lhs = alhs;
        rhs = arhs;
        op = aop;
    }

    @Override
    public void accept(AstVisitor visitor) {
        visitor.visit(this);
    }
}

