package Compiler.IR.Instruction;

import Compiler.IR.IRBasicBlock;
import Compiler.IR.IRVisitor;
import Compiler.IR.Value.IRValue;
import Compiler.IR.Value.Reg;

import java.util.Map;

public class MoveIR extends IRInstruction {
    public Reg lhs;
    public IRValue rhs;

    public MoveIR(IRBasicBlock parentBB, Reg lhs, IRValue rhs) {
        super(parentBB);
        this.lhs = lhs;
        this.rhs = rhs;
        renewUseRegs();
        renewDefReg();
    }
    @Override
    public void renewDefReg() {
        defReg = lhs;
    }

    @Override
    public void mapDefReg(Reg vreg) {
        lhs = vreg;
        renewDefReg();
    }

    @Override
    public void renewUseRegs() {
        useRegs.clear();
        if (rhs instanceof Reg)
            useRegs.add((Reg) rhs);
    }

    @Override
    public void mapUseRegs(Map<Reg, Reg> fullMap) {
        if (rhs instanceof Reg) rhs = fullMap.get(rhs);
        renewUseRegs();
    }

    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
