package Compiler.frontend;

import Compiler.AST.Decl.ClassDeclNode;
import Compiler.AST.Decl.FuncDeclNode;
import Compiler.AST.Decl.VarDeclNode;
import Compiler.AST.Expr.AssignExprNode;
import Compiler.AST.Expr.ExprNode;
import Compiler.AST.Expr.IdentifierExprNode;
import Compiler.AST.ProgramNode;
import Compiler.AST.Stat.ExprStatNode;
import Compiler.AST.Stat.StatNode;
import Compiler.AST.TokenLocation;
import Compiler.AST.TypeNode;
import Compiler.entities.*;
import Compiler.type.*;
import Compiler.utils.SemanticError;

import java.util.*;

public class GlobalClassScanner extends BaseScanner {
    public GlobalScope globalScope;
    public List<String> globalVarName;
    public List<ExprNode> globalVarInit;

    public GlobalClassScanner() {
        globalScope = new GlobalScope();
        globalVarName = new ArrayList<>();
        globalVarInit = new ArrayList<>();
    }

    public void putGlobalBIFuncs() {
        putGlobalBIFunc(globalScope, "print", Collections.singletonList(new VarEntity("str", new StringType())), new VoidType());
        putGlobalBIFunc(globalScope, "println", Collections.singletonList(new VarEntity("str", new StringType())), new VoidType());
        putGlobalBIFunc(globalScope, "getString", new ArrayList<>(), new StringType());
        putGlobalBIFunc(globalScope, "getInt", new ArrayList<>(), new IntType());
        putGlobalBIFunc(globalScope, "toString", Collections.singletonList(new VarEntity("i", new IntType())), new StringType());
    }

    public void putBIFunc(Scope ascope, String aname, List<VarEntity> aparameters, Type aretType) {
        FuncEntity funcEntity = new FuncEntity(aname, new FunctionType(aname), ascope);
        funcEntity.isBuiltin = true;
        funcEntity.isMember = true;
        funcEntity.parameters = aparameters;
        funcEntity.retType = aretType;
        ascope.putFunc(aname, funcEntity);
    }

    public void putGlobalBIFunc(GlobalScope ascope, String aname, List<VarEntity> aparameters, Type aretType) {
        FuncEntity funcEntity = new FuncEntity(aname, new FunctionType(aname), ascope);
        funcEntity.isBuiltin = true;
        funcEntity.isMember = false;
        funcEntity.parameters = aparameters;
        funcEntity.retType = aretType;
        ascope.putFunc(aname, funcEntity);
    }

    public void putStringClass() {
        ClassEntity stringClass = new ClassEntity("@str", new ClassType("@str"), globalScope);
        putBIFunc(stringClass.classScope, "length", new ArrayList<>(), new IntType());
        putBIFunc(stringClass.classScope, "substring", Arrays.asList(new VarEntity("left", new IntType()), new VarEntity("right", new IntType())), new StringType());
        putBIFunc(stringClass.classScope, "parseInt", new ArrayList<>(), new IntType());
        putBIFunc(stringClass.classScope, "ord", Collections.singletonList(new VarEntity("pos", new IntType())), new IntType());
        globalScope.putClass("@str", stringClass);
    }

    public void putArrayClass() {
        ClassEntity arrayClass = new ClassEntity("@arr", new ClassType("@arr"), globalScope);
        putBIFunc(arrayClass.classScope, "size", new ArrayList<>(), new IntType());
        globalScope.putClass("@arr", arrayClass);
    }

    @Override
    public void visit(ProgramNode node) {
        putGlobalBIFuncs();
        putStringClass();
        putArrayClass();
        for (ClassDeclNode decl : node.classes) {
            decl.accept(this);
        }
        for (VarDeclNode decl : node.variables) {
            globalVarName.add(decl.name);
            globalVarInit.add(decl.initExpr);
            decl.initExpr = null;
        }
        FuncDeclNode initFunc = makeInitFunc();
        if (initFunc != null)
            node.add(initFunc);
    }

    public FuncDeclNode makeInitFunc() {
        TypeNode retType = new TypeNode(new TokenLocation(-1, -1), new VoidType());
        List<VarDeclNode> params = new ArrayList<>();
        List<StatNode> stats = new ArrayList<>();
        for (int i = 0; i < globalVarName.size(); ++i) {
            if (globalVarInit.get(i) == null) continue;
            IdentifierExprNode lhs = new IdentifierExprNode(new TokenLocation(-1, -1), globalVarName.get(i));
            AssignExprNode assignExpr = new AssignExprNode(new TokenLocation(-1, -1), lhs, globalVarInit.get(i));
            stats.add(new ExprStatNode(new TokenLocation(-1, -1), assignExpr));
        }
        if (stats.isEmpty())
            return null;
        FuncDeclNode func = new FuncDeclNode(new TokenLocation(-1, -1), "@init", retType);
        func.stats = stats;
        func.params = params;
        return func;
    }

    @Override
    public void visit(ClassDeclNode node) {
        if (globalScope.checkKey(node.name)) {
            throw new SemanticError(node.location, "class name already defined in global scope");
        }
        ClassEntity classEntity = new ClassEntity(node.name, new ClassType(node.name), globalScope);
        globalScope.putClass(node.name, classEntity);
        node.classEntity = classEntity;
    }
}