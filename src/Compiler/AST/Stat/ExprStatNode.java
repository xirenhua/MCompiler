package Compiler.AST.Stat;

import Compiler.AST.AstVisitor;
import Compiler.AST.Expr.ExprNode;
import Compiler.AST.TokenLocation;

public class ExprStatNode extends StatNode {
    public ExprNode iExpr;

    public ExprStatNode(TokenLocation alocation, ExprNode aiExpr) {
        location = alocation;
        iExpr = aiExpr;
    }

    @Override
    public void accept(AstVisitor visitor) {
        visitor.visit(this);
    }
}
