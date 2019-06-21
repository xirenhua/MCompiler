package Compiler.AST.Expr;

import Compiler.AST.AstVisitor;
import Compiler.AST.TokenLocation;
import Compiler.AST.TypeNode;

import java.util.ArrayList;
import java.util.List;

public class NewExprNode extends ExprNode {
    public TypeNode baseType;
    public List<ExprNode> exprDimensions;
    public int numDimension;

    public NewExprNode(TokenLocation alocation, TypeNode abaseType) {
        location = alocation;
        baseType = abaseType;
        exprDimensions = new ArrayList<>();
    }

    public void add(ExprNode expr) {
        exprDimensions.add(expr);
    }

    @Override
    public void accept(AstVisitor visitor) {
        visitor.visit(this);
    }
}
