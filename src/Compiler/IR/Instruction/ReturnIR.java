package Compiler.IR.Instruction;

import Compiler.IR.IRBasicBlock;
import Compiler.IR.IRVisitor;
import Compiler.IR.Value.IRValue;
import Compiler.IR.Value.Reg;

import java.util.Map;

public class ReturnIR extends TransferIR {

    public IRValue retValue;

    public ReturnIR(IRBasicBlock parentBB, IRValue retValue) {
        super(parentBB);
        this.retValue = retValue;
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
        if (retValue != null && retValue instanceof Reg)
            useRegs.add((Reg) retValue);
    }

    @Override
    public void mapUseRegs(Map<Reg, Reg> fullMap) {
        if (retValue != null && retValue instanceof Reg)
            retValue = fullMap.get(retValue);
        renewUseRegs();
    }

    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
