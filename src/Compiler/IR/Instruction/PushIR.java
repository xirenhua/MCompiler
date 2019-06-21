package Compiler.IR.Instruction;

import Compiler.IR.IRBasicBlock;
import Compiler.IR.IRVisitor;
import Compiler.IR.Value.IRValue;
import Compiler.IR.Value.Reg;

import java.util.Map;

public class PushIR extends IRInstruction {
    public IRValue value;

    public PushIR(IRBasicBlock parentBB, IRValue value) {
        super(parentBB);
        this.value = value;
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