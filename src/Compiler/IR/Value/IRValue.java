package Compiler.IR.Value;

import Compiler.IR.IRVisitor;

public abstract class IRValue {
    public abstract void accept(IRVisitor visitor);
}
