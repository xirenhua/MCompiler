package Compiler.AST.Expr;

import Compiler.AST.AstVisitor;
import Compiler.AST.TokenLocation;
import Compiler.entities.Entity;

public class IdentifierExprNode extends ExprNode {
    public String name;
    public Entity varfuncEntity;

    public IdentifierExprNode(TokenLocation alocation, String aname) {
        location = alocation;
        name = aname;
    }

    @Override
    public void accept(AstVisitor visitor) {
        visitor.visit(this);
    }
}
