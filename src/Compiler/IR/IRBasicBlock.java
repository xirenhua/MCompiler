package Compiler.IR;

import Compiler.IR.Instruction.*;
import Compiler.utils.CompileError;

import java.util.HashSet;
import java.util.Set;

public class IRBasicBlock {
    public String name;
    public IRFunction func;
    public IRInstruction firstInst, lastInst;
    public boolean hasTransfer;
    public int postIndex, preIndex;
    public Set<IRBasicBlock> nextBBSet;

    public IRBasicBlock(IRFunction func, String name) {
        this.name = name;
        this.func = func;
        this.firstInst = null;
        this.lastInst = null;
        hasTransfer = false;
        postIndex = preIndex = 0;
        nextBBSet = new HashSet<>();
    }

    public void reInit() {
        firstInst = null;
        lastInst = null;
        hasTransfer = false;
    }


    public void appendInst(IRInstruction inst) {
        if (hasTransfer) {
            throw new CompileError("already have transfer when appendInst");
        }
        if (firstInst == null) {
            firstInst = lastInst = inst;
        } else {
            lastInst.addNext(inst);
        }
    }

    public void setTransfer(TransferIR inst) {
        appendInst(inst);
        hasTransfer = true;
        if (inst instanceof BranchIR) {
            nextBBSet.add(((BranchIR) inst).thenBB);
            nextBBSet.add(((BranchIR) inst).elseBB);
        } else if (inst instanceof JumpIR) {
            nextBBSet.add(((JumpIR) inst).targetBB);
        } else if (inst instanceof ReturnIR) {
            func.retInstList.add((ReturnIR) inst);
        } else {
            throw new CompileError("invalid transfer type when setTransfer");
        }
    }

    public void removeTransfer() {
        hasTransfer = false;
        if (lastInst instanceof BranchIR) {
            nextBBSet.remove(((BranchIR) lastInst).thenBB);
            nextBBSet.remove(((BranchIR) lastInst).elseBB);
        } else if (lastInst instanceof JumpIR) {
            nextBBSet.remove(((JumpIR) lastInst).targetBB);
        } else if (lastInst instanceof ReturnIR) {
            // ??
        } else {
            throw new CompileError("invalid transfer type when removeTransfer");
        }
    }

    public void accept(IRVisitor vis) {
        vis.visit(this);
    }
}
