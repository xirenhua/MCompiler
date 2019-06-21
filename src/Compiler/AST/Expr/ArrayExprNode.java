package Compiler.AST.Expr;

import Compiler.AST.AstVisitor;
import Compiler.AST.TokenLocation;

public class ArrayExprNode extends ExprNode {
    public ExprNode address;
    public ExprNode index;

    public ArrayExprNode(TokenLocation alocation, ExprNode aadress, ExprNode aindex) {
        location = alocation;
        address = aadress;
        index = aindex;
    }

    @Override
    public void accept(AstVisitor visitor) {
        visitor.visit(this);
    }
}
