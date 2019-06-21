package Compiler.IR.Instruction;

import Compiler.IR.IRBasicBlock;
import Compiler.IR.IRVisitor;
import Compiler.IR.Value.IRValue;
import Compiler.IR.Value.Reg;

import java.util.Map;

public class CompIR extends IRInstruction {
    public enum IRCmpOp {
        GREATER, LESS, GREATER_EQUAL, LESS_EQUAL, EQUAL, INEQUAL
    }

    public Reg dest;
    public IRCmpOp op;
    public IRValue lhs, rhs;

    public CompIR(IRBasicBlock parentBB, Reg dest, IRCmpOp op, IRValue lhs, IRValue rhs) {
        super(parentBB);
        this.dest = dest;
        this.op = op;
        this.lhs = lhs;
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
        if (lhs instanceof Reg)
            useRegs.add((Reg) lhs);
        if (rhs instanceof Reg)
            useRegs.add((Reg) rhs);
    }

    @Override
    public void mapUseRegs(Map<Reg, Reg> fullMap) {
        if (lhs instanceof Reg) lhs = fullMap.get(lhs);
        if (rhs instanceof Reg) rhs = fullMap.get(rhs);
        renewUseRegs();
    }

    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
