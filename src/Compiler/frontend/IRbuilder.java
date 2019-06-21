package Compiler.frontend;

import Compiler.AST.ProgramNode;
import Compiler.AST.Decl.*;
import Compiler.AST.Expr.*;
import Compiler.AST.Stat.*;

import Compiler.AST.TokenLocation;
import Compiler.IR.IRBasicBlock;
import Compiler.IR.IRFunction;
import Compiler.IR.IRRoot;
import Compiler.IR.Instruction.*;
import Compiler.IR.Value.*;

import Compiler.type.*;
import Compiler.entities.*;
import Compiler.utils.CompileError;
import Compiler.utils.Configuration;

import java.util.ArrayList;
import java.util.List;

import static Compiler.IR.Instruction.CompIR.IRCmpOp.*;

public class IRbuilder extends BaseScanner {

    public IRRoot ir;
    public GlobalScope globalScope;
    public IRBasicBlock currentBB;
    public IRBasicBlock currentLoopBeforeBB, currentLoopAfterBB;
    public IRFunction currentFunc;
    public String currentClassName;
    public Scope currentScope;
    public boolean isFuncArgDecl, wantAddr;

    public IRbuilder(GlobalScope iglobalScope) {
        ir = new IRRoot();
        globalScope = iglobalScope;
        currentBB = null;
        currentFunc = null;
        currentLoopBeforeBB = currentLoopAfterBB = null;
        currentClassName = null;
        currentScope = iglobalScope;
        wantAddr = isFuncArgDecl = false;
    }

    public boolean checkIdentifierThisMemberAccess(IdentifierExprNode node) {
        if (currentClassName != null) {
            VarEntity varEntity = (VarEntity) currentScope.recurGetKey(node.name);
            return (varEntity.reg == null);
        } else {
            return false;
        }
    }

    public boolean isMemoryAccess(ExprNode node) {
        return (node instanceof ArrayExprNode) || (node instanceof MemberExprNode) || (node instanceof IdentifierExprNode && checkIdentifierThisMemberAccess((IdentifierExprNode) node));
    }

    public boolean isLogicalExpression(ExprNode node) {
        return ((node.type instanceof BoolType) && !(node instanceof LiteralExprNode));
    }

    private void processAssign(Reg dest, int addrOffset, ExprNode rhs, boolean needMemOp) {
        if (rhs.trueBB != null) {
            IRBasicBlock mergeBB = new IRBasicBlock(currentFunc, "assignMerge");
            if (needMemOp) {
                rhs.trueBB.appendInst(new StoreIR(rhs.trueBB, new Imm(1), Configuration.REG_SIZE, dest, addrOffset));
                rhs.falseBB.appendInst(new StoreIR(rhs.falseBB, new Imm(0), Configuration.REG_SIZE, dest, addrOffset));
            } else {
                rhs.trueBB.appendInst(new MoveIR(rhs.trueBB, dest, new Imm(1)));
                rhs.falseBB.appendInst(new MoveIR(rhs.falseBB, dest, new Imm(0)));
            }
            rhs.trueBB.setTransfer(new JumpIR(rhs.trueBB, mergeBB));
            rhs.falseBB.setTransfer(new JumpIR(rhs.falseBB, mergeBB));
            currentBB = mergeBB;
        } else {
            if (needMemOp) {
                currentBB.appendInst(
                        new StoreIR(currentBB, rhs.reg, Configuration.REG_SIZE, dest, addrOffset));
            } else {
                currentBB.appendInst(new MoveIR(currentBB, dest, rhs.reg));
            }
        }
    }

    @Override
    public void visit(ProgramNode node) {
        currentScope = globalScope;
        for (DeclNode decl : node.declarations) {
            if (decl instanceof FuncDeclNode) {
                FuncEntity funcEntity = ((FuncDeclNode) decl).funcEntity;
                IRFunction newIRFunc = new IRFunction(funcEntity.name, funcEntity);
                ir.addFunc(newIRFunc);
            } else if (decl instanceof ClassDeclNode) {
                currentClassName = decl.name;
                currentScope = ((ClassDeclNode) decl).classEntity.classScope;
                for (FuncDeclNode method : ((ClassDeclNode) decl).methods) {
                    IRFunction newIRFunc = new IRFunction("@" + currentClassName + "@" + method.name, method.funcEntity);
                    ir.addFunc(newIRFunc);
                }
                FuncDeclNode constructor = ((ClassDeclNode) decl).constructor;
                if (constructor != null) {
                    IRFunction newIRFunc = new IRFunction("@" + currentClassName + "@" + constructor.name, constructor.funcEntity);
                    ir.addFunc(newIRFunc);
                }
                currentScope = currentScope.parent;
                currentClassName = null;
            }
        }

        for (DeclNode decl : node.declarations) {
            if (decl instanceof VarDeclNode) {
                decl.accept(this);
            } else if (decl instanceof ClassDeclNode) {
                decl.accept(this);
            } else if (decl instanceof FuncDeclNode) {
                decl.accept(this);
            } else {
                throw new CompileError("invalid declType when visit programNode");
            }
        }
        for (IRFunction func : ir.funcs.values()) {
            func.calcReversePostOrder();
            func.calcReversePreOrder();
            func.calcCalleeSet();
        }
        ir.updateRecursiveCalleeSet();
    }

    @Override
    public void visit(FuncDeclNode node) {
        String funcName = node.name;
        if (currentClassName != null) {
            funcName = "@" + currentClassName + "@" + funcName;
        }

        currentFunc = ir.getFunc(funcName);
        currentBB = currentFunc.createStartBB();
        currentScope = node.funcEntity.funcScope;


        if (currentClassName != null) {
            VarEntity entity = (VarEntity) currentScope.recurGetKey("this");
            VirtualReg vreg = new VirtualReg("thisParam");
            entity.reg = vreg;
            currentFunc.addArg(vreg);
        }
        isFuncArgDecl = true;
        for (VarDeclNode argDecl : node.params) {
            argDecl.accept(this);
        }
        isFuncArgDecl = false;

        if (funcName.equals("main")) {
            IRFunction init = ir.getFunc("@init");
            if (init != null)
                currentBB.appendInst(new FuncCallIR(currentBB, init, new ArrayList<>(), null));
        }

        for (StatNode stat : node.stats) {
            stat.accept(this);
            if (currentBB.hasTransfer)
                break;
        }
        currentScope = currentScope.parent;

        if (!currentBB.hasTransfer) {
            if (node.retType == null || node.retType.type instanceof VoidType) {
                currentBB.setTransfer(new ReturnIR(currentBB, null));
            } else {
                // specially for those who can omit return type
                currentBB.setTransfer(new ReturnIR(currentBB, new Imm(0)));
            }
        }

        if (currentFunc.retInstList.size() > 1) {
            IRBasicBlock mergeEndBB = new IRBasicBlock(currentFunc, currentFunc.name + "_end");
            VirtualReg retReg;
            if (node.retType == null || node.retType.type instanceof VoidType) {
                retReg = null;
            } else {
                retReg = new VirtualReg("return_value");
            }

            for (ReturnIR retInst : currentFunc.retInstList) {
                IRBasicBlock bb = retInst.parentBB;
                if (retInst.retValue != null) {
                    retInst.addBefore(new MoveIR(bb, retReg, retInst.retValue));
                }
                retInst.remove();
                bb.setTransfer(new JumpIR(bb, mergeEndBB));
            }
            currentFunc.retInstList.clear();
            mergeEndBB.setTransfer(new ReturnIR(mergeEndBB, retReg));
            currentFunc.endBB = mergeEndBB;
        } else {
            currentFunc.endBB = currentFunc.retInstList.get(0).parentBB;
        }
        currentFunc = null;
    }

    @Override
    public void visit(ClassDeclNode node) {
        currentClassName = node.name;
        currentScope = node.classEntity.classScope;
        for (FuncDeclNode decl : node.methods) {
            decl.accept(this);
        }
        FuncDeclNode constructor = node.constructor;
        if (constructor != null) {
            constructor.accept(this);
        }
        currentScope = globalScope;
        currentClassName = null;
    }

    @Override
    public void visit(VarDeclNode node) {
        VarEntity entity = node.varEntity;
        if (currentScope instanceof GlobalScope) {
            StaticVar data = new StaticVar(node.name, Configuration.REG_SIZE);
            ir.addStaticVar(data);
            entity.reg = data;
        } else {
            VirtualReg vreg = new VirtualReg(node.name);
            entity.reg = vreg;
            if (isFuncArgDecl) {
                currentFunc.addArg(vreg);
            }
            if (node.initExpr != null) {
                if (isLogicalExpression(node.initExpr)) {
                    node.initExpr.trueBB = new IRBasicBlock(currentFunc, "varDeclTrue");
                    node.initExpr.falseBB = new IRBasicBlock(currentFunc, "varDeclFalse");
                }
                node.initExpr.accept(this);
                processAssign(vreg, 0, node.initExpr, false);
            }
        }
    }

    @Override
    public void visit(BreakStatNode node) {
        currentBB.setTransfer(new JumpIR(currentBB, currentLoopAfterBB));
    }

    @Override
    public void visit(ContinueStatNode node) {
        currentBB.setTransfer(new JumpIR(currentBB, currentLoopBeforeBB));
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
        currentScope = node.blockScope;
        for (StatNode stat : node.iStats) {
            stat.accept(this);
            if (currentBB.hasTransfer)
                break;
        }
        currentScope = currentScope.parent;
    }

    @Override
    public void visit(ReturnStatNode node) {
        if (node.retExpr == null) {
            currentBB.setTransfer(new ReturnIR(currentBB, null));
        } else {
            if (isLogicalExpression(node.retExpr)) {
                node.retExpr.trueBB = new IRBasicBlock(currentFunc, "returnTrue");
                node.retExpr.falseBB = new IRBasicBlock(currentFunc, "returnFalse");
                node.retExpr.accept(this);
                VirtualReg vreg = new VirtualReg("returnBool");
                processAssign(vreg, 0, node.retExpr, false);
                currentBB.setTransfer(new ReturnIR(currentBB, vreg));
            } else {
                node.retExpr.accept(this);
                currentBB.setTransfer(new ReturnIR(currentBB, node.retExpr.reg));
            }
        }
    }

    @Override
    public void visit(IfStatNode node) {
        IRBasicBlock thenBB = new IRBasicBlock(currentFunc, "if_then");
        IRBasicBlock mergeBB = new IRBasicBlock(currentFunc, "if_merge");
        IRBasicBlock elseBB = new IRBasicBlock(currentFunc, "if_else");

        if (node.condExpr instanceof LiteralExprNode) {
            boolean wtf = Boolean.parseBoolean(((LiteralExprNode) (node.condExpr)).value);
            currentBB.setTransfer(new JumpIR(currentBB, (wtf) ? thenBB : ((node.elseStat != null) ? elseBB : mergeBB)));
        } else {
            node.condExpr.trueBB = thenBB;
            node.condExpr.falseBB = (node.elseStat != null) ? elseBB : mergeBB;
            node.condExpr.accept(this);
        }

        currentBB = thenBB;
        currentScope = node.thenScope;
        node.thenStat.accept(this);
        currentScope = currentScope.parent;
        if (!currentBB.hasTransfer)
            currentBB.setTransfer(new JumpIR(currentBB, mergeBB));

        if (node.elseStat != null) {
            currentBB = elseBB;
            currentScope = node.elseScope;
            node.elseStat.accept(this);
            currentScope = currentScope.parent;
            if (!currentBB.hasTransfer)
                currentBB.setTransfer(new JumpIR(currentBB, mergeBB));
        }
        currentBB = mergeBB;
    }

    @Override
    public void visit(WhileStatNode node) {
        IRBasicBlock condBB = new IRBasicBlock(currentFunc, "while_cond");
        IRBasicBlock thenBB = new IRBasicBlock(currentFunc, "while_then");
        IRBasicBlock mergeBB = new IRBasicBlock(currentFunc, "while_merge");

        IRBasicBlock bkLoopBeforeBB = currentLoopBeforeBB;
        IRBasicBlock bkLoopAfterBB = currentLoopAfterBB;
        currentLoopBeforeBB = condBB;
        currentLoopAfterBB = mergeBB;

        currentBB.setTransfer(new JumpIR(currentBB, condBB));

        currentBB = condBB;
        if (node.condExpr instanceof LiteralExprNode) {
            boolean wtf = Boolean.parseBoolean(((LiteralExprNode) (node.condExpr)).value);
            currentBB.setTransfer(new JumpIR(currentBB, (wtf) ? thenBB : mergeBB));
        } else {
            node.condExpr.trueBB = thenBB;
            node.condExpr.falseBB = mergeBB;
            node.condExpr.accept(this);
        }

        currentBB = thenBB;
        currentScope = node.whileScope;
        node.thenStat.accept(this);
        currentScope = currentScope.parent;
        if (!currentBB.hasTransfer)
            currentBB.setTransfer(new JumpIR(currentBB, condBB));

        currentLoopBeforeBB = bkLoopBeforeBB;
        currentLoopAfterBB = bkLoopAfterBB;
        currentBB = mergeBB;
    }

    @Override
    public void visit(ForStatNode node) {
        IRBasicBlock condBB = new IRBasicBlock(currentFunc, "for_cond");
        IRBasicBlock thenBB = new IRBasicBlock(currentFunc, "for_then");
        IRBasicBlock updateBB = new IRBasicBlock(currentFunc, "for_update");
        IRBasicBlock mergeBB = new IRBasicBlock(currentFunc, "for_merge");

        if (node.condExpr == null)
            condBB = thenBB;
        if (node.updateExpr == null)
            updateBB = condBB;

        ir.forRecMap.put(node, new IRRoot.ForRecord(condBB, updateBB, thenBB, mergeBB));

        IRBasicBlock bkLoopBeforeBB = currentLoopBeforeBB;
        IRBasicBlock bkLoopAfterBB = currentLoopAfterBB;
        currentLoopBeforeBB = updateBB;
        currentLoopAfterBB = mergeBB;

        if (node.initExpr != null) {
            node.initExpr.accept(this);
        }
        currentBB.setTransfer(new JumpIR(currentBB, condBB));

        if (node.condExpr != null) {
            currentBB = condBB;
            if (node.condExpr instanceof LiteralExprNode) {
                boolean wtf = Boolean.parseBoolean(((LiteralExprNode) (node.condExpr)).value);
                currentBB.setTransfer(new JumpIR(currentBB, (wtf) ? thenBB : mergeBB));
            } else {
                node.condExpr.trueBB = thenBB;
                node.condExpr.falseBB = mergeBB;
                node.condExpr.accept(this);
            }
        }

        currentBB = thenBB;
        currentScope = node.forScope;
        if (node.thenStat != null)
            node.thenStat.accept(this);
        currentScope = currentScope.parent;
        if (!currentBB.hasTransfer)
            currentBB.setTransfer(new JumpIR(currentBB, updateBB));

        if (node.updateExpr != null) {
            currentBB = updateBB;
            node.updateExpr.accept(this);
            if (!currentBB.hasTransfer)
                currentBB.setTransfer(new JumpIR(currentBB, condBB));
        }

        currentLoopBeforeBB = bkLoopBeforeBB;
        currentLoopAfterBB = bkLoopAfterBB;
        currentBB = mergeBB;
    }


    @Override
    public void visit(IdentifierExprNode node) {
        VarEntity varEntity = (VarEntity) node.varfuncEntity;
        if (node.name.equals("this")) {
            varEntity = (VarEntity) currentScope.recurGetKey("this");
        }
        if (varEntity.reg == null) {
            IdentifierExprNode thisExpr = new IdentifierExprNode(new TokenLocation(-1, -1), "this");
            thisExpr.type = new ClassType(currentClassName);
            MemberExprNode memberExprNode = new MemberExprNode(new TokenLocation(-1, -1), thisExpr, node.name);
            memberExprNode.accept(this);
            if (wantAddr) {
                node.addr = memberExprNode.addr;
                node.addrOffset = memberExprNode.addrOffset;
            } else {
                node.reg = memberExprNode.reg;
                if (node.trueBB != null) {
                    currentBB.setTransfer(new BranchIR(currentBB, node.reg, node.trueBB, node.falseBB));
                }
            }
        } else {
            node.reg = varEntity.reg;
            if (node.trueBB != null) {
                currentBB.setTransfer(new BranchIR(currentBB, node.reg, node.trueBB, node.falseBB));
            }
        }
    }

    @Override
    public void visit(LiteralExprNode node) {
        switch (node.typename) {
            case "int":
                node.reg = new Imm(Integer.parseInt(node.value));
                break;
            case "null":
                node.reg = new Imm(0);
                break;
            case "bool":
                node.reg = new Imm(Boolean.parseBoolean(node.value) ? 1 : 0);
                break;
            case "string":
                StaticString statStr = ir.getStaticStr(node.value);
                if (statStr == null) {
                    statStr = new StaticString(node.value);
                    ir.addStaticStr(statStr);
                }
                node.reg = statStr;
                break;
            default:
                assert false;
        }
    }

    @Override
    public void visit(ArrayExprNode node) {
        boolean wantAddrBak = wantAddr;
        wantAddr = false;
        node.address.accept(this);
        node.index.accept(this);
        wantAddr = wantAddrBak;

        VirtualReg vreg = new VirtualReg("arrayExpr");
        Imm elementSize = new Imm(node.type.size);
        currentBB.appendInst(new BinaryIR(currentBB, vreg, BinaryIR.IRBinaryOp.MUL,
                node.index.reg, elementSize));
        currentBB.appendInst(new BinaryIR(currentBB, vreg, BinaryIR.IRBinaryOp.ADD,
                node.address.reg, vreg));

        if (wantAddr) {
            node.addr = vreg;
            node.addrOffset = Configuration.REG_SIZE;
        } else {
            currentBB.appendInst(
                    new LoadIR(currentBB, vreg, node.type.size, vreg, Configuration.REG_SIZE));
            node.reg = vreg;
            if (node.trueBB != null) {
                currentBB.setTransfer(new BranchIR(currentBB, node.reg, node.trueBB, node.falseBB));
            }
        }
    }

    @Override
    public void visit(MemberExprNode node) {
        // we don't access method here
        boolean wantAddrBak = wantAddr;
        wantAddr = false;
        node.obj.accept(this);
        wantAddr = wantAddrBak;

        Reg classAddr = (Reg) node.obj.reg;
        String className = ((ClassType) node.obj.type).name;
        ClassEntity classEntity = globalScope.classMap.get(className);
        VarEntity varEntity = (VarEntity) classEntity.classScope.recurGetKey(node.field_method);

        if (wantAddr) {
            node.addr = classAddr;
            node.addrOffset = varEntity.addrOffset;
        } else {
            VirtualReg vreg = new VirtualReg("membExpr");
            node.reg = vreg;
            currentBB.appendInst(new LoadIR(currentBB, vreg, varEntity.type.size, classAddr,
                    varEntity.addrOffset));
            if (node.trueBB != null) {
                currentBB.setTransfer(new BranchIR(currentBB, node.reg, node.trueBB, node.falseBB));
            }
        }
    }

    @Override
    public void visit(FuncCallExprNode node) {
        FuncEntity funcEntity = node.funcEntity;
        String funcName = funcEntity.name;
        List<IRValue> args = new ArrayList<>();
        if (funcEntity.isMember) {
            if (node.funcExpr instanceof MemberExprNode) {
                ExprNode objExpr = ((MemberExprNode) node.funcExpr).obj;
                objExpr.accept(this);
                String className = "";
                if (objExpr.type instanceof ClassType) {
                    className = ((ClassType) (objExpr.type)).name;
                } else if (objExpr.type instanceof ArrayType) {
                    className = "@arr";
                } else if (objExpr.type instanceof StringType) {
                    className = "@str";
                } else {
                    throw new CompileError("invalid obj type in funcCallExpr");
                }
                funcName = "@" + className + "@" + funcName;
                args.add(objExpr.reg);
            } else {
                // funcCall inside method(builtIn is impossible)
                funcName = "@" + currentClassName + "@" + funcName;
                args.add(((VarEntity) (currentScope.recurGetKey("this"))).reg);
            }
        }
        if (funcEntity.isBuiltin) {
            processBuiltInFuncCall(node, args, funcName);
            return;
        }
        //  ?? what if bool type
        for (ExprNode arg : node.arguments) {
            arg.accept(this);
            args.add(arg.reg);
        }
        IRFunction irFunction = ir.getFunc(funcName);
        VirtualReg vreg = new VirtualReg("funcCall");
        currentBB.appendInst(new FuncCallIR(currentBB, irFunction, args, vreg));
        node.reg = vreg;
        if (node.trueBB != null) {
            currentBB.setTransfer(new BranchIR(currentBB, vreg, node.trueBB, node.falseBB));
        }
    }

    public void processPrintFuncCall(ExprNode arg, String funcName) {
        if (arg instanceof BinaryExprNode) {
            // print(A + B); -> print(A); print(B);
            // println(A + B); -> print(A); println(B);
            processPrintFuncCall(((BinaryExprNode) arg).lhs, "print");
            processPrintFuncCall(((BinaryExprNode) arg).rhs, funcName);
            return;
        }

        IRFunction calleeFunc;
        List<IRValue> vArgs = new ArrayList<>();
        if (arg instanceof FuncCallExprNode && ((FuncCallExprNode) arg).funcEntity.name.equals("toString")) {
            // print(toString(n)); -> printInt(n);
            ExprNode intExpr = ((FuncCallExprNode) arg).arguments.get(0);
            intExpr.accept(this);
            calleeFunc = ir.getBiFunc(funcName + "Int");
            vArgs.add(intExpr.reg);
        } else {
            arg.accept(this);
            calleeFunc = ir.getBiFunc(funcName);
            vArgs.add(arg.reg);
        }
        currentBB.appendInst(new FuncCallIR(currentBB, calleeFunc, vArgs, null));
    }

    public void processBuiltInFuncCall(FuncCallExprNode node, List<IRValue> vArgs, String funcName) {
        ExprNode arg0, arg1;
        VirtualReg vreg;
        IRFunction callee;
        switch (funcName) {
            case IRRoot.BUILTIN_PRINT:
            case IRRoot.BUILTIN_PRINTLN:
                processPrintFuncCall(node.arguments.get(0), funcName);
                break;
            case IRRoot.BUILTIN_GET_STRING:
                vreg = new VirtualReg("getString");
                callee = ir.getBiFunc(IRRoot.BUILTIN_GET_STRING);
                currentBB.appendInst(new FuncCallIR(currentBB, callee, vArgs, vreg));
                node.reg = vreg;
                break;
            case IRRoot.BUILTIN_GET_INT:
                vreg = new VirtualReg("getInt");
                callee = ir.getBiFunc(IRRoot.BUILTIN_GET_INT);
                currentBB.appendInst(new FuncCallIR(currentBB, callee, vArgs, vreg));
                node.reg = vreg;
                break;
            case IRRoot.BUILTIN_TO_STRING:
                arg0 = node.arguments.get(0);
                arg0.accept(this);
                vArgs.add(arg0.reg);
                vreg = new VirtualReg("toString");
                callee = ir.getBiFunc(IRRoot.BUILTIN_TO_STRING);
                currentBB.appendInst(new FuncCallIR(currentBB, callee, vArgs, vreg));
                node.reg = vreg;
                break;
            case IRRoot.BUILTIN_STRING_SUBSTRING:
                arg0 = node.arguments.get(0);
                arg0.accept(this);
                arg1 = node.arguments.get(1);
                arg1.accept(this);
                vArgs.add(arg0.reg);
                vArgs.add(arg1.reg);
                vreg = new VirtualReg("stringSubString");
                callee = ir.getBiFunc(IRRoot.BUILTIN_STRING_SUBSTRING);
                currentBB.appendInst(new FuncCallIR(currentBB, callee, vArgs, vreg));
                node.reg = vreg;
                break;
            case IRRoot.BUILTIN_STRING_PARSEINT:
                vreg = new VirtualReg("stringParseInt");
                callee = ir.getBiFunc(IRRoot.BUILTIN_STRING_PARSEINT);
                currentBB.appendInst(new FuncCallIR(currentBB, callee, vArgs, vreg));
                node.reg = vreg;
                break;
            case IRRoot.BUILTIN_STRING_ORD:
                arg0 = node.arguments.get(0);
                arg0.accept(this);
                vArgs.add(arg0.reg);
                vreg = new VirtualReg("stringOrd");
                callee = ir.getBiFunc(IRRoot.BUILTIN_STRING_ORD);
                currentBB.appendInst(new FuncCallIR(currentBB, callee, vArgs, vreg));
                node.reg = vreg;
                break;
            case IRRoot.BUILTIN_STRING_LENGTH:
            case IRRoot.BUILTIN_ARRAY_SIZE:
                vreg = new VirtualReg("sizeOrLength");
                currentBB.appendInst(new LoadIR(currentBB, vreg, Configuration.REG_SIZE, (Reg) vArgs.get(0), 0));
                node.reg = vreg;
                break;
            default:
                throw new CompileError("invalid builtIn function call");
        }
    }

    private void processArrayNew(NewExprNode node, VirtualReg oreg, Reg addr, int idx) {
        VirtualReg vreg = new VirtualReg(null);
        ExprNode dim = node.exprDimensions.get(idx);
        boolean wantAddrBak = wantAddr;
        wantAddr = false;
        dim.accept(this);
        wantAddr = wantAddrBak;

        currentBB.appendInst(new BinaryIR(currentBB, vreg, BinaryIR.IRBinaryOp.MUL, dim.reg, new Imm(Configuration.REG_SIZE)));
        currentBB.appendInst(new BinaryIR(currentBB, vreg, BinaryIR.IRBinaryOp.ADD, vreg, new Imm(Configuration.REG_SIZE)));
        currentBB.appendInst(new HeapAllocIR(currentBB, vreg, vreg));
        currentBB.appendInst(new StoreIR(currentBB, dim.reg, Configuration.REG_SIZE, vreg, 0));
        if (idx < node.exprDimensions.size() - 1) {
            VirtualReg loop_idx = new VirtualReg(null);
            VirtualReg addrNow = new VirtualReg(null);
            currentBB.appendInst(new MoveIR(currentBB, loop_idx, new Imm(0)));
            currentBB.appendInst(new MoveIR(currentBB, addrNow, vreg));
            IRBasicBlock condBB = new IRBasicBlock(currentFunc, "while_cond");
            IRBasicBlock bodyBB = new IRBasicBlock(currentFunc, "while_body");
            IRBasicBlock afterBB = new IRBasicBlock(currentFunc, "while_after");
            currentBB.setTransfer(new JumpIR(currentBB, condBB));

            currentBB = condBB;
            CompIR.IRCmpOp op = CompIR.IRCmpOp.LESS;
            VirtualReg cmpReg = new VirtualReg(null);
            currentBB.appendInst(new CompIR(currentBB, cmpReg, op, loop_idx, dim.reg));
            currentBB.setTransfer(new BranchIR(currentBB, cmpReg, bodyBB, afterBB));

            currentBB = bodyBB;
            currentBB.appendInst(new BinaryIR(currentBB, addrNow, BinaryIR.IRBinaryOp.ADD, addrNow,
                    new Imm(Configuration.REG_SIZE)));
            processArrayNew(node, null, addrNow, idx + 1);
            currentBB.appendInst(new BinaryIR(currentBB, loop_idx, BinaryIR.IRBinaryOp.ADD, loop_idx,
                    new Imm(1)));
            currentBB.setTransfer(new JumpIR(currentBB, condBB));

            currentBB = afterBB;
        }
        if (idx == 0) {
            currentBB.appendInst(new MoveIR(currentBB, oreg, vreg));
        } else {
            currentBB.appendInst(new StoreIR(currentBB, vreg, Configuration.REG_SIZE, addr, 0));
        }
    }

    @Override
    // ?? can i create new int()???
    public void visit(NewExprNode node) {
        VirtualReg vreg = new VirtualReg(null);
        Type newType = node.baseType.type;
        if (newType instanceof ClassType) {
            String className = ((ClassType) newType).name;
            ClassEntity classEntity = globalScope.classMap.get(className);
            if (node.numDimension == 0) {
                currentBB.appendInst(new HeapAllocIR(currentBB, vreg, new Imm(classEntity.memorySize)));
                String funcName = "@" + className + "@" + className;
                IRFunction irFunc = ir.getFunc(funcName);
                if (irFunc != null) {
                    List<IRValue> args = new ArrayList<>();
                    args.add(vreg);
                    currentBB.appendInst(new FuncCallIR(currentBB, irFunc, args, null));
                }
            } else {
                processArrayNew(node, vreg, null, 0);
            }
        } else if (newType instanceof PrimitiveType) {
            processArrayNew(node, vreg, null, 0);
        } else {
            throw new CompileError("invalid new type");
        }
        node.reg = vreg;
    }

    @Override
    public void visit(UnaryExprNode node) {
        VirtualReg vreg;
        switch (node.op) {
            case "+":
                node.reg = node.iExpr.reg;
                break;

            case "-":
                vreg = new VirtualReg("Unary");
                node.reg = vreg;
                node.iExpr.accept(this);
                currentBB.appendInst(new UnaryIR(currentBB, vreg, UnaryIR.IRUnaryOp.NEG,
                        node.iExpr.reg));
                break;

            case "~":
                vreg = new VirtualReg(null);
                node.reg = vreg;
                node.iExpr.accept(this);
                currentBB.appendInst(new UnaryIR(currentBB, vreg, UnaryIR.IRUnaryOp.BITWISE_NOT,
                        node.iExpr.reg));
                break;

            case "!":
                if (node.iExpr instanceof LiteralExprNode) {
                    boolean wtf = Boolean.parseBoolean(((LiteralExprNode) node.iExpr).value);
                    currentBB.setTransfer(new JumpIR(currentBB, (wtf) ? node.trueBB : node.falseBB));
                } else {
                    node.iExpr.trueBB = node.falseBB;
                    node.iExpr.falseBB = node.trueBB;
                    node.iExpr.accept(this);
                }
                break;
            default:
                throw new CompileError("invalid prefix operation");
        }
    }

    @Override
    public void visit(BinaryExprNode node) {
        switch (node.op) {
            case "&&":
            case "||":
                processLogicBinary(node);
                break;
            case "*":
            case "/":
            case "%":
            case "+":
            case "-":
            case "<<":
            case ">>":
            case "&":
            case "|":
            case "^":
                processArithBinary(node);
                break;

            case ">":
            case "<":
            case ">=":
            case "<=":
            case "==":
            case "!=":
                processCmpBinary(node);
                break;
        }
    }

    public void processCmpBinary(BinaryExprNode node) {
        if (node.lhs.type instanceof StringType) {
            processStringBinary(node);
            return;
        }
        node.lhs.accept(this);
        node.rhs.accept(this);
        IRValue lhs = node.lhs.reg;
        IRValue rhs = node.rhs.reg;
        boolean bothConst = (lhs instanceof Imm) && (rhs instanceof Imm);
        int lhsImm = 0, rhsImm = 0;
        if (lhs instanceof Imm)
            lhsImm = ((Imm) lhs).value;
        if (rhs instanceof Imm)
            rhsImm = ((Imm) rhs).value;

        VirtualReg vreg = new VirtualReg("cmpBinary");
        node.reg = vreg;
        CompIR.IRCmpOp op = null;
        IRValue tmp;
        switch (node.op) {
            case ">":
                op = GREATER;
                if (bothConst) {
                    node.reg = (lhsImm > rhsImm) ? new Imm(1) : new Imm(0);
                    return;
                }
                if (lhs instanceof Imm) {
                    tmp = lhs;
                    lhs = rhs;
                    rhs = tmp;
                    op = LESS;
                }
                break;
            case "<":
                op = LESS;
                if (bothConst) {
                    node.reg = (lhsImm < rhsImm) ? new Imm(1) : new Imm(0);
                    return;
                }
                if (lhs instanceof Imm) {
                    tmp = lhs;
                    lhs = rhs;
                    rhs = tmp;
                    op = GREATER;
                }
                break;
            case ">=":
                op = GREATER_EQUAL;
                if (bothConst) {
                    node.reg = (lhsImm >= rhsImm) ? new Imm(1) : new Imm(0);
                    return;
                }
                if (lhs instanceof Imm) {
                    tmp = lhs;
                    lhs = rhs;
                    rhs = tmp;
                    op = LESS_EQUAL;
                }
                break;
            case "<=":
                op = LESS_EQUAL;
                if (bothConst) {
                    node.reg = (lhsImm <= rhsImm) ? new Imm(1) : new Imm(0);
                    return;
                }
                if (lhs instanceof Imm) {
                    tmp = lhs;
                    lhs = rhs;
                    rhs = tmp;
                    op = GREATER_EQUAL;
                }
                break;
            case "==":
                op = EQUAL;
                if (bothConst) {
                    node.reg = (lhsImm == rhsImm) ? new Imm(1) : new Imm(0);
                    return;
                }
                if (lhs instanceof Imm) {
                    tmp = lhs;
                    lhs = rhs;
                    rhs = tmp;
                }
                break;
            case "!=":
                op = INEQUAL;
                if (bothConst) {
                    node.reg = (lhsImm != rhsImm) ? new Imm(1) : new Imm(0);
                    return;
                }
                if (lhs instanceof Imm) {
                    tmp = lhs;
                    lhs = rhs;
                    rhs = tmp;
                }
                break;
            default:
                assert false;
        }
        currentBB.appendInst(new CompIR(currentBB, vreg, op, lhs, rhs));
        if (node.trueBB != null) {
            currentBB.setTransfer(new BranchIR(currentBB, vreg, node.trueBB, node.falseBB));
        }
    }

    public void processStringBinary(BinaryExprNode node) {
        node.lhs.accept(this);
        node.rhs.accept(this);
        IRFunction func = null;
        ExprNode tmp;
        switch (node.op) {
            case "+":
                func = ir.getBiFunc(IRRoot.BUILTIN_STRING_CONCAT);
                break;
            case "==":
                func = ir.getBiFunc(IRRoot.BUILTIN_STRING_EQUAL);
                break;
            case "!=":
                func = ir.getBiFunc(IRRoot.BUILTIN_STRING_INEQUAL);
                break;
            case "<":
                func = ir.getBiFunc(IRRoot.BUILTIN_STRING_LESS);
                break;
            case "<=":
                func = ir.getBiFunc(IRRoot.BUILTIN_STRING_LESS_EQUAL);
                break;
            case ">":
                tmp = node.lhs;
                node.lhs = node.rhs;
                node.rhs = tmp;
                func = ir.getBiFunc(IRRoot.BUILTIN_STRING_LESS);
                break;
            case ">=":
                tmp = node.lhs;
                node.lhs = node.rhs;
                node.rhs = tmp;
                func = ir.getBiFunc(IRRoot.BUILTIN_STRING_LESS_EQUAL);
                break;
            default:
                assert false;
        }
        List<IRValue> args = new ArrayList<>();
        args.add(node.lhs.reg);
        args.add(node.rhs.reg);
        VirtualReg vreg = new VirtualReg("stringBinary");
        node.reg = vreg;
        currentBB.appendInst(new FuncCallIR(currentBB, func, args, vreg));
        if (node.trueBB != null) {
            currentBB.setTransfer(new BranchIR(currentBB, vreg, node.trueBB, node.falseBB));
        }
    }

    public void processArithBinary(BinaryExprNode node) {
        if (node.lhs.type instanceof StringType) {
            processStringBinary(node);
            return;
        }
        node.lhs.accept(this);
        node.rhs.accept(this);

        IRValue lhs = node.lhs.reg;
        IRValue rhs = node.rhs.reg;
        boolean bothConst = (lhs instanceof Imm) && (rhs instanceof Imm);
        int lhsImm = 0, rhsImm = 0;
        if (lhs instanceof Imm)
            lhsImm = ((Imm) lhs).value;
        if (rhs instanceof Imm)
            rhsImm = ((Imm) rhs).value;

        VirtualReg vreg = new VirtualReg("arithBinary");
        node.reg = vreg;
        switch (node.op) {
            case "*":
                if (bothConst) {
                    node.reg = new Imm(lhsImm * rhsImm);
                } else {
                    currentBB.appendInst(new BinaryIR(currentBB, vreg, BinaryIR.IRBinaryOp.MUL, lhs, rhs));
                }
                break;
            case "/":
                if (bothConst && rhsImm != 0) {
                    node.reg = new Imm(lhsImm / rhsImm);
                } else {
                    ir.hasDivShift = true;
                    currentBB.appendInst(new BinaryIR(currentBB, vreg, BinaryIR.IRBinaryOp.DIV, lhs, rhs));
                }
                break;
            case "%":
                if (bothConst && rhsImm != 0) {
                    node.reg = new Imm(lhsImm % rhsImm);
                } else {
                    ir.hasDivShift = true;
                    currentBB.appendInst(new BinaryIR(currentBB, vreg, BinaryIR.IRBinaryOp.MOD, lhs, rhs));
                }
                break;
            case "+":
                if (bothConst) {
                    node.reg = new Imm(lhsImm + rhsImm);
                } else {
                    currentBB.appendInst(new BinaryIR(currentBB, vreg, BinaryIR.IRBinaryOp.ADD, lhs, rhs));
                }
                break;
            case "-":
                if (bothConst) {
                    node.reg = new Imm(lhsImm - rhsImm);
                } else {
                    currentBB.appendInst(new BinaryIR(currentBB, vreg, BinaryIR.IRBinaryOp.SUB, lhs, rhs));
                }
                break;
            case "<<":
                if (bothConst) {
                    node.reg = new Imm(lhsImm << rhsImm);
                } else {
                    ir.hasDivShift = true;
                    currentBB.appendInst(new BinaryIR(currentBB, vreg, BinaryIR.IRBinaryOp.SHL, lhs, rhs));
                }
                break;
            case ">>":
                if (bothConst) {
                    node.reg = new Imm(lhsImm >> rhsImm);
                } else {
                    ir.hasDivShift = true;
                    currentBB.appendInst(new BinaryIR(currentBB, vreg, BinaryIR.IRBinaryOp.SHR, lhs, rhs));
                }
                break;
            case "&":
                if (bothConst) {
                    node.reg = new Imm(lhsImm & rhsImm);
                } else {
                    currentBB.appendInst(new BinaryIR(currentBB, vreg, BinaryIR.IRBinaryOp.BITWISE_AND, lhs, rhs));
                }
                break;
            case "|":
                if (bothConst) {
                    node.reg = new Imm(lhsImm | rhsImm);
                } else {
                    currentBB.appendInst(new BinaryIR(currentBB, vreg, BinaryIR.IRBinaryOp.BITWISE_OR, lhs, rhs));
                }
                break;
            case "^":
                if (bothConst) {
                    node.reg = new Imm(lhsImm ^ rhsImm);
                } else {
                    currentBB.appendInst(new BinaryIR(currentBB, vreg, BinaryIR.IRBinaryOp.BITWISE_XOR, lhs, rhs));
                }
                break;
            default:
                assert false;
        }
    }

    public void processLogicBinary(BinaryExprNode node) {
        ExprNode lhs = node.lhs;
        ExprNode rhs = node.rhs;
        boolean l, r = false;
        if (node.op.equals("&&")) {
            if ((lhs instanceof LiteralExprNode) && (rhs instanceof LiteralExprNode)) {
                l = Boolean.parseBoolean(((LiteralExprNode) lhs).value);
                r = Boolean.parseBoolean(((LiteralExprNode) rhs).value);
                currentBB.setTransfer(new JumpIR(currentBB, (l && r) ? node.trueBB : node.falseBB));
            } else if (lhs instanceof LiteralExprNode) {
                l = Boolean.parseBoolean(((LiteralExprNode) lhs).value);
                if (!l) {
                    currentBB.setTransfer(new JumpIR(currentBB, node.falseBB));
                    return;
                }
                node.rhs.trueBB = node.trueBB;
                node.rhs.falseBB = node.falseBB;
                node.rhs.accept(this);
            } else if (rhs instanceof LiteralExprNode) {
                r = Boolean.parseBoolean(((LiteralExprNode) rhs).value);
                // ?? not accept left(maybe problem)
                if (!r) {
                    currentBB.setTransfer(new JumpIR(currentBB, node.falseBB));
                    return;
                }
                node.lhs.trueBB = node.trueBB;
                node.lhs.falseBB = node.falseBB;
                node.lhs.accept(this);
            } else {
                node.lhs.trueBB = new IRBasicBlock(currentFunc, "and_lhs_true");
                node.lhs.falseBB = node.falseBB;
                node.lhs.accept(this);
                currentBB = node.lhs.trueBB;
                node.rhs.trueBB = node.trueBB;
                node.rhs.falseBB = node.falseBB;
                node.rhs.accept(this);
            }
        } else if (node.op.equals("||")) {
            if ((lhs instanceof LiteralExprNode) && (rhs instanceof LiteralExprNode)) {
                l = Boolean.parseBoolean(((LiteralExprNode) lhs).value);
                r = Boolean.parseBoolean(((LiteralExprNode) rhs).value);
                currentBB.setTransfer(new JumpIR(currentBB, (l || r) ? node.trueBB : node.falseBB));
            } else if (lhs instanceof LiteralExprNode) {
                l = Boolean.parseBoolean(((LiteralExprNode) lhs).value);
                if (l) {
                    currentBB.setTransfer(new JumpIR(currentBB, node.trueBB));
                    return;
                }
                node.rhs.trueBB = node.trueBB;
                node.rhs.falseBB = node.falseBB;
                node.rhs.accept(this);
            } else if (rhs instanceof LiteralExprNode) {
                r = Boolean.parseBoolean(((LiteralExprNode) rhs).value);
                if (r) {
                    // ?? not accept left maybe problem
                    currentBB.setTransfer(new JumpIR(currentBB, node.trueBB));
                    return;
                }
                node.lhs.trueBB = node.trueBB;
                node.lhs.falseBB = node.falseBB;
                node.lhs.accept(this);
            } else {
                node.lhs.trueBB = node.trueBB;
                node.lhs.falseBB = new IRBasicBlock(currentFunc, "or_lhs_false");
                node.lhs.accept(this);
                currentBB = node.lhs.falseBB;
                node.rhs.trueBB = node.trueBB;
                node.rhs.falseBB = node.falseBB;
                node.rhs.accept(this);
            }
        } else {
            assert false;
        }
    }

    @Override
    public void visit(AssignExprNode node) {
        boolean needMemOp = isMemoryAccess(node.lhs);

        wantAddr = needMemOp;
        node.lhs.accept(this);
        wantAddr = false;

        if (isLogicalExpression(node.rhs)) {
            node.rhs.trueBB = new IRBasicBlock(currentFunc, "true");
            node.rhs.falseBB = new IRBasicBlock(currentFunc, "false");
        }
        node.rhs.accept(this);

        Reg dest;
        int addrOffset;
        if (needMemOp) {
            dest = node.lhs.addr;
            addrOffset = node.lhs.addrOffset;
        } else {
            dest = (Reg) node.lhs.reg;
            addrOffset = 0;
        }
        processAssign(dest, addrOffset, node.rhs, needMemOp);
        // ?? maybe some problem
        node.reg = node.rhs.reg;
    }

    @Override
    public void visit(PostfixExprNode node) {
        processSelfIncDec(node.iExpr, node, true, node.op.equals("++"));
    }

    public void processSelfIncDec(ExprNode expr, ExprNode node, boolean isSuffix, boolean isInc) {
        boolean needMemOp = isMemoryAccess(expr);
        boolean bakWantAddr = wantAddr;

        wantAddr = false;
        expr.accept(this);

        if (isSuffix) {
            VirtualReg vreg = new VirtualReg("suffix");
            currentBB.appendInst(new MoveIR(currentBB, vreg, expr.reg));
            node.reg = vreg;
        } else {
            node.reg = expr.reg;
        }

        Imm one = new Imm(1);
        BinaryIR.IRBinaryOp op = isInc ? BinaryIR.IRBinaryOp.ADD : BinaryIR.IRBinaryOp.SUB;

        if (needMemOp) {
            // get addr of expr
            wantAddr = true;
            expr.accept(this);

            VirtualReg vreg = new VirtualReg("suffix_memo");
            currentBB.appendInst(new BinaryIR(currentBB, vreg, op, expr.reg, one));
            currentBB.appendInst(new StoreIR(currentBB, vreg, Configuration.REG_SIZE, expr.addr, expr.addrOffset));
            if (!isSuffix) {
                expr.reg = vreg;
            }
        } else {
            currentBB.appendInst(
                    new BinaryIR(currentBB, (Reg) expr.reg, op, expr.reg, one));
        }
        wantAddr = bakWantAddr;
    }

    @Override
    public void visit(PrefixExprNode node) {
        processSelfIncDec(node.iExpr, node, false, node.op.equals("++"));
    }
}
