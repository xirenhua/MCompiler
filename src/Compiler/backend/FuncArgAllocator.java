package Compiler.backend;


import Compiler.IR.IRFunction;
import Compiler.IR.IRRoot;
import Compiler.IR.Instruction.IRInstruction;
import Compiler.IR.Instruction.LoadIR;
import Compiler.IR.Value.StackSlot;
import Compiler.IR.Value.VirtualReg;
import Compiler.NASM.NASMRegSet;
import Compiler.utils.Configuration;

public class FuncArgAllocator {
    public IRRoot ir;

    public FuncArgAllocator(IRRoot ir) {
        this.ir = ir;
    }

    public void processFuncArgs(IRFunction func) {
        IRInstruction firtInst = func.startBB.firstInst;
        for (int i = 6; i < func.argVRegList.size(); ++i) {
            VirtualReg argVreg = func.argVRegList.get(i);
            StackSlot argSlot = new StackSlot(func, "arg" + i, true);
            func.argSlotMap.put(argVreg, argSlot);
            firtInst.addBefore(new LoadIR(firtInst.parentBB, argVreg, Configuration.REG_SIZE, argSlot, 0));
        }
        if (func.argVRegList.size() > 0)
            func.argVRegList.get(0).forcePhyReg = NASMRegSet.rdi;
        if (func.argVRegList.size() > 1)
            func.argVRegList.get(1).forcePhyReg = NASMRegSet.rsi;
        if (func.argVRegList.size() > 2)
            func.argVRegList.get(2).forcePhyReg = NASMRegSet.rdx;
        if (func.argVRegList.size() > 3)
            func.argVRegList.get(3).forcePhyReg = NASMRegSet.rcx;
        if (func.argVRegList.size() > 4)
            func.argVRegList.get(4).forcePhyReg = NASMRegSet.r8;
        if (func.argVRegList.size() > 5)
            func.argVRegList.get(5).forcePhyReg = NASMRegSet.r9;
    }

    public void run() {
        for (IRFunction irFunction : ir.funcs.values()) {
            processFuncArgs(irFunction);
        }
    }
}
