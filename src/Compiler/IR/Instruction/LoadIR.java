package Compiler.IR.Instruction;

import Compiler.IR.IRBasicBlock;
import Compiler.IR.IRVisitor;
import Compiler.IR.Value.IRValue;
import Compiler.IR.Value.Reg;
import Compiler.IR.Value.StackSlot;
import Compiler.IR.Value.StaticVar;

import java.util.Map;

public class LoadIR extends IRInstruction {

    public Reg dest;
    public int size;
    public IRValue addr;
    public int addrOffset;

    public LoadIR(IRBasicBlock parentBB, Reg dest, int size, Reg addr, int addrOffset) {
        super(parentBB);
        this.dest = dest;
        this.size = size;
        this.addr = addr;
        this.addrOffset = addrOffset;
        renewUseRegs();
        renewDefReg();
    }

    public LoadIR(IRBasicBlock parentBB, Reg dest, int size, StaticVar address) {
        super(parentBB);
        this.dest = dest;
        this.size = size;
        this.addr = address;
        this.addrOffset = 0;
        renewUseRegs();
        renewDefReg();
    }

    public LoadIR(IRBasicBlock parentBB, Reg dest, int size, StackSlot address, int addrOffset) {
        super(parentBB);
        this.dest = dest;
        this.size = size;
        this.addr = address;
        this.addrOffset = addrOffset;
        renewUseRegs();
        renewDefReg();
    }

    @Override
    public void renewDefReg() {
        defReg = dest;
    }

    @Override
    public void mapDefReg(Reg vreg) {
        dest = vreg;
        renewDefReg();
    }

    @Override
    public void renewUseRegs() {
        useRegs.clear();
        if (addr instanceof Reg)
            useRegs.add((Reg) addr);
    }

    @Override
    public void mapUseRegs(Map<Reg, Reg> fullMap) {
        if (addr instanceof Reg) addr = fullMap.get(addr);
        renewUseRegs();
    }

    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}