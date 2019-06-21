package Compiler.IR.Value;

import Compiler.IR.IRVisitor;

public class PhysicalReg extends Reg {
    public final String name;
    public final boolean isGeneral, isCallerSave, isCalleeSave, isPosArg;
    public final int posArgId;

    public PhysicalReg(String name, boolean isGeneral, boolean isCallerSave, boolean isCalleeSave, int posArgId) {
        this.name = name;
        this.isGeneral = isGeneral;
        this.isCallerSave = isCallerSave;
        this.isCalleeSave = isCalleeSave;
        this.posArgId = posArgId;
        this.isPosArg = (posArgId != -1);
    }

    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}