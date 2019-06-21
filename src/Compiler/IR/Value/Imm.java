package Compiler.IR.Value;

import Compiler.IR.IRVisitor;

public class Imm extends IRValue {
    public int value;

    public Imm(int value) {
        this.value = value;
    }

    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
