package Compiler.AST.Stat;

import Compiler.AST.AstVisitor;
import Compiler.AST.Decl.VarDeclNode;
import Compiler.AST.TokenLocation;

public class VarDeclStatNode extends StatNode {
    public VarDeclNode ivarDecl;

    public VarDeclStatNode(TokenLocation alocation, VarDeclNode aivarDecl) {
        location = alocation;
        ivarDecl = aivarDecl;
    }

    @Override
    public void accept(AstVisitor visitor) {
        visitor.visit(this);
    }
}
