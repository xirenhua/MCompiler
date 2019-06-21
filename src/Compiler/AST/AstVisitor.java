package Compiler.AST;

import Compiler.AST.Decl.*;
import Compiler.AST.Expr.*;
import Compiler.AST.Stat.*;

public interface AstVisitor {
    void visit(ProgramNode node);

    void visit(FuncDeclNode node);

    void visit(ClassDeclNode node);

    void visit(VarDeclNode node);

    void visit(TypeNode node);

    void visit(IfStatNode node);

    void visit(WhileStatNode node);

    void visit(ForStatNode node);

    void visit(BreakStatNode node);

    void visit(ContinueStatNode node);

    void visit(ReturnStatNode node);

    void visit(VarDeclStatNode node);

    void visit(ExprStatNode node);

    void visit(BlockStatNode node);

    void visit(IdentifierExprNode node);

    void visit(LiteralExprNode node);

    void visit(ArrayExprNode node);

    void visit(MemberExprNode node);

    void visit(FuncCallExprNode node);

    void visit(NewExprNode node);

    void visit(UnaryExprNode node);

    void visit(BinaryExprNode node);

    void visit(AssignExprNode node);

    void visit(PostfixExprNode node);

    void visit(PrefixExprNode node);
}
