package Compiler.frontend;


import java.io.PrintStream;

import Compiler.AST.AstVisitor;
import Compiler.AST.ProgramNode;
import Compiler.AST.Stat.*;
import Compiler.AST.Decl.*;
import Compiler.AST.Expr.*;
import Compiler.AST.TypeNode;


public class AstPrinter implements AstVisitor {
    private static final String INDENT_UNIT = "    ";
    private StringBuilder indentStrBuilder = new StringBuilder();
    private PrintStream out;

    public AstPrinter(PrintStream out) {
        this.out = out;
    }

    private void indent() {
        indentStrBuilder.append(INDENT_UNIT);
    }

    private void unindent() {
        indentStrBuilder.delete(indentStrBuilder.length() - INDENT_UNIT.length(), indentStrBuilder.length());
    }

    private String getIndentStr() {
        return indentStrBuilder.toString();
    }

    private void println(String str) {
        out.println(getIndentStr() + str);
    }

    private void print(String str) {
        out.print(getIndentStr() + str);
    }

    private void printf(String str, Object... args) {
        out.printf(getIndentStr() + str, args);
    }


    @Override
    public void visit(ProgramNode node) {
        if (node == null) System.err.println("???");
        printf("@ ProgramNode %s:\n", node.location.toString());
        if (!(node.declarations.isEmpty())) {
            println(">>> decls:");
            for (DeclNode decl : node.declarations) {
                decl.accept(this);
            }
        } else {
            println(">>> decls: null");
        }
    }

    @Override
    public void visit(FuncDeclNode node) {
        indent();
        printf("@ FuncDeclNode %s:\n", node.location.toString());
        if (node.retType != null) {
            println(">>> returnType:");
            node.retType.accept(this);
        } else {
            println(">>> returnType: null");
        }
        printf(">>> name: %s\n", node.name);
        if (!(node.params.isEmpty())) {
            println(">>> parameterList:");
            for (VarDeclNode parameter : node.params) {
                parameter.accept(this);
            }
        } else {
            println(">>> parameterList: null");
        }
        if (node.stats.isEmpty()) {
            println(">>> body: null");
        } else {
            println(">>> body:");
            for (StatNode stat : node.stats) {
                if (stat != null) {
                    stat.accept(this);
                }
            }
        }
        unindent();
    }

    @Override
    public void visit(ClassDeclNode node) {
        indent();
        printf("@ ClassDeclNode %s:\n", node.location.toString());
        printf(">>> name: %s\n", node.name);
        if (!(node.fields.isEmpty())) {
            println(">>> varMember:");
            for (VarDeclNode varMem : node.fields) {
                varMem.accept(this);
            }
        } else {
            println(">>> varMember: null");
        }
        if (!(node.methods.isEmpty())) {
            println(">>> funcMember:");
            for (FuncDeclNode funcMem : node.methods) {
                funcMem.accept(this);
            }
        } else {
            println(">>> funcMember: null");
        }
        unindent();
    }

    @Override
    public void visit(VarDeclNode node) {
        indent();
        printf("@ VaeDeclNode %s:\n", node.location.toString());
        println(">>> type:");
        node.retType.accept(this);
        printf(">>> name: %s\n", node.name);
        if (node.initExpr != null) {
            println(">>> init:");
            node.initExpr.accept(this);
        } else {
            println(">>> init: null");
        }
        unindent();
    }

    @Override
    public void visit(BlockStatNode node) {
        indent();
        printf("@ BlockStmtNode %s:\n", node.location.toString());
        if (node.iStats.isEmpty()) {
            println(">>> body: null");
        } else {
            println(">>> body:");
            for (StatNode stat : node.iStats) {
                stat.accept(this);
            }
        }
        unindent();
    }

    @Override
    public void visit(ExprStatNode node) {
        indent();
        printf("@ ExprStmtNode %s:\n", node.location.toString());
        println(">>> expr:");
        node.iExpr.accept(this);
        unindent();
    }

    @Override
    public void visit(IfStatNode node) {
        indent();
        printf("@ CondStmtNode %s:\n", node.location.toString());
        println(">>> cond:");
        node.condExpr.accept(this);
        println(">>> thenStmt:");
        node.thenStat.accept(this);
        if (node.elseStat != null) {
            println(">>> elseStmt:");
            node.elseStat.accept(this);
        } else {
            println(">>> elseStmt: null");
        }
        unindent();
    }

    @Override
    public void visit(WhileStatNode node) {
        indent();
        printf("@ WhileStmtNode %s:\n", node.location.toString());
        println(">>> cond:");
        node.condExpr.accept(this);
        println(">>> stmt:");
        node.thenStat.accept(this);
        unindent();
    }

    @Override
    public void visit(ForStatNode node) {
        indent();
        printf("@ ForStmtNode %s:\n", node.location.toString());
        if (node.initExpr != null) {
            println(">>> init:");
            node.initExpr.accept(this);
        } else {
            println(">>> init: null");
        }
        if (node.condExpr != null) {
            println(">>> cond:");
            node.condExpr.accept(this);
        } else {
            println(">>> cond: null");
        }
        if (node.updateExpr != null) {
            println(">>> step:");
            node.updateExpr.accept(this);
        } else {
            println(">>> step: null");
        }
        println(">>> stmt:");
        node.thenStat.accept(this);
        unindent();
    }

    @Override
    public void visit(VarDeclStatNode node) {
        indent();
        printf("@ VarDeclStmtNode %s:\n", node.location.toString());
        println(">>> varDecl:");
        node.ivarDecl.accept(this);
        unindent();

    }

    @Override
    public void visit(ContinueStatNode node) {
        indent();
        printf("@ ContinueStmtNode %s:\n", node.location.toString());
        unindent();
    }

    @Override
    public void visit(BreakStatNode node) {
        indent();
        printf("@ BreakStmtNode %s:\n", node.location.toString());
        unindent();
    }

    @Override
    public void visit(ReturnStatNode node) {
        indent();
        printf("@ ReturnStmtNode %s:\n", node.location.toString());
        if (node.retExpr != null) {
            println(">>> expr:");
            node.retExpr.accept(this);
        } else {
            println(">>> expr: null");
        }
        unindent();
    }

    @Override
    public void visit(PostfixExprNode node) {
        indent();
        printf("@ PosfixExprNode %s:\n", node.location.toString());
        printf(">>> op: %s\n", node.op);
        println(">>> expr:");
        node.iExpr.accept(this);
        unindent();
    }

    @Override
    public void visit(FuncCallExprNode node) {
        indent();
        printf("@ FuncCallExprNode %s:\n", node.location.toString());
        println(">>> func:");
        node.funcExpr.accept(this);
        if (!(node.arguments.isEmpty())) {
            println(">>> args:");
            for (ExprNode arg : node.arguments) {
                arg.accept(this);
            }
        } else {
            println(">>> args: null");
        }
        unindent();
    }

    @Override
    public void visit(ArrayExprNode node) {
        indent();
        printf("@ ArrayExprNode %s:\n", node.location.toString());
        println(">>> arr:");
        node.address.accept(this);
        println(">>> sub:");
        node.index.accept(this);
        unindent();
    }

    @Override
    public void visit(MemberExprNode node) {
        indent();
        printf("@ MemberExprNode %s:\n", node.location.toString());
        println(">>> expr:");
        node.obj.accept(this);
        printf(">>> member: %s\n", node.field_method);
        unindent();
    }

    @Override
    public void visit(PrefixExprNode node) {
        indent();
        printf("@ PrefixExprNode %s:\n", node.location.toString());
        printf(">>> op: %s\n", node.op);
        println(">>> expr:");
        node.iExpr.accept(this);
        unindent();
    }

    @Override
    public void visit(UnaryExprNode node) {
        indent();
        printf("@ PrefixExprNode %s:\n", node.location.toString());
        printf(">>> op: %s\n", node.op);
        println(">>> expr:");
        node.iExpr.accept(this);
        unindent();
    }

    @Override
    public void visit(NewExprNode node) {
        indent();
        printf("@ NewExprNode %s:\n", node.location.toString());
        println(">>> newType:");
        node.baseType.accept(this);
        if (node.exprDimensions != null) {
            println(">>> exprdims:");
            for (ExprNode dim : node.exprDimensions) {
                dim.accept(this);
            }
            printf(">>> numDim: %d\n", node.numDimension);
        } else {
            println(">>> numDim: 0");
        }
        unindent();
    }

    @Override
    public void visit(BinaryExprNode node) {
        indent();
        printf("@ BinaryExprNode %s:\n", node.location.toString());
        printf(">>> op: %s\n", node.op);
        println(">>> lhs:");
        node.lhs.accept(this);
        println(">>> rhs:");
        node.rhs.accept(this);
        unindent();
    }

    @Override
    public void visit(AssignExprNode node) {
        indent();
        printf("@ AssignExprNode %s:\n", node.location.toString());
        println(">>> lhs:");
        node.lhs.accept(this);
        println(">>> rhs:");
        node.rhs.accept(this);
        unindent();
    }

    @Override
    public void visit(IdentifierExprNode node) {
        indent();
        printf("@ IdentifierExprNode %s:\n", node.location.toString());
        printf(">>> identifier: %s\n", node.name);
        unindent();
    }


    @Override
    public void visit(LiteralExprNode node) {
        indent();
        printf("@ LiteralExprNode %s:\n", node.location.toString());
        printf(">>> identifier: %s\n", node.value);
        unindent();
    }

    @Override
    public void visit(TypeNode node) {
        indent();
        printf("@ TypeNode %s:\n", node.location.toString());
        printf(">>> type: %s\n", node.type.toString());
        unindent();
    }
}
