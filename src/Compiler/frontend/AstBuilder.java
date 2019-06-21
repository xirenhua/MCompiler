package Compiler.frontend;

import Compiler.AST.Decl.*;
import Compiler.AST.Expr.*;
import Compiler.AST.Stat.*;
import Compiler.AST.TypeNode;
import Compiler.AST.AstNode;
import Compiler.AST.ProgramNode;
import Compiler.AST.TokenLocation;
import Compiler.parser.MBaseVisitor;
import Compiler.parser.MParser.*;
import Compiler.type.*;
import Compiler.utils.SyntaxError;


import static Compiler.parser.MParser.Identifier;
import static Compiler.parser.MParser.THIS;

public class AstBuilder extends MBaseVisitor<AstNode> {
    @Override
    public AstNode visitProgram(ProgramContext ctx) {
        TokenLocation location = new TokenLocation(ctx);
        ProgramNode program = new ProgramNode(location);
        for (DeclarationContext c : ctx.declaration()) {
            if (c.classDecl() != null)
                program.add((ClassDeclNode) visit(c.classDecl()));
            else if (c.funcDecl() != null)
                program.add((FuncDeclNode) visit(c.funcDecl()));
            else
                program.add((VarDeclNode) visit(c.varDecl()));
        }
        return program;
    }

    @Override
    public AstNode visitDeclaration(DeclarationContext ctx) {
        return null;
    }

    @Override
    public AstNode visitVarDecl(VarDeclContext ctx) {
        TokenLocation location = new TokenLocation(ctx);
        String name = ctx.Identifier().getText();
        TypeNode retType = (TypeNode) visit(ctx.type());
        VarDeclNode varDecl = new VarDeclNode(location, name, retType);
        if (ctx.varInit() != null)
            varDecl.initExpr = (ExprNode) visit(ctx.varInit());
        return varDecl;
    }

    @Override
    public AstNode visitClassDecl(ClassDeclContext ctx) {
        TokenLocation location = new TokenLocation(ctx);
        String name = ctx.Identifier().getText();
        ClassDeclNode classDecl = new ClassDeclNode(location, name);
        for (VarDeclContext varDecl : ctx.classBody().varDecl()) {
            classDecl.add((VarDeclNode) visit(varDecl));
        }
        for (FuncDeclContext funcDecl : ctx.classBody().funcDecl()) {
            classDecl.add((FuncDeclNode) visit(funcDecl));
        }
        for (ConstructorDeclContext constructorDecl : ctx.classBody().constructorDecl()) {
            if (classDecl.constructor == null) {
                classDecl.constructor = (FuncDeclNode) visit(constructorDecl);
            } else {
                throw new SyntaxError(classDecl.location, "Too much constructors");
            }
        }
        return classDecl;
    }

    @Override
    public AstNode visitFuncDecl(FuncDeclContext ctx) {
        TokenLocation location = new TokenLocation(ctx);
        String name = ctx.Identifier().getText();
        TypeNode retType = (TypeNode) visit(ctx.type());
        FuncDeclNode funcDecl = new FuncDeclNode(location, name, retType);
        for (ParamDeclContext paramDecl : ctx.paramList().paramDecl()) {
            funcDecl.add((VarDeclNode) visit(paramDecl));
        }
        for (StatContext stat : ctx.funcBody().stat()) {
            funcDecl.add((StatNode) visit(stat));
        }
        return funcDecl;
    }


    @Override
    public AstNode visitConstructorDecl(ConstructorDeclContext ctx) {
        TokenLocation location = new TokenLocation(ctx.Identifier().getSymbol());
        String name = ctx.Identifier().getText();
        TypeNode retType = null;
        FuncDeclNode funcDecl = new FuncDeclNode(location, name, retType);
        for (ParamDeclContext paramDecl : ctx.paramList().paramDecl()) {
            funcDecl.add((VarDeclNode) visit(paramDecl));
        }
        for (StatContext stat : ctx.funcBody().stat()) {
            funcDecl.add((StatNode) visit(stat));
        }
        return funcDecl;
    }

    @Override
    public AstNode visitVarInit(VarInitContext ctx) {
        return visit(ctx.expr());
    }

    @Override
    public AstNode visitParamList(ParamListContext ctx) {
        return null;
    }

    @Override
    public AstNode visitParamDecl(ParamDeclContext ctx) {
        TokenLocation location = new TokenLocation(ctx);
        String name = ctx.Identifier().getText();
        TypeNode retType = (TypeNode) visit(ctx.type());
        return new VarDeclNode(location, name, retType);
    }

    @Override
    public AstNode visitFuncBody(FuncBodyContext ctx) {
        return null;
    }

    @Override
    public AstNode visitClassBody(ClassBodyContext ctx) {
        return null;
    }

    @Override
    public AstNode visitType(TypeContext ctx) {
        if (ctx.empty().isEmpty()) {
            return visit(ctx.atomType());
        } else {
            TokenLocation location = new TokenLocation(ctx);
            TypeNode baseType = (TypeNode) visit(ctx.atomType());
            int dimension = ctx.empty().size();
            ArrayType type = new ArrayType(baseType.type, dimension);
            return new TypeNode(location, type);
        }
    }

    @Override
    public AstNode visitAtomType(AtomTypeContext ctx) {
        if (ctx.primitiveType() != null) {
            return visit(ctx.primitiveType());
        } else {
            return visit(ctx.classType());
        }
    }


    @Override
    public AstNode visitPrimitiveType(PrimitiveTypeContext ctx) {
        TokenLocation location = new TokenLocation(ctx);
        switch (ctx.token.getText()) {
            case "bool":
                return new TypeNode(location, new BoolType());
            case "int":
                return new TypeNode(location, new IntType());
            case "void":
                return new TypeNode(location, new VoidType());
            default:
                return new TypeNode(location, new StringType());
        }
    }


    @Override
    public AstNode visitClassType(ClassTypeContext ctx) {
        TokenLocation location = new TokenLocation(ctx);
        return new TypeNode(location, new ClassType(ctx.token.getText()));
    }

    @Override
    public AstNode visitIfStat(IfStatContext ctx) {
        TokenLocation location = new TokenLocation(ctx);
        ExprNode condExpr = (ExprNode) visit(ctx.expr());
        StatNode thenStat = (StatNode) visit(ctx.stat(0));
        IfStatNode ifStat = new IfStatNode(location, condExpr, thenStat);
        if (ctx.ELSE() != null) {
            ifStat.elseStat = (StatNode) visit(ctx.stat(1));
        }
        return ifStat;
    }

    @Override
    public AstNode visitWhileStat(WhileStatContext ctx) {
        TokenLocation location = new TokenLocation(ctx);
        ExprNode condExpr = (ExprNode) visit(ctx.expr());
        StatNode thenStat = (StatNode) visit(ctx.stat());
        return new WhileStatNode(location, condExpr, thenStat);
    }

    @Override
    public AstNode visitForStat(ForStatContext ctx) {
        TokenLocation location = new TokenLocation(ctx);
        StatNode thenStat = (StatNode) visit(ctx.stat());
        ForStatNode forStat = new ForStatNode(location, thenStat);
        if (ctx.forInit != null) {
            forStat.initExpr = (ExprNode) visit(ctx.forInit);
        }
        if (ctx.forCondition != null) {
            forStat.condExpr = (ExprNode) visit(ctx.forCondition);
        }
        if (ctx.forUpdate != null) {
            forStat.updateExpr = (ExprNode) visit(ctx.forUpdate);
        }
        return forStat;
    }

    @Override
    public AstNode visitBreakStat(BreakStatContext ctx) {
        return new BreakStatNode(new TokenLocation(ctx));
    }

    @Override
    public AstNode visitContinueStat(ContinueStatContext ctx) {
        return new ContinueStatNode(new TokenLocation(ctx));
    }

    @Override
    public AstNode visitReturnStat(ReturnStatContext ctx) {
        TokenLocation location = new TokenLocation(ctx);
        ReturnStatNode returnStat = new ReturnStatNode(location);
        if (ctx.expr() != null) {
            returnStat.retExpr = (ExprNode) visit(ctx.expr());
        }
        return returnStat;
    }

    @Override
    public AstNode visitVarDeclStat(VarDeclStatContext ctx) {
        TokenLocation location = new TokenLocation(ctx);
        VarDeclNode ivarDecl = (VarDeclNode) visit(ctx.varDecl());
        return new VarDeclStatNode(location, ivarDecl);
    }

    @Override
    public AstNode visitExprStat(ExprStatContext ctx) {
        TokenLocation location = new TokenLocation(ctx);
        ExprNode iExpr = (ExprNode) visit(ctx.expr());
        return new ExprStatNode(location, iExpr);
    }

    @Override
    public AstNode visitBlockStat(BlockStatContext ctx) {
        TokenLocation location = new TokenLocation(ctx);
        BlockStatNode blockStat = new BlockStatNode(location);
        for (StatContext stat : ctx.stat()) {
            blockStat.add((StatNode) visit(stat));
        }
        return blockStat;
    }

    @Override
    public AstNode visitEmptyStat(EmptyStatContext ctx) {
        return null;
    }

    @Override
    public AstNode visitPostfixExpr(PostfixExprContext ctx) {
        TokenLocation location = new TokenLocation(ctx);
        ExprNode iExpr = (ExprNode) visit(ctx.expr());
        String op = ctx.postfix.getText();
        return new PostfixExprNode(location, iExpr, op);
    }

    @Override
    public AstNode visitFuncCallExpr(FuncCallExprContext ctx) {
        TokenLocation location = new TokenLocation(ctx);
        ExprNode funcExpr = (ExprNode) visit(ctx.expr());
        FuncCallExprNode funcCallExpr = new FuncCallExprNode(location, funcExpr);
        for (ExprContext expr : ctx.exprlist().expr()) {
            funcCallExpr.add((ExprNode) visit(expr));
        }
        return funcCallExpr;
    }

    @Override
    public AstNode visitArrayExpr(ArrayExprContext ctx) {
        TokenLocation location = new TokenLocation(ctx);
        ExprNode adress = (ExprNode) visit(ctx.expr(0));
        ExprNode index = (ExprNode) visit(ctx.expr(1));
        return new ArrayExprNode(location, adress, index);
    }

    @Override
    public AstNode visitMemberExpr(MemberExprContext ctx) {
        TokenLocation location = new TokenLocation(ctx);
        ExprNode obj = (ExprNode) visit(ctx.expr());
        String field_method = ctx.Identifier().getText();
        return new MemberExprNode(location, obj, field_method);
    }

    @Override
    public AstNode visitPrefixExpr(PrefixExprContext ctx) {
        TokenLocation location = new TokenLocation(ctx);
        ExprNode iExpr = (ExprNode) visit(ctx.expr());
        String op = ctx.prefix.getText();
        return new PrefixExprNode(location, iExpr, op);
    }

    @Override
    public AstNode visitUnaryExpr(UnaryExprContext ctx) {
        TokenLocation location = new TokenLocation(ctx);
        ExprNode iExpr = (ExprNode) visit(ctx.expr());
        String op = ctx.prefix.getText();
        return new UnaryExprNode(location, iExpr, op);
    }

    @Override
    public AstNode visitNewExpr(NewExprContext ctx) {
        return visit(ctx.creator());
    }

    @Override
    public AstNode visitBinaryExpr(BinaryExprContext ctx) {
        TokenLocation location = new TokenLocation(ctx);
        ExprNode lhs = (ExprNode) visit(ctx.left);
        ExprNode rhs = (ExprNode) visit(ctx.right);
        String op = ctx.op.getText();
        return new BinaryExprNode(location, lhs, rhs, op);
    }

    @Override
    public AstNode visitAssignExpr(AssignExprContext ctx) {
        TokenLocation location = new TokenLocation(ctx);
        ExprNode lhs = (ExprNode) visit(ctx.left);
        ExprNode rhs = (ExprNode) visit(ctx.right);
        return new AssignExprNode(location, lhs, rhs);
    }

    @Override
    public AstNode visitPrimaryExpr(PrimaryExprContext ctx) {
        if (ctx.token == null) {
            return visit(ctx.expr());
        } else if (ctx.token.getType() == Identifier || ctx.token.getType() == THIS) {
            return new IdentifierExprNode(new TokenLocation(ctx), ctx.token.getText());
        } else {
            return new LiteralExprNode(ctx.token);
        }
    }

    @Override
    public AstNode visitExprlist(ExprlistContext ctx) {
        return null;
    }

    @Override
    public AstNode visitCreator(CreatorContext ctx) {
        TokenLocation location = new TokenLocation(ctx);
        if (ctx.classType() != null) {
            TypeNode baseType = (TypeNode) visit(ctx.classType());
            NewExprNode newExpr = new NewExprNode(location, baseType);
            newExpr.numDimension = 0;
            return newExpr;
        } else {
            TypeNode baseType = (TypeNode) visit(ctx.atomType());
            NewExprNode newExpr = new NewExprNode(location, baseType);
            for (ExprContext expr : ctx.expr()) {
                newExpr.add((ExprNode) visit(expr));
            }
            newExpr.numDimension = ctx.expr().size() + ctx.empty().size();
            return newExpr;
        }
    }

    @Override
    public AstNode visitEmpty(EmptyContext ctx) {
        return null;
    }
}