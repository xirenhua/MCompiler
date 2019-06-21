package Compiler.AST.Expr;

import Compiler.AST.AstVisitor;
import Compiler.AST.TokenLocation;
import Compiler.entities.Entity;

public class MemberExprNode extends ExprNode {
    public ExprNode obj;
    public String field_method;
    public Entity varfuncEntity;

    public MemberExprNode(TokenLocation alocation, ExprNode aobj, String afield_method) {
        location = alocation;
        obj = aobj;
        field_method = afield_method;
    }

    @Override
    public void accept(AstVisitor visitor) {
        visitor.visit(this);
    }
}
