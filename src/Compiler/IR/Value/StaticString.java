package Compiler.IR.Value;

import Compiler.IR.IRVisitor;
import Compiler.utils.Configuration;

public class StaticString extends Reg {
    public String value;

    public StaticString(String value) {
        this.value = value;
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
