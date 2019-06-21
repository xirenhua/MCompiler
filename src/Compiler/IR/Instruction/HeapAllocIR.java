package Compiler.IR.Instruction;

import Compiler.IR.IRBasicBlock;
import Compiler.IR.IRVisitor;
import Compiler.IR.Value.IRValue;
import Compiler.IR.Value.Reg;

import java.util.Map;

public class HeapAllocIR extends IRInstruction {

    public Reg dest;
    public IRValue allocSize;

    public HeapAllocIR(IRBasicBlock parentBB, Reg dest, IRValue allocSize) {
        super(parentBB);
        this.dest = dest;
        this.allocSize = allocSize;
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
        if (allocSize instanceof Reg)
            useRegs.add((Reg) allocSize);
    }

    @Override
    public void mapUseRegs(Map<Reg, Reg> fullMap) {
        if (allocSize instanceof Reg) allocSize = fullMap.get(allocSize);
        renewUseRegs();
    }

    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
