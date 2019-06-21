package Compiler.IR.Instruction;

import Compiler.IR.IRBasicBlock;
import Compiler.IR.IRVisitor;
import Compiler.IR.Value.Reg;
import Compiler.IR.Value.VirtualReg;
import Compiler.utils.CompileError;

import java.util.*;

public abstract class IRInstruction {
    public IRInstruction prevInst, nextInst;
    public IRBasicBlock parentBB;
    public List<Reg> useRegs;
    public Reg defReg;
    public Set<VirtualReg> liveIn;
    public Set<VirtualReg> liveOut;
    public boolean removed;


    public IRInstruction(IRBasicBlock parentBB) {
        this.prevInst = null;
        this.nextInst = null;
        this.parentBB = parentBB;
        useRegs = new ArrayList<>();
        defReg = null;
        liveIn = new HashSet<>();
        liveOut = new HashSet<>();
        removed = false;
    }

    public void addBefore(IRInstruction before) {
        if (prevInst != null) {
            prevInst.nextInst = before;
            before.prevInst = prevInst;
        } else {
            parentBB.firstInst = before;
        }
        this.prevInst = before;
        before.nextInst = this;
    }


    public void addNext(IRInstruction next) {
        if (this.nextInst != null) {
            nextInst.prevInst = next;
            next.nextInst = nextInst;
        } else {
            parentBB.lastInst = next;
        }
        this.nextInst = next;
        next.prevInst = this;
    }

    public void remove() {
        if (removed) {
            throw new CompileError("cannot remove an instruction already removed");
        }
        removed = true;
        if (prevInst != null)
            prevInst.nextInst = nextInst;
        if (nextInst != null)
            nextInst.prevInst = prevInst;
        if (this instanceof TransferIR) {
            parentBB.removeTransfer();
        }
        if (this == parentBB.firstInst) parentBB.firstInst = nextInst;
        if (this == parentBB.lastInst) parentBB.lastInst = prevInst;
    }

    public void replace(IRInstruction inst) {
        if (removed) {
            throw new CompileError("cannot remove an instruction already removed");
        }
        removed = true;
        inst.prevInst = prevInst;
        inst.nextInst = nextInst;
        if (prevInst != null) prevInst.nextInst = inst;
        if (nextInst != null) nextInst.prevInst = inst;
        if (this == parentBB.firstInst) parentBB.firstInst = inst;
        if (this == parentBB.lastInst) parentBB.lastInst = inst;
    }

    public abstract void renewUseRegs();

    public abstract void mapUseRegs(Map<Reg, Reg> fullMap);

    public abstract void renewDefReg();

    public abstract void mapDefReg(Reg reg);

    public abstract void accept(IRVisitor visitor);
}
