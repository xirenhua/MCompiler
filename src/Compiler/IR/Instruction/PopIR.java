package Compiler.IR.Instruction;

import Compiler.IR.IRBasicBlock;
import Compiler.IR.IRVisitor;
import Compiler.IR.Value.PhysicalReg;
import Compiler.IR.Value.Reg;

import java.util.Map;

public class PopIR extends IRInstruction {
    public PhysicalReg preg;

    public PopIR(IRBasicBlock parentBB, PhysicalReg preg) {
        super(parentBB);
        this.preg = preg;
        renewUseRegs();
        renewDefReg();
    }

    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void renewDefReg() {
    }

    @Override
    public void mapDefReg(Reg vreg) {
    }

    @Override
    public void renewUseRegs() {

    }

    @Override
    public void mapUseRegs(Map<Reg, Reg> fullMap) {
    }
}