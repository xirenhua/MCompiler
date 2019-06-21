package Compiler.AST;

import java.util.*;

import Compiler.AST.Decl.*;

public class ProgramNode extends AstNode {
    public List<FuncDeclNode> functions;
    public List<ClassDeclNode> classes;
    public List<VarDeclNode> variables;
    public List<DeclNode> declarations;

    public ProgramNode(TokenLocation alocation) {
        location = alocation;
        functions = new ArrayList<>();
        classes = new ArrayList<>();
        variables = new ArrayList<>();
        declarations = new ArrayList<>();
    }

    public void add(FuncDeclNode d) {
        functions.add(d);
        declarations.add(d);
    }

    public void add(ClassDeclNode d) {
        classes.add(d);
        declarations.add(d);
    }

    public void add(VarDeclNode d) {
        variables.add(d);
        declarations.add(d);
    }

    @Override
    public void accept(AstVisitor visitor) {
        visitor.visit(this);
    }
}
