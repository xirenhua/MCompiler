package Compiler.AST.Stat;

import Compiler.AST.AstVisitor;
import Compiler.AST.TokenLocation;

public class BreakStatNode extends StatNode {
    public BreakStatNode(TokenLocation alocation) {
        location = alocation;
    }

    @Override
    public void accept(AstVisitor visitor) {
        visitor.visit(this);
    }
}
