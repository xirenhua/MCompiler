package Compiler.AST.Stat;

import Compiler.AST.AstVisitor;
import Compiler.AST.Expr.ExprNode;
import Compiler.AST.TokenLocation;

public class ReturnStatNode extends StatNode {
    public ExprNode retExpr;

    public ReturnStatNode(TokenLocation alocation) {
        location = alocation;
    }

    @Override
    public void accept(AstVisitor visitor) {
        visitor.visit(this);
    }
}
