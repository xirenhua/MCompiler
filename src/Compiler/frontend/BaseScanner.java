package Compiler.frontend;

import Compiler.AST.AstVisitor;
import Compiler.AST.Decl.*;
import Compiler.AST.Expr.*;
import Compiler.AST.Stat.*;
import Compiler.AST.TypeNode;
import Compiler.AST.ProgramNode;

public class BaseScanner implements AstVisitor {
    @Override
    public void visit(ProgramNode node) {
    }

    @Override
    public void visit(FuncDeclNode node) {
    }

    @Override
    public void visit(ClassDeclNode node) {
    }

    @Override
    public void visit(VarDeclNode node) {
    }

    @Override
    public void visit(TypeNode node) {
    }

    @Override
    public void visit(IfStatNode node) {
    }

    @Override
    public void visit(WhileStatNode node) {
    }

    @Override
    public void visit(ForStatNode node) {
    }

    @Override
    public void visit(BreakStatNode node) {
    }

    @Override
    public void visit(ContinueStatNode node) {
    }

    @Override
    public void visit(ReturnStatNode node) {
    }

    @Override
    public void visit(VarDeclStatNode node) {
    }

    @Override
    public void visit(ExprStatNode node) {
    }

    @Override
    public void visit(BlockStatNode node) {
    }

    @Override
    public void visit(IdentifierExprNode node) {
    }

    @Override
    public void visit(LiteralExprNode node) {
    }

    @Override
    public void visit(ArrayExprNode node) {
    }

    @Override
    public void visit(MemberExprNode node) {
    }

    @Override
    public void visit(FuncCallExprNode node) {
    }

    @Override
    public void visit(NewExprNode node) {
    }

    @Override
    public void visit(UnaryExprNode node) {
    }

    @Override
    public void visit(BinaryExprNode node) {
    }

    @Override
    public void visit(AssignExprNode node) {
    }

    @Override
    public void visit(PostfixExprNode node) {
    }

    @Override
    public void visit(PrefixExprNode node) {
    }
}