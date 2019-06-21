package Compiler.AST.Decl;

import Compiler.AST.AstVisitor;
import Compiler.AST.Stat.StatNode;
import Compiler.AST.TokenLocation;
import Compiler.AST.TypeNode;
import Compiler.entities.FuncEntity;

import java.util.ArrayList;
import java.util.List;

public class FuncDeclNode extends DeclNode {
    // indicate whether methods use classname
    // indicate whether constructor by retType null
    public TypeNode retType;
    public List<VarDeclNode> params;
    public List<StatNode> stats;
    public FuncEntity funcEntity;

    public FuncDeclNode(TokenLocation alocation, String aname, TypeNode aretType) {
        location = alocation;
        name = aname;
        retType = aretType;
        params = new ArrayList<>();
        stats = new ArrayList<>();
    }

    public void add(VarDeclNode param) {
        params.add(param);
    }

    public void add(StatNode stat) {
        stats.add(stat);
    }

    @Override
    public void accept(AstVisitor visitor) {
        visitor.visit(this);
    }
}
