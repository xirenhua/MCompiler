package Compiler.IR.Value;

import Compiler.IR.IRFunction;
import Compiler.IR.IRVisitor;

public class StackSlot extends IRValue {
    public IRFunction parentFunc;
    public String name;

    public StackSlot(IRFunction parentFunc, String name, boolean isArgSlot) {
        this.parentFunc = parentFunc;
        this.name = name;
        if (!isArgSlot)
            parentFunc.slots.add(this);
    }

    @Override
    public void accept(IRVisitor visitor) {
    }
}
