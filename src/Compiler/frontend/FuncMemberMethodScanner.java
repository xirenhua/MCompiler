package Compiler.frontend;

import Compiler.AST.Decl.ClassDeclNode;
import Compiler.AST.Decl.FuncDeclNode;
import Compiler.AST.Decl.VarDeclNode;
import Compiler.AST.ProgramNode;
import Compiler.AST.TokenLocation;
import Compiler.entities.*;
import Compiler.type.*;
import Compiler.utils.SemanticError;

import java.util.ArrayList;
import java.util.List;

public class FuncMemberMethodScanner extends BaseScanner {
    public GlobalScope globalScope;
    public Scope currentScope;
    public String currClassName;
    public TypeTool typeTool;
    public int currOffset;

    public FuncMemberMethodScanner(GlobalScope scope) {
        globalScope = scope;
        currentScope = scope;
        currClassName = null;
        typeTool = new TypeTool(globalScope.classMap);
        currOffset = 0;
    }

    public void checkMain() {
        if (!(globalScope.funcMap.containsKey("main"))) {
            throw new SemanticError(new TokenLocation(0, 0), "no main function");
        }
        FuncEntity mainfuncEntity = globalScope.funcMap.get("main");
        if (!(mainfuncEntity.retType instanceof IntType)) {
            throw new SemanticError(new TokenLocation(0, 0), "return type of main function not int type");
        }
        if (!(mainfuncEntity.parameters.isEmpty())) {
            throw new SemanticError(new TokenLocation(0, 0), "parameter of main should be empty");
        }
    }

    @Override
    public void visit(ProgramNode node) {
        for (FuncDeclNode funcDecl : node.functions) {
            funcDecl.accept(this);
        }
        for (ClassDeclNode classDecl : node.classes) {
            classDecl.accept(this);
        }
    }

    // for Global function and class methods;
    // check return type in symbolTable builder
    @Override
    public void visit(FuncDeclNode node) {
        if (currentScope.checkKey(node.name)) {
            throw new SemanticError(node.location, "name already defined current scope");
        }
        FuncEntity funcEntity = new FuncEntity(node.name, new FunctionType(node.name), currentScope);
        // return type
        if (node.retType == null) {
            // special case: constructor
            if (!(node.name.equals(currClassName))) {
                throw new SemanticError(node.location, "constructor name different from class");
            }
            funcEntity.retType = null;
        } else {
            if (!(typeTool.checkReturnType(node.retType.type))) {
                throw new SemanticError(node.retType.location, "illegal return type");
            }
            funcEntity.retType = node.retType.type;
        }
        // param list
        currentScope = funcEntity.funcScope;
        List<VarEntity> paramList = new ArrayList<>();
        for (VarDeclNode varDecl : node.params) {
            varDecl.accept(this);
            paramList.add(varDecl.varEntity);
        }
        if (currClassName != null) {
            currentScope.putVar("this", new VarEntity("this", new ClassType(currClassName)));
        }
        currentScope = currentScope.parent;
        funcEntity.parameters = paramList;
        if (currClassName != null) {
            funcEntity.isMember = true;
        } else {
            funcEntity.isMember = false;
        }
        funcEntity.isBuiltin = false;
        node.funcEntity = funcEntity;
        currentScope.putFunc(node.name, funcEntity);
    }


    // just for fields of classes, not for normal variable declarations
    @Override
    public void visit(VarDeclNode node) {
        if (currentScope.checkKey(node.name)) {
            throw new SemanticError(node.location, "name already defined current class scope");
        }
        if (!(typeTool.checkDeclType(node.retType.type))) {
            throw new SemanticError(node.location, "illegal decl type");
        }
        VarEntity varEntity = new VarEntity(node.name, node.retType.type);
        varEntity.addrOffset = currOffset;
        currOffset += node.retType.type.size;
        node.varEntity = varEntity;
        currentScope.putVar(node.name, varEntity);
    }


    @Override
    public void visit(ClassDeclNode node) {
        // constructor more than one considered syntax error
        // and dealt with in astBuilder
        currClassName = node.name;
        currentScope = node.classEntity.classScope;
        currOffset = 0;
        for (VarDeclNode varDecl : node.fields) {
            visit(varDecl);
        }
        for (FuncDeclNode funcDecl : node.methods) {
            visit(funcDecl);
        }
        if (node.constructor != null) {
            visit(node.constructor);
        }
        node.classEntity.memorySize = currOffset;
        currClassName = null;
        currentScope = globalScope;
    }
}