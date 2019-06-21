package Compiler.IR.Value;

import Compiler.IR.IRVisitor;

public class StaticVar extends Reg {
    public String name;
    public int size;

    public StaticVar(String name, int size) {
        this.name = name;
        this.size = size;
    }

    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
