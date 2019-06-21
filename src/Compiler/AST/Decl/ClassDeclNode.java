package Compiler.AST.Decl;

import Compiler.AST.AstVisitor;
import Compiler.AST.TokenLocation;
import Compiler.entities.ClassEntity;

import java.util.ArrayList;
import java.util.List;

public class ClassDeclNode extends DeclNode {
    public List<VarDeclNode> fields;
    public List<FuncDeclNode> methods;
    public FuncDeclNode constructor;
    public ClassEntity classEntity;

    public ClassDeclNode(TokenLocation alocation, String aname) {
        location = alocation;
        name = aname;
        fields = new ArrayList<>();
        methods = new ArrayList<>();
    }

    public void add(VarDeclNode varDecl) {
        fields.add(varDecl);
    }

    public void add(FuncDeclNode funcDecl) {
        methods.add(funcDecl);
    }

    @Override
    public void accept(AstVisitor visitor) {
        visitor.visit(this);
    }
}
