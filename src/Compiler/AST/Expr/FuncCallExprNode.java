package Compiler.AST.Expr;

import Compiler.AST.AstVisitor;
import Compiler.AST.TokenLocation;
import Compiler.entities.FuncEntity;

import java.util.ArrayList;
import java.util.List;

public class FuncCallExprNode extends ExprNode {
    public ExprNode funcExpr;
    public List<ExprNode> arguments;
    public FuncEntity funcEntity;

    public FuncCallExprNode(TokenLocation alocation, ExprNode afuncExpr) {
        location = alocation;
        funcExpr = afuncExpr;
        arguments = new ArrayList<>();
    }

    public void add(ExprNode expr) {
        arguments.add(expr);
    }

    @Override
    public void accept(AstVisitor visitor) {
        visitor.visit(this);
    }
}
