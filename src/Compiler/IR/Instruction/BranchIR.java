package Compiler.IR.Instruction;

import Compiler.IR.IRBasicBlock;
import Compiler.IR.IRVisitor;
import Compiler.IR.Value.IRValue;
import Compiler.IR.Value.Reg;

import java.util.Map;

public class BranchIR extends TransferIR {
    public IRValue cond;
    public IRBasicBlock thenBB, elseBB;

    public BranchIR(IRBasicBlock parentBB, IRValue cond, IRBasicBlock thenBB, IRBasicBlock elseBB) {
        super(parentBB);
        this.cond = cond;
        this.thenBB = thenBB;
        this.elseBB = elseBB;
        renewUseRegs();
        renewDefReg();
    }

    @Override
    public void renewDefReg() {
    }

    @Override
    public void mapDefReg(Reg vreg) {
    }

    @Override
    public void renewUseRegs() {
        useRegs.clear();
        if (cond instanceof Reg)
            useRegs.add((Reg) cond);
    }

    @Override
    public void mapUseRegs(Map<Reg, Reg> fullMap) {
        if (cond instanceof Reg) cond = fullMap.get(cond);
        renewUseRegs();
    }

    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
