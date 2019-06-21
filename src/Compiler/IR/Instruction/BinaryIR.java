package Compiler.IR.Instruction;

import Compiler.IR.IRBasicBlock;
import Compiler.IR.IRVisitor;
import Compiler.IR.Value.*;

import java.util.Map;

public class BinaryIR extends IRInstruction {

    public enum IRBinaryOp {
        ADD, SUB, MUL, DIV, MOD, SHL, SHR, BITWISE_AND, BITWISE_OR, BITWISE_XOR
    }

    public Reg dest;
    public IRBinaryOp op;
    public IRValue lhs, rhs;

    public BinaryIR(IRBasicBlock parentBB, Reg dest, IRBinaryOp op, IRValue lhs, IRValue rhs) {
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
