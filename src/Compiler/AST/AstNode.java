package Compiler.AST;

abstract public class AstNode {
    public TokenLocation location;

    public AstNode() {
    }

    abstract public void accept(AstVisitor visitor);
}
