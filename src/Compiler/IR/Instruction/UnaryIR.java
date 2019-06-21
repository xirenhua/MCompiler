package Compiler.IR.Instruction;

import Compiler.IR.IRBasicBlock;
import Compiler.IR.IRVisitor;
import Compiler.IR.Value.IRValue;
import Compiler.IR.Value.Reg;

import java.util.Map;

public class UnaryIR extends IRInstruction {
    public enum IRUnaryOp {
        BITWISE_NOT, NEG
    }

    public Reg dest;
    public IRUnaryOp op;
    public IRValue rhs;

    public UnaryIR(IRBasicBlock parentBB, Reg dest, IRUnaryOp op, IRValue rhs) {
        super(parentBB);
        this.dest = dest;
        this.op = op;
        this.rhs = rhs;
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
