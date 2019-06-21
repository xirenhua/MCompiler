package Compiler.frontend;

import Compiler.AST.Decl.*;
import Compiler.AST.Expr.*;
import Compiler.AST.ProgramNode;
import Compiler.AST.Stat.*;
import Compiler.entities.*;
import Compiler.type.*;
import Compiler.utils.SemanticError;

public class SymbolTableBuilder extends BaseScanner {
    public GlobalScope globalScope;
    public Scope currentScope;
    public Type currRetType;
    public FuncEntity currFuncEntity;
    public TypeTool typeTool;
    public int numLoop;

    public SymbolTableBuilder(GlobalScope ascope) {
        globalScope = ascope;
        currentScope = globalScope;
        typeTool = new TypeTool(globalScope.classMap);
        currRetType = null;
        currFuncEntity = null;
        numLoop = 0;
    }

    @Override
    public void visit(ProgramNode node) {
        for (DeclNode decl : node.declarations) {
            if (decl instanceof VarDeclNode) {
                visit((VarDeclNode) decl);
            } else if (decl instanceof FuncDeclNode) {
                visit((FuncDeclNode) decl);
            } else {
                visit((ClassDeclNode) decl);
            }
        }
    }

    @Override
    public void visit(VarDeclNode node) {
        if (currentScope.checkKey(node.name)) {
            throw new SemanticError(node.location, "name already defined in current scope");
        }
        if (!(typeTool.checkDeclType(node.retType.type))) {
            throw new SemanticError(node.retType.location, "illegal decl type");
        }
        if (node.initExpr != null) {
            node.initExpr.accept(this);
            if (!(typeTool.checkAssignType(node.retType.type, node.initExpr.type))) {
                throw new SemanticError(node.location, String.format("Assign type mismatch %s vs %s", node.retType.type.toString(), node.initExpr.type.toString()));
            }
        }
        VarEntity varEntity = new VarEntity(node.name, node.retType.type);
        node.varEntity = varEntity;
        currentScope.putVar(node.name, varEntity);
    }

    @Override
    public void visit(FuncDeclNode node) {
        currentScope = node.funcEntity.funcScope;
        if (node.retType == null) {
            // constructor
            currRetType = null;
        } else {
            currRetType = node.retType.type;
        }
        for (StatNode stat : node.stats) {
            if (stat != null) {
                stat.accept(this);
            }
        }
        currentScope = currentScope.parent;
    }

    @Override
    public void visit(ClassDeclNode node) {
        currentScope = node.classEntity.classScope;
        for (FuncDeclNode funcDecl : node.methods) {
            funcDecl.accept(this);
        }
        if (node.constructor != null) {
            node.constructor.accept(this);
        }
        currentScope = currentScope.parent;
    }

    @Override
    public void visit(IfStatNode node) {
        node.condExpr.accept(this);
        if (!(node.condExpr.type instanceof BoolType)) {
            throw new SemanticError(node.condExpr.location, "condition expr not bool type");
        }
        node.thenScope = new LocalScope(currentScope);
        currentScope = node.thenScope;
        node.thenStat.accept(this);
        currentScope = node.thenScope.parent;
        if (node.elseStat != null) {
            node.elseScope = new LocalScope(currentScope);
            currentScope = node.elseScope;
            node.elseStat.accept(this);
            currentScope = node.thenScope.parent;
        }
    }

    @Override
    public void visit(WhileStatNode node) {
        ++numLoop;
        node.condExpr.accept(this);
        if (!(node.condExpr.type instanceof BoolType)) {
            throw new SemanticError(node.condExpr.location, "condition expr not bool type");
        }
        node.whileScope = new LocalScope(currentScope);
        currentScope = node.whileScope;
        node.thenStat.accept(this);
        currentScope = node.whileScope.parent;
        --numLoop;
    }

    @Override
    public void visit(ForStatNode node) {
        ++numLoop;
        if (node.initExpr != null) {
            node.initExpr.accept(this);
        }
        if (node.condExpr != null) {
            node.condExpr.accept(this);
            if (!(node.condExpr.type instanceof BoolType)) {
                throw new SemanticError(node.condExpr.location, "condition expr not bool type");
            }
        }
        if (node.updateExpr != null) {
            node.updateExpr.accept(this);
        }
        node.forScope = new LocalScope(currentScope);
        currentScope = node.forScope;
        if (node.thenStat != null)
            node.thenStat.accept(this);
        currentScope = node.forScope.parent;
        --numLoop;
    }

    @Override
    public void visit(ContinueStatNode node) {
        if (numLoop <= 0)
            throw new SemanticError(node.location, "Continue statement cannot be used outside of loop statement");
    }

    @Override
    public void visit(BreakStatNode node) {
        if (numLoop <= 0)
            throw new SemanticError(node.location, "Break statement cannot be used outside of loop statement");
    }

    @Override
    public void visit(ReturnStatNode node) {
        if (node.retExpr != null) {
            node.retExpr.accept(this);
            if (!(typeTool.checkReturnMatch(currRetType, node.retExpr.type))) {
                throw new SemanticError(node.location, "mismatched return type");
            }
        } else {
            if (!((currRetType == null) || (currRetType instanceof VoidType))) {
                throw new SemanticError(node.location, "you return nothing when there is a return type");
            }
        }
    }

    @Override
    public void visit(VarDeclStatNode node) {
        node.ivarDecl.accept(this);
    }

    @Override
    public void visit(ExprStatNode node) {
        node.iExpr.accept(this);
    }

    @Override
    public void visit(BlockStatNode node) {
        node.blockScope = new LocalScope(currentScope);
        currentScope = node.blockScope;
        for (StatNode stat : node.iStats) {
            stat.accept(this);
        }
        currentScope = currentScope.parent;
    }

    @Override
    public void visit(IdentifierExprNode node) {
        Entity ent = currentScope.recurGetKey(node.name);
        if (ent == null) {
            throw new SemanticError(node.location, "name not defined");
        }
        if (ent instanceof VarEntity) {
            node.varfuncEntity = ent;
            node.isLeftValue = true;
        } else if (ent instanceof FuncEntity) {
            currFuncEntity = (FuncEntity) ent;
            node.varfuncEntity = ent;
            node.isLeftValue = false;
        }
        node.type = ent.type;
    }

    @Override
    public void visit(LiteralExprNode node) {
        switch (node.typename) {
            case "int":
                node.type = new IntType();
                break;
            case "null":
                node.type = new NullType();
                break;
            case "bool":
                node.type = new BoolType();
                break;
            case "string":
                node.type = new StringType();
            default:
                assert false;
        }
        node.isLeftValue = false;
    }

    @Override
    public void visit(ArrayExprNode node) {
        node.address.accept(this);
        if (!(node.address.type instanceof ArrayType)) {
            throw new SemanticError(node.address.location, String.format("Array expression baseType %s not array type", node.address.type.toString()));
        }
        node.index.accept(this);
        if (!(node.index.type instanceof IntType)) {
            throw new SemanticError(node.index.location, "array expression index not int type");
        }
        node.isLeftValue = true;
        if (((ArrayType) node.address.type).dimension > 1) {
            node.type = new ArrayType(((ArrayType) node.address.type).baseType, ((ArrayType) node.address.type).dimension - 1);
        } else {
            node.type = ((ArrayType) node.address.type).baseType;
        }
    }

    @Override
    public void visit(MemberExprNode node) {
        node.obj.accept(this);
        String objname;
        if (node.obj.type instanceof ClassType) {
            objname = ((ClassType) node.obj.type).name;
        } else if (node.obj.type instanceof ArrayType) {
            objname = "@arr";
        } else if (node.obj.type instanceof StringType) {
            objname = "@str";
        } else {
            throw new SemanticError(node.obj.location, "Object is not a class while trying to access methods");
        }
        ClassEntity ent = globalScope.classMap.get(objname);
        node.varfuncEntity = ent.classScope.recurGetKey(node.field_method);
        node.type = node.varfuncEntity.type;
        if (node.varfuncEntity instanceof VarEntity) {
            node.isLeftValue = true;
        } else if (node.varfuncEntity instanceof FuncEntity) {
            currFuncEntity = (FuncEntity) node.varfuncEntity;
            node.isLeftValue = false;
        }
    }

    @Override
    public void visit(FuncCallExprNode node) {
        node.funcExpr.accept(this);
        if (!(node.funcExpr.type instanceof FunctionType)) {
            throw new SemanticError(node.location, "Called a non function");
        }
        node.funcEntity = currFuncEntity;
        if (node.arguments.size() != node.funcEntity.parameters.size()) {
            throw new SemanticError(node.location, "function arguments wrong number");
        }
        for (int i = 0; i < node.arguments.size(); ++i) {
            node.arguments.get(i).accept(this);
            if (!typeTool.checkArguments(node.arguments.get(i).type, node.funcEntity.parameters.get(i).type)) {
                throw new SemanticError(node.arguments.get(i).location, "arguments type mismatch");
            }
        }
        node.type = node.funcEntity.retType;
        node.isLeftValue = false;
    }

    @Override
    public void visit(NewExprNode node) {
        if (node.baseType.type instanceof VoidType) {
            throw new SemanticError(node.location, "illegal new void type");
        }
        if (node.numDimension == 0) {
            node.isLeftValue = false;
            node.type = node.baseType.type;
        } else {
            for (ExprNode exprNode : node.exprDimensions) {
                exprNode.accept(this);
                if (!(exprNode.type instanceof IntType)) {
                    throw new SemanticError(exprNode.location, "new Expression dimension not int type");
                }
            }
            node.isLeftValue = false;
            node.type = new ArrayType(node.baseType.type, node.numDimension);
        }
    }

    @Override
    public void visit(UnaryExprNode node) {
        node.iExpr.accept(this);
        switch (node.op) {
            case "+":
            case "-":
            case "~":
                if (!(node.iExpr.type instanceof IntType)) {
                    throw new SemanticError(node.location, "Unary Expr +-~ no int type");
                }
                node.type = new IntType();
                node.isLeftValue = false;
                break;
            case "!":
                if (!(node.iExpr.type instanceof BoolType)) {
                    throw new SemanticError(node.location, "Unary Expr ! no bool type");
                }
                node.type = new BoolType();
                node.isLeftValue = false;
                break;
            default:
                assert false;
        }
    }

    @Override
    public void visit(BinaryExprNode node) {
        node.lhs.accept(this);
        node.rhs.accept(this);
        switch (node.op) {
            case "+":
                if (node.lhs.type instanceof StringType && node.rhs.type instanceof StringType) {
                    node.type = new StringType();
                    node.isLeftValue = false;
                    break;
                }
            case "*":
            case "/":
            case "%":
            case "-":
            case "<<":
            case ">>":
            case "|":
            case "&":
            case "^":
                if (!(node.lhs.type instanceof IntType))
                    throw new SemanticError(node.location, "lhs node int type");
                if (!(node.rhs.type instanceof IntType))
                    throw new SemanticError(node.location, "rhs node int type");
                node.type = new IntType();
                node.isLeftValue = false;
                break;
            case ">":
            case "<":
            case ">=":
            case "<=":
                if (!(node.lhs.type instanceof IntType || node.lhs.type instanceof StringType))
                    throw new SemanticError(node.location, "lhs not int or string type");
                if (!(node.rhs.type instanceof IntType || node.rhs.type instanceof StringType))
                    throw new SemanticError(node.location, "rhs not int or string type");
                if (!(node.lhs.type.equals(node.rhs.type)))
                    throw new SemanticError(node.location, "lhs and rhs not match");
                node.type = new BoolType();
                node.isLeftValue = false;
                break;
            case "==":
            case "!=":
                if (!typeTool.checkEQEQ(node.lhs.type, node.rhs.type)) {
                    throw new SemanticError(node.location, "lhs and rhs not match");
                }
                node.type = new BoolType();
                node.isLeftValue = false;
                break;
            case "||":
            case "&&":
                if (!(node.lhs.type instanceof BoolType))
                    throw new SemanticError(node.location, "lhs not bool type");
                if (!(node.rhs.type instanceof BoolType))
                    throw new SemanticError(node.location, "rhs not bool type");
                node.type = new BoolType();
                node.isLeftValue = false;
                break;
        }
    }

    @Override
    public void visit(AssignExprNode node) {
        node.lhs.accept(this);
        node.rhs.accept(this);
        if (!node.lhs.isLeftValue) {
            throw new SemanticError(node.location, "cannot assign to a non leftvalue");
        }
        if (!(typeTool.checkEQ(node.lhs.type, node.rhs.type))) {
            throw new SemanticError(node.location, "assign operator cannot be applied to different type");
        }
        node.type = node.lhs.type;
        node.isLeftValue = false;
    }

    @Override
    public void visit(PostfixExprNode node) {
        node.iExpr.accept(this);
        if (!(node.iExpr.type instanceof IntType)) {
            throw new SemanticError(node.location, "not int type");
        }
        if (!node.iExpr.isLeftValue) {
            throw new SemanticError(node.location, "not left value");
        }
        node.type = new IntType();
        node.isLeftValue = false;
    }

    @Override
    public void visit(PrefixExprNode node) {
        node.iExpr.accept(this);
        if (!(node.iExpr.type instanceof IntType)) {
            throw new SemanticError(node.location, "not int type");
        }
        if (!node.iExpr.isLeftValue) {
            throw new SemanticError(node.location, "not left value");
        }
        node.type = new IntType();
        node.isLeftValue = false;
    }
}