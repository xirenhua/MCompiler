package Compiler.AST.Expr;

import Compiler.AST.AstVisitor;
import Compiler.AST.TokenLocation;

public class AssignExprNode extends ExprNode {
    public ExprNode lhs;
    public ExprNode rhs;

    public AssignExprNode(TokenLocation alocation, ExprNode alhs, ExprNode arhs) {
        location = alocation;
        lhs = alhs;
        rhs = arhs;
    }

    @Override
    public void accept(AstVisitor visitor) {
        visitor.visit(this);
    }
}
