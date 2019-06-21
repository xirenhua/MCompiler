package Compiler.IR.Value;

import Compiler.IR.IRVisitor;

public class VirtualReg extends Reg {
    public String name;
    public PhysicalReg forcePhyReg;

    public VirtualReg(String name) {
        this.name = name;
        forcePhyReg = null;
    }


    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
