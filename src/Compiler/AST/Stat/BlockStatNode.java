package Compiler.AST.Stat;

import Compiler.AST.AstVisitor;
import Compiler.AST.TokenLocation;
import Compiler.entities.LocalScope;

import java.util.ArrayList;
import java.util.List;

public class BlockStatNode extends StatNode {
    public List<StatNode> iStats;
    public LocalScope blockScope;

    public BlockStatNode(TokenLocation alocation) {
        location = alocation;
        iStats = new ArrayList<>();
    }

    public void add(StatNode stat) {
        iStats.add(stat);
    }

    @Override
    public void accept(AstVisitor visitor) {
        visitor.visit(this);
    }
}