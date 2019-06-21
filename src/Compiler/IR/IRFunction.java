package Compiler.IR;

import Compiler.entities.FuncEntity;
import Compiler.IR.Instruction.*;
import Compiler.IR.Value.*;

import java.util.*;

public class IRFunction {
    public String name;
    public String builinLabel;
    public boolean isBuitIn;
    public IRBasicBlock startBB, endBB;
    public FuncEntity funcEntity;
    public List<VirtualReg> argVRegList;
    public List<ReturnIR> retInstList;
    public Set<IRFunction> calleeSet;
    public Set<IRFunction> recurCalleeSet;
    public List<IRBasicBlock> revPostOrder;
    public List<IRBasicBlock> revPreOrder;
    public List<StackSlot> slots;
    public Map<VirtualReg, StackSlot> argSlotMap;
    public Set<PhysicalReg> refPRegs;
    public Set<IRBasicBlock> dfsVisited;

    public IRFunction(String newName, FuncEntity funcEntity) {
        name = newName;
        builinLabel = null;
        isBuitIn = false;
        startBB = endBB = null;
        this.funcEntity = funcEntity;
        argVRegList = new ArrayList<>();
        retInstList = new ArrayList<>();
        calleeSet = new HashSet<>();
        recurCalleeSet = new HashSet<>();
        revPostOrder = new ArrayList<>();
        revPreOrder = new ArrayList<>();
        slots = new ArrayList<>();
        argSlotMap = new HashMap<>();
        refPRegs = new HashSet<>();
        dfsVisited = new HashSet<>();
    }

    public IRFunction(String newName, String ibuiltInLabel) {
        name = newName;
        builinLabel = ibuiltInLabel;
        isBuitIn = true;
        startBB = endBB = null;
        this.funcEntity = null;
        argVRegList = new ArrayList<>();
        retInstList = new ArrayList<>();
        calleeSet = new HashSet<>();
        recurCalleeSet = new HashSet<>();
        revPostOrder = new ArrayList<>();
        revPreOrder = new ArrayList<>();
        slots = new ArrayList<>();
        argSlotMap = new HashMap<>();
        refPRegs = new HashSet<>();
        dfsVisited = new HashSet<>();
    }


    public void accept(IRVisitor vis) {
        vis.visit(this);
    }

    public IRBasicBlock createStartBB() {
        startBB = new IRBasicBlock(this, name + "_start");
        return startBB;
    }

    public void addArg(VirtualReg vreg) {
        argVRegList.add(vreg);
    }

    public void calcCalleeSet() {
        calleeSet.clear();
        for (IRBasicBlock bb : revPostOrder) {
            for (IRInstruction inst = bb.firstInst; inst != null; inst = inst.nextInst) {
                if (inst instanceof FuncCallIR) {
                    calleeSet.add(((FuncCallIR) inst).func);
                }
            }
        }
    }

    public void calcReversePostOrder() {
        revPostOrder.clear();
        dfsVisited.clear();
        dfsPostOrder(startBB);
        for (int i = 0; i < revPostOrder.size(); ++i) {
            revPostOrder.get(i).postIndex = i;
        }
        Collections.reverse(revPostOrder);
    }

    public void dfsPostOrder(IRBasicBlock bb) {
        if (dfsVisited.contains(bb)) return;
        dfsVisited.add(bb);
        for (IRBasicBlock nextBB : bb.nextBBSet) {
            dfsPostOrder(nextBB);
        }
        revPostOrder.add(bb);
    }

    public void calcReversePreOrder() {
        revPreOrder.clear();
        dfsVisited.clear();
        dfsPreOrder(startBB);
        for (int i = 0; i < revPreOrder.size(); ++i) {
            revPreOrder.get(i).preIndex = i;
        }
        Collections.reverse(revPreOrder);
    }

    public void dfsPreOrder(IRBasicBlock bb) {
        if (dfsVisited.contains(bb)) return;
        dfsVisited.add(bb);
        revPreOrder.add(bb);
        for (IRBasicBlock nextBB : bb.nextBBSet) {
            dfsPreOrder(nextBB);
        }
    }
}