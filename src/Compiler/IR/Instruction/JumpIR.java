package Compiler.IR.Instruction;

import Compiler.IR.IRBasicBlock;
import Compiler.IR.IRVisitor;
import Compiler.IR.Value.Reg;

import java.util.Map;

public class JumpIR extends TransferIR {

    public IRBasicBlock targetBB;

    public JumpIR(IRBasicBlock parentBB, IRBasicBlock targetBB) {
        super(parentBB);
        this.targetBB = targetBB;
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
    }

    @Override
    public void mapUseRegs(Map<Reg, Reg> fullMap) {
    }

    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
