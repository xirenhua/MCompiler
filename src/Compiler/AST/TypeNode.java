package Compiler.AST;

import Compiler.type.Type;

public class TypeNode extends AstNode {
    public Type type;

    public TypeNode(TokenLocation alocation, Type atype) {
        location = alocation;
        type = atype;
    }

    @Override
    public void accept(AstVisitor visitor) {
        visitor.visit(this);
    }
}