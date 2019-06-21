package Compiler.AST.Stat;

import Compiler.AST.AstVisitor;
import Compiler.AST.TokenLocation;

public class ContinueStatNode extends StatNode {
    public ContinueStatNode(TokenLocation alocation) {
        location = alocation;
    }

    @Override
    public void accept(AstVisitor visitor) {
        visitor.visit(this);
    }
}
