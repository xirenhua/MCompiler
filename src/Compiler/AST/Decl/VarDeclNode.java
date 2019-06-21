package Compiler.AST.Decl;

import Compiler.AST.AstVisitor;
import Compiler.AST.TokenLocation;
import Compiler.AST.TypeNode;
import Compiler.AST.Expr.ExprNode;
import Compiler.entities.VarEntity;

public class VarDeclNode extends DeclNode {
    public TypeNode retType;
    public ExprNode initExpr;
    public VarEntity varEntity;

    public VarDeclNode(TokenLocation alocation, String aname, TypeNode aretType) {
        location = alocation;
        name = aname;
        retType = aretType;
    }

    @Override
    public void accept(AstVisitor visitor) {
        visitor.visit(this);
    }
}
